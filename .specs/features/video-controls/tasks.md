# Video Controls Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: inline (sem design.md: mesmo padrão das features anteriores - regras em funções `internal` puras testadas na JVM, Views nativas no `FrameLayout` do carro, geometria do player lida da página por `evaluateJavascript`)
**Status**: In Progress
**Approved**: 2026-11-18 - user: "sim"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

---

## Test Coverage Matrix

> Generated from codebase, project guidelines, and spec - confirm before Execute. Guidelines found: `app/build.gradle.kts` (`testLogging` passed/failed/skipped), specs anteriores ("só JVM", sem Robolectric).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Regras puras (`internal fun` em `YoutubeCarAppService.kt`) | unit | 1:1 com os ACs que decidem algo (lado, soma, limites, fração, eixo, rótulo, fallback) | `app/src/test/java/dev/begati/youtubeauto/*Test.kt` | `./gradlew testDebugUnitTest --rerun` |
| Callbacks do `SurfaceCallback`, Views, JS injetado | none (walk) | Walk no DHU + DevTools (`currentTime`) | - | `./gradlew assembleDebug` |
| Strings | unit (existente) | `I18nTest` já exige as 7 traduções de cada string | `I18nTest.kt` | `./gradlew testDebugUnitTest --rerun` |

## Gate Check Commands

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tarefas com testes unitários | `./gradlew testDebugUnitTest --rerun` |
| Build | Tarefas de wiring/View | `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun` |

---

## Execution Plan

### Phase 1: Regras e gestos no carro

```
T1 → T2 → T3 → T4
```

---

## Task Breakdown

### T1: Regras dos gestos como funções puras

**What**: `internal fun`s para: lado/salto do duplo toque dentro do player (±10 s ou nada), soma de duplos toques seguidos (janela 1 s, mesmo lado), destino do toque na barra (fração × duração, nada sem duração), eixo do arrasto, destino do arrasto (início + dx/largura × duração, limitado), rótulo "m:ss / m:ss" e diferença com sinal (reusa `clock`), conversão px da página → px da superfície; com `VideoControlsTest`.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: `clock()`, `webViewBounds()`
**Requirement**: CTRL-01, CTRL-02, CTRL-03, CTRL-05, CTRL-07, CTRL-08, CTRL-09, CTRL-10, CTRL-11, CTRL-12, CTRL-13, CTRL-14, CTRL-16, CTRL-17

**Done when**:

- [x] Um teste por AC listado, nome começando pelo ID (`CTRL-01 ...`)
- [x] Gate check passes: `./gradlew testDebugUnitTest --rerun`

**Tests**: unit
**Gate**: quick
**Commit**: `feat: add the rules for car video gestures`

---

### T2: Duplo toque pula 10 s com bolha na lateral

**What**: `onScale` com fator 2,0 em `/watch` lê a geometria do player na página, decide com T1, chama `jsSeekBy` e mostra a bolha (seta `ic_rewind`/`ic_fast_forward` + "%d s", meia-lua escura no lado tocado, some em 0,6 s); string `seek_seconds` nos 7 idiomas.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt` (+ `res/values*/strings.xml`)
**Depends on**: T1
**Reuses**: `jsSeekBy`, `audioCover` (padrão de View), `fitToArea`
**Requirement**: CTRL-01, CTRL-02, CTRL-04, CTRL-05, CTRL-06, CTRL-07, CTRL-08

**Done when**:

- [x] Walk no DHU: dois toques à direita sobem `currentTime` ~10 s e mostram a bolha à direita; à esquerda, o contrário; dois duplos toques seguidos mostram "20 s"
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: double tap the car video to skip 10 seconds`

---

### T3: Toque na barra de progresso pula para o ponto

**What**: `onClick` em `/watch` lê a barra (só com os controles visíveis) e a duração; dentro da barra, pula para o ponto e não repassa o toque; fora dela ou sem duração, repassa como hoje.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T2
**Reuses**: geometria lida em T2, regra de T1
**Requirement**: CTRL-09, CTRL-10, CTRL-11

**Done when**:

- [ ] Walk no DHU: controles visíveis, toque no meio da barra leva `currentTime` a ~50% da duração; toque fora da barra continua mostrando/escondendo os controles
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: tap the car video's progress bar to jump there`

---

### T4: Arrasto horizontal avança ou volta com indicador

