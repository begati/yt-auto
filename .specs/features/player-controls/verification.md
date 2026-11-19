# Player Controls verification

**Verdict**: FAIL
**Proofs**: `proofs.md` at b693858
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | walk - DHU HD + DevTools `getComputedStyle` of `player-middle-controls`, prev/next and play/pause (`**Walked:**` C1) | no - AC 1 names five states (tocando, pausado, carregando, entre vídeos, anúncio); the walk read `paused-mode`, `playing-mode,buffering-mode` after a seek and `playing-mode` only. Between videos (ended / autoplay countdown) and an ad were never read, and those are exactly the states where the old rule failed. The rule hides the parent with inherited `visibility`, so a button YouTube sets `visibility:visible` on in those states would show while the recorded evidence stays the same |
| C2 | walk - DHU HD screenshot h3 with controls shown, "1:12 / 25:20" and the progress bar (`**Walked:**` C2) | yes - the rule touches only `player-top-controls` and `player-middle-controls` (YoutubeCarAppService.kt:161-162); the bar and time sit outside both and the screenshot shows them |
| C3 | walk - DHU HD + DevTools, `html.yta-watch` removed by hand, middle controls and play read `visible/visible` (`**Walked:**` C3) | yes - the option was not turned off as the proof says, but the class is the only scope of the rule and is set solely by `yta.flag('fullscreen')&&location.pathname==='/watch'` (YoutubeCarAppService.kt:183-184); nothing else adds `yta-watch` or hides `player-middle-controls` |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| AC 1's "entre vídeos" and "anúncio" states have no proof; the middle buttons could reappear there and C1 stays green | C1 walk covers 3 of AC 1's 5 states; `YoutubeCarAppService.kt:162` hides only the parent with `visibility:hidden`, which a descendant `visibility:visible` overrides | walk an ad (skipAds off) and the end of a video into the next (autoplay), reading `getComputedStyle` of `player-middle-controls` and each button; or hide the descendants too (`html.yta-watch player-middle-controls,html.yta-watch player-middle-controls *{visibility:hidden!important}`) and walk those two states | ac30950 |

Probed without a finding: miniplayer and SPA navigation away from `/watch` (class drops within 500 ms, AC 3 territory, not fullscreen); `/shorts/<id>` (redirected to `/watch`, covered by the class); fullscreen flag off with YouTube's own fullscreen button (no `yta-watch`, buttons drawn as YouTube does, as AC 3 asks); ad skip buttons (`.ytp-*skip*`, outside `player-middle-controls`, still clickable); taps (hidden elements take no pointer events, so the tap reaches the player as the spec decision intends).
