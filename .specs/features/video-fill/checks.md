# Video Fill Checks

**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

## Execution Plan

1. [x] Superfície inteira em `/watch` (FILL-01..04) - files: `YoutubeCarAppService.kt`, `VideoFillTest.kt`, `SettingsTest.kt` - verify: run unit - commit: `feat: fill the car surface with the video on watch pages`
2. [x] Proporção padrão e CSS (ASPECT-03, 05..09) - files: `YoutubeCarAppService.kt`, `VideoFillTest.kt` - verify: run unit + build - commit: `feat: add video aspect setting`
3. [ ] Botão de proporção (ASPECT-01, 02, 04) - files: `YoutubeCarAppService.kt`, `ic_aspect_ratio.xml`, `VideoFillTest.kt` - verify: run unit + build + walk - commit: `feat: add aspect ratio button to the car map strip`

## Checks

**C1** - Em `/watch` com tela cheia ligada, a WebView ocupa a superfície inteira (FILL-01, AC 1)
Proof: run unit | FILL-01 watch page with fullscreen fills the surface PASSED

**C2** - Fora de `/watch` a WebView fica na área visível (FILL-02, AC 2)
Proof: run unit | FILL-02 other pages stay in the visible area PASSED

**C3** - Com tela cheia desligada, `/watch` fica na área visível (FILL-03, AC 3)
Proof: run unit | FILL-03 fullscreen off keeps watch in the visible area PASSED

**C4** - Navegar entre `/watch` e outra página refaz a disposição (FILL-04, AC 4)
Proof: walk - abrir vídeo (sem margem), voltar para Início (lista dentro da área visível), abrir outro vídeo (sem margem)

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

**C11** - Sem valor salvo, o padrão é Ajustar (ASPECT-07, AC 11)
Proof: run unit | ASPECT-07 default is contain PASSED

**C12** - Valor salvo inválido lê como Ajustar (ASPECT-08, AC 12)
Proof: run unit | ASPECT-08 invalid stored aspect reads as contain PASSED

**C13** - Mudar o padrão nas Configurações descarta a escolha do botão (ASPECT-09, AC 13)
Proof: run unit | ASPECT-09 changing the default drops the tapped mode PASSED

## Handoff

**Dispatched:** none yet
**Walked:** pending
