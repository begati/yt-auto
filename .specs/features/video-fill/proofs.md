# video-fill proofs

**HEAD**: 0b82b9d
**Tree**: clean
**Shell**: /bin/sh
**Result**: 21 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / FILL-01 margins and hidden strips are covered PASSED` | pass |
| C2 | `walk - lista do Início sem margem preta; barras do host por cima da lista` | pass (Walked) |
| C3 | `walk - opção desligada no celular com vídeo aberto: página normal do YouTube, sem margem` | pass (Walked) |
| C4 | `run unit / FILL-04 shown action strips do not shrink the page PASSED` | pass |
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
| C15 | `run unit / FILL-05 a media card beside the app limits the page PASSED` | pass |
| C16 | `run unit / FILL-06 no visible area yet fills the surface PASSED` | pass |
| C17 | `walk - vídeo aberto, botão de layout do DHU: vídeo em Ajustar ocupa só a área à esquerda do card, sem corte` | pass (Walked) |
| C18 | `run unit / FILL-07 an empty inverted or stale area fills the surface PASSED` | pass |
| C19 | `walk - modo dividido, grade de apps, abrir o app: innerWidth 800 dois segundos depois (antes: 464 por ~10 s)` | pass (Walked) |
| C20 | `walk - com Preencher pelo botão: modo dividido objectFit cover em 464 px, de volta à tela cheia cover em 800 p` | pass (Walked) |
| Build | `./gradlew assembleDebug` | pass |

## Output

`Run unit` - exit 0

```

VideoFillTest > ASPECT-06 options are Ajustar Preencher Esticar in order PASSED

VideoFillTest > FILL-04 shown action strips do not shrink the page PASSED

VideoFillTest > ASPECT-07 default is contain PASSED

VideoFillTest > ASPECT-04 tapping leaves the saved default unchanged PASSED

VideoFillTest > ASPECT-09 changing the default drops the tapped mode PASSED

VideoFillTest > ASPECT-01 tapping cycles contain cover fill contain PASSED

BUILD SUCCESSFUL in 1s
22 actionable tasks: 1 executed, 21 up-to-date
```
