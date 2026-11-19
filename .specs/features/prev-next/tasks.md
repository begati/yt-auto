# Previous / Next Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: inline (regra em `internal fun` pura testada na JVM; sinal da página pelo `yta` do `PAGE_JS`; ações na map action strip do `NavigationTemplate`)
**Status**: Approved
**Approved**: 2026-11-19 - user: "sim"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

---

## Test Coverage Matrix

> Generated from codebase, project guidelines, and spec - confirm before Execute. Guidelines found: `app/build.gradle.kts` (`testLogging`), specs anteriores ("só JVM", sem Robolectric).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Regras puras (`internal fun` em `YoutubeCarAppService.kt`) | unit | 1:1 com os ACs que decidem algo (quais botões e em que ordem, o que o anterior faz) | `app/src/test/java/dev/begati/youtubeauto/PrevNextTest.kt` | `./gradlew testDebugUnitTest --rerun` |
| JS injetado, template | none (walk) | Walk no DHU + DevTools | - | `./gradlew assembleDebug` |

## Gate Check Commands

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tarefas com testes unitários | `./gradlew testDebugUnitTest --rerun` |
| Build | Tarefas de wiring | `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun` |

---

## Execution Plan

### Phase 1: Anterior e próximo

```
T1 → T2 → T3
```

---

## Task Breakdown

### T1: Regras da barra lateral e do anterior

**What**: `internal fun mapStrip(onWatch: Boolean, hasNext: Boolean): List<String>` - `/watch`: "previous", "next" (só com próximo), "aspect", "settings"; fora: "aspect", "settings". `internal fun previousRestarts(hasPrevious: Boolean) = !hasPrevious`; com `PrevNextTest`.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: `videoIdFromUrl`
**Requirement**: PN-01, PN-02, PN-04, PN-05, PN-06

**Done when**:

- [x] Um teste por AC listado, nome começando pelo ID
- [x] Gate check passes: `./gradlew testDebugUnitTest --rerun`

**Tests**: unit
**Gate**: quick
**Commit**: `feat: add the rules for the car previous and next buttons`

---

### T2: A página avisa se há anterior e próximo

**What**: no poll do `PAGE_JS`, ler os dois `.player-middle-controls-prev-next-button` (anterior, próximo; habilitado = sem `icon-disable`) e chamar `yta.neighbors(prev, next)` quando mudam; o Kotlin guarda e chama `invalidate()` quando muda.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T1
**Reuses**: poll de 500 ms do `PAGE_JS`
**Requirement**: PN-02, PN-07

**Done when**:

- [x] Walk DHU + DevTools: vídeo de mix reporta (true, true); vídeo solto (false, true)
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: report whether the car video has a previous and a next`

---

### T3: Botões anterior e próximo na barra lateral

**What**: a map action strip monta as ações por `mapStrip`; próximo chama `JS_NEXT`; anterior chama `previousVideo()` do player com vídeo anterior e `JS_RESTART` sem; ícones `ic_skip_previous`, `ic_skip_next`.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T2
**Reuses**: `JS_NEXT`, `JS_RESTART`, ações atuais de proporção e configurações
**Requirement**: PN-01, PN-03, PN-04, PN-05, PN-06

**Done when**:

- [x] Walk DHU: barra com anterior/próximo no vídeo, sem eles no Início; próximo troca de vídeo; anterior no vídeo solto volta a 0
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: add previous and next buttons to the car video`

---

## Checks

**C1** - Em `/watch` a barra lateral é anterior, próximo, proporção, configurações (PN-01, AC 1)
Proof: run unit | PN-01 on a video the side strip is previous, next, aspect, settings PASSED

**C2** - Sem próximo, o próximo some (PN-02, AC 2)
Proof: run unit | PN-02 without a next video the next button is gone PASSED

**C3** - Próximo abre o próximo vídeo (PN-03, AC 3)
Proof: walk - DHU + DevTools: toque em próximo num mix muda o `v=` da URL

**C4** - Anterior com vídeo anterior abre o anterior (PN-04, AC 4)
Proof: run unit | PN-04 previous opens the previous video when there is one PASSED

**C5** - Anterior sem vídeo anterior volta ao início (PN-05, AC 5)
Proof: run unit | PN-05 previous restarts the video when there is none PASSED

**C6** - Fora de vídeo, só proporção e configurações (PN-06, AC 6)
Proof: run unit | PN-06 off a video the side strip is aspect and settings PASSED

**C7** - A barra se atualiza quando o próximo aparece ou some (PN-07, AC 7)
Proof: walk - DHU + DevTools: página reporta `neighbors` ao trocar de vídeo; screenshots da barra num mix e no Início

---

## Handoff

**Settled mid-build:** none
**Dispatched:** none
**Walked:** 2026-11-19, orchestrator, DHU 800x480 + phone over USB, debug build installed with data kept. Reach: YT Auto on the DHU, a feed or search video opened by a tap (or DevTools `location.href`), a tap on the video to bring up the host's strips. T2: DevTools `window.__ytaNb` reads `false,true` on a lone video (VkSrmUJgKhQ) and on the first video of a mix (JvMXVHVr72A&list=RD9bZkp7q19f0); `true,true` after next opened a second video. C1/C7: on a video the side strip shows previous, next, aspect, settings (s1h). C3: next on 7Ry0YV4rYuI opened UAdlZQ3Ggeg (`v=` changed). C4: there, `__ytaNb` `true,true`; at 40 s, previous went back to 7Ry0YV4rYuI. C5: on 7Ry0YV4rYuI (`false,true`) at 40 s, previous left `currentTime` at 1 s, same video. C2: a feed video with no next showed previous, aspect, settings only (s2h). C6/C7: the native back from a video to Início rebuilt the side strip to aspect and settings only (s5h).

---

## Phase Execution Map

```
Phase 1:  T1 → T2 → T3
```
