# gesture-hint proofs

**HEAD**: a78ddd0
**Tree**: clean
**Shell**: /bin/sh
**Result**: 6 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / HINT-01 the first video on watch asks for the hint PASSED` | pass |
| C2 | `run unit / HINT-02 the hint shows only once PASSED` | pass |
| C3 | `run unit / HINT-03 a video off watch neither shows nor uses up the hint PASSED` | pass |
| C5 | `run unit / HINT-04 a video not on screen neither shows nor uses up the hint PASSED` | pass |
| C4 | `walk - DHU com a marca apagada: screenshot do toast ao abrir um vídeo; segundo vídeo sem toast` | pass (Walked) |
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
