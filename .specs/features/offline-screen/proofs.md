# offline-screen proofs

**HEAD**: b89dd91
**Tree**: clean
**Shell**: /bin/sh
**Result**: 10 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `walk - rede desligada, app aberto: screenshot do DHU com a tela de carregamento, sem página de erro da WebView` | pass (Walked) |
| C2 | `walk - log chromium de carregamento da URL a cada ~5 s com a rede desligada` | pass (Walked) |
| C3 | `walk - rede religada: a página carrega antes do próximo ciclo de 5 s` | pass (Walked) |
| C4 | `walk - depois de religar, a página do YouTube aparece e o log não mostra mais recargas` | pass (Walked) |
| C5 | `walk - screenshots durante várias tentativas mostram só a tela de carregamento` | pass (Walked) |
| C6 | `walk - Início carregado, rede cortada, feed rolado: miniaturas falham e a página continua, sem a tela` | pass (Walked) |
| C7 | `walk - abrir vídeo, cortar rede, tocar "Início" (falha), religar: Início carrega; "Voltar" leva ao vídeo` | pass (Walked) |
| C8 | `walk - rede desligada, abrir o app no celular: screenshot com a tela de carregamento; religar: página do Googl` | pass (Walked) |
| C9 | `run unit / OFF-09 only network errors show the loading screen PASSED` | pass |
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
