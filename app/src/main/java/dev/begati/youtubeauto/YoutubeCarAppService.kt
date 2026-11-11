package dev.begati.youtubeauto

import android.app.Activity
import android.app.Presentation
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import android.service.media.MediaBrowserService
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.car.app.AppManager
import androidx.car.app.CarAppService
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.ItemList
import androidx.car.app.model.SearchTemplate
import androidx.car.app.model.Toggle
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.validation.HostValidator
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import kotlin.concurrent.thread

// Sets the saved quality on the current video; stamps the time so the resulting change event isn't saved as a pick.
private const val JS_APPLY_QUALITY =
    "(p=>{if(p&&p.setPlaybackQualityRange){window.__ytaQAt=Date.now();const q=yta.quality();p.setPlaybackQualityRange(q,q)}})" +
        "(document.getElementById('movie_player'))"

// Sets the saved speed and turns captions on/off on the current video. load/unloadModule is used because
// toggleSubtitlesOn proved unreliable on m.youtube.com (stale isSubtitlesOn, toggles ignored).
// ponytail: internal player API of m.youtube.com, re-check via chrome://inspect if speed or captions stop applying
private const val JS_APPLY_PLAYBACK =
    "(p=>{if(!p||!p.setPlaybackRate)return;window.__ytaRAt=Date.now();p.setPlaybackRate(+yta.speed());" +
        "p[yta.flag('captions')?'loadModule':'unloadModule']('captions')})(document.getElementById('movie_player'))"

// Injected on every full page load (m.youtube.com is a SPA, so the guard keeps it to once per window):
// - fakes a visible page so YouTube keeps playing while the car shows another app (Maps etc.), if "background" is on
// - reports play/pause of the main player (not feed previews) and the video's title/channel/thumbnail to the app through the `yta` bridge
//   (from the page's navigator.mediaSession, falling back to document.title, the player's author and the ytimg thumbnail)
// - hides the playlist "Próximo / Mix de ..." bar that covers the player on short car screens
// - stretches the player over the whole screen on /watch pages (polled, SPA navigation has no load event), if "fullscreen" is on,
//   with the video's object-fit from yta.aspect() (Ajustar/Preencher/Esticar)
// - clicks "Pular anúncio" as soon as it shows up, if "skipAds" is on
// Flags are read through the bridge on every use, so settings changes apply without a reload.
// - starts each video at the saved default quality (360p unless changed); applied once per video id, and ads
//   have their own id so they don't eat it. A quality picked later in the player menu becomes the new default.
// - same per-video moment: applies the saved speed (a speed picked in the player becomes the default too)
//   and captions on/off
// - reports the watch position every 500 ms so a rebuilt car screen resumes where it was
// - opens /shorts/<id> as /watch?v=<id> (the shorts page blocks playback behind an "open the app" dialog)
// - unmutes a video that autoplayed muted (opened from the phone or the media card), during its first 5 s
// ponytail: tied to m.youtube.com markup; re-inspect via chrome://inspect when YouTube changes it
private const val PAGE_JS =
    "(()=>{if(window.__yta)return;window.__yta=1;" +
        "const P=Document.prototype,H=Object.getOwnPropertyDescriptor(P,'hidden').get," +
        "V=Object.getOwnPropertyDescriptor(P,'visibilityState').get;" +
        "Object.defineProperty(document,'hidden',{get(){return yta.flag('background')?false:H.call(this)}});" +
        "Object.defineProperty(document,'visibilityState',{get(){return yta.flag('background')?'visible':V.call(this)}});" +
        "window.addEventListener('visibilitychange',e=>{if(yta.flag('background'))e.stopImmediatePropagation()},true);" +
        "const meta=()=>{const m=navigator.mediaSession&&navigator.mediaSession.metadata;" +
        "const id=new URLSearchParams(location.search).get('v');" +
        "const pv=document.getElementById('movie_player'),vd=pv&&pv.getVideoData&&pv.getVideoData();" +
        // document.title is a bare "YouTube" until the page fills it in: skip that placeholder.
        "const tt=m&&m.title||document.title.replace(/ - YouTube\$/,'');if(tt==='YouTube')return;" +
        "yta.meta(tt,m&&m.artist||vd&&vd.author||''," +
        "m&&m.artwork&&m.artwork.length?m.artwork[m.artwork.length-1].src:(id?'https://i.ytimg.com/vi/'+id+'/hqdefault.jpg':''))};" +
        // Only the main player (watch page or miniplayer) counts: a feed preview playing is not "a video playing".
        "const main=e=>e.target.closest&&!!e.target.closest('#movie_player');" +
        "document.addEventListener('play',e=>{if(!main(e))return;yta.playing(true);meta();setTimeout(meta,1500)},true);" +
        "document.addEventListener('pause',e=>main(e)&&yta.playing(false),true);" +
        "let qId='';document.addEventListener('play',()=>{const p=document.getElementById('movie_player');" +
        "const id=p&&p.getVideoData&&p.getVideoData().video_id;" +
        "if(!id||id===qId||!p.setPlaybackQualityRange)return;qId=id;" + JS_APPLY_QUALITY + ";" + JS_APPLY_PLAYBACK + ";" +
        // Changes in the first 5 s are our own call or a video lacking that quality, not the user's pick.
        "if(!p.__yta){p.__yta=1;p.addEventListener('onPlaybackQualityChange'," +
        "v=>{if(Date.now()-window.__ytaQAt>5000&&yta.quality()!=='auto')yta.saveQuality(v)});" +
        // Same for speed; the id check also skips YouTube resetting the rate as the next video loads.
        "p.addEventListener('onPlaybackRateChange',r=>{if(Date.now()-window.__ytaRAt>2000&&" +
        "p.getVideoData().video_id===qId)yta.saveSpeed(String(r))})}},true);" +
        "const s=document.createElement('style');" +
        "s.textContent='ytm-playlist-engagement-panel{display:none!important}" +
        "html.yta-watch #player-container-id{position:fixed!important;inset:0!important;" +
        "width:100vw!important;height:100vh!important;z-index:99999!important;background:#000!important}" +
        // Wrappers between the container and the video (#player keeps an aspect-ratio padding, the video
        // container has height 0): without this the video gets 100% of 0 px and the screen stays black.
        "html.yta-watch #player-container-id #player,html.yta-watch #player-container-id .html5-video-container" +
        "{position:absolute!important;inset:0!important;width:100%!important;height:100%!important;padding:0!important}" +
        "html.yta-watch #player-container-id .html5-video-player,html.yta-watch #player-container-id video" +
        "{width:100%!important;height:100%!important;left:0!important;top:0!important;object-fit:var(--yta-fit,contain)!important}';" +
        "document.head.appendChild(s);" +
        "let umId='',umN=0,pl=null;setInterval(()=>{" +
        // Shorts pages on m.youtube.com block playback behind an "Assistir no app YouTube" dialog; the watch page of
        // the same id plays normally, so open that instead (as videos sent from the phone do).
        "const sh=location.pathname.match(/^\\/shorts\\/([\\w-]{11})/);if(sh){location.replace('/watch?v='+sh[1]);return}" +
        // Play/pause and metadata also by polling: a video that starts before this script is injected (loadUrl from
        // the phone or the media card) fires its play event too early to be heard.
        // pl starts unknown so a new document (Início after a video) reports its state even when nothing plays.
        "const v=document.querySelector('#movie_player video'),on=!!v&&!v.paused&&!v.ended;if(on!==pl){pl=on;yta.playing(on)}" +
        // Title and channel fill in late after a load (document.title is "YouTube" at first): report while playing;
        // the app drops repeats.
        "if(on&&location.pathname==='/watch')meta();" +
        "document.documentElement.classList.toggle('yta-watch'," +
        "yta.flag('fullscreen')&&location.pathname==='/watch');" +
        "document.documentElement.style.setProperty('--yta-fit',yta.aspect());" +
        "if(yta.flag('skipAds'))document.querySelector('.ytp-skip-ad-button,.ytp-ad-skip-button,.ytp-ad-skip-button-modern')?.click();" +
        // Only the URL's own video counts: during an ad the player reports the ad's id and time.
        "const p=document.getElementById('movie_player'),id=new URLSearchParams(location.search).get('v');" +
        // A video opened without a tap on the car (phone remote, media card) autoplays muted, and the player mutes it
        // again right after the play event; unmute during its first 5 s of playback, so a later mute by hand sticks.
        "if(id&&p&&p.isMuted&&p.getPlayerState()===1&&p.getVideoData().video_id===id){if(id!==umId){umId=id;umN=0}if(umN++<10&&p.isMuted())p.unMute()}" +
        "if(id&&p&&p.getVideoData&&p.getVideoData().video_id===id)yta.time(id,Math.floor(p.getCurrentTime()),Math.floor(p.getDuration()||0),p.getPlaybackRate?p.getPlaybackRate():1)},500)})()"

