# Phone Remote verification

**Verdict**: PASS (round 2: findings closed by dbdad90; round 1 was FAIL)
**Proofs**: `proofs.md` at e93cac0 (HEAD 43cac9e adds docs only; `git diff e93cac0..HEAD -- app/` is empty)
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | walk - tap on a Home video on the phone, DHU on `/watch?v=<id>`, phone stays on `/` | yes - reads both pages' location and the phone's `<video>` state |
| C2 | walk - car app closed, tap on the phone, bar notice, open the app on the DHU | yes - the kept video was seen playing after opening the app |
| C3 | walk - DevTools on the phone, five scrolls of Home, playing count stays 0 | yes |
| C4 | walk - `location.href` to a `/watch` on the phone | yes - full load, but the detection is the same location poll an SPA push would hit |
| C21 | walk - /shorts no carro vira /watch e toca (Walked) | yes - round 2, author walk: `/watch?v=cUQ1MDJDc8s`, `paused` false, sem diálogo aberto |
| C5 | `RemoteTest.kt` `CAST-01 video id from watch shorts and youtu-be links` | yes - drops of the `&v=` branch, the youtu.be branch or `isVideoId` each fail it |
| C6 | `RemoteTest.kt` `CAST-05 a short opens as a watch url` | yes - round 2: testa o caminho real, watchUrl(videoIdFromUrl(short)) |
| C7 | walk - bar with art, title, channel matching the DHU | yes |
| C8 | walk - next on the DHU card, new title in 1.4 s | yes |
| C9 | walk - six play/pause taps, car `paused` alternates, icon follows | yes |
| C10 | walk - previous, car `currentTime` 541 to 0 | yes |
| C11 | walk - next, car video id changes | yes |
| C12 | `RemoteTest.kt` `REMOTE-06 clock formats m-ss and h-mm-ss` | yes - 3599/3600 boundary and negative covered |
| C13 | walk - bar time 0:08, 0:56, 1:12 and duration equal to the car's | yes |
| C14 | walk - drag to half, car `currentTime` 23 to 541 of 1044 | yes |
| C15 | walk - Proporção, car `objectFit` contain to cover, saved `aspect` unchanged | yes |
| C16 | walk - 720p, `getPlaybackQuality()` hd720 and saved `quality` hd720 | yes |
| C17 | walk - 1.5x, `playbackRate` 1.5 and saved `speed` 1.5 | yes |
| C18 | walk - force-stop, app open on the DHU Home, phone open, no bar | yes |
| C19 | walk - DHU on the app grid, force-stop, phone open, bar notice | yes |
| C20 | walk - same as C19, tap on a video, toast text | yes |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| C6 proves dead code: `castUrl` is called only by the test, so AC 5 at the real layer has no test and a change to `watchUrl` or `onPlayFromMediaId` would stay green | `YoutubeCarAppService.kt:447` defined, no caller in `app/src/main`; real path `:1212` `playFromMediaId(id)` then `:313`/`:563` `watchUrl(mediaId)` | delete `castUrl`; make `watchUrl` internal and pure (the id is already `isVideoId`, so no `Uri.encode` needed) and point C6 at `watchUrl(videoIdFromUrl(short)!!)` | dbdad90 - C6 (CAST-05 agora testa videoIdFromUrl + watchUrl; castUrl removido) |
| A touch on the seek bar while the duration is 0 restarts the car video: `seek.max` is 0, progress is 0, release sends `seekTo(0)` | `:1067` `seek.max = d.toInt()` with d 0 right after every `setMeta` (durationMs reset) and during ads (no `yta.time` while the ad's id plays); `:956` seeks unconditionally | disable the SeekBar (`seek.isEnabled = d > 0`) or skip `seekTo` when `b.max == 0` | dbdad90 - código: seek.isEnabled só com duração > 0 e player do carro; não percorrido (anúncio) |
| Unmute counter is keyed on the URL id, so a preroll ad eats the 5 s window and the main video can stay muted if the player re-mutes it after the ad; it also unmutes every new autoplayed video for 5 s, undoing a mute the user set on the car | `:145` `if(id!==umId){umId=id;umN=0}` counts while `getPlayerState()===1`, ad included; no check of `getVideoData().video_id===id` | count only when `p.getVideoData().video_id===id` (as `yta.time` does), and only unmute videos opened by `onPlayFromMediaId` (e.g. a flag set there), not every new id | dbdad90 - código: contador de unmute só com getVideoData().video_id igual ao da URL; desligar o mudo de vídeos novos segue proposital (critério 1); não percorrido com anúncio |
| Bar time runs past the duration and keeps running during ads: extrapolation is unbounded while the state stays PLAYING and no time report arrives | `:1077` `s.position + elapsed * s.playbackSpeed` with no clamp; `yta.time` stops during ads but the ad's `play` keeps STATE_PLAYING | clamp `seconds` to the metadata duration when it is > 0 | dbdad90 - código: tempo extrapolado limitado à duração |
| Re-entering the same `/watch` URL on the phone is not cast and not undone: `last` keeps the old href after `history.back()`, so a second SPA push (or forward) to it leaves the phone on that watch page | `:160-161` `location.href!==last`, `last` never cleared | reset `last=''` when the path is not watch/shorts | dbdad90 - walk: o mesmo /shorts enviado duas vezes pelo celular volta para / nas duas |
| Controls stay active with no car player: with metadata from an earlier session and `carPlaying()` false, play/pause/seek/next go to a callback with `webView` null (or `idleCallback`, which has no onPlay) and do nothing, with no message | `:1061` controls shown whenever `m != null` and not pending; `js()` no-op on null `webView` | when `!carOpen` show the last video with the controls disabled (or the AC 17 notice line under the title) | dbdad90 - código: controles com alpha 0.4 e desabilitados sem player, canal "Abra o YouTube Auto no carro para controlar"; não percorrido |
| `videoIdFromUrl` disagrees with the browser on repeated `v=` (takes the last, URLSearchParams takes the first), accepts any host (`https://example.com/watch?v=<id>` casts), and rejects scheme-less `youtu.be/<id>` that T1 lists | probe at HEAD: `watch?v=AAAAAAAAAAA&v=BBBBBBBBBBB` gives BBBBBBBBBBB; `example.com/watch?v=dQw4w9WgXcQ` gives the id; `youtu.be/dQw4w9WgXcQ` gives null; `:434` regex `[^#]*&` greedy and `[^/]+` host | make the query part lazy (`[^#]*?&`), restrict the host to `(?:[a-z]+\.)?youtube\.com` and `youtu\.be`, add `(?:https?://)?` to the youtu.be branch; add these three to C5 | dbdad90 - C5 (casos novos: www com feature=, youtu.be sem esquema, primeiro v=, example.com e host falso) |
| Session churn: every 500 ms report publishes a new PlaybackState, so the phone bar re-binds art, icon and texts twice a second and the host gets 2 IPCs/s; the rate is fixed at 1f, so at 1.5x the extrapolated time lags between reports; and on a video change `setTime` can publish the old video's metadata with the new duration | `:612` publish on every `setTime`; `:350` speed `1f`; `:613-617` `meta?.let` uses the previous `meta` before the new `setMeta` arrives | publish only on state change or when the reported position drifts > 1 s from extrapolation; pass the real `playbackRate` from the page; in `setTime` add the duration only when the reporting id matches the published video's id | dbdad90 - código: PlaybackState só republicado quando a posição desvia > 1,5 s ou a velocidade muda, com a velocidade real; metadata antiga com duração nova por até 1 relato segue (baixo impacto) |
