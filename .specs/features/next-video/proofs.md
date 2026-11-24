# next-video proofs

**HEAD**: 190cb58
**Tree**: clean
**Shell**: /bin/sh
**Result**: 10 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / NEXT-01 a video from a playlist node opens with its playlist PASSED` | pass |
| C2 | `run unit / NEXT-02 a video from a feed or the phone opens without a playlist PASSED` | pass |
| C3 | `run unit / NEXT-03 an invalid playlist id opens the video alone PASSED` | pass |
| C4 | `run unit / NEXT-04 outside a playlist with autoplay on the end starts the next video PASSED` | pass |
| C5 | `run unit / NEXT-05 in a playlist the app leaves the next video to YouTube PASSED` | pass |
| C6 | `run unit / NEXT-06 with autoplay off or unknown the app does not start the next video PASSED` | pass |
| C7 | `run unit / NEXT-07 one end starts the next video at most once PASSED` | pass |
| C8 | `walk - DHU + DevTools: with autoplay on and the next call made inert, the video stays ended and the countdown ` | pass (Walked) |
| C9 | `run unit / NEXT-09 the end of an ad does not start the next video PASSED` | pass |
| Build | `./gradlew assembleDebug` | pass |

## Output

`Run unit` - exit 0

```

VideoFillTest > ASPECT-06 options are Ajustar Preencher Esticar in order PASSED

VideoFillTest > FILL-04 shown action strips do not shrink the page PASSED

VideoFillTest > ASPECT-08 invalid stored aspect reads as the default PASSED

VideoFillTest > ASPECT-04 the button pick is saved as the default PASSED

VideoFillTest > ASPECT-07 default on a fresh install is cover (fill the screen) PASSED

VideoFillTest > ASPECT-05 the saved aspect applies PASSED

BUILD SUCCESSFUL in 2s
24 actionable tasks: 1 executed, 23 up-to-date
```