// Phone page (once per document): the phone is a remote, so it never plays.
// - a tap on a video link (capture phase, before YouTube's own handlers) goes to remote.cast and is cancelled
//   when the link has a video id; links without one are left alone
// - landing on /watch or /shorts some other way (autoplay, SPA navigation) goes back and casts that page
// - every <video> that starts playing (feed previews included) is paused
// ponytail: tied to m.youtube.com markup and routing; re-check via chrome://inspect if videos start playing on the phone
private const val PHONE_JS =
    "(()=>{if(window.__ytr)return;window.__ytr=1;" +
        "const abs=h=>new URL(h,location.href).href;" +
        "document.addEventListener('click',e=>{const a=e.target.closest&&e.target.closest('a[href]');" +
        "if(a&&remote.cast(abs(a.getAttribute('href')))){e.preventDefault();e.stopImmediatePropagation()}},true);" +
        "document.addEventListener('play',e=>e.target.pause&&e.target.pause(),true);" +
        "let last='';setInterval(()=>{document.querySelectorAll('video').forEach(v=>v.paused||v.pause());" +
        "if(!/^\\/(watch|shorts\\/)/.test(location.pathname))last='';" +
        "else if(location.href!==last){last=location.href;if(remote.cast(last))history.back()}},300)})()"

// Controls act on the main player (watch page or miniplayer), the same video whose state yta.playing reports.
private const val JS_PLAY = "document.querySelector('#movie_player video')?.play()"
private const val JS_PAUSE = "document.querySelector('#movie_player video')?.pause()"
private const val JS_TOGGLE = "(v=>v&&(v.paused?v.play():v.pause()))(document.querySelector('#movie_player video'))"
private const val JS_RESTART = "(v=>v&&(v.currentTime=0))(document.querySelector('#movie_player video'))"
// Player's own "next": the playlist's next item, or the autoplay pick outside a playlist.
private const val JS_NEXT = "document.getElementById('movie_player')?.nextVideo?.()"

// Player quality names (YouTube's) -> labels. "medium" (360p) is the bandwidth-saving default.
internal val QUALITIES = linkedMapOf(
    // Six options: car hosts truncate lists while driving, and 144p isn't worth a slot.
    "auto" to "Automática", "small" to "240p", "medium" to "360p",
    "large" to "480p", "hd720" to "720p", "hd1080" to "1080p",
)

// Settings, shared by the car's SettingsScreen and the phone's menu. Stored values are the option keys.
internal class Choice(val key: String, val title: String, val options: Map<String, String>, val default: String)
internal class Flag(val key: String, val title: String, val default: Boolean = true)

internal val QUALITY = Choice("quality", "Qualidade do vídeo", QUALITIES, "medium")
internal val SPEED = Choice(
    "speed", "Velocidade",
    linkedMapOf("0.75" to "0,75x", "1" to "Normal", "1.25" to "1,25x", "1.5" to "1,5x", "1.75" to "1,75x", "2" to "2x"),
    "1",
)
internal val HOME_PAGE = Choice(
    "home", "Página inicial",
    linkedMapOf(
        "https://m.youtube.com" to "Início",
        "https://m.youtube.com/feed/subscriptions" to "Inscrições",
        "https://m.youtube.com/feed/history" to "Histórico",
    ),
    "https://m.youtube.com",
)
internal val TEXT_ZOOM = Choice("textZoom", "Tamanho do texto", linkedMapOf("100" to "100%", "115" to "115%", "130" to "130%", "150" to "150%"), "130")
// Keys are the CSS object-fit values applied to the video on /watch.
internal val ASPECT = Choice(
    "aspect", "Proporção do vídeo",
    linkedMapOf("contain" to "Ajustar", "cover" to "Preencher", "fill" to "Esticar"),
    "contain",
)
internal val CHOICES = listOf(QUALITY, SPEED, ASPECT, HOME_PAGE, TEXT_ZOOM)
// Flags default to on (the behavior before they were settings), except captions.
internal val FLAGS = listOf(
    Flag("skipAds", "Pular anúncios"),
    Flag("fullscreen", "Tela cheia ao abrir vídeo"),
    Flag("background", "Tocar com outro app na tela"),
    Flag("captions", "Legendas", default = false),
)

private fun prefs(ctx: Context) = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
internal fun choice(prefs: SharedPreferences, c: Choice) = prefs.getString(c.key, null)?.takeIf { it in c.options } ?: c.default
internal fun setChoice(prefs: SharedPreferences, c: Choice, v: String) {
    if (v in c.options) prefs.edit().putString(c.key, v).apply()
}
internal fun flag(prefs: SharedPreferences, key: String) =
    prefs.getBoolean(key, FLAGS.find { it.key == key }?.default ?: true)
internal fun setFlag(prefs: SharedPreferences, key: String, on: Boolean) = prefs.edit().putBoolean(key, on).apply()

// Aspect picked with the car button, over the saved default. Not saved, so it lasts until the process dies
// or the saved default changes (tapAfterChange).
internal fun aspect(prefs: SharedPreferences, tap: String?) = tap ?: choice(prefs, ASPECT)

// Next mode in ASPECT's order: Ajustar -> Preencher -> Esticar -> Ajustar.
internal fun tapAspect(prefs: SharedPreferences, tap: String?): String {
    val keys = ASPECT.options.keys.toList()
    return keys[(keys.indexOf(aspect(prefs, tap)) + 1) % keys.size]
}

// A new default, set from the car or the phone, drops the button's pick.
internal fun tapAfterChange(key: String?, tap: String?) = if (key == ASPECT.key) null else tap

// Home button label: the chosen home page's name (Início / Inscrições / Histórico).
internal fun homeLabel(prefs: SharedPreferences) = HOME_PAGE.options.getValue(choice(prefs, HOME_PAGE))

private fun choice(ctx: Context, c: Choice) = choice(prefs(ctx), c)
private fun setChoice(ctx: Context, c: Choice, v: String) = setChoice(prefs(ctx), c, v)
private fun flag(ctx: Context, key: String) = flag(prefs(ctx), key)
private fun setFlag(ctx: Context, key: String, on: Boolean) = setFlag(prefs(ctx), key, on)

