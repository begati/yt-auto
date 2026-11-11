# phone-remote proofs

**HEAD**: e93cac0
**Tree**: clean
**Shell**: /bin/sh
**Result**: 21 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `walk - toque num vídeo do Início no celular: DHU em /watch?v=<id>; celular continua no Início` | pass (Walked) |
| C2 | `walk - app do carro fechado, toque no celular: aviso na barra; abrir o app no DHU: o vídeo abre` | pass (Walked) |
| C3 | `walk - DevTools no celular: nenhum <video> com paused false depois de rolar o Início e tocar em vídeos` | pass (Walked) |
| C4 | `walk - location.href para um /watch no celular via DevTools: celular volta, DHU abre o vídeo` | pass (Walked) |
| C5 | `run unit / CAST-01 video id from watch shorts and youtu-be links PASSED` | pass |
| C6 | `run unit / CAST-05 a short opens as a watch url PASSED` | pass |
| C7 | `walk - screenshot do celular com a barra e o vídeo do DHU` | pass (Walked) |
| C8 | `walk - "próximo" no DHU: barra do celular mostra o novo título` | pass (Walked) |
| C9 | `walk - DevTools no carro: paused alterna; ícone da barra muda` | pass (Walked) |
| C10 | `walk - currentTime do carro perto de 0 depois do toque` | pass (Walked) |
| C11 | `walk - id do vídeo do carro muda depois do toque` | pass (Walked) |
| C12 | `run unit / REMOTE-06 clock formats m-ss and h-mm-ss PASSED` | pass |
| C13 | `walk - dois screenshots com 2 s de intervalo: tempo avança e bate com o currentTime do carro` | pass (Walked) |
| C14 | `walk - arrastar até a metade: currentTime do carro perto da metade da duração` | pass (Walked) |
| C15 | `walk - yta.aspect() do carro muda; aspect em settings.xml inalterado` | pass (Walked) |
| C16 | `walk - getPlaybackQuality() do player do carro e quality em settings.xml com o valor escolhido` | pass (Walked) |
| C17 | `walk - playbackRate do vídeo do carro e speed em settings.xml com o valor escolhido` | pass (Walked) |
| C18 | `walk - processo novo, app aberto no DHU no Início, celular aberto: sem barra` | pass (Walked) |
| C19 | `walk - DHU em outro app, processo novo, celular aberto: barra com o aviso` | pass (Walked) |
| C20 | `walk - mesmo cenário de C19, toque num vídeo: aviso rápido na tela do celular` | pass (Walked) |
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
