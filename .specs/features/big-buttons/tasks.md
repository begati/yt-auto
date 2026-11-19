# Big Buttons Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: inline (sem design.md: mesmo padrão das features anteriores - regras em funções `internal` puras testadas na JVM, Views nativas no `FrameLayout` do carro; visual pelo "Design Brief" da spec)
**Status**: Approved
**Approved**: 2026-11-18 - user: "aprovado, pode implementar"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

---

## Test Coverage Matrix

> Generated from codebase, project guidelines, and spec - confirm before Execute. Guidelines found: `app/build.gradle.kts` (`testLogging` passed/failed/skipped), specs anteriores ("só JVM", sem Robolectric).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Regras puras (`internal fun` em `YoutubeCarAppService.kt`) | unit | 1:1 com os ACs que decidem algo (quais botões, ordem, ação nativa, quando some, limites da página, para onde vai o toque) | `app/src/test/java/dev/begati/youtubeauto/BigButtonsTest.kt` | `./gradlew testDebugUnitTest --rerun` |
| Views, `SurfaceCallback`, template | none (walk) | Walk no DHU (screenshot, `tap`, DevTools) | - | `./gradlew assembleDebug` |
| READMEs | none | - | - | build gate only |

## Gate Check Commands

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tarefas com testes unitários | `./gradlew testDebugUnitTest --rerun` |
| Build | Tarefas de wiring/View/docs | `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun` |

---

## Execution Plan

### Phase 1: Regras e barra

```
T1 → T2 → T3 → T4 → T5 → T6
```

---

## Task Breakdown

### T1: Regras da barra como funções puras

**What**: `internal fun`s para: barra própria ligada pela Car API (≥ 5); botões da barra em ordem (voltar, home, play/pause só com `playToggleShown`, busca, proporção); ações da action strip nativa (só configurações com barra própria, as de hoje sem ela) e se há map action strip; se a barra se esconde sozinha (`/watch` tocando); limites da página com a barra fixa (termina acima da barra); se um toque vai para a barra ou segue como hoje; com `BigButtonsTest`.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: `playToggleShown`, `videoIdFromUrl`, `webViewBounds`
**Requirement**: BTN-01, BTN-03, BTN-05, BTN-06, BTN-07, BTN-08

**Done when**:

- [x] Um teste por AC listado, nome começando pelo ID (`BTN-01 ...`)
- [x] Gate check passes: `./gradlew testDebugUnitTest --rerun`

**Tests**: unit
**Gate**: quick
**Commit**: `feat: add the rules for the big car buttons`

---

### T2: Barra de botões grandes no rodapé

**What**: View da barra no `FrameLayout` do carro, acima do vídeo, da capa de só áudio e da tela offline: placa preta translúcida de cantos arredondados centralizada no rodapé, botões de 76 × 76 dp com ícone branco de 40 dp, ripple, `contentDescription` com as strings existentes, ordem espelhada em RTL; os cliques chamam as mesmas ações da action strip de hoje (extraídas para funções usadas pelos dois); ícones de play/pause e proporção seguem o estado; recriada com a superfície.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T1
**Reuses**: `navOverlay` (padrão de placa), `button()` do celular (ripple, 48 dp, rótulo), `aspectIcon`, `cycleAspect`, `JS_TOGGLE`
**Requirement**: BTN-01, BTN-02, BTN-04, BTN-09, BTN-10

**Done when**:

- [ ] Walk no DHU: barra no rodapé com os 5 botões; cada um faz o que a ação nativa fazia; Layout Inspector/`uiautomator` mostra cada botão com ≥ 76 dp
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: draw big buttons at the bottom of the car screen`

---

### T3: Action strip nativa só com configurações

**What**: `onGetTemplate` monta as strips pelas regras de T1: com barra própria, action strip só com configurações e sem map action strip; sem ela (Car API < 5), as strips de hoje e nenhuma barra própria.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T2
**Reuses**: regras de T1, ações extraídas em T2
**Requirement**: BTN-03, BTN-06

**Done when**:

- [ ] Walk no DHU: só o botão de configurações do Android Auto aparece e abre as configurações
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: keep only settings in the native car action strip`

---

### T4: Barra some com o vídeo tocando e volta com um toque

**What**: com `/watch` tocando, a barra some em fade (~200 ms) 5 s depois do último toque (`onClick`, `onScroll`, `onScale`); um toque com ela escondida a mostra e segue para a página; pausar ou sair do vídeo a mostra e a mantém.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T3
**Reuses**: regra de T1, `ui` handler, `setPlaying`
**Requirement**: BTN-07, BTN-10

**Done when**:

- [ ] Walk no DHU: vídeo tocando, barra some em ~5 s; um toque a mostra e a página recebe o toque; pausar mostra e mantém
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: hide the big car buttons while a video plays`

---

### T5: Página acima da barra fixa e toque na barra não vai para o vídeo

**What**: `fitToArea` usa a regra de T1: com a barra fixa, a página termina acima dela; no vídeo, a barra flutua. `onClick` entrega à barra o toque dentro dela visível (antes do toque na barra de progresso); fora, segue como hoje. Mudança de área visível reposiciona a barra.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T4
**Reuses**: `fitToArea`, `webViewBounds`, regras de T1
**Requirement**: BTN-05, BTN-08, BTN-10

**Done when**:

- [ ] Walk no DHU: Início rolado até o fim mostra o último item acima da barra; toque num botão sobre a barra de progresso não muda `currentTime`
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: keep the page above the big car buttons`

---

### T6: READMEs descrevem a barra

**What**: seção "Na tela do carro" / "On the car screen" dos dois READMEs: botões grandes no rodapé (voltar, home, play/pause, busca, proporção), configurações no botão do Android Auto, barra some durante o vídeo; screenshot `docs/car-video.png` refeito se a barra aparecer nele.
**Where**: `README.md`, `README.pt-BR.md`
**Depends on**: T5
**Reuses**: texto atual da seção
**Requirement**: BTN-01

**Done when**:

- [ ] Os dois READMEs descrevem a barra e não citam mais os botões no topo e na lateral
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `docs: describe the big car buttons in the readmes`

---

## Checks

**C1** - Com Car API ≥ 5 a barra própria liga e lista voltar, home, play/pause (com `playToggleShown`), busca e proporção, nessa ordem (BTN-01, AC 1)
Proof: run unit | BTN-01 the bar lists back, home, play/pause, search and aspect in order PASSED

**C2** - Cada botão da barra tem área de toque ≥ 76 × 76 dp (BTN-02, AC 2)
Proof: walk - DHU (densidade 160): `uiautomator dump` ou Layout Inspector com cada botão ≥ 76 × 76 px

**C3** - Com barra própria, a action strip nativa tem só configurações e não há map action strip (BTN-03, AC 3)
Proof: run unit | BTN-03 with the big bar the native strip keeps only settings PASSED

**C4** - Voltar sai da tela cheia nativa ou volta uma página (BTN-04, AC 4)
Proof: walk - DHU: num vídeo aberto a partir do Início, voltar retorna ao Início (`location.href`)

**C5** - Home abre a lista "Página inicial" (BTN-04, AC 5)
Proof: walk - DHU: screenshot da lista "Página inicial" depois do toque em home

**C6** - Play/pause alterna a reprodução e troca o ícone (BTN-04, AC 6)
Proof: walk - DHU: DevTools `video.paused` alterna a cada toque; screenshots com o ícone de pause e de play

**C7** - Busca abre a tela de pesquisa (BTN-04, AC 7)
Proof: walk - DHU: screenshot da tela de pesquisa depois do toque em busca

**C8** - Proporção passa para a próxima, mostra o aviso e troca o ícone (BTN-04, AC 8)
Proof: walk - DHU: três toques mostram "Proporção: Preencher/Esticar/Ajustar" e os ícones crop/open_in_full/fit_screen (ordem a partir do valor salvo); pref volta ao valor inicial

**C9** - Toque fora da barra segue para a página como hoje; toque dentro dela visível vai para o botão (BTN-05, AC 9)
Proof: run unit | BTN-05 a tap goes to the bar only inside the shown bar PASSED

**C10** - Com Car API < 5 não há barra própria e as strips nativas são as de hoje (BTN-06, AC 10)
Proof: run unit | BTN-06 below car api 5 the native strips stay as they were PASSED

**C11** - A barra se esconde sozinha só em `/watch` tocando; fora disso fica visível (BTN-07, story 2 AC 1 e 3)
Proof: run unit | BTN-07 the bar hides by itself only while a watch page plays PASSED

**C12** - Vídeo tocando: a barra some ~5 s depois do último toque, e um toque a mostra e chega à página (BTN-07, story 2 AC 1 e 2)
Proof: walk - DHU: screenshots a 2 s e a 6 s do último toque (com e sem barra); um toque a mostra e os controles do YouTube aparecem

**C13** - Com a barra fixa a página termina acima dela (BTN-08, story 2 AC 4)
Proof: run unit | BTN-08 with the bar fixed the page ends above it PASSED

**C14** - A barra fica por cima do vídeo, da capa de só áudio e da tela offline (BTN-09, story 2 AC 5)
Proof: walk - DHU: screenshots da barra sobre a capa de só áudio (GPS falso ~11 m/s) e sobre a tela offline (rede do celular cortada)

**C15** - Pausar com a barra escondida a mostra; área visível nova a reposiciona; superfície nova a recria com o estado atual (BTN-10, Edge Cases)
Proof: walk - DHU: pausa pelo volante/card de mídia com a barra escondida mostra a barra; com o card de mídia ao lado a barra fica dentro da página; reconectar o DHU mostra o ícone de play/pause e de proporção certos

---

## Handoff

**Settled mid-build:** none
**Dispatched:** none
**Walked:** none

---

## Phase Execution Map

```
Phase 1:  T1 → T2 → T3 → T4 → T5 → T6
```
