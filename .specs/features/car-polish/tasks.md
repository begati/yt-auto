# Car Polish Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: none - design inline nas tarefas. Botão: uma função pura decide se Tocar/Pausar aparece (URL `/watch` ou vídeo do player principal tocando); o template é invalidado quando isso muda. Ícone: ícone adaptativo em vetor. Tema: o `AppTheme` deixa de ser DayNight (escuro sempre, no celular) e a `Presentation` do carro recebe esse tema explicitamente; a WebView passa `prefers-color-scheme: dark` e o YouTube no padrão da conta fica escuro, sem recarregar nada na troca dia/noite e sem mexer no cookie `PREF`
**Status**: Approved
**Approved**: 2026-11-11 - user: "vou mudar de ideia, mantenha tema tanto do app quanto no carro sempre em dark. pode ver os demais."
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

Medido antes do plano (DevTools da WebView, 2026-11-11): com o celular no claro (`cmd uimode night no`), a página do celular passa a `prefers-color-scheme: light` e a do carro segue `dark`. Remover o atributo `darker-dark-theme` do `<html>` não muda o visual da página: quem decide é o `prefers-color-scheme`, que a WebView tira do tema do contexto (`isLightTheme`, targetSdk 35).

---

## Test Coverage Matrix

> Generated from codebase and earlier features' decision ("só JVM", sem Robolectric). Guidelines found: none - strong defaults applied; `app/build.gradle.kts` (JUnit 4, `testLogging` uma linha por teste).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Lógica pura (quando o botão Tocar/Pausar aparece) | unit | 1:1 com PLAYBTN-01/02/03/05; `/watch`, `/shorts`, Início, busca, Inscrições, tocando e pausado | `app/src/test/java/dev/begati/youtubeauto/CarPolishTest.kt` | `./gradlew testDebugUnitTest` |
| Template do carro, `PAGE_JS`, temas, recursos de ícone | none (walk) | walk no DHU + celular, com DevTools da WebView para ler estado | - | `./gradlew assembleDebug` |

## Gate Check Commands

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tarefas com teste unitário | `./gradlew testDebugUnitTest` |
| Build | Tarefas sem teste (walk) e fim de fase | `./gradlew assembleDebug testDebugUnitTest` |

---

## Execution Plan

### Phase 1: Botão Tocar/Pausar

```
T1 → T2 → T3
```

### Phase 2: Ícone

```
T4
```

### Phase 3: Sempre escuro

```
T5 → T6
```

---

## Task Breakdown

### T1: Regra de quando o botão aparece

**What**: `internal fun playToggleShown(url: String, playing: Boolean): Boolean` - verdadeiro em `/watch` (tocando ou pausado) ou com o vídeo tocando em qualquer página; falso no resto
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: `videoIdFromUrl` (decide o que é página de vídeo)
**Requirement**: PLAYBTN-01, PLAYBTN-02, PLAYBTN-03, PLAYBTN-05

**Done when**:

- [x] Gate check passes: `./gradlew testDebugUnitTest`
- [x] Testes `PLAYBTN-01`, `PLAYBTN-02`, `PLAYBTN-03` e `PLAYBTN-05` em `CarPolishTest.kt` passam

**Tests**: unit
**Gate**: quick

**Commit**: `feat: decide when the car shows the play button`

---

### T2: Barra de ações usa a regra

**What**: `onGetTemplate` só adiciona Tocar/Pausar quando `playToggleShown(pageUrl, playing)`; `doUpdateVisitedHistory` chama `invalidate()` quando o resultado muda (sair de `/watch` pausado esconde o botão)
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T1
**Reuses**: `playToggleShown`, `setPlaying`
**Requirement**: PLAYBTN-01, PLAYBTN-02, PLAYBTN-03, PLAYBTN-05

**Done when**:

- [ ] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [ ] Walk: Início sem Tocar; vídeo aberto mostra Pausar/Tocar; voltar ao Início pausado esconde

**Tests**: none
**Gate**: build

**Commit**: `feat: hide the play button when no video is open`

---

### T3: Prévia do feed não conta como tocando

**What**: no `PAGE_JS` do carro, `yta.playing` passa a refletir só o vídeo do player principal (`#movie_player video`, inclusive no miniplayer); prévias do feed não mudam o estado
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T2
**Reuses**: laço de 500 ms e listeners `play`/`pause` do `PAGE_JS`
**Requirement**: PLAYBTN-04

**Done when**:

- [ ] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [ ] Walk: no Início do carro com uma prévia tocando (ou um `<video>` fora do player tocando via DevTools), o botão continua escondido

**Tests**: none
**Gate**: build

**Commit**: `fix: ignore feed previews when reporting car playback`

---

### T4: Ícone adaptativo

**What**: ícone adaptativo em vetor `@mipmap/ic_launcher` - fundo em gradiente ciano → azul-royal, volante branco com triângulo de play no centro - e o manifesto passa a usá-lo
**Where**: `app/src/main/res/mipmap-anydpi/ic_launcher.xml` (com `drawable/ic_launcher_foreground.xml`, `drawable/ic_launcher_background.xml` e `android:icon` no manifesto)
**Depends on**: None
**Reuses**: -
**Requirement**: ICON-01, ICON-02

