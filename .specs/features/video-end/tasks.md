# Video End Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: inline (mesmo padrão das features anteriores: regra em `internal fun` pura testada na JVM, sinal da página pelo `yta` do `PAGE_JS`, View nativa no `FrameLayout` do carro, CSS no estilo injetado)
**Status**: Approved
**Approved**: 2026-11-19 - user: "sim"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

---

## Test Coverage Matrix

> Generated from codebase, project guidelines, and spec - confirm before Execute. Guidelines found: `app/build.gradle.kts` (`testLogging`), specs anteriores ("só JVM", sem Robolectric).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Regra pura (`internal fun` em `YoutubeCarAppService.kt`) | unit | 1:1 com os ACs que decidem quando a contagem começa (END-01..03) | `app/src/test/java/dev/begati/youtubeauto/VideoEndTest.kt` | `./gradlew testDebugUnitTest --rerun` |
| JS injetado, Views, CSS | none (walk) | Walk no DHU HD + DevTools | - | `./gradlew assembleDebug` |
| Strings | unit (existente) | `I18nTest` exige as 7 traduções | `I18nTest.kt` | `./gradlew testDebugUnitTest --rerun` |

## Gate Check Commands

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tarefas com testes unitários | `./gradlew testDebugUnitTest --rerun` |
| Build | Tarefas de wiring/View/CSS | `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun` |

---

## Execution Plan

### Phase 1: Contagem e botão de tela cheia

```
T1 → T2 → T3 → T4
```

---

## Task Breakdown

### T1: Regra de quando a contagem começa

**What**: `internal fun endCountdownDue(ended: Boolean, autonav: Boolean?, endedForMs: Long): Boolean` - terminado com reprodução automática desligada: já; ligada ou desconhecida: a partir de 10 s terminado; não terminado: nunca; com `VideoEndTest`.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: -
**Requirement**: END-01, END-02, END-03

**Done when**:

- [x] Um teste por AC listado, nome começando pelo ID
- [x] Gate check passes: `./gradlew testDebugUnitTest --rerun`

**Tests**: unit
**Gate**: quick
**Commit**: `feat: add the rule for the end of video countdown`

---

### T2: A página avisa o fim do vídeo e a reprodução automática

**What**: no poll do `PAGE_JS`, guardar o último estado conhecido da reprodução automática (do botão do YouTube quando ele está no DOM) e chamar `yta.ended(ended, autonav)` quando o estado de fim muda e a cada poll enquanto terminado; o Kotlin guarda desde quando está terminado.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T1
**Reuses**: poll de 500 ms do `PAGE_JS`, `PLAYER_ID`
**Requirement**: END-01, END-02, END-03

**Done when**:

- [x] Walk DHU HD + DevTools: vídeo pulado para o fim chama `ended(true, …)`; o valor de reprodução automática bate com o rótulo do botão
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: report the end of the car video and the autoplay state`

---

### T3: Painel de contagem que leva à página inicial

**What**: painel central (família do painel de arrastar) com "Voltando para <página inicial>" e barra que esvazia em 5 s, por cima do vídeo e da capa de só áudio; no fim abre `choice(HOME_PAGE)`; um toque abre na hora; vídeo voltando a tocar ou saindo de `/watch` esconde sem abrir; string `going_home` nos 7 idiomas.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt` (+ `res/values*/strings.xml`)
**Depends on**: T2
**Reuses**: `dragIndicator` (padrão de painel), `homeLabel`, `choice(HOME_PAGE)`, `onClick`
**Requirement**: END-04, END-05, END-06, END-07, END-08

**Done when**:

- [ ] Walk DHU HD: contagem conta 5 s e abre a página inicial; toque abre na hora; play esconde
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: go back to the start page when a car video ends`

---

### T4: Esconder o botão de tela cheia do YouTube

**What**: no estilo injetado, `html.yta-watch .ytwPlayerBottomControlsFullscreenButtonWrapper` e tudo dentro dele com `visibility:hidden`.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T3
**Reuses**: regra dos botões do meio (spec player-controls)
**Requirement**: END-09, END-10

**Done when**:

- [ ] Walk DHU HD + DevTools: botão e filhos `hidden` tocando e terminado; sem `yta-watch`, visível
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `fix: hide YouTube's fullscreen button on the car video`

---

## Checks

**C1** - Terminado com reprodução automática desligada: a contagem começa já (END-01, AC 1)
Proof: run unit | END-01 autoplay off starts the countdown at the end PASSED

**C2** - Ligada ou desconhecida: começa só com 10 s terminado (END-02, AC 2)
Proof: run unit | END-02 autoplay on or unknown waits 10 s at the end PASSED

**C3** - Não terminado (outro vídeo tocando): nunca começa (END-03, AC 3)
Proof: run unit | END-03 a video that is not ended never starts the countdown PASSED

**C4** - O painel mostra "Voltando para <página>" e a barra esvazia em 5 s (END-04, AC 4)
Proof: walk - DHU HD: screenshots no início e ~2,5 s depois (barra cheia, depois pela metade) com o texto

**C5** - No fim da contagem abre a página inicial escolhida (END-05, AC 5)
Proof: walk - DHU HD + DevTools: `location.href` vira a página inicial ~5 s depois do painel aparecer

**C6** - Um toque durante a contagem abre a página na hora (END-06, AC 6)
Proof: walk - DHU HD + DevTools: toque no painel; `location.href` muda em menos de 1 s

**C7** - Play ou sair do vídeo esconde a contagem sem abrir a página (END-07, AC 7)
Proof: walk - DHU HD: play nativo durante a contagem esconde o painel e o vídeo segue em `/watch` depois de 6 s

**C8** - Página inicial Inscrições: texto e destino dela; por cima da capa de só áudio (END-08, Edge Cases)
Proof: walk - DHU HD: com "Página inicial" = Inscrições, o painel diz "Voltando para Inscrições" e abre `/feed/subscriptions`; opção restaurada

**C9** - Tela cheia: botão de tela cheia do YouTube e filhos escondidos, tocando e terminado (END-09, P2 AC 1)
Proof: walk - DHU HD + DevTools: `getComputedStyle` do wrapper e filhos `hidden` em `playing-mode` e `ended-mode`

**C10** - Sem tela cheia o botão aparece (END-10, P2 AC 2)
Proof: walk - DHU HD + DevTools: com `yta-watch` removida, o wrapper fica `visible`; classe recolocada

---

## Handoff

**Settled mid-build:** none
**Dispatched:** none
**Walked:** none

---

## Phase Execution Map

```
Phase 1:  T1 → T2 → T3 → T4
```
