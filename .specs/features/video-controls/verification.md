# Video Controls verification

**Verdict**: FAIL
**Proofs**: `proofs.md` at cfc2d26
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | `VideoControlsTest` `CTRL-01 double tap on the right half skips forward 10 s` | yes - asserts +10 on the right half, the midpoint, and a narrowed player; `onScale` wires `doubleTapSkip` -> `jsSeekBy(skip)` |
| C2 | `VideoControlsTest` `CTRL-02 double tap on the left half skips back 10 s` | yes - asserts -10, including x=399 next to the midpoint |
| C3 | walk - DHU (C3 in `**Walked:**`): left double tap at 3 s -> 0.57, right near the end -> duration | yes - the path is `jsSeekBy`, which clamps with `Math.max(0,Math.min(v.duration||1e9,...))` |
| C4 | walk - DHU (C4): bubble on each side with `ic_rewind`/`ic_fast_forward` and "10 s" | yes - `showSkip` picks gravity/icon from the sign of the total and `R.string.seek_seconds` |
| C5 | `VideoControlsTest` `CTRL-05 repeated double taps on the same side within 1 s add up` | yes - same side within 1 s, after 1 s, other side, and from zero; `showSkip` seeks 10 s per tap and shows `addSkip`'s total |
| C6 | walk - DHU (C6): screenshot 1.2 s after the double tap has no bubble | yes, loosely - 1.2 s cannot tell 0.6 s from 1.1 s; the code posts `hideSkip` at 600 ms (+200 ms fade) |
| C7 | `VideoControlsTest` `CTRL-07 a pinch scale factor does not skip` | yes - 1.9 and 2.1 sit on both sides of 2.0 |
| C8 | `VideoControlsTest` `CTRL-08 double tap outside the player or off watch does nothing` | yes - y outside, no player, off watch (x outside is untested; my own example passes) |
| C9 | `VideoControlsTest` `CTRL-09 a tap on the visible bar seeks to its fraction of the duration` | yes - 0, 1/4, 1/2 and 1 of an offset bar |
| C10 | `VideoControlsTest` `CTRL-10 a tap off the bar or with the bar hidden goes to the page` | yes for `barTapMs`; the hidden-bar detection (`visibility==='visible'` in `JS_GEOMETRY`) is proven only by the C10 walk line, which shows it |
| C11 | `VideoControlsTest` `CTRL-11 a tap on the bar without a duration goes to the page` | yes - `durationMs <= 0` -> null; `JS_GEOMETRY` sends 0 for a non-finite duration |
| C12 | `VideoControlsTest` `CTRL-12 a mostly horizontal drag on watch seeks` | yes |
| C13 | `VideoControlsTest` `CTRL-13 the drag label shows target, duration and signed difference` | yes - both signs, zero, and h:mm:ss |
| C14 | `VideoControlsTest` `CTRL-14 the drag target follows the finger across the player width` | yes for the formula; no for "tempo no início do gesto" - the wiring feeds it `positionMs`, which no test covers (Finding 1) |
| C15 | walk - DHU (C15/C16): the user dragged both ways, "funcionou muito bem", `currentTime` 502 s afterwards | no - the walk has none of the evidence the check names: no screenshot of the indicator during or after, no comparison of `currentTime` with the shown target (Finding 2). The code path (`endDrag` at 300 ms, same `dragId`, hides `dragView`) does match the criterion |
| C16 | `VideoControlsTest` `CTRL-16 a mostly vertical drag scrolls the page` | yes - includes the tie (5, -5) |
| C17 | `VideoControlsTest` `CTRL-17 a drag off watch or without a duration scrolls the page` | yes |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| The drag starts from a stale, drifting time, not from the time at the start of the gesture (CTRL-14). A small drag can jump up to ~2 s forward, and a second drag started within ~500 ms of the first one's seek starts from the time before that seek, which undoes it | `onScroll` sets `dragStartMs = positionMs`. `positionMs` is refreshed only every 500 ms, and `setTime`'s `else` branch sets it to `expected` without moving `published`, so the next tick counts the elapsed time twice: simulating 1x playback with 500 ms ticks gives errors of 0, +500, +1500, 0 ms in a repeating cycle. `positionMs` is not set to the target after `endDrag` seeks. `Geometry.positionMs` (the page's `currentTime`, read by `JS_GEOMETRY`) is never used | take the start from the page's `currentTime` when the gesture starts, or at least set `positionMs = dragTargetMs` when `endDrag` seeks. Separately, fix the extrapolation in `setTime` (keep the baseline). Add a check on whatever pure function holds the start time | |
| C15's walk does not show what its proof claims: no screenshot of the indicator during or after the drag, no comparison of `currentTime` with the target shown (±1 s) | tasks.md `**Walked:**`, C15/C16: the user said "funcionou muito bem" and `currentTime` was 502 s after several drags | walk C15 again as its proof says: one drag, a screenshot during and one after, and `currentTime` compared with the target shown | |

Probed beyond the checks: `onScroll`'s sign (`dragFingerX -= distanceX`; Android's `distanceX` is positive when the finger moves left, so a finger moving right advances, which is correct), `dragSeeks` gets `abs`, so the sign does not matter there. `onScale` returns early on a factor other than 2 or off `/watch`. `onClick` calls `tap()` off `/watch`, and on `/watch` whenever `barTapMs` gives null. `endDrag` seeks only when the video id matches the one from the start of the gesture. Page px to surface px is `rect * density + webViewBounds offset`; `textZoom` changes only the text size, not the CSS px scale. Own examples, run at HEAD as a throwaway test and deleted, all passed: a narrower, offset player (x left and right of it, y above it, the midpoint edge), an offset 500 px bar on a 10 h video (ends, quarter, 1 px outside, negative duration), an 8 h drag, a horizontal/vertical tie, the label at exactly 1 h, and `addSkip` at 1000 and 1001 ms.
