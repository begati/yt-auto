# Offline Screen Checks

**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

## Execution Plan

1. [x] Tela de carregamento e novas tentativas a cada 5 s (OFF-01, 02, 04..07) - files: `YoutubeCarAppService.kt` - verify: build + walk - commit: `feat: show a loading screen and retry when the car page fails offline`
2. [x] Nova tentativa ao voltar a rede, lógica numa classe reaproveitável (OFF-03) - files: `YoutubeCarAppService.kt`, `AndroidManifest.xml` - verify: build + walk - commit: `feat: retry the failed page as soon as the network returns`
3. [x] Mesma tela no app do celular (OFF-08) - files: `YoutubeCarAppService.kt` - verify: build + walk - commit: `feat: show the loading screen on the phone too`

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
Proof: walk - Início carregado, rede cortada, feed rolado: miniaturas falham e a página continua, sem a tela

**C7** - "Voltar" após reconectar não passa por páginas de erro (OFF-07, AC 7)
Proof: walk - abrir vídeo, cortar rede, tocar "Início" (falha), religar: Início carrega; "Voltar" leva ao vídeo

**C8** - No celular, sem rede, a tela de login mostra a mesma tela de carregamento e volta sozinha (OFF-08, AC 8)
Proof: walk - rede desligada, abrir o app no celular: screenshot com a tela de carregamento; religar: página do Google/YouTube carrega

**C9** - Só erros de rede mostram a tela; esquema desconhecido, URL inválida e SSL não (OFF-09, AC 9)
Proof: run unit | OFF-09 only network errors show the loading screen PASSED

## Handoff

**Dispatched:** Verifier round 1 at `a04e5e4` (no screens sub-agent: no criterion cites a capture)
**Walked:** 2026-11-11 (round 2, após o Verifier): com rede, `location.href='foo-app://x'` via DevTools mostra a página "Página da Web não disponível ... net::ERR_UNKNOWN_URL_SCHEME" da WebView, sem a tela de carregamento (C9); sem rede, "Início" ainda mostra a tela (o erro offline está entre os códigos de rede). Início carregado, rede cortada, `scrollBy` no feed via DevTools: 16 imagens com `naturalWidth` 0 e a página do YouTube continua visível, só com o spinner dela, sem a tela de carregamento (C6). Com o `released`/`removeCallbacks` novos, de novo: tentativas a cada 5 s (56820, 61826) e, religada a rede, o YouTube carrega em 68338, antes da tentativa de ~71826 (C2, C3). Round 1: DHU 800x480 + telefone motorola_signature via USB, rede cortada com `adb shell svc wifi disable` e `svc data disable` (o DHU segue pelo ADB). Carro: sem rede, "Início" (dois toques: o 1º revela a barra) mostra fundo preto, animação girando e "Sem conexão. Tentando de novo…", sem a página "Página da Web não disponível" que fica por baixo (`location.href` `chrome-error://chromewebdata/` via DevTools) (C1, C5). `performance.timeOrigin` do documento lido a cada 2 s muda a cada 5 s: 73, 78, 83, 88 (C2). Religar a rede logo após uma tentativa: o YouTube carrega 2,3 s depois, antes da próxima tentativa agendada (C3); a tela some e o documento para de ser recriado (C4). Com a página carregada e rede ligada, a tela não aparece (C6). Vídeo aberto, rede cortada, "Início" falha, rede religada, Início carrega; "Voltar" leva ao `/watch`, `history.length` 3 (C7). Celular: rede cortada, `am start -S` da `LoginActivity`: a mesma tela abaixo da barra "YouTube Auto"; religada, o Início do YouTube aparece sozinho em menos de 10 s (C8).