class YoutubeCarAppService : CarAppService() {
    // ponytail: accepts any host, fine for a sideloaded personal app; restrict to Google's hosts if ever published
    override fun createHostValidator() = HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    override fun onCreateSession() = object : Session() {
        override fun onCreateScreen(intent: Intent): Screen = YoutubeScreen(carContext)
    }
}

// One MediaSession per process: the car screen drives it, MediaService exposes it to Android Auto's
// media UI (now-playing card, wheel keys, browse list). Main thread only, all callers run there.
private var sharedSession: MediaSession? = null

// Last page shown in the car, SPA navigations included. Process-wide so it also survives the car
// screen being recreated (e.g. a host that ends the session while the reverse camera is up).
private var pageUrl = ""

// Video picked from the car's media list while the navigation screen was closed; it opens it on start.
private var pendingUrl: String? = null

// Last watch position reported by the page (videoId to seconds); process-wide like pageUrl.
private var resumeAt: Pair<String, Int>? = null

// The live car screen, for what the phone remote controls beyond the media session; null while there's none.
private var carLink: YoutubeScreen? = null

// Next aspect for this run (car button and phone remote); returns its label. The page picks it up on its next poll.
private fun cycleAspect(ctx: Context): String {
    val p = prefs(ctx)
    aspectTap = tapAspect(p, aspectTap)
    return ASPECT.options.getValue(aspect(p, aspectTap))
}

// See aspect(); process-wide like pageUrl.
@Volatile private var aspectTap: String? = null
// Held here because SharedPreferences keeps listeners only weakly.
private val dropAspectTap = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> aspectTap = tapAfterChange(key, aspectTap) }

/** [url] with `t=` set to the saved position when it is a watch page of that same video, else [url]. */
internal fun withResume(url: String, at: Pair<String, Int>?): String {
    if (at == null || at.second < 5) return url
    val id = Regex("[?&]v=([^&#]+)").find(url)?.groupValues?.get(1)
    if (id != at.first || !url.contains("/watch?")) return url
    return url.replace(Regex("&t=[^&#]*"), "") + "&t=${at.second}s"
}

/**
 * WebView bounds (left, top, right, bottom) on a [w] x [h] surface, from the host's visible area.
 * A side the host insets by a third of the surface or less (its margin, the auto-hiding action strips) is
 * covered, so the buttons float over the page; a deeper inset is a media card beside the app, and the page
 * stops at it. No area yet: the whole surface.
 */
// ponytail: one-third threshold from DHU measures (strips inset 88 of 400/800, the card 336 of 800); retune per host if a card gets covered
internal fun webViewBounds(w: Int, h: Int, area: List<Int>?): List<Int> {
    val full = listOf(0, 0, w, h)
    val (l, t, r, b) = area ?: return full
    val bounds = listOf(
        if (l > w / 3) l.coerceAtMost(w) else 0,
        if (t > h / 3) t.coerceAtMost(h) else 0,
        if (w - r > w / 3) r.coerceAtLeast(0) else w,
        if (h - b > h / 3) b.coerceAtLeast(0) else h,
    )
    // An empty, inverted or stale rect (from before a surface resize) leaves nothing: show the whole page instead.
    return if (bounds[0] < bounds[2] && bounds[1] < bounds[3]) bounds else full
}

// Watch page the car opens for a video id (from the phone, the media list or a short).
internal fun watchUrl(videoId: String) = "https://m.youtube.com/watch?v=" + java.net.URLEncoder.encode(videoId, "UTF-8")

// Callback while no car screen is alive: the WebView (the actual player) only exists in that screen.
private val idleCallback = object : MediaSession.Callback() {
    override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
        pendingUrl = watchUrl(mediaId)
        sharedSession?.setPlaybackState(waitingForCar)
    }
}

// Session state while a video waits for the car screen to be opened; the phone bar shows its message.
// Lazy: a top-level Android object built at class load breaks the JVM unit tests (android.jar stubs throw).
private val waitingForCar by lazy {
    PlaybackState.Builder()
        .setState(PlaybackState.STATE_ERROR, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 0f)
        .setErrorMessage("Abra o YT Auto no carro para tocar")
        .build()
}

// The car player exists, so the car app is open or playing in the background. Android Auto keeps the car service
// and its screen alive after the app is closed, so a screen alone doesn't mean that.
private fun carPlaying() = carLink?.hasPlayer == true

private fun mediaSession(ctx: Context) = sharedSession
    ?: MediaSession(ctx.applicationContext, "yt-auto").also {
        it.setCallback(idleCallback)
        it.setPlaybackState(playbackState(false))
        it.isActive = true
        sharedSession = it
    }

// [positionMs] is extrapolated by readers (car media card, phone bar) while playing, so a report every 500 ms is enough.
private fun playbackState(playing: Boolean, positionMs: Long = PlaybackState.PLAYBACK_POSITION_UNKNOWN, rate: Float = 1f): PlaybackState =
    PlaybackState.Builder()
        .setActions(
            PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE or PlaybackState.ACTION_PLAY_PAUSE or
                PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID
            // No ACTION_SEEK_TO: with it Android Auto swaps previous/next for -10/+10 s on its media card (session
            // extras don't stop it). onSeekTo still runs for the phone bar, which calls seekTo directly.
        )
        .setState(if (playing) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED, positionMs, rate)
        .build()

// Browse tree: root -> Histórico / Inscrições -> videos (mediaId = videoId).
private val FEEDS = linkedMapOf(
    "history" to ("Histórico" to "https://www.youtube.com/feed/history"),
    "subscriptions" to ("Inscrições" to "https://www.youtube.com/feed/subscriptions"),
)

/** Plain text of a YouTube text object: {simpleText}, {runs:[{text}]} or {content}. */
private fun ytText(o: Any?): String? = (o as? JSONObject)?.let { t ->
    t.optString("simpleText").ifEmpty { null }
        ?: t.optJSONArray("runs")?.let { r -> (0 until r.length()).joinToString("") { r.optJSONObject(it)?.optString("text").orEmpty() } }?.ifEmpty { null }
        ?: t.optString("content").ifEmpty { null }
}

/** Collects videoId -> (title, channel) from any renderer shape in ytInitialData, in page order. */
private fun collectVideos(v: Any?, out: LinkedHashMap<String, Pair<String, String>>) {
    when (v) {
        is JSONObject -> {
            val id = v.optString("videoId").ifEmpty {
                if (v.optString("contentType") == "LOCKUP_CONTENT_TYPE_VIDEO") v.optString("contentId") else ""
            }
            val lockup = v.optJSONObject("metadata")?.optJSONObject("lockupMetadataViewModel")
            val title = ytText(v.opt("title")) ?: ytText(v.opt("headline")) ?: ytText(lockup?.opt("title"))
            if (id.isNotEmpty() && title != null && id !in out) {
                out[id] = title to (ytText(v.opt("shortBylineText")) ?: ytText(v.opt("ownerText")) ?: "")
            }
            for (k in v.keys()) collectVideos(v.opt(k), out)
        }
        is JSONArray -> for (i in 0 until v.length()) collectVideos(v.opt(i), out)
    }
}

internal data class FeedVideo(val id: String, val title: String, val channel: String)

/** Videos of a feed page's ytInitialData, in page order, first occurrence only, at most 50. */
internal fun parseFeed(html: String): List<FeedVideo> {
    val marker = "var ytInitialData = "
    val start = html.indexOf(marker).takeIf { it >= 0 } ?: return emptyList()
    val data = JSONTokener(html.substring(start + marker.length)).nextValue()
    val videos = LinkedHashMap<String, Pair<String, String>>()
    collectVideos(data, videos)
    return videos.entries.take(50).map { (id, info) -> FeedVideo(id, info.first, info.second) }
}

