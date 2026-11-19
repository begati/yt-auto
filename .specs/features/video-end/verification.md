# Video End verification

**Verdict**: FAIL
**Proofs**: `proofs.md` at 9ea8524
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | `VideoEndTest.kt` `END-01 autoplay off starts the countdown at the end` | yes - asserts `endCountdownDue(true, false, 0)` true; the rule is the only gate in `setEnded` (YoutubeCarAppService.kt:1018) besides the fullscreen flag, which the Out of Scope row allows; the -1/0/1 mapping from the toggle's `aria-pressed` was read in the walk (T2, `__ytaAn` false) |
| C2 | `VideoEndTest.kt` `END-02 autoplay on or unknown waits 10 s at the end` | yes - both `true` and `null`, 0 / 9 999 false and 10 000 true, so a shifted bound or `autonav != true` fails it; the page reports `ended` on every 500 ms poll while ended (`if(en\|\|en!==window.__ytaEn)`, YoutubeCarAppService.kt:196, interval at :211), so `endedAt` is kept and 10 s is reached |
| C3 | `VideoEndTest.kt` `END-03 a video that is not ended never starts the countdown` | yes - `ended=false` with all three autoplay states at 60 s; a new video makes `getPlayerState()` leave 0, `en` false resets `endedAt` and calls `stopEnd` (:1017-1019) |
| C4 | walk - DHU HD screenshots k1, k2 (`**Walked:**` C4) | yes - text "Voltando para Início" read; bar nearly full then under half 2.5 s apart, which a 10 s or non-emptying bar would not show. Walk was LTR only (see RTL finding) |
| C5 | walk - DHU HD + DevTools `location.href` = `https://m.youtube.com/` ~6 s after the panel (`**Walked:**` C5) | yes - with C4's panel still shown at ~4.5 s, an early or never-firing `goHome` fails the walk |
| C6 | walk - DHU HD + DevTools, tap at 2.5 s, `location.href` home 0.8 s later (`**Walked:**` C6) | yes - `onClick` returns `goHome.run()` first while `endCounting` (:1718); the tap does not reach the page, as AC 6 wants |
| C7 | walk - DHU HD, native play during the countdown, still `/watch` 6 s later (k4) (`**Walked:**` C7) | no - AC 7 has two branches; the walk proves only "o vídeo volta a tocar". "a página sai do vídeo" (back, home, search) has no proof, and the code relies on the next 500 ms poll to cancel it, which loses a race near the end of the countdown (finding 2) |
| C8 | walk - DHU HD, Página inicial = Inscrições, panel "Voltando para Inscrições", opens `/feed/subscriptions` (k5) (`**Walked:**` C8) | no - the check also claims the panel over the audio cover; the Walked line says it was not walked. Code order supports it (`endIndicator` added after `audioCover`, :1308) but no proof would fail if it were under |
| C9 | walk - DHU HD + DevTools `getComputedStyle` of the wrapper and 8 descendants, `playing-mode` and `ended-mode` (`**Walked:**` C9) | yes - the rule (:163-165) has no player-state condition and hides descendants with `!important`, so other states (paused, ad) take the same rule |
| C10 | walk - DHU HD + DevTools, `html.yta-watch` removed, wrapper `visible` (`**Walked:**` C10) | yes - the option was not toggled, but `yta-watch` is set only by `yta.flag('fullscreen')&&location.pathname==='/watch'` (:186-187) and is the sole scope of the rule |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| RTL (ar): the first countdown on a surface empties the bar toward the wrong side | `startEnd` sets `pivotX = width` in RTL (YoutubeCarAppService.kt:1029), but the panel is `GONE` until that moment and has never been laid out, so `endFill.width` is 0 and the pivot is the left edge; later countdowns read the stale laid-out width and are right. No walk in RTL | the track is a fixed `dp(240)`: use that width (e.g. keep `val barW = dp(240)` in `endIndicator` and set `pivotX = if (RTL) barW.toFloat() else 0f`), or set the pivot in `endFill.post { }`; walk once in `ar` || e11bfcb |
| Leaving the video by the car's back / home / search near the end of the countdown can still open the start page | the strip actions (:1146 `webView?.goBack()`, :1148-1151 `loadUrl(url)`, :1159-1162 `loadUrl(searchUrl)`) do not call `stopEnd`; cancel waits for the page poll (500 ms) to report `en` false. Back at ~4.6 s: `goHome` fires before the poll and loads Início over the page the driver went back to. C7's walk covers only play | call `stopEnd()` at the top of those three listeners (and in `open()` for phone/media-card loads); walk back during the countdown and check `location.href` is the previous page 6 s later || e11bfcb |
| C8's audio-cover branch is unproven | `**Walked:**`: "The panel over the audio cover was not walked (needs mock GPS)" | walk it with a mock location (or the `moving` flag forced by DevTools/adb), screenshot the panel over the cover; or drop the cover clause from C8 and cite the add order at :1308 in a separate check || e11bfcb |
| The countdown runs and navigates while it cannot be seen: settings/search/home menu pushed over the video, or the surface gone (driver in Maps) | `setEnded` starts on `due` alone (:1018-1019); nothing checks `screenManager.top === this` or `display?.surface`. With autoplay on/unknown the 10 s often passes while a list is open, and the start page loads under it with no panel ever shown (AC 4 "WHILE a contagem corre ... mostrar") | gate the start: `due && screenManager.top === this && display?.surface != null` (the poll keeps reporting `ended`, so it starts on return), and `stopEnd()` when one is pushed / in `onSurfaceDestroyed` || e11bfcb |

Probed without a finding: own examples at HEAD (throwaway test, removed) - `endCountdownDue(true,false,3_600_000)` true, `(true,true,10_001)` and `(true,null,Long.MAX_VALUE)` true, `(false,false,0)` false, all as the spec says; `./gradlew testDebugUnitTest --rerun` green incl. `I18nTest` (`going_home` in 7 locales with `%1$s`); fullscreen flag off (`due` false, no countdown, as Out of Scope says); a second video ending after a cancelled countdown (`en` false resets `endedAt`, fresh 10 s); full page load resets `__ytaEn` (first poll reports false); screen destroy mid-countdown (`teardown` removes `goHome`, :1340); a drag on the ended video seeks, `en` turns false and cancels; gesture hint and SponsorBlock/ad skip do not touch `endCounting`.
