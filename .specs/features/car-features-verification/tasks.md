# Car Features Verification Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: none - extração de funções puras, sem decisão de arquitetura
**Status**: In Progress
**Approved**: 2026-11-10 - user: "aprovado, pode seguir"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

---

## Test Coverage Matrix

> Generated from codebase, project guidelines, and spec - confirm before Execute. Guidelines found: none - strong defaults applied. No tests existed; test type and command chosen by the user ("só JVM").

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Lógica pura (parser do feed, configurações, rótulo home, tamanho da capa, ID de vídeo) | unit | 1:1 com os critérios da spec; cada caso de borda listado testado | `app/src/test/java/dev/begati/youtubeauto/*Test.kt` | `./gradlew testDebugUnitTest` |
| Telas do carro, WebView/JS, MediaSession, providers | none | fora do escopo da spec (só JVM) - build gate | - | `./gradlew assembleDebug` |
| Config Gradle | none | build gate | - | `./gradlew assembleDebug` |

## Gate Check Commands

> Generated from codebase - confirm before Execute.

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tarefas com testes unitários | `./gradlew testDebugUnitTest` |
| Full | Sem testes de integração/e2e neste projeto | `./gradlew testDebugUnitTest` |
| Build | Tarefa de config e fim de fase | `./gradlew assembleDebug testDebugUnitTest` |

---

## Execution Plan

### Phase 1: Foundation

```
T1
```

### Phase 2: Testable logic

```
T1 → T2 → T3 → T4 → T5 → T6
```

---

## Task Breakdown

### T1: Dependências de teste e log por teste

**What**: Adicionar `testImplementation` de `junit:junit:4.13.2` e `org.json:json:20240303`, e `testLogging` que imprime cada teste aprovado
**Where**: `app/build.gradle.kts`
**Depends on**: None
**Reuses**: bloco `dependencies` existente
**Requirement**: FEED-01

**Done when**:

- [x] `./gradlew assembleDebug testDebugUnitTest` verde
- [x] APK sem as dependências de teste (só `testImplementation`)

**Tests**: none
**Gate**: build

**Commit**: `test: add JVM unit test dependencies`

---

### T2: Parser do feed extraído e testado

**What**: Extrair `parseFeed(html): List<FeedVideo>` (id, título, canal) de `fetchFeed`, que passa a só baixar e converter para `MediaItem`; testes FEED-01..09 em `FeedParserTest.kt`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T1
**Reuses**: `ytText`, `collectVideos`
**Requirement**: FEED-01, FEED-02, FEED-03, FEED-04, FEED-05, FEED-06, FEED-07, FEED-08, FEED-09

**Done when**:

- [ ] Gate check passes: `./gradlew testDebugUnitTest`
- [ ] Test count: 9 tests pass

**Tests**: unit
**Gate**: quick

**Commit**: `test: cover feed parsing for history and subscriptions`

---

### T3: Configurações sobre SharedPreferences e testadas

**What**: `choice`/`setChoice`/`flag`/`setFlag` passam a receber `SharedPreferences` (chamadores usam `prefs(ctx)`); testes SET-01..08 em `SettingsTest.kt` com `SharedPreferences` em memória
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T2
**Reuses**: `CHOICES`, `FLAGS`, `QUALITY`, `HOME_PAGE`, `TEXT_ZOOM`
**Requirement**: SET-01, SET-02, SET-03, SET-04, SET-05, SET-06, SET-07, SET-08

**Done when**:

- [ ] Gate check passes: `./gradlew testDebugUnitTest`
- [ ] Test count: 17 tests pass

**Tests**: unit
**Gate**: quick

**Commit**: `test: cover settings defaults and validation`

---

### T4: Rótulo do botão home extraído e testado

**What**: Extrair `homeLabel(prefs)` usada pelo botão home; testes HOME-01..03 em `SettingsTest.kt`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T3
**Reuses**: `HOME_PAGE`, `choice`
**Requirement**: HOME-01, HOME-02, HOME-03

**Done when**:

- [ ] Gate check passes: `./gradlew testDebugUnitTest`
- [ ] Test count: 20 tests pass

**Tests**: unit
**Gate**: quick

**Commit**: `test: cover home button label`

---

### T5: Tamanho da capa extraído e testado

**What**: Extrair `artSize(w, h): Pair<Int, Int>` usada por `setMeta`; testes ART-01..02 em `MediaHelpersTest.kt`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T4
**Reuses**: regra de escala de `setMeta`
**Requirement**: ART-01, ART-02

**Done when**:

- [ ] Gate check passes: `./gradlew testDebugUnitTest`
- [ ] Test count: 22 tests pass

**Tests**: unit
**Gate**: quick

**Commit**: `test: cover artwork downscale`

---

### T6: Validação do ID de vídeo extraída e testada

**What**: Extrair `isVideoId(s)` usada por `ThumbProvider.openFile`; testes THUMB-01..02 em `MediaHelpersTest.kt`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T5
**Reuses**: `VIDEO_ID`
**Requirement**: THUMB-01, THUMB-02

