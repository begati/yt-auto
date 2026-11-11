# video-fill proofs

**HEAD**: 3489ccb
**Tree**: clean
**Shell**: /bin/sh
**Result**: 14 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / FILL-01 watch page with fullscreen fills the surface PASSED` | pass |
| C2 | `run unit / FILL-02 other pages stay in the visible area PASSED` | pass |
| C3 | `run unit / FILL-03 fullscreen off keeps watch in the visible area PASSED` | pass |
| C4 | `walk - abrir vídeo (sem margem), voltar para Início (lista dentro da área visível), abrir outro vídeo (sem mar` | pass (Walked) |
| C5 | `run unit / ASPECT-01 tapping cycles contain cover fill contain PASSED` | pass |
| C6 | `walk - tocar o botão de proporção mostra "Preencher", depois "Esticar", depois "Ajustar"` | pass (Walked) |
| C7 | `walk - com vídeo 16:9 na tela do carro: Ajustar com barras, Preencher recortado, Esticar distorcido` | pass (Walked) |
| C8 | `run unit / ASPECT-04 tapping leaves the saved default unchanged PASSED` | pass |
| C9 | `run unit / ASPECT-05 without a tap the saved default applies PASSED` | pass |
| C10 | `run unit / ASPECT-06 options are Ajustar Preencher Esticar in order PASSED` | pass |
| C11 | `run unit / ASPECT-07 default is contain PASSED` | pass |
| C12 | `run unit / ASPECT-08 invalid stored aspect reads as contain PASSED` | pass |
| C13 | `run unit / ASPECT-09 changing the default drops the tapped mode PASSED` | pass |
| Build | `./gradlew assembleDebug` | pass |

## Output

`Run unit` - exit 0

```

VideoFillTest > ASPECT-06 options are Ajustar Preencher Esticar in order PASSED

VideoFillTest > ASPECT-07 default is contain PASSED

VideoFillTest > ASPECT-04 tapping leaves the saved default unchanged PASSED

VideoFillTest > FILL-03 fullscreen off keeps watch in the visible area PASSED

VideoFillTest > ASPECT-09 changing the default drops the tapped mode PASSED

VideoFillTest > ASPECT-01 tapping cycles contain cover fill contain PASSED

BUILD SUCCESSFUL in 1s
22 actionable tasks: 1 executed, 21 up-to-date
```