**Done when**:

- [ ] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [ ] Walk: screenshot do launcher do DHU e da gaveta do celular com o ícone novo ao lado do YouTube e do YT Music

**Tests**: none
**Gate**: build

**Commit**: `feat: give the app its own steering-wheel icon`

---

### T5: Tema do app sempre escuro no celular

**What**: `AppTheme` passa a herdar de `@android:style/Theme.DeviceDefault` (escuro) em vez de `Theme.DeviceDefault.DayNight`; página do YouTube e Configurações do celular ficam escuras com o celular no claro
**Where**: `app/src/main/res/values/themes.xml`
**Depends on**: None
**Reuses**: `AppTheme`
**Requirement**: THEME-02, THEME-03

**Done when**:

- [ ] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [ ] Walk: `cmd uimode night no`; DevTools do celular com `prefers-color-scheme: dark`; screenshots da página e das Configurações escuras

**Tests**: none
**Gate**: build

**Commit**: `feat: keep the phone app dark regardless of the system theme`

---

### T6: Página do carro sempre escura

**What**: a `Presentation` do carro é criada com o `AppTheme` escuro explícito (`Presentation(carContext, display, R.style.AppTheme)`), para a WebView do carro não depender do modo do celular nem do dia/noite da central
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T5
**Reuses**: `AppTheme`
**Requirement**: THEME-01, THEME-04

**Done when**:

- [ ] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [ ] Walk: celular no claro, app reaberto no DHU: DevTools do carro com `prefers-color-scheme: dark`; troca dia/noite com vídeo tocando não recarrega (mesmo `performance.timeOrigin`, `currentTime` maior)

**Tests**: none
**Gate**: build

**Commit**: `feat: keep the car page dark regardless of phone and car mode`

---

## Checks

**C1** - Em `/watch` o botão aparece, tocando ou pausado (PLAYBTN-01, AC 1)
Proof: run unit | PLAYBTN-01 shown on a watch page playing or paused PASSED

**C2** - Fora de `/watch`, com vídeo tocando, o botão aparece (PLAYBTN-02, AC 2)
Proof: run unit | PLAYBTN-02 shown off the watch page while a video plays PASSED

**C3** - Fora de `/watch` e sem vídeo tocando, o botão não aparece (Início, busca, Inscrições) (PLAYBTN-03, AC 3)
Proof: run unit | PLAYBTN-03 hidden on home search and feeds when nothing plays PASSED

**C4** - No Início do carro, a barra de ações tem só Voltar, Início e Pesquisar (PLAYBTN-03, AC 3)
Proof: walk - screenshot do DHU no Início, barra de ações aberta

**C5** - Prévia do feed tocando não mostra o botão (PLAYBTN-04, AC 4)
Proof: walk - DevTools no carro: um `<video>` fora do `#movie_player` tocando no Início; barra sem Tocar/Pausar

**C6** - Sair de `/watch` pausado para o Início esconde o botão (PLAYBTN-05, AC 5)
Proof: run unit | PLAYBTN-05 leaving the watch page paused hides the button PASSED

**C7** - Sair de `/watch` pausado esconde o botão na barra do carro (PLAYBTN-05, AC 5)
Proof: walk - pausar um vídeo no DHU, tocar no botão da página inicial: barra sem Tocar

**C8** - Ícone do celular e do Android Auto é o volante branco com play sobre gradiente ciano → azul-royal (ICON-01, AC 6)
Proof: walk - screenshots do launcher do DHU e da gaveta do celular

**C9** - Ícone sem vermelho, sem fundo branco, sem retângulo com play e sem círculo com play (ICON-02, AC 7)
Proof: walk - screenshot da gaveta do celular com o ícone ao lado do YouTube e do YT Music

**C10** - Celular no claro: página do carro escura (THEME-01, AC 8)
Proof: walk - `cmd uimode night no`, app reaberto no DHU: DevTools do carro `matchMedia('(prefers-color-scheme: dark)').matches` true; screenshot do DHU escuro

**C11** - Celular no claro: página do YouTube no celular escura (THEME-02, AC 9)
Proof: walk - `cmd uimode night no`, app reaberto no celular: DevTools do celular com `prefers-color-scheme: dark`; screenshot escuro

**C12** - Celular no claro: Configurações do celular escuras (THEME-03, AC 10)
Proof: walk - `cmd uimode night no`: screenshot das Configurações escuras

**C13** - Troca dia/noite da central com vídeo tocando mantém a página escura e não recarrega (THEME-04, AC 11)
Proof: walk - DevTools do carro antes e depois da troca: mesmo `performance.timeOrigin`, `paused` false, `currentTime` maior e `prefers-color-scheme: dark`

---

## Handoff

**Settled mid-build:** tema sempre escuro no celular e no carro - user: "vou mudar de ideia, mantenha tema tanto do app quanto no carro sempre em dark. pode ver os demais."
**Dispatched:** none
**Walked:** none