/**
 * Scrapes a signed-in feed page with the WebView's cookies (shared with the login screen) and
 * returns its videos. Desktop page because it embeds ytInitialData as a JSON literal.
 */
// ponytail: HTML scrape of ytInitialData, breaks if YouTube changes it; innertube /youtubei/v1/browse with
// SAPISIDHASH auth is the sturdier upgrade.
private fun fetchFeed(url: String): List<MediaBrowser.MediaItem> {
    val conn = URL(url).openConnection().apply {
        setRequestProperty("Cookie", CookieManager.getInstance().getCookie(url).orEmpty())
        setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0 Safari/537.36")
        setRequestProperty("Accept-Language", "pt-BR,pt;q=0.9")
        connectTimeout = 10_000
        readTimeout = 15_000
    }
    val html = conn.getInputStream().bufferedReader().use { it.readText() }
    return parseFeed(html).map { v ->
        MediaBrowser.MediaItem(
            MediaDescription.Builder().setMediaId(v.id).setTitle(v.title).setSubtitle(v.channel)
                .setIconUri(Uri.parse("content://$THUMBS_AUTHORITY/${v.id}")).build(),
            MediaBrowser.MediaItem.FLAG_PLAYABLE
        )
    }
}

/** Artwork size capped at 320 px on the longest side, proportion kept; smaller art is left as is. */
internal fun artSize(w: Int, h: Int): Pair<Int, Int> {
    val scale = 320.0 / maxOf(w, h)
    return if (scale >= 1) w to h else Math.round(w * scale).toInt() to Math.round(h * scale).toInt()
}

private const val THUMBS_AUTHORITY = "dev.begati.youtubeauto.thumbs"
private val VIDEO_ID = Regex("[A-Za-z0-9_-]{11}")

/** YouTube video id: exactly 11 of A-Z a-z 0-9 _ -. Guards the exported ThumbProvider against path tricks. */
internal fun isVideoId(s: String) = VIDEO_ID.matches(s)

// Video id of a watch, shorts or youtu.be link (absolute or path-only), else null.
internal fun videoIdFromUrl(url: String): String? {
    // First v= wins, as in the browser's URLSearchParams; only YouTube hosts (or a path on the current one).
    val m = Regex("""^(?:https?://(?:[\w-]+\.)?youtube\.com)?/(?:watch\?(?:[^#]*?&)??v=|shorts/)([^&#?/]+)""").find(url)
        ?: Regex("""^(?:https?://)?youtu\.be/([^?#/]+)""").find(url)
        ?: return null
    return m.groupValues[1].takeIf(::isVideoId)
}

// Car's Tocar/Pausar: only with a video to control - a video page (playing or paused) or one playing elsewhere (miniplayer).
internal fun playToggleShown(url: String, playing: Boolean) = playing || videoIdFromUrl(url) != null

// Playback time on the phone bar: m:ss, or h:mm:ss from one hour on.
internal fun clock(seconds: Long): String {
    val s = seconds.coerceAtLeast(0)
    return if (s >= 3600) "%d:%02d:%02d".format(s / 3600, s % 3600 / 60, s % 60) else "%d:%02d".format(s / 60, s % 60)
}

/**
 * Serves content://…thumbs/<videoId> as the video's i.ytimg.com thumbnail: Android Auto only loads
 * browse-list artwork from content:// URIs. Exported because the car host reads it; it only ever
 * returns public YouTube thumbnails, and the id regex blocks path tricks.
 */
// ponytail: cache dir grows unbounded (~15 KB per video); Android clears cacheDir under storage pressure
class ThumbProvider : ContentProvider() {
    override fun onCreate() = true

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        val id = uri.lastPathSegment?.takeIf(::isVideoId) ?: throw java.io.FileNotFoundException(uri.toString())
        val file = File(context!!.cacheDir, "thumbs/$id.jpg")
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            // Write to a temp name first so a failed download never leaves a half file in the cache.
            val tmp = File(file.path + ".tmp")
            URL("https://i.ytimg.com/vi/$id/mqdefault.jpg").openStream().use { input -> tmp.outputStream().use(input::copyTo) }
            tmp.renameTo(file)
        }
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override fun getType(uri: Uri) = "image/jpeg"
    override fun query(uri: Uri, p: Array<String>?, s: String?, a: Array<String>?, o: String?) = null
    override fun insert(uri: Uri, values: ContentValues?) = null
    override fun delete(uri: Uri, s: String?, a: Array<String>?) = 0
    override fun update(uri: Uri, v: ContentValues?, s: String?, a: Array<String>?) = 0
}

/** Title and subtitle of the placeholder row shown when a feed has no videos. */
internal fun feedNotice(signedIn: Boolean, failed: Boolean) = when {
    !signedIn -> "Você não está logado" to "Faça login no app do celular"
    failed -> "Não foi possível carregar" to "Verifique a conexão do celular"
    else -> "Nada por aqui" to "Essa lista está vazia no YouTube"
}

/**
 * Makes the app a media source for Android Auto: the now-playing card shows the session's
 * title/thumbnail, and the browse list offers history and subscriptions. Playback itself lives in
 * the navigation screen's WebView, so picking a video there needs that screen open.
 */
class MediaService : MediaBrowserService() {
    override fun onCreate() {
        super.onCreate()
        sessionToken = mediaSession(this).sessionToken
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?) = BrowserRoot("root", null)

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowser.MediaItem>>) {
        if (parentId == "root") {
            result.sendResult(FEEDS.map { (id, feed) ->
                MediaBrowser.MediaItem(
                    MediaDescription.Builder().setMediaId(id).setTitle(feed.first).build(),
                    MediaBrowser.MediaItem.FLAG_BROWSABLE
                )
            }.toMutableList())
            return
        }
        val url = FEEDS[parentId]?.second ?: return result.sendResult(mutableListOf())
        result.detach()
        thread {
            // SAPISID is the cookie Google sets on sign-in.
            val signedIn = "SAPISID=" in CookieManager.getInstance().getCookie(url).orEmpty()
            val fetched = runCatching { fetchFeed(url) }
            val items = fetched.getOrDefault(emptyList()).ifEmpty {
                val (title, subtitle) = feedNotice(signedIn, fetched.isFailure)
                listOf(
                    MediaBrowser.MediaItem(
                        MediaDescription.Builder().setMediaId("empty").setTitle(title).setSubtitle(subtitle).build(),
                        0
                    )
                )
            }
            mainExecutor.execute { result.sendResult(items.toMutableList()) }
        }
    }
}

/**
 * Renders a WebView onto the car's navigation Surface through a VirtualDisplay + Presentation.
 * The WebView outlives the surface (the display just loses its surface), so audio keeps going
 * while another car app is on screen; everything is torn down only when the screen is destroyed.
 */