**What**: `onScroll` em `/watch` com duração: o primeiro evento decide o eixo; horizontal acumula dx, mostra no centro "m:ss / m:ss" e "+m:ss", e 300 ms sem eventos pula para o destino (mesmo vídeo) e esconde; vertical ou fora de `/watch` rola como hoje.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T3
**Reuses**: regras de T1, padrão de View de T2
**Requirement**: CTRL-12, CTRL-13, CTRL-14, CTRL-15, CTRL-16, CTRL-17

**Done when**:

- [ ] Walk no DHU (arrasto com o mouse na janela do DHU): indicador com destino e diferença; ao soltar, `currentTime` no destino; arrasto vertical no Início rola a lista
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: drag across the car video to seek`

---

## Checks

**C1** - Duplo toque na metade direita do player avança 10 s (CTRL-01, AC 1)
Proof: run unit | CTRL-01 double tap on the right half skips forward 10 s PASSED

**C2** - Duplo toque na metade esquerda volta 10 s (CTRL-02, AC 2)
Proof: run unit | CTRL-02 double tap on the left half skips back 10 s PASSED

**C3** - O salto para em 0 e na duração (CTRL-03, AC 3)
Proof: walk - DHU: duplo toque à esquerda nos primeiros 5 s deixa `currentTime` em 0; perto do fim, à direita, deixa na duração (o `jsSeekBy` existente limita)

**C4** - A bolha aparece no lado tocado com a seta do sentido e "10 s" (CTRL-04, AC 4)
Proof: walk - DHU: screenshots da bolha à direita (`ic_fast_forward`, "10 s") e à esquerda (`ic_rewind`, "10 s")

**C5** - Duplo toque no mesmo lado em até 1 s soma 10 s ao total mostrado; outro lado ou depois de 1 s recomeça (CTRL-05, AC 5)
Proof: run unit | CTRL-05 repeated double taps on the same side within 1 s add up PASSED

**C6** - A bolha some 0,6 s depois do último duplo toque (CTRL-06, AC 6)
Proof: walk - DHU: screenshot ~1 s depois do duplo toque sem a bolha

**C7** - Fator de escala diferente de 2,0 não pula (CTRL-07, AC 7)
Proof: run unit | CTRL-07 a pinch scale factor does not skip PASSED

**C8** - Fora do player ou fora de `/watch` o duplo toque não pula (CTRL-08, AC 8)
Proof: run unit | CTRL-08 double tap outside the player or off watch does nothing PASSED

**C9** - Toque na barra visível pula para a fração tocada da duração (CTRL-09, AC 9)
Proof: run unit | CTRL-09 a tap on the visible bar seeks to its fraction of the duration PASSED

**C10** - Toque fora da barra, ou com a barra escondida, segue para a página (CTRL-10, AC 10)
Proof: run unit | CTRL-10 a tap off the bar or with the bar hidden goes to the page PASSED

**C11** - Sem duração conhecida o toque na barra segue para a página (CTRL-11, AC 11)
Proof: run unit | CTRL-11 a tap on the bar without a duration goes to the page PASSED

**C12** - Arrasto que começa mais horizontal que vertical, em `/watch` com duração, vira avanço (CTRL-12, AC 12)
Proof: run unit | CTRL-12 a mostly horizontal drag on watch seeks PASSED

**C13** - O indicador mostra "destino / duração" e a diferença com sinal, h:mm:ss a partir de 1 h (CTRL-13, AC 13)
Proof: run unit | CTRL-13 the drag label shows target, duration and signed difference PASSED

**C14** - Destino = início + dx/largura × duração, limitado a 0..duração, direita avança (CTRL-14, AC 14)
Proof: run unit | CTRL-14 the drag target follows the finger across the player width PASSED

**C15** - 300 ms sem `onScroll` pula para o destino e esconde o indicador (CTRL-15, AC 15)
Proof: walk - DHU: arrasto com o mouse; screenshot com o indicador durante e sem ele depois; DevTools `currentTime` igual ao destino mostrado (±1 s)

**C16** - Arrasto que começa mais vertical rola a página (CTRL-16, AC 16)
Proof: run unit | CTRL-16 a mostly vertical drag scrolls the page PASSED

**C17** - Fora de `/watch` ou sem duração o arrasto rola a página (CTRL-17, AC 17)
Proof: run unit | CTRL-17 a drag off watch or without a duration scrolls the page PASSED

---

## Handoff

**Settled mid-build:** none
**Dispatched:** none
**Walked:** none
