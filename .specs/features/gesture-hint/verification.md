# Gesture Hint verification

**Verdict**: FAIL
**Proofs**: `proofs.md` at aba2790
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | `VideoControlsTest.kt` `GestureHintTest` `HINT-01 the first video on watch asks for the hint` | yes - `takeGestureHint` returns true on fresh prefs on watch; the toast call in `setPlaying` is proved by C4's walk |
| C2 | `VideoControlsTest.kt` `GestureHintTest` `HINT-02 the hint shows only once` | yes - second call false and `gestureHintShown=true` is in the prefs, not in memory |
| C3 | `VideoControlsTest.kt` `GestureHintTest` `HINT-03 a video off watch neither shows nor uses up the hint` | yes - off watch returns false and a later watch call still returns true. This is the function only: the call site passes `onWatch` (`videoIdFromUrl(pageUrl) != null`), checked by reading. Feed previews never reach `setPlaying(true)` because the JS `main()` filter only lets `#movie_player` through, and the poll reads `#movie_player video` |
| C4 | walk - `**Walked:**` 2026-11-18, DHU 800x480 | yes - first video on `/watch` showed the pt toast with the whole text and `gestureHintShown=true` was saved; a second video played with no toast for 8 s |
| C5 | `VideoControlsTest.kt` `GestureHintTest` `HINT-04 a video not on screen neither shows nor uses up the hint` | yes - added in round 2 by the orchestrator: not visible returns false and leaves the key unset, a later visible call returns true |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| The hint can fire and be used up when the video is not on the car screen | `setPlaying` (YoutubeCarAppService.kt:953) only checks `p` and `onWatch`. With `background` on, `onSurfaceDestroyed` keeps the WebView playing. So a first video started from the phone or the media card (`onPlayFromMediaId` -> `open` -> `loadUrl`) while the car shows Maps calls `setPlaying(true)` on `/watch`. The toast then pops over another app, or the host drops it, and either way the mark is saved. The same happens while moving with `audioWhileMoving`: the cover hides the video, but the toast still tells the driver to tap it, and the mark is saved. This breaks the spec's goal: the driver should see the hint once, when the gestures apply | Show and save the hint only when the video is visible in the car (for example `display?.surface != null && cover?.visibility != View.VISIBLE`). Otherwise leave the mark unset, so the next play while the video is visible shows it. Add a check for that case | bb10e43 - `takeGestureHint` takes `visible` (surface attached, audio cover hidden); HINT-04 / C5 |

Probed also, with no defect found:
- Throwaway tests at HEAD, since deleted. Five off-watch calls do not write the key, and then one on-watch call returns true. An explicit `gestureHintShown=false` stored alongside other prefs (an existing user) still shows the hint. `videoIdFromUrl` is null for Início, results with `watch?v=` in the query, subscriptions and a channel, and finds the id in `watch?v=..&list=..` and `watch?list=..&v=..`.
- Order of events: `onWatch` is read when `setPlaying` runs on the main executor, and `pageUrl` comes from `doUpdateVisitedHistory`, so it is the current URL. In the miniplayer on Início, `onWatch` is false.
- Translations: all 7 locales have the string, and they mean the same as the pt text. The ru string says "перемотка" twice, which is fine.