class YoutubeScreen(ctx: CarContext) : Screen(ctx), SurfaceCallback {
    private var display: VirtualDisplay? = null
    private var displaySize: List<Int>? = null
    private var presentation: Presentation? = null
    private var webView: WebView? = null
    private var root: FrameLayout? = null
    // Host's visible area as left, top, right, bottom (see webViewBounds).
    private var area: List<Int>? = null
    // Uptime of the last template refresh asked to confirm a media card (see onVisibleAreaChanged).
    private var confirmAt = 0L
    // The WebView's client: loading screen and retries while offline (see OfflineRetry).
    private var client: OfflineRetry? = null
    // Set while a video is fullscreen; calling it exits fullscreen.
    private var exitFullscreen: WebChromeClient.CustomViewCallback? = null
    private var playing = false
    // Whether the last template built had Tocar/Pausar: a page change that flips it rebuilds the template.
    private var toggleShown = false
    private var positionMs = PlaybackState.PLAYBACK_POSITION_UNKNOWN
    private var durationMs = 0L
    private var rate = 1f
    // elapsedRealtime of the PlaybackState last published (see setTime).
    private var published = 0L
    // Metadata last published, to add the duration once the page reports it.
    private var meta: MediaMetadata? = null
    // Title, channel and artwork URL of the metadata last published, to skip duplicate reports and stale downloads.
    private var metaKey = ""

    // Steering-wheel / car media-card controls land here while this screen is alive.
    private val session = mediaSession(ctx).apply {
        setCallback(object : MediaSession.Callback() {
            override fun onPlay() { js(JS_PLAY) }
            override fun onPause() { js(JS_PAUSE) }
            override fun onSkipToNext() { js(JS_NEXT) }
            override fun onSkipToPrevious() { js(JS_RESTART) }
            override fun onSeekTo(pos: Long) { js("(v=>v&&(v.currentTime=${pos / 1000.0}))(document.querySelector('#movie_player video'))") }
            override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
                pageUrl = watchUrl(mediaId) // no player yet: it loads pageUrl when the car shows the screen
                val wv = webView
                if (wv != null) wv.loadUrl(pageUrl) else sharedSession?.setPlaybackState(waitingForCar)
            }
        })
    }

    init {
        carLink = this
        ctx.getCarService(AppManager::class.java).setSurfaceCallback(this)
        pendingUrl?.let { pageUrl = it }
        pendingUrl = null
        if (pageUrl.isEmpty()) pageUrl = choice(ctx, HOME_PAGE)
        prefs(ctx).registerOnSharedPreferenceChangeListener(dropAspectTap) // same listener: registering again is a no-op
        setPlaying(false)
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                teardown()
                if (carLink === this@YoutubeScreen) carLink = null
                // Session is process-wide (MediaService holds its token): detach, don't release.
                session.setCallback(idleCallback)
                session.setPlaybackState(playbackState(false))
            }
        })
    }

    val hasPlayer get() = webView != null

    /** Re-applies the saved quality and speed to the current video (after the car settings or the phone change them). */
    fun applyPlayback() {
        js(JS_APPLY_QUALITY)
        js(JS_APPLY_PLAYBACK)
    }

    private fun js(code: String) {
        webView?.evaluateJavascript(code, null)
    }

    private fun setPlaying(p: Boolean) {
        playing = p
        published = SystemClock.elapsedRealtime()
        session.setPlaybackState(playbackState(p, positionMs, rate))
        invalidate()
    }

    // Position and duration of the URL's video, from the page every 500 ms. No invalidate(): the template doesn't show them.
    private fun setTime(id: String, seconds: Int, duration: Int, playbackRate: Float) {
        resumeAt = id to seconds
        // Readers extrapolate from the last published state, so republish only when that drifts (seek, rate change,
        // stall), not every 500 ms.
        val now = SystemClock.elapsedRealtime()
        val expected = positionMs + if (playing) ((now - published) * rate).toLong() else 0L
        positionMs = seconds * 1000L
        if (playbackRate != rate || kotlin.math.abs(positionMs - expected) > 1500) {
            rate = playbackRate
            published = now
            session.setPlaybackState(playbackState(playing, positionMs, rate))
        } else {
            positionMs = expected // keep the published baseline
        }
        val ms = duration * 1000L
        if (ms > 0 && ms != durationMs) {
            durationMs = ms
            meta?.let { session.setMetadata(MediaMetadata.Builder(it).putLong(MediaMetadata.METADATA_KEY_DURATION, ms).build()) }
        }
    }

    private fun setMeta(title: String, artist: String, art: String) {
        val key = "$title|$artist|$art"
        if (key == metaKey) return
        metaKey = key
        durationMs = 0L // the next time report brings the new video's duration
        fun publish(bmp: Bitmap?) {
            if (metaKey != key || !lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) return
            meta = MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, title)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
                .apply { bmp?.let { putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, it) } }
                .apply { if (durationMs > 0) putLong(MediaMetadata.METADATA_KEY_DURATION, durationMs) }
                .build()
            session.setMetadata(meta)
        }
        publish(null)
        if (!art.startsWith("https://")) return
        thread {
            val bmp = runCatching {
                val full = URL(art).openStream().use(BitmapFactory::decodeStream)
                // Metadata crosses Binder (~1 MB limit), so cap the artwork size.
                val (w, h) = artSize(full.width, full.height)
                if (w != full.width || h != full.height) Bitmap.createScaledBitmap(full, w, h, true) else full
            }.getOrNull()
            carContext.mainExecutor.execute { publish(bmp) }
        }
    }

    override fun onGetTemplate() = NavigationTemplate.Builder()
        .setActionStrip(
            ActionStrip.Builder()
                .addAction(Action.Builder().setTitle("Voltar").setOnClickListener { exitFullscreen?.onCustomViewHidden() ?: webView?.goBack() }.build())
                // Named after the chosen home page (Início / Inscrições / Histórico).
                .addAction(Action.Builder().setTitle(homeLabel(prefs(carContext))).setOnClickListener {
                    pageUrl = choice(carContext, HOME_PAGE)
                    webView?.loadUrl(pageUrl)
                }.build())
                .apply {
                    toggleShown = playToggleShown(pageUrl, playing)
                    if (toggleShown)
                        addAction(Action.Builder().setTitle(if (playing) "Pausar" else "Tocar").setOnClickListener { js(JS_TOGGLE) }.build())
                }
                .addAction(Action.Builder().setTitle("Pesquisar").setOnClickListener {
                    screenManager.push(SearchScreen(carContext) { query ->
                        pageUrl = "https://m.youtube.com/results?search_query=" + Uri.encode(query)
                        webView?.loadUrl(pageUrl)
                    })
                }.build())
                .build()
        )
        .setMapActionStrip(
            ActionStrip.Builder()
                // Video aspect: Ajustar -> Preencher -> Esticar, for this run only (the default is a setting).
                .addAction(
                    Action.Builder()
                        .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.ic_aspect_ratio)).build())
                        .setOnClickListener { CarToast.makeText(carContext, cycleAspect(carContext), CarToast.LENGTH_SHORT).show() }
                        .build()
                )
                .addAction(
                    Action.Builder()
                        .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.ic_settings)).build())
                        .setOnClickListener {
                            screenManager.push(SettingsScreen(carContext) {
                                // Flags are read live by the page; zoom, quality, speed and captions need a push.
                                webView?.settings?.textZoom = choice(carContext, TEXT_ZOOM).toInt()
                                applyPlayback()
                                invalidate() // home button label
                            })
                        }
                        .build()
                )
                .build()
        )
        .build()

    override fun onSurfaceAvailable(container: SurfaceContainer) {
        val surface = container.surface ?: return
        val size = listOf(container.width, container.height, container.dpi)
        // Same size: reattach to the live display, page and playback untouched.
        // Different size: Presentation dismisses itself on metric changes, so rebuild (reloads pageUrl).
        if (display != null && size == displaySize) {
            display?.surface = surface
            return
        }
        teardown()
        val dm = carContext.getSystemService(DisplayManager::class.java)
        val vd = dm.createVirtualDisplay("youtube", container.width, container.height, container.dpi, surface, 0)
        display = vd
        displaySize = size
        // Explicit dark theme without an action bar (AppTheme has one, a black band over the page): the WebView asks
        // YouTube for dark mode whatever the phone or the car day/night mode is.
        presentation = Presentation(carContext, vd.display, android.R.style.Theme_DeviceDefault_NoActionBar).apply {
            val frame = FrameLayout(context)
            val offline = offlineView(context)
            val wv = WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                // Bigger text and tap targets for a touchscreen at arm's length.
                settings.textZoom = choice(carContext, TEXT_ZOOM).toInt()
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun playing(p: Boolean) = carContext.mainExecutor.execute { setPlaying(p) }

                    @JavascriptInterface
                    fun quality() = choice(carContext, QUALITY)

                    @JavascriptInterface
                    fun speed() = choice(carContext, SPEED)

                    @JavascriptInterface
                    fun saveQuality(q: String) = setChoice(carContext, QUALITY, q)

                    @JavascriptInterface
                    fun saveSpeed(r: String) = setChoice(carContext, SPEED, r)

                    @JavascriptInterface
                    fun time(id: String, t: Int, dur: Int, rate: Float) = carContext.mainExecutor.execute { setTime(id, t, dur, rate) }

                    @JavascriptInterface
                    fun flag(key: String) = flag(carContext, key)

                    @JavascriptInterface
                    fun aspect() = aspect(prefs(carContext), aspectTap)

                    @JavascriptInterface
                    fun meta(title: String, artist: String, art: String) =
                        carContext.mainExecutor.execute { setMeta(title, artist, art) }
                }, "yta")
                webViewClient = object : OfflineRetry(carContext, offline) {
                    override fun onLoaded(view: WebView) {
                        view.evaluateJavascript(PAGE_JS, null)
                    }

                    override fun doUpdateVisitedHistory(view: WebView, url: String?, isReload: Boolean) {
                        url ?: return
                        pageUrl = url
                        if (playToggleShown(url, playing) != toggleShown) invalidate()
                    }
                }
                client = webViewClient as OfflineRetry // the object above
                webChromeClient = object : WebChromeClient() {
                    private var custom: View? = null

                    override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                        custom = view
                        exitFullscreen = callback
                        frame.addView(view)
                        fitToArea() // the native fullscreen video also stops at a media card
                    }

                    override fun onHideCustomView() {
                        custom?.let(frame::removeView)
                        custom = null
                        exitFullscreen = null
                    }
                }
                loadUrl(withResume(pageUrl, resumeAt))
            }
            webView = wv
            root = frame
            frame.addView(wv)
            frame.addView(offline)
            fitToArea()
            setContentView(frame)
            show()
        }
    }

    // Keep the display, WebView and playback alive; only drop the surface we can no longer draw on.
    override fun onSurfaceDestroyed(container: SurfaceContainer) {
        display?.surface = null
        if (!flag(carContext, "background")) js(JS_PAUSE)
    }

    private fun teardown() {
        client?.release()
        client = null
        webView?.destroy()
        presentation?.dismiss()
        display?.release()
        webView = null
        root = null
        exitFullscreen = null
        presentation = null
        display = null
        displaySize = null
    }

    // Visible area: it shrinks while the action strips show and when a media card sits beside the app.
    // Going back full screen from the split view, the host sends the full area and then repeats the split one,
    // correcting it only when the strips next hide (~10 s). So an area with a card is confirmed once by a template
    // refresh, which makes the host resend the real area; the 2 s window keeps a real card from looping.
    // ponytail: host quirk seen on the DHU; drop the refresh if the host starts sending the right area on its own
    override fun onVisibleAreaChanged(visibleArea: android.graphics.Rect) {
        area = visibleArea.run { listOf(left, top, right, bottom) }
        val (w, h) = displaySize ?: return
        fitToArea()
        val now = SystemClock.uptimeMillis()
        if (webViewBounds(w, h, area) != listOf(0, 0, w, h) && now - confirmAt > 2000) {
            confirmAt = now
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) invalidate()
            }, 500)
        }
    }

    // Sizes every child of the frame: the WebView and, while shown, the native fullscreen video.
    private fun fitToArea() {
        val frame = root ?: return
        val (w, h) = displaySize ?: return
        val (l, t, r, b) = webViewBounds(w, h, area)
        for (i in 0 until frame.childCount) {
            frame.getChildAt(i).layoutParams = FrameLayout.LayoutParams(r - l, b - t).apply {
                leftMargin = l
                topMargin = t
            }
        }
    }

    override fun onScroll(distanceX: Float, distanceY: Float) {
        webView?.scrollBy(distanceX.toInt(), distanceY.toInt())
    }

    // Called only on hosts with Car API level >= 5.
    override fun onClick(x: Float, y: Float) {
        val target = root ?: return
        val t = SystemClock.uptimeMillis()
        for (action in intArrayOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP)) {
            val e = MotionEvent.obtain(t, t, action, x, y, 0)
            target.dispatchTouchEvent(e)
            e.recycle()
        }
    }
}

