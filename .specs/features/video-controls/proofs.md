# video-controls proofs

**HEAD**: cfc2d26
**Tree**: clean
**Shell**: /bin/sh
**Result**: 18 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / CTRL-01 double tap on the right half skips forward 10 s PASSED` | pass |
| C2 | `run unit / CTRL-02 double tap on the left half skips back 10 s PASSED` | pass |
| C3 | `walk - DHU: duplo toque à esquerda nos primeiros 5 s deixa currentTime em 0; perto do fim, à direita, deixa na` | pass (Walked) |
| C4 | `walk - DHU: screenshots da bolha à direita (ic_fast_forward, "10 s") e à esquerda (ic_rewind, "10 s")` | pass (Walked) |
| C5 | `run unit / CTRL-05 repeated double taps on the same side within 1 s add up PASSED` | pass |
| C6 | `walk - DHU: screenshot ~1 s depois do duplo toque sem a bolha` | pass (Walked) |
| C7 | `run unit / CTRL-07 a pinch scale factor does not skip PASSED` | pass |
| C8 | `run unit / CTRL-08 double tap outside the player or off watch does nothing PASSED` | pass |
| C9 | `run unit / CTRL-09 a tap on the visible bar seeks to its fraction of the duration PASSED` | pass |
| C10 | `run unit / CTRL-10 a tap off the bar or with the bar hidden goes to the page PASSED` | pass |
| C11 | `run unit / CTRL-11 a tap on the bar without a duration goes to the page PASSED` | pass |
| C12 | `run unit / CTRL-12 a mostly horizontal drag on watch seeks PASSED` | pass |
| C13 | `run unit / CTRL-13 the drag label shows target, duration and signed difference PASSED` | pass |
| C14 | `run unit / CTRL-14 the drag target follows the finger across the player width PASSED` | pass |
| C15 | `walk - DHU: arrasto com o mouse; screenshot com o indicador durante e sem ele depois; DevTools currentTime igu` | pass (Walked) |
| C16 | `run unit / CTRL-16 a mostly vertical drag scrolls the page PASSED` | pass |
| C17 | `run unit / CTRL-17 a drag off watch or without a duration scrolls the page PASSED` | pass |
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

BUILD SUCCESSFUL in 1s
22 actionable tasks: 1 executed, 21 up-to-date
```
