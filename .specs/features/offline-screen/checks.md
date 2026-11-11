# Offline Screen Checks

**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

## Execution Plan

1. [x] Tela de carregamento e novas tentativas a cada 5 s (OFF-01, 02, 04..07) - files: `YoutubeCarAppService.kt` - verify: build + walk - commit: `feat: show a loading screen and retry when the car page fails offline`
2. [ ] Nova tentativa ao voltar a rede (OFF-03) - files: `YoutubeCarAppService.kt`, `AndroidManifest.xml` - verify: build + walk - commit: `feat: retry the failed page as soon as the network returns`

Sem teste JVM: todo o comportamento está nos callbacks da WebView e do `ConnectivityManager` (o projeto só testa na JVM, sem Robolectric); a prova é o walk no DHU com a rede do telefone cortada por `adb shell svc wifi/data`.

## Checks

**C1** - Falha de rede na página principal mostra fundo preto, animação girando e "Sem conexão. Tentando de novo…" (OFF-01, AC 1)
Proof: walk - rede desligada, app aberto: screenshot do DHU com a tela de carregamento, sem página de erro da WebView

**C2** - Com a tela visível, a página é recarregada a cada 5 s (OFF-02, AC 2)
Proof: walk - log `chromium` de carregamento da URL a cada ~5 s com a rede desligada

**C3** - Ao voltar a rede, a página recarrega na hora (OFF-03, AC 3)
Proof: walk - rede religada: a página carrega antes do próximo ciclo de 5 s

**C4** - Página carregada esconde a tela e para as tentativas (OFF-04, AC 4)
Proof: walk - depois de religar, a página do YouTube aparece e o log não mostra mais recargas

**C5** - Entre tentativas falhas a página de erro da WebView não aparece (OFF-05, AC 5)
Proof: walk - screenshots durante várias tentativas mostram só a tela de carregamento

**C6** - Falha de recurso que não é a página principal não mostra a tela (OFF-06, AC 6)
Proof: walk - o código só reage a `request.isForMainFrame`; com rede, a página carrega normalmente sem a tela

**C7** - "Voltar" após reconectar não passa por páginas de erro (OFF-07, AC 7)
Proof: walk - abrir vídeo, cortar rede, tocar "Início" (falha), religar: Início carrega; "Voltar" leva ao vídeo

## Handoff

**Dispatched:** none yet
**Walked:** pending