// No network (DNS, connection, timeout, I/O; UNKNOWN covers ERR_INTERNET_DISCONNECTED on some WebView builds).
// Anything else (an intent:// or app link, a bad URL, SSL) is not cured by retrying, so it keeps the WebView's page.
internal fun isNetworkError(code: Int) = code in setOf(
    WebViewClient.ERROR_HOST_LOOKUP, WebViewClient.ERROR_CONNECT, WebViewClient.ERROR_TIMEOUT,
    WebViewClient.ERROR_IO, WebViewClient.ERROR_UNKNOWN,
)

/**
 * WebViewClient that covers the page with [overlay] while its main frame can't load (no network), and reloads it
 * every 5 s and as soon as a network with internet comes up, until it loads. Call [release] when the WebView goes.
 * Shared by the car screen and the phone's login screen.
 */
internal open class OfflineRetry(ctx: Context, private val overlay: View) : WebViewClient() {
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var web: WebView? = null
    // Set when the main frame fails, cleared when a new load starts, so the error page's onPageFinished keeps the overlay.
    private var failed = false
    // After release the WebView is gone or going: nothing may reload it any more.
    private var released = false
    private val retry = Runnable { if (!released) web?.reload() } // reload, not loadUrl: no error entries in the back history
    private val connectivity = ctx.getSystemService(android.net.ConnectivityManager::class.java)
    private val network = object : android.net.ConnectivityManager.NetworkCallback() {
        override fun onAvailable(n: android.net.Network) {
            handler.post {
                // Only while the current load is the failed one: a new navigation (Início, search) must not be replaced.
                if (released || !failed) return@post
                handler.removeCallbacks(retry)
                retry.run()
            }
        }
    }

    init {
        connectivity.registerNetworkCallback(
            android.net.NetworkRequest.Builder()
                .addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build(),
            network,
        )
    }

    fun release() {
        released = true
        web = null
        handler.removeCallbacks(retry)
        connectivity.unregisterNetworkCallback(network)
    }

    /** The page loaded without a network error. */
    open fun onLoaded(view: WebView) {}

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        if (released) return
        web = view
        failed = false
        handler.removeCallbacks(retry) // a new load (or the retry itself) is under way
    }

    // HTTP errors come through onReceivedHttpError; this one is every other load error, network or not.
    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
        if (released || !request.isForMainFrame || !isNetworkError(error.errorCode)) return
        web = view
        failed = true
        overlay.visibility = View.VISIBLE
        handler.removeCallbacks(retry)
        handler.postDelayed(retry, 5000)
    }

    override fun onPageFinished(view: WebView, url: String?) {
        if (failed) return // WebView's own error page under the overlay
        overlay.visibility = View.GONE
        handler.removeCallbacks(retry)
        onLoaded(view)
    }
}

