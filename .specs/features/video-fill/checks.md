# Video Fill Checks

**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

## Execution Plan

1. [x] Superfície inteira em `/watch` (FILL-01..04) - files: `YoutubeCarAppService.kt`, `VideoFillTest.kt`, `SettingsTest.kt` - verify: run unit - commit: `feat: fill the car surface with the video on watch pages`
2. [x] Proporção padrão e CSS (ASPECT-03, 05..09) - files: `YoutubeCarAppService.kt`, `VideoFillTest.kt` - verify: run unit + build - commit: `feat: add video aspect setting`
3. [x] Botão de proporção (ASPECT-01, 02, 04) - files: `YoutubeCarAppService.kt`, `ic_aspect_ratio.xml`, `VideoFillTest.kt` - verify: run unit + build + walk - commit: `feat: add aspect ratio button to the car map strip`

## Checks

**C1** - Em `/watch` a WebView ocupa a superfície inteira (FILL-01, AC 1)
Proof: run unit | FILL-01 margins and hidden strips are covered PASSED

**C2** - Fora de `/watch` a WebView também ocupa a superfície inteira (FILL-02, AC 2)
Proof: walk - lista do Início sem margem preta; barras do host por cima da lista

**C3** - Com "Tela cheia ao abrir vídeo" desligada a WebView continua na superfície inteira (FILL-03, AC 3)
Proof: walk - opção desligada no celular com vídeo aberto: página normal do YouTube, sem margem

**C4** - Navegar entre `/watch` e outra página não muda o tamanho da WebView (FILL-04, AC 4)
Proof: run unit | FILL-04 shown action strips do not shrink the page PASSED

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

**C15** - Com o card de mídia ao lado, a WebView para na borda do card (FILL-05, AC 14)
Proof: run unit | FILL-05 a media card beside the app limits the page PASSED

**C16** - Sem área visível informada, a WebView ocupa a superfície inteira (FILL-06, AC 15)
Proof: run unit | FILL-06 no visible area yet fills the surface PASSED

**C17** - No modo dividido do DHU o vídeo fica inteiro à esquerda do card (FILL-05, AC 14)
Proof: walk - vídeo aberto, botão de layout do DHU: vídeo em Ajustar ocupa só a área à esquerda do card, sem corte

**C18** - Área visível vazia, invertida ou de uma superfície anterior dá a superfície inteira (FILL-07, AC 16)
Proof: run unit | FILL-07 an empty inverted or stale area fills the surface PASSED

**C19** - Voltando do modo dividido à tela cheia, a página ocupa a superfície inteira em até 1 s (FILL-08, AC 17)
Proof: walk - vídeo tocando: botão de layout para dividido (464), ícone do app para tela cheia: `innerWidth` 800 em 1,5 s, duas vezes; e pela grade de apps também 800 em 1,5 s

**C20** - A proporção em uso se mantém ao trocar de layout (FILL-09, AC 18)
Proof: walk - com Preencher pelo botão: modo dividido `objectFit` `cover` em 464 px, de volta à tela cheia `cover` em 800 px

## Handoff

**Dispatched:** Verifier round 1 at `3489ccb` (no screens sub-agent: no criterion cites a capture) - returned
**Walked:** 2026-11-11 (round 6, após relato do usuário): com vídeo tocando, ir ao modo dividido e voltar pelo ícone do app não recria a superfície; o log mostra `visible (12,88)-(712,388)` seguido de `(22,34)-(464,376)` no mesmo milissegundo, e nenhum `onSurfaceAvailable`, por isso o refresh da round 5 não disparava. O refresh passou a sair de `onVisibleAreaChanged` quando a área tem card. Walk: vídeo tocando, dividido `innerWidth` 464, ícone do app 800 em 1,5 s, duas vezes, `paused` false; com Preencher, dividido 464 `cover`, grade de apps e "YouTube Auto" 800 `cover` em 1,5 s (C19, C20). Round 5 ( após relato do usuário): log dos callbacks ao voltar da grade de apps para o app: `visible (12,12)-(788,388)`, `(12,88)-(712,388)` e por último `(22,34)-(464,376)` (área do modo dividido, já em tela cheia); o host só corrige ~10 s depois. Com `invalidate()` 500 ms após `onSurfaceAvailable`, o host reenvia `(12,88)-(712,388)`. Walk: vídeo aberto, Preencher pelo botão; botão de layout do DHU: `innerWidth` 464 e `objectFit` `cover`; botão de layout de novo (grade), "YouTube Auto" na grade: 2 s depois `innerWidth` 800 e `objectFit` `cover`, tela sem corte (C19, C20). Round 4 ( após o Verifier do round 3): app aberto direto no modo dividido mostra o Início só à esquerda do card; o ícone do app na barra do DHU volta à tela cheia e a página cobre a superfície de novo. Tela cheia nativa do player (`onShowCustomView`) não foi percorrida: recebe os mesmos limites por `fitToArea`. Round 3 ( após relato do usuário): áreas visíveis medidas no DHU (superfície 800x400): barras escondidas `(12,12)-(788,388)`, barras visíveis `(12,88)-(712,388)`, modo dividido `(22,34)-(464,376)`. Início e vídeo cobrem a superfície inteira; com o vídeo aberto, o botão de layout do DHU (canto inferior esquerdo) põe o card de mídia à direita e o vídeo se encaixa em 0..464 px, inteiro em Ajustar (C17). Round 2 (, `HEAD` após as correções): Início sem margem, lista sob as barras do host, superfície inteira (C2); vídeo aberto também (C1, C4). Celular (LoginActivity > engrenagem): "Configurações > Vídeo" lista "Proporção do vídeo: Ajustar"; o diálogo mostra Ajustar, Preencher, Esticar; escolher Esticar grava `aspect=fill` e `yta.aspect()` no carro lê `fill` (C14). "Tela cheia ao abrir vídeo" desligada no celular com vídeo aberto: página normal do YouTube (cabeçalho "Premium" no topo da tela), ainda na superfície inteira, sem margem (C3); religada em seguida. Ida e volta do padrão (C13): botão do carro leva a `cover`, padrão no celular Esticar lê `fill`, de volta a Ajustar lê `contain` (antes da correção lia `cover`). Round 1:, DHU (800x480) + telefone motorola_signature via USB, app já logado. P1 sem margem: Início mostra a lista dentro da área visível (barras do host em volta); tocar no 1º vídeo abre `/watch` ocupando a superfície inteira, botões "Voltar/Início/Pausar/Pesquisar" e os botões do mapa por cima, sem encolher o vídeo; "Início" (dois toques: o 1º só revela a barra) volta à lista na área visível; abrir outro vídeo ocupa a superfície de novo (C4). P1 botão: ícone de proporção acima da engrenagem; três toques mostram os toasts "Preencher", "Esticar", "Ajustar", e `getComputedStyle(video).objectFit` via DevTools da WebView lê `cover`, `fill`, `contain` a cada toque (C6, C7); com Preencher o vídeo 16:9 é recortado, com Esticar distorcido, com Ajustar tem barras laterais.
