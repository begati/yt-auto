# Critique Fixes Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: inline (regras em `internal` puras testadas na JVM; Views nativas no `FrameLayout` do carro; templates do Android Auto)
**Status**: Approved
**Approved**: 2026-11-19 - user: "sim"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

---

## Test Coverage Matrix

> Generated from codebase, project guidelines, and spec - confirm before Execute. Guidelines found: `app/build.gradle.kts` (`testLogging`), specs anteriores ("só JVM", sem Robolectric).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Regras puras (`mapStrip`, `GESTURE_ROWS`) | unit | 1:1 com os ACs que decidem algo (botões da lateral, linhas e ordem dos gestos) | `app/src/test/java/dev/begati/youtubeauto/*Test.kt` | `./gradlew testDebugUnitTest --rerun` |
| Views, templates | none (walk) | Walk no DHU | - | `./gradlew assembleDebug` |
| Strings | unit (existente) | `I18nTest` exige os 11 idiomas | `I18nTest.kt` | `./gradlew testDebugUnitTest --rerun` |
| PRODUCT.md | none | - | - | build gate only |

## Gate Check Commands

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tarefas com testes unitários | `./gradlew testDebugUnitTest --rerun` |
| Build | Tarefas de View/docs | `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun` |

---

## Execution Plan

### Phase 1: Correções da crítica

```
T1 → T2 → T3 → T4 → T5
```

---

## Task Breakdown

### T1: Proporção só no vídeo

**What**: `mapStrip` fora de `/watch` devolve só "settings"; `PN-06` e o AC 6 da spec prev-next passam a dizer isso.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: `mapStrip`, `PrevNextTest`
**Requirement**: FIX-04

**Done when**:

- [x] `FIX-04` testado (fora do vídeo só configurações; no vídeo os quatro)
- [x] Gate check passes: `./gradlew testDebugUnitTest --rerun`

**Tests**: unit
**Gate**: quick
**Commit**: `fix: show the aspect button only on the car video`

---

### T2: Contagem com "Toque para ir agora" e no rodapé sobre a capa

**What**: `endIndicator` ganha a linha `going_home_tap` (20 sp, 70% branca) abaixo do título; `startEnd` ancora o painel no rodapé (24 dp) com a capa de só áudio visível, no centro sem ela; string nos 11 idiomas.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt` (+ `res/values*/strings.xml`)
**Depends on**: T1
**Reuses**: `endIndicator`, `cover`
**Requirement**: FIX-01, FIX-02

**Done when**:

- [x] Walk DHU: painel no centro com as duas linhas; com a capa de só áudio, no rodapé
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `fix: tell the countdown's tap and keep it off the audio cover art`

---

### T3: Tela Gestos em lista

**What**: `internal val GESTURE_ROWS` (título, detalhe, ícone) na ordem toque duplo, arrastar, barra; `GesturesScreen` vira `ListTemplate` com essas linhas; 6 strings novas nos 11 idiomas; `gestures_text` sai; com teste da ordem e do conteúdo.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt` (+ `res/values*/strings.xml`)
**Depends on**: T2
**Reuses**: `GesturesScreen`, `SettingsScreen` (padrão de `Row`)
**Requirement**: FIX-03

**Done when**:

- [x] `FIX-03` testado; walk DHU: Configurações > Gestos com três linhas
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: unit
**Gate**: build
**Commit**: `fix: list the car gestures in three short rows`

---

### T4: Cantos e barra iguais nos painéis

**What**: painel de arrastar e da contagem com `cornerRadius` 16 dp; barra do arrastar com 6 dp.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T3
**Reuses**: `dragIndicator`, `endIndicator`
**Requirement**: FIX-05

**Done when**:

- [x] Walk DHU: screenshots do arrastar e da contagem com os mesmos cantos e barra
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `fix: give the car panels the same corners and bar`

---

### T5: PRODUCT.md com os 11 idiomas

**What**: as duas menções a "7 idiomas" passam a listar os 11.
**Where**: `PRODUCT.md`
**Depends on**: T4
**Reuses**: -
**Requirement**: FIX-06

**Done when**:

- [x] `grep` não acha "7 idiomas" no PRODUCT.md
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `docs: list the eleven languages in the product context`

---

## Checks

**C1** - A contagem mostra "Toque para ir agora" abaixo do título (FIX-01, AC 1)
Proof: walk - DHU: screenshot do painel com as duas linhas

**C2** - Com a capa de só áudio o painel fica no rodapé; sem ela, no centro (FIX-02, AC 2-3)
Proof: walk - DHU: screenshots com e sem a capa (GPS de teste)

**C3** - Gestos: três linhas na ordem toque duplo, arrastar, barra, com ícone, título e detalhe (FIX-03, AC 1-2)
Proof: run unit | FIX-03 the gestures screen lists double tap, drag and bar in order PASSED

**C4** - Fora do vídeo a lateral tem só configurações; no vídeo, os quatro (FIX-04)
Proof: run unit | FIX-04 off a video the side strip has settings only PASSED

**C5** - Painéis da contagem e do arrastar com cantos de 16 dp e barra de 6 dp (FIX-05)
Proof: walk - DHU: screenshots dos dois painéis

**C6** - PRODUCT.md lista os 11 idiomas (FIX-06)
Proof: `! grep -q "7 idiomas" PRODUCT.md && grep -q "pl, tr" PRODUCT.md`

---

## Handoff

**Settled mid-build:** user, on the home icon: keep the menu ("Manter o menu (Recomendado)"), then "e se ambos seguir o que foi configurado no celular?" and "Linha nas configurações (Recomendado)" - added to the spec as FIX-07/08 and task T6
**Dispatched:** none
**Walked:** 2026-11-19, orchestrator, DHU 1280x720 + phone over USB, debug build installed with data kept. C1: a video ended by DevTools `currentTime = duration - 1`: the centered panel reads "Voltando para Início" and under it "Toque para ir agora" (w1). C2: phone "Só áudio com o carro em movimento" on, location granted by `pm grant`, a shell test GPS provider moving ~15 m/s, app restarted: the headphones cover shows (w3); a video ended under it shows the panel at the bottom, under the cover's art, title and line (w4); without the cover the panel is centered (w1). The test provider was removed, the mock-location op reset, both location permissions revoked and the option turned off again. C3: car Configurações > Gestos shows three rows - "Toque duplo: ±10 s" / "Metade direita avança, metade esquerda volta", "Arrastar para o lado" / "Avança ou volta: a largura da tela é o vídeo todo", "Barra de progresso" / "Toque no vídeo e depois na barra para ir ao ponto" - each with its icon (x3). C5: the countdown panel draws with 16 dp corners and a 6 dp bar (y1, 1280x720). The drag panel was not walked: the DHU has no swipe command and its drag comes only from a mouse drag by a person; `dragIndicator` sets the same `cornerRadius = dp(16)` and `LayoutParams(dp(240), dp(6))`.

---

## Phase Execution Map

```
Phase 1:  T1 → T2 → T3 → T4 → T5
```
