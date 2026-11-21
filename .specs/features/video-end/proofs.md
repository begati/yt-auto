# video-end proofs

**HEAD**: df9d960
**Tree**: clean
**Shell**: /bin/sh
**Result**: 11 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / END-01 autoplay off starts the countdown at the end PASSED` | pass |
| C2 | `run unit / END-02 autoplay on or unknown waits 10 s at the end PASSED` | pass |
| C3 | `run unit / END-03 a video that is not ended never starts the countdown PASSED` | pass |
| C4 | `walk - DHU HD: screenshots no início e ~2,5 s depois (barra cheia, depois pela metade) com o texto` | pass (Walked) |
| C5 | `walk - DHU HD + DevTools: location.href vira a página inicial ~5 s depois do painel aparecer` | pass (Walked) |
| C6 | `walk - DHU HD + DevTools: toque no painel; location.href muda em menos de 1 s` | pass (Walked) |
| C7 | `walk - DHU HD: play nativo durante a contagem esconde o painel e o vídeo segue em /watch depois de 6 s` | pass (Walked) |
| C8 | `walk - DHU HD: com "Página inicial" = Inscrições, o painel diz "Voltando para Inscrições" e abre /feed/subscri` | pass (Walked) |
| C9 | `walk - DHU HD + DevTools: getComputedStyle do wrapper e filhos hidden em playing-mode e ended-mode` | pass (Walked) |
| C10 | `walk - DHU HD + DevTools: com yta-watch removida, o wrapper fica visible; classe recolocada` | pass (Walked) |
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