/**
 * Phone remote bar under the page: the car's video (art, title, channel), play/pause, previous, next and a seek bar,
 * all through [car], the shared media session. Hidden until the session has a video or a pending one.
 */
private class RemoteBar(ctx: Context, private val car: MediaController) : android.widget.LinearLayout(ctx) {
    private fun dp(n: Int) = (n * resources.displayMetrics.density).toInt()
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val art = android.widget.ImageView(ctx).apply { scaleType = android.widget.ImageView.ScaleType.CENTER_CROP }
    private val title = text(16f)
    private val channel = text(13f).apply { alpha = 0.7f }
    private val playPause = button(android.R.drawable.ic_media_play) {
        if (car.playbackState?.state == PlaybackState.STATE_PLAYING) car.transportControls.pause() else car.transportControls.play()
    }
    private val position = text(12f)
    private val duration = text(12f)
    private val controls = android.widget.LinearLayout(ctx).apply {
        addView(button(android.R.drawable.ic_media_previous) { car.transportControls.skipToPrevious() })
        addView(playPause)
        addView(button(android.R.drawable.ic_media_next) { car.transportControls.skipToNext() })
        addView(button(R.drawable.ic_settings) { showMenu(this) })
    }
    private var dragging = false
    private val seek = android.widget.SeekBar(ctx).apply {
        setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(b: android.widget.SeekBar, p: Int, fromUser: Boolean) {
                if (fromUser) position.text = clock(p.toLong())
            }
            override fun onStartTrackingTouch(b: android.widget.SeekBar) { dragging = true }
            override fun onStopTrackingTouch(b: android.widget.SeekBar) {
                dragging = false
                car.transportControls.seekTo(b.progress * 1000L)
            }
        })
    }
    private val timeRow = android.widget.LinearLayout(ctx).apply {
        gravity = android.view.Gravity.CENTER_VERTICAL
        addView(position)
        addView(seek, LayoutParams(0, -2, 1f))
        addView(duration)
    }
    private val callback = object : MediaController.Callback() {
        override fun onMetadataChanged(m: MediaMetadata?) = update()
        override fun onPlaybackStateChanged(s: PlaybackState?) = update()
    }
    private var carOpen = carPlaying()
    private val tick = object : Runnable {
        override fun run() {
            if (carPlaying() != carOpen) update() // the car player came or went: no session event for that
            updateTime()
            handler.postDelayed(this, 1000)
        }
    }

    init {
        orientation = VERTICAL
        setBackgroundColor(0xFF1F1F1F.toInt())
        setPadding(dp(12), dp(8), dp(4), dp(4))
        addView(android.widget.LinearLayout(ctx).apply {
            gravity = android.view.Gravity.CENTER_VERTICAL
            addView(art, LayoutParams(dp(64), dp(36)))
            addView(android.widget.LinearLayout(ctx).apply {
                orientation = VERTICAL
                setPadding(dp(10), 0, dp(4), 0)
                addView(title)
                addView(channel)
            }, LayoutParams(0, -2, 1f))
            addView(controls)
        })
        addView(timeRow)
        car.registerCallback(callback, handler)
        update()
        handler.post(tick)
    }

    fun release() {
        handler.removeCallbacks(tick)
        car.unregisterCallback(callback)
    }

    // Proporção (this run only, like the car button), Qualidade and Velocidade (saved, then pushed to the car video).
    private fun showMenu(anchor: View) {
        val ctx = context
        android.widget.PopupMenu(ctx, anchor).apply {
            menu.add("Proporção: " + ASPECT.options.getValue(aspect(prefs(ctx), aspectTap))).setOnMenuItemClickListener {
                android.widget.Toast.makeText(ctx, "Proporção: " + cycleAspect(ctx), android.widget.Toast.LENGTH_SHORT).show()
                true
            }
            for (c in listOf(QUALITY, SPEED)) {
                val sub = menu.addSubMenu(c.title + ": " + c.options.getValue(choice(prefs(ctx), c)))
                c.options.forEach { (key, label) ->
                    sub.add(label).setOnMenuItemClickListener {
                        setChoice(prefs(ctx), c, key)
                        carLink?.applyPlayback()
                        true
                    }
                }
            }
            show()
        }
    }

    private fun text(size: Float) = android.widget.TextView(context).apply {
        textSize = size
        setTextColor(android.graphics.Color.WHITE)
        maxLines = 1
        ellipsize = android.text.TextUtils.TruncateAt.END
    }

    private fun button(icon: Int, onClick: () -> Unit) = android.widget.ImageButton(context).apply {
        setImageResource(icon)
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
        imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
        setOnClickListener { onClick() }
    }

    private fun update() {
        val m = car.metadata
        val s = car.playbackState
        // A video sent while no car screen is alive leaves only the session's error message ("Abra o YT Auto...").
        val pending = s?.state == PlaybackState.STATE_ERROR
        carOpen = carPlaying()
        // Nothing to control and no car screen: say where videos play, since the phone never plays them.
        val notice = !pending && m == null && !carOpen
        visibility = if (m != null || pending || notice) VISIBLE else GONE
        title.text = when {
            pending -> s?.errorMessage
            notice -> "Os vídeos tocam só no carro. Abra o YT Auto no Android Auto."
            else -> m?.getString(MediaMetadata.METADATA_KEY_TITLE)
        }
        title.maxLines = if (notice || pending) 2 else 1
        channel.text = if (pending || notice) "" else m?.getString(MediaMetadata.METADATA_KEY_ARTIST)
        art.setImageBitmap(if (pending || notice) null else m?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART))
        // Only a message: nothing to play, pause or seek yet.
        val message = if (pending || notice) GONE else VISIBLE
        art.visibility = message
        controls.visibility = message
        timeRow.visibility = message
        playPause.setImageResource(
            if (s?.state == PlaybackState.STATE_PLAYING) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        )
        val d = (m?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L) / 1000
        seek.max = d.toInt()
        // No duration yet (new video, ad, live): a touch would seek the car to 0.
        seek.isEnabled = d > 0 && carOpen
        // Metadata from earlier but no car player now: nothing would answer the buttons.
        controls.alpha = if (carOpen) 1f else 0.4f
        for (i in 0 until controls.childCount) controls.getChildAt(i).isEnabled = carOpen
        if (!carOpen && !pending && m != null) channel.text = "Abra o YT Auto no carro para controlar"
        duration.text = clock(d)
        updateTime()
    }

    // Position extrapolated from the last report, so the bar moves every second between the car's 500 ms reports.
    private fun updateTime() {
        val s = car.playbackState ?: return
        if (dragging || s.position < 0) return
        val elapsed = if (s.state == PlaybackState.STATE_PLAYING) android.os.SystemClock.elapsedRealtime() - s.lastPositionUpdateTime else 0
        val max = seek.max.toLong()
        // Clamped: the state stays PLAYING through ads, while time reports stop.
        val seconds = ((s.position + elapsed * s.playbackSpeed).toLong() / 1000).let { if (max > 0) it.coerceAtMost(max) else it }
        seek.progress = seconds.toInt()
        position.text = clock(seconds)
    }
}