**Done when**:

- [ ] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [ ] Test count: 24 tests pass

**Tests**: unit
**Gate**: build

**Commit**: `test: cover thumbnail id validation`

---

## Checks

**C1** - O parser devolve id, título e canal de cada vídeo na ordem do JSON (FEED-01, AC 1)
Proof: run unit | FEED-01 returns id title and channel in page order PASSED

**C2** - Um videoId repetido aparece uma vez só, na primeira posição (FEED-02, AC 2)
Proof: run unit | FEED-02 keeps only the first occurrence of a videoId PASSED

**C3** - Com 51+ vídeos, o parser devolve exatamente os 50 primeiros (FEED-03, AC 3)
Proof: run unit | FEED-03 caps the list at the first 50 videos PASSED

**C4** - Título lido de simpleText, de runs concatenados sem separador e de content (FEED-04, AC 4)
Proof: run unit | FEED-04 reads simpleText runs and content titles PASSED

**C5** - lockupViewModel de vídeo usa contentId e o título do lockup (FEED-05, AC 5)
Proof: run unit | FEED-05 reads lockup videos by contentId and lockup title PASSED

**C6** - lockupViewModel que não é vídeo é ignorado (FEED-06, AC 6)
Proof: run unit | FEED-06 ignores lockups that are not videos PASSED

**C7** - HTML sem `var ytInitialData = ` devolve lista vazia (FEED-07, AC 7)
Proof: run unit | FEED-07 returns empty list without ytInitialData PASSED

**C8** - videoId sem título é ignorado (FEED-08, AC 8)
Proof: run unit | FEED-08 skips videoIds without a title PASSED

**C9** - Canal vem de shortBylineText, senão ownerText, senão vazio (FEED-09, AC 9)
Proof: run unit | FEED-09 channel falls back from shortBylineText to ownerText to empty PASSED

**C10** - Sem nada salvo: qualidade `medium`, home `https://m.youtube.com`, texto `130` (SET-01, AC 1)
Proof: run unit | SET-01 defaults are 360p home page and 130 percent text PASSED

**C11** - Sem nada salvo: skipAds, fullscreen e background são true (SET-02, AC 2)
Proof: run unit | SET-02 flags default to on PASSED

**C12** - Valor salvo fora das opções (`tiny`) lê como o padrão (SET-03, AC 3)
Proof: run unit | SET-03 invalid stored choice reads as default PASSED

**C13** - Salvar `highres` mantém o valor anterior (SET-04, AC 4)
Proof: run unit | SET-04 saving an invalid choice keeps the previous value PASSED

**C14** - Valor válido salvo é lido de volta (SET-05, AC 5)
Proof: run unit | SET-05 a saved valid choice is read back PASSED

**C15** - Flag salva false lê false (SET-06, AC 6)
Proof: run unit | SET-06 a flag saved off reads off PASSED

**C16** - Qualidades são exatamente Automática, 240p, 360p, 480p, 720p, 1080p, nessa ordem (SET-07, AC 7)
Proof: run unit | SET-07 quality options are exactly the six labels in order PASSED

**C17** - Páginas Início, Inscrições, Histórico e tamanhos 100%, 115%, 130%, 150%, nessa ordem (SET-08, AC 8)
Proof: run unit | SET-08 home page and text size options in order PASSED

**C18** - Home em feed/subscriptions rotula o botão `Inscrições` (HOME-01, AC 1)
Proof: run unit | HOME-01 subscriptions home page labels the button PASSED

**C19** - Home em feed/history rotula o botão `Histórico` (HOME-02, AC 2)
Proof: run unit | HOME-02 history home page labels the button PASSED

**C20** - Sem home salva o botão é `Início` (HOME-03, AC 3)
Proof: run unit | HOME-03 default home page labels the button PASSED

**C21** - Maior lado acima de 320 vira 320, proporção mantida (321x321 vira 320x320) (ART-01, AC 1)
Proof: run unit | ART-01 shrinks the longest side to 320 keeping proportion PASSED

**C22** - Maior lado de 320 ou menos mantém o tamanho (ART-02, AC 2)
Proof: run unit | ART-02 keeps art of 320 or less untouched PASSED

**C23** - Só IDs de 11 caracteres em A-Z a-z 0-9 _ - são aceitos (THUMB-01, AC 1)
Proof: run unit | THUMB-01 accepts 11 char ids from the allowed alphabet PASSED

**C24** - IDs com `/`, `.` ou tamanho diferente de 11 são recusados (THUMB-02, AC 2)
Proof: run unit | THUMB-02 rejects ids with slash dot or wrong length PASSED

---

## Handoff

**Settled mid-build:** none
**Dispatched:** none
**Walked:** none - escopo só JVM, sem tela testada

---

## Phase Execution Map

```
Phase 1 → Phase 2

Phase 1:  T1
Phase 2:  T2 → T3 → T4 → T5 → T6
```
