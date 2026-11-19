# prev-next proofs

**HEAD**: 0d112c6
**Tree**: dirty - .impeccable/critique/2026-11-19T14-22-29Z__egati-youtubeauto-youtubecarappservice-kt-7019eb6a.md
**Shell**: /bin/sh
**Result**: 8 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / PN-01 on a video the side strip is previous, next, aspect, settings PASSED` | pass |
| C2 | `run unit / PN-02 without a next video the next button is gone PASSED` | pass |
| C3 | `walk - DHU + DevTools: toque em próximo num mix muda o v= da URL` | pass (Walked) |
| C4 | `run unit / PN-04 previous opens the previous video when there is one PASSED` | pass |
| C5 | `run unit / PN-05 previous restarts the video when there is none PASSED` | pass |
| C6 | `run unit / FIX-04 off a video the side strip has settings only PASSED` | pass |
| C7 | `walk - DHU + DevTools: página reporta neighbors ao trocar de vídeo; screenshots da barra num mix e no Início` | pass (Walked) |
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
24 actionable tasks: 1 executed, 23 up-to-date
```