/** Black loading screen with a spinner and "Sem conexão. Tentando de novo…", hidden until a page fails. */
private fun offlineView(ctx: Context): View = FrameLayout(ctx).apply {
    setBackgroundColor(android.graphics.Color.BLACK)
    visibility = View.GONE
    isClickable = true // taps don't reach the error page underneath
    addView(
        android.widget.LinearLayout(ctx).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            addView(android.widget.ProgressBar(ctx, null, android.R.attr.progressBarStyleLarge).apply {
                isIndeterminate = true
                indeterminateTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
            })
            addView(android.widget.TextView(ctx).apply {
                text = "Sem conexão. Tentando de novo…"
                setTextColor(android.graphics.Color.WHITE)
                textSize = 20f
                setPadding(0, 24, 0, 0)
            })
        },
        FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, android.view.Gravity.CENTER),
    )
}

/** Car settings, same values as the phone's "Configurações" menu. [onChanged] re-applies them to the live page. */
class SettingsScreen(ctx: CarContext, private val onChanged: () -> Unit) : Screen(ctx) {
    override fun onGetTemplate(): ListTemplate {
        val list = ItemList.Builder()
        CHOICES.forEach { c ->
            list.addItem(
                Row.Builder().setTitle(c.title).addText(c.options.getValue(choice(carContext, c))).setBrowsable(true)
                    .setOnClickListener {
                        screenManager.push(ChoiceScreen(carContext, c) {
                            invalidate()
                            onChanged()
                        })
                    }
                    .build()
            )
        }
        FLAGS.forEach { f ->
            list.addItem(
                Row.Builder().setTitle(f.title)
                    .setToggle(Toggle.Builder { on ->
                        setFlag(carContext, f.key, on)
                        onChanged()
                    }.setChecked(flag(carContext, f.key)).build())
                    .build()
            )
        }
        return ListTemplate.Builder()
            .setTitle("Configurações")
            .setHeaderAction(Action.BACK)
            .setSingleList(list.build())
            .build()
    }
}

/** Single-choice picker for one [Choice] setting. */
private class ChoiceScreen(ctx: CarContext, private val c: Choice, private val onPicked: () -> Unit) : Screen(ctx) {
    override fun onGetTemplate(): ListTemplate {
        val keys = c.options.keys.toList()
        val list = ItemList.Builder()
        c.options.values.forEach { list.addItem(Row.Builder().setTitle(it).build()) }
        list.setSelectedIndex(keys.indexOf(choice(carContext, c)))
        list.setOnSelectedListener { i ->
            setChoice(carContext, c, keys[i])
            onPicked()
            screenManager.pop()
        }
        return ListTemplate.Builder()
            .setTitle(c.title)
            .setHeaderAction(Action.BACK)
            .setSingleList(list.build())
            .build()
    }
}

/** Car keyboard: the virtual display has no IME, so text input goes through the host's SearchTemplate. */
class SearchScreen(ctx: CarContext, private val onQuery: (String) -> Unit) : Screen(ctx) {
    override fun onGetTemplate() = SearchTemplate.Builder(object : SearchTemplate.SearchCallback {
        override fun onSearchSubmitted(searchText: String) {
            onQuery(searchText)
            screenManager.pop()
        }
    })
        .setHeaderAction(Action.BACK)
        .setShowKeyboardByDefault(true)
        .setItemList(ItemList.Builder().build())
        .build()
}

/**
 * Phone launcher screen to sign in to YouTube. WebView cookies are app-wide, so the car WebView
 * picks up the session. Google rejects embedded WebViews by the "; wv" UA token, hence the strip.
 */
// ponytail: UA trick can break if Google tightens WebView detection; fallback is a cookie export from a real browser
class LoginActivity : Activity() {
    private var client: OfflineRetry? = null
    private var web: WebView? = null
    private var bar: RemoteBar? = null
    // Phone -> car: the same session the steering wheel uses; it opens the video in the car, or keeps it for later.
    private val car by lazy { MediaController(this, mediaSession(this).sessionToken) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val offline = offlineView(this)
        val retrying = object : OfflineRetry(this, offline) {
            override fun onLoaded(view: WebView) = view.evaluateJavascript(PHONE_JS, null)
        }.also { client = it }
        bar = RemoteBar(this, car)
        val page = FrameLayout(this).apply {
            addView(WebView(context).also { web = it }.apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.userAgentString = settings.userAgentString.replace("; wv", "")
                settings.mediaPlaybackRequiresUserGesture = true // no autoplay on the phone; PHONE_JS pauses the rest
                addJavascriptInterface(object {
                    /** Sends [url]'s video to the car; false when it has no video id (the page then follows the link). */
                    @JavascriptInterface
                    fun cast(url: String): Boolean {
                        val id = videoIdFromUrl(url) ?: return false
                        runOnUiThread {
                            if (!carPlaying()) {
                                android.widget.Toast.makeText(
                                    this@LoginActivity, "Vídeo guardado: ele toca quando você abrir o YT Auto no carro",
                                    android.widget.Toast.LENGTH_LONG,
                                ).show()
                            }
                            car.transportControls.playFromMediaId(id, null)
                        }
                        return true
                    }
                }, "remote")
                webViewClient = retrying
                loadUrl("https://accounts.google.com/ServiceLogin?service=youtube&continue=https://m.youtube.com")
            })
            addView(offline)
        }
        setContentView(android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            addView(page, android.widget.LinearLayout.LayoutParams(-1, 0, 1f))
            addView(bar)
        })
    }

    override fun onDestroy() {
        bar?.release()
        client?.release()
        web?.destroy() // e.g. on rotation: the old page must not keep loading in the background
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }

    // Settings live here, on the phone, so they're not fiddled with while driving.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // The gear is white; tint it to the action bar's icon color so it shows in light mode too.
        val tint = obtainStyledAttributes(intArrayOf(android.R.attr.colorControlNormal)).run {
            getColorStateList(0).also { recycle() }
        }
        menu.add("Configurações")
            .setIcon(getDrawable(R.drawable.ic_settings)!!.mutate().apply { setTintList(tint) })
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, SettingsActivity::class.java))
        return true
    }
}

/**
 * Phone settings screen, same values as the car's SettingsScreen. Framework preferences (deprecated
 * but built in) keep it native-looking without pulling in AppCompat/androidx.preference.
 */
@Suppress("DEPRECATION")
class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Configurações"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(android.R.id.content, Prefs()).commit()
        }
    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }

    class Prefs : android.preference.PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            preferenceManager.sharedPreferencesName = "settings" // same file prefs() uses
            val ctx = activity
            val screen = preferenceManager.createPreferenceScreen(ctx)
            preferenceScreen = screen
            fun category(title: String) = android.preference.PreferenceCategory(ctx).also {
                it.title = title
                screen.addPreference(it)
            }
            fun list(c: Choice) = android.preference.ListPreference(ctx).apply {
                key = c.key
                title = c.title
                dialogTitle = c.title
                entries = c.options.values.toTypedArray()
                entryValues = c.options.keys.toTypedArray()
                setDefaultValue(c.default)
                value = choice(ctx, c) // also drops stale values to the default
                summary = "%s" // shows the chosen option
            }
            fun switch(f: Flag) = android.preference.SwitchPreference(ctx).apply {
                key = f.key
                title = f.title
                setDefaultValue(f.default)
            }
            category("Vídeo").apply {
                addPreference(list(QUALITY))
                addPreference(list(SPEED))
                addPreference(list(ASPECT))
                FLAGS.forEach { addPreference(switch(it)) }
            }
            category("Navegação").apply {
                addPreference(list(HOME_PAGE))
                addPreference(list(TEXT_ZOOM))
            }
        }
    }
}
