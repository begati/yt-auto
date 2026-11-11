# offline-screen proofs

**HEAD**: a04e5e4
**Tree**: clean
**Shell**: /bin/sh
**Result**: 9 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `walk - rede desligada, app aberto: screenshot do DHU com a tela de carregamento, sem página de erro da WebView` | pass (Walked) |
| C2 | `walk - log chromium de carregamento da URL a cada ~5 s com a rede desligada` | pass (Walked) |
| C3 | `walk - rede religada: a página carrega antes do próximo ciclo de 5 s` | pass (Walked) |
| C4 | `walk - depois de religar, a página do YouTube aparece e o log não mostra mais recargas` | pass (Walked) |
| C5 | `walk - screenshots durante várias tentativas mostram só a tela de carregamento` | pass (Walked) |
| C6 | `walk - o código só reage a request.isForMainFrame; com rede, a página carrega normalmente sem a tela` | pass (Walked) |
| C7 | `walk - abrir vídeo, cortar rede, tocar "Início" (falha), religar: Início carrega; "Voltar" leva ao vídeo` | pass (Walked) |
| C8 | `walk - rede desligada, abrir o app no celular: screenshot com a tela de carregamento; religar: página do Googl` | pass (Walked) |
| Build | `./gradlew assembleDebug` | pass |
