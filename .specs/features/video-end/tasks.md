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

- [x] Walk DHU HD: contagem conta 5 s e abre a página inicial; toque abre na hora; play esconde
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

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

- [x] Walk DHU HD + DevTools: botão e filhos `hidden` tocando e terminado; sem `yta-watch`, visível
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

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
**Dispatched:** Verifier at HEAD 9ea8524 (diff 4dd7a1f..9ea8524), returned FAIL with 4 findings, all closed in e11bfcb
**Walked:** 2026-11-19, orchestrator, DHU 1280x720 (160 dpi) + phone over USB, debug build installed with data kept. Reach: YT Auto on the DHU, a video opened by DevTools `location.href` (VkSrmUJgKhQ, whose page shows YouTube's autoplay toggle, `aria-pressed="false"`), a tap to show the controls, then DevTools `currentTime = duration - 1`. T2: the page's `__ytaEn` false while playing, true with `getPlayerState() === 0` after the end; `__ytaAn` false, matching the toggle's label "Reprodução automática desativada". C4: screenshots ~2 s and ~4.5 s after the end show the centered panel "Voltando para Início" with the bar nearly full, then under half (k1, k2). C5: ~6 s after the panel, the car page's `location.href` is `https://m.youtube.com/`. C6: a tap at the panel 2.5 s into the countdown, `location.href` is `https://m.youtube.com/` 0.8 s later. C7: the native play during the countdown hides the panel and restarts the video; 6 s later still `/watch`, playing (k4). C8: phone Configurações > Página inicial = Inscrições: the panel reads "Voltando para Inscrições" (k5) and the car opens `/feed/subscriptions`; the option was set back to Início. The panel over the audio cover was not walked (needs mock GPS); it is added after the cover in the frame. C9: DevTools on `.ytwPlayerBottomControlsFullscreenButtonWrapper` and its 8 descendants: all `hidden` in `playing-mode` (controls shown by a tap) and in `ended-mode`; the corner icon is gone in the ended screenshot (l2). C10: with `html.yta-watch` removed by DevTools, the wrapper reads `visible`; the class was put back. Round 2 (after the Verifier, fixes in e11bfcb): back during the countdown - Inscrições, then the video, end, the panel showing at ~3 s (m2), the native back at ~4.6 s: 6 s later the car page is `/feed/subscriptions`, the page gone back to, not Início. Settings over the video during the countdown: 8 s later the car page is still `/watch`; back from settings, the panel counts again (m5) and Início opens. Audio cover: phone "Só áudio com o carro em movimento" on, location granted by `pm grant`, a shell test GPS provider moving ~15 m/s: the car shows the headphones cover; a video ended under it shows the panel "Voltando para Início" over the cover (o2) and Início opens; the provider was removed, the permission revoked (the app's prompt answered "Não permitir") and the option turned off again. RTL not walked: a per-app locale (`cmd locale set-app-locales … ar`) changes the phone's app but not the car screen, which follows the host's language; the bar's RTL pivot is now the track's fixed 240 dp.

---

## Phase Execution Map

```
Phase 1:  T1 → T2 → T3 → T4
```
