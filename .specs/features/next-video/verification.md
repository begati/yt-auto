# Next Video verification

**Verdict**: FAIL
**Proofs**: `proofs.md` at 317cce3
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | `NextVideoTest.kt` `NEXT-01 a video from a playlist node opens with its playlist` | yes - exact URL for `watch_later`, `liked` and `list:<id>`, through `playlistMediaId` and `watchUrl` together |
| C2 | `NextVideoTest.kt` `NEXT-02 a video from a feed or the phone opens without a playlist` | yes - exact bare URL for `history`, `subscriptions` and a bare id (phone, share and voice all pass an 11-char id checked by `isVideoId`) |
| C3 | `NextVideoTest.kt` `NEXT-03 an invalid playlist id opens the video alone` | yes - `bad&list`, empty and `x y` would leak into the URL without the `PLAYLIST_ID` check |
| C4 | `NextVideoTest.kt` `NEXT-04 outside a playlist with autoplay on the end starts the next video` | yes - true when ended outside a list (also with `&t=`), false before the end |
| C5 | `NextVideoTest.kt` `NEXT-05 in a playlist the app leaves the next video to YouTube` | yes - `&list=` and `?list=` first both false |
| C6 | `NextVideoTest.kt` `NEXT-06 with autoplay off or unknown the app does not start the next video` | yes - false and null both asserted false; `autonav != false` would fail it |
| C7 | `NextVideoTest.kt` `NEXT-07 one end starts the next video at most once` | yes at the rule layer (`sent` = true gives false); the `nextSent` set/reset in `setEnded` is wiring, walked |
| C8 | walk - DHU + DevTools, next call made inert | yes - the early next does not touch `endedAt` or the countdown; `endCountdownDue(true, true, >=10 s)` still starts it, as the walk shows |
| C9 | `NextVideoTest.kt` `NEXT-09 the end of an ad does not start the next video` | added by the fix of the finding below; asserts false for another id and an empty id, true for the URL's id |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| The early next may skip the chosen video when the player reports ENDED during or right after an ad, and no check or walk covers it | `PAGE_JS` (`YoutubeCarAppService.kt:195`) reports the end from `getPlayerState()===0` without comparing the player's video with the URL's `v` (the comment at :190 says the player reports the ad's id during an ad); before this diff a transient 0 only fed the 10 s countdown, now `setEnded` calls `JS_NEXT` on the first report (:1067). Not reproduced - risk, not a seen failure | Walk a video with a pre-roll/mid-roll ad with autoplay on and record that the chosen video plays; if ENDED shows during the ad, only report the end when the player's current video id equals the URL's `v` | 8b44a89 - the page passes the player's video id and `earlyNextDue` requires it to be the URL's `v`; proven by C9 (NEXT-09) |
