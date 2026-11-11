# Video Fill Checks

**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

## Execution Plan

1. [x] Superfície inteira em `/watch` (FILL-01..04) - files: `YoutubeCarAppService.kt`, `VideoFillTest.kt`, `SettingsTest.kt` - verify: run unit - commit: `feat: fill the car surface with the video on watch pages`
2. [x] Proporção padrão e CSS (ASPECT-03, 05..09) - files: `YoutubeCarAppService.kt`, `VideoFillTest.kt` - verify: run unit + build - commit: `feat: add video aspect setting`
3. [x] Botão de proporção (ASPECT-01, 02, 04) - files: `YoutubeCarAppService.kt`, `ic_aspect_ratio.xml`, `VideoFillTest.kt` - verify: run unit + build + walk - commit: `feat: add aspect ratio button to the car map strip`

## Checks

**C1** - Em `/watch` a WebView ocupa a superfície inteira (FILL-01, AC 1)
Proof: walk - vídeo aberto sem margem preta; barras do host por cima do vídeo

**C2** - Fora de `/watch` a WebView também ocupa a superfície inteira (FILL-02, AC 2)
Proof: walk - lista do Início sem margem preta; barras do host por cima da lista

**C3** - Com "Tela cheia ao abrir vídeo" desligada a WebView continua na superfície inteira (FILL-03, AC 3)
Proof: walk - a disposição da WebView não lê a opção: `frame.addView(wv)` com o tamanho do `FrameLayout`, sem `onVisibleAreaChanged`

**C4** - Navegar entre `/watch` e outra página não muda o tamanho da WebView (FILL-04, AC 4)
Proof: walk - abrir vídeo, voltar para Início, abrir outro vídeo: superfície inteira nos três

**C5** - Cada toque avança Ajustar → Preencher → Esticar → Ajustar (ASPECT-01, AC 5)
Proof: run unit | ASPECT-01 tapping cycles contain cover fill contain PASSED

**C6** - O toque mostra toast com o nome do novo modo (ASPECT-02, AC 6)
Proof: walk - tocar o botão de proporção mostra "Preencher", depois "Esticar", depois "Ajustar"

**C7** - O vídeo de `/watch` usa o `object-fit` do modo atual (ASPECT-03, AC 7)
Proof: walk - com vídeo 16:9 na tela do carro: Ajustar com barras, Preencher recortado, Esticar distorcido

**C8** - Tocar no botão não altera o padrão salvo (ASPECT-04, AC 8)
Proof: run unit | ASPECT-04 tapping leaves the saved default unchanged PASSED

**C9** - Sem escolha pelo botão, vale o padrão salvo (ASPECT-05, AC 9)
Proof: run unit | ASPECT-05 without a tap the saved default applies PASSED

**C10** - "Proporção do vídeo" oferece Ajustar, Preencher, Esticar, nessa ordem, mapeadas para contain, cover, fill (ASPECT-06, AC 10)
Proof: run unit | ASPECT-06 options are Ajustar Preencher Esticar in order PASSED

**C14** - O celular mostra "Proporção do vídeo" com as três opções, e o padrão escolhido lá chega ao carro (ASPECT-06, AC 10)
Proof: walk - Configurações do celular: "Proporção do vídeo" em "Vídeo", diálogo Ajustar/Preencher/Esticar; escolher Esticar faz `yta.aspect()` do carro ler `fill`

**C11** - Sem valor salvo, o padrão é Ajustar (ASPECT-07, AC 11)
Proof: run unit | ASPECT-07 default is contain PASSED

**C12** - Valor salvo inválido lê como Ajustar (ASPECT-08, AC 12)
Proof: run unit | ASPECT-08 invalid stored aspect reads as contain PASSED

**C13** - Mudar o padrão nas Configurações descarta a escolha do botão (ASPECT-09, AC 13)
Proof: run unit | ASPECT-09 changing the default drops the tapped mode PASSED

## Handoff

**Dispatched:** Verifier round 1 at `3489ccb` (no screens sub-agent: no criterion cites a capture) - returned
**Walked:** 2026-11-11 (round 2, `HEAD` após as correções): Início sem margem, lista sob as barras do host, superfície inteira (C2); vídeo aberto também (C1, C4). Celular (LoginActivity > engrenagem): "Configurações > Vídeo" lista "Proporção do vídeo: Ajustar"; o diálogo mostra Ajustar, Preencher, Esticar; escolher Esticar grava `aspect=fill` e `yta.aspect()` no carro lê `fill` (C14). "Tela cheia ao abrir vídeo" desligada no celular com vídeo aberto: página normal do YouTube (cabeçalho "Premium" no topo da tela), ainda na superfície inteira, sem margem (C3); religada em seguida. Ida e volta do padrão (C13): botão do carro leva a `cover`, padrão no celular Esticar lê `fill`, de volta a Ajustar lê `contain` (antes da correção lia `cover`). Round 1:, DHU (800x480) + telefone motorola_signature via USB, app já logado. P1 sem margem: Início mostra a lista dentro da área visível (barras do host em volta); tocar no 1º vídeo abre `/watch` ocupando a superfície inteira, botões "Voltar/Início/Pausar/Pesquisar" e os botões do mapa por cima, sem encolher o vídeo; "Início" (dois toques: o 1º só revela a barra) volta à lista na área visível; abrir outro vídeo ocupa a superfície de novo (C4). P1 botão: ícone de proporção acima da engrenagem; três toques mostram os toasts "Preencher", "Esticar", "Ajustar", e `getComputedStyle(video).objectFit` via DevTools da WebView lê `cover`, `fill`, `contain` a cada toque (C6, C7); com Preencher o vídeo 16:9 é recortado, com Esticar distorcido, com Ajustar tem barras laterais.
