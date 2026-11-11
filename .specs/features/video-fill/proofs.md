# video-fill proofs

**HEAD**: a93ad69
**Tree**: clean
**Shell**: /bin/sh
**Result**: 15 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `walk - vídeo aberto sem margem preta; barras do host por cima do vídeo` | pass (Walked) |
| C2 | `walk - lista do Início sem margem preta; barras do host por cima da lista` | pass (Walked) |
| C3 | `walk - a disposição da WebView não lê a opção: frame.addView(wv) com o tamanho do FrameLayout, sem onVisibleAr` | pass (Walked) |
| C4 | `walk - abrir vídeo, voltar para Início, abrir outro vídeo: superfície inteira nos três` | pass (Walked) |
| C5 | `run unit / ASPECT-01 tapping cycles contain cover fill contain PASSED` | pass |
| C6 | `walk - tocar o botão de proporção mostra "Preencher", depois "Esticar", depois "Ajustar"` | pass (Walked) |
| C7 | `walk - com vídeo 16:9 na tela do carro: Ajustar com barras, Preencher recortado, Esticar distorcido` | pass (Walked) |
| C8 | `run unit / ASPECT-04 tapping leaves the saved default unchanged PASSED` | pass |
| C9 | `run unit / ASPECT-05 without a tap the saved default applies PASSED` | pass |
| C10 | `run unit / ASPECT-06 options are Ajustar Preencher Esticar in order PASSED` | pass |
| C14 | `walk - Configurações do celular: "Proporção do vídeo" em "Vídeo", diálogo Ajustar/Preencher/Esticar; escolher ` | pass (Walked) |
| C11 | `run unit / ASPECT-07 default is contain PASSED` | pass |
| C12 | `run unit / ASPECT-08 invalid stored aspect reads as contain PASSED` | pass |
| C13 | `run unit / ASPECT-09 changing the default drops the tapped mode PASSED` | pass |
| Build | `./gradlew assembleDebug` | pass |

## Output

`Run unit` - exit 0

```

VideoFillTest > ASPECT-05 without a tap the saved default applies PASSED

VideoFillTest > ASPECT-06 options are Ajustar Preencher Esticar in order PASSED

VideoFillTest > ASPECT-07 default is contain PASSED

VideoFillTest > ASPECT-04 tapping leaves the saved default unchanged PASSED

VideoFillTest > ASPECT-09 changing the default drops the tapped mode PASSED

VideoFillTest > ASPECT-01 tapping cycles contain cover fill contain PASSED

BUILD SUCCESSFUL in 1s
22 actionable tasks: 2 executed, 20 up-to-date
```
