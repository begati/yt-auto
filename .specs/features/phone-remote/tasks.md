# Phone Remote Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: none - design inline nas tarefas: o celular fala com o carro pela `MediaSession` que já existe no processo (tocar/pausar/anterior/próximo/posição/`playFromMediaId`), e por um gancho de processo (`carLink`) para proporção, qualidade e velocidade
**Status**: In Progress
**Approved**: 2026-11-11 - user: "aprovado"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

---

## Test Coverage Matrix

> Generated from codebase and the earlier features' decision ("só JVM", sem Robolectric).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Lógica pura (id de vídeo de uma URL, formato de tempo) | unit | 1:1 com os critérios; cada formato de URL e limite de tempo | `app/src/test/java/dev/begati/youtubeauto/RemoteTest.kt` | `./gradlew testDebugUnitTest` |
| `MediaSession`, WebView/JS do carro e do celular, barra nativa do celular | none (walk) | walk no DHU + telefone, com DevTools da WebView para ler estado | - | `./gradlew assembleDebug` |

## Gate Check Commands

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tarefas com teste unitário | `./gradlew testDebugUnitTest` |
| Build | Tarefas sem teste (walk) e fim de fase | `./gradlew assembleDebug testDebugUnitTest` |

---

## Execution Plan

### Phase 1: Lógica pura

```
T1 → T2
```

### Phase 2: Carro publica e aceita comandos

```
T3 → T4
```

### Phase 3: Celular como controle

```
T5 → T6 → T7
```

---

## Task Breakdown

### T1: Id do vídeo a partir de uma URL

**What**: `internal fun videoIdFromUrl(url: String): String?` para `/watch?v=<id>`, `/shorts/<id>`, `youtu.be/<id>` (com ou sem esquema/host, com outros parâmetros); `null` para o resto ou id inválido (`isVideoId`)
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: `isVideoId`, `watchUrl`
**Requirement**: CAST-01, CAST-05

**Done when**:

- [x] Gate check passes: `./gradlew testDebugUnitTest`
- [x] Testes `CAST-01` e `CAST-05` em `RemoteTest.kt` passam

**Tests**: unit
**Gate**: quick

**Commit**: `feat: extract the video id from watch, shorts and youtu.be links`

---

### T2: Formato do tempo da barra

**What**: `internal fun clock(seconds: Long): String` → `m:ss` abaixo de 1 hora, `h:mm:ss` a partir dela; negativo vira `0:00`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T1
**Reuses**: -
**Requirement**: REMOTE-06

**Done when**:

- [x] Gate check passes: `./gradlew testDebugUnitTest`
- [x] Teste `REMOTE-06` em `RemoteTest.kt` passa

**Tests**: unit
**Gate**: quick

**Commit**: `feat: format playback time for the phone remote`

---

### T3: Posição, duração e seek na sessão de mídia

**What**: o `PAGE_JS` passa a reportar também a duração (`yta.time(id, t, dur)`); o carro publica posição no `PlaybackState` (sem anunciar `ACTION_SEEK_TO`: com ele o Android Auto troca anterior/próximo do card por -10/+10 s, e os extras de reserva de espaço não impedem; `onSeekTo` atende o `seekTo` do celular mesmo assim) e duração no `MediaMetadata`, e trata `onSeekTo` movendo o `currentTime` do vídeo; publicar posição não chama `invalidate()`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None (phase 1 done)
**Reuses**: `sharedSession`, `playbackState`, `setMeta`, bridge `yta`
**Requirement**: REMOTE-06, REMOTE-07

**Done when**:

- [x] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [x] Walk: o card de mídia do DHU mostra a barra de progresso andando

**Tests**: none
**Gate**: build

**Commit**: `feat: publish position and duration and accept seek in the media session`

---

### T4: Gancho do carro para proporção, qualidade e velocidade

**What**: objeto de processo `carLink` que o `YoutubeScreen` preenche enquanto vive, com `applyPlayback()` (reaplica qualidade e velocidade salvas) e `cycleAspect()` (o mesmo do botão do carro: `tapAspect` + toast); o botão do carro passa a usar `cycleAspect()`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T3
**Reuses**: `JS_APPLY_QUALITY`, `JS_APPLY_PLAYBACK`, `tapAspect`, `aspectTap`
**Requirement**: REMOTE-08, REMOTE-09, REMOTE-10

**Done when**:

- [x] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [x] Botão de proporção do carro segue funcionando (walk)

**Tests**: none
**Gate**: build

**Commit**: `refactor: expose aspect and playback re-apply to the phone through carLink`

---

### T5: Celular manda o vídeo para o carro em vez de tocar

**What**: JS injetado na WebView do celular: clique (fase de captura) em link de vídeo chama `remote.cast(url)` e é cancelado; navegação SPA ou de página para `/watch`/`/shorts` volta (`history.back`) e chama `remote.cast`; todo `play` de `<video>` é pausado. `cast` usa `videoIdFromUrl` e `MediaController.transportControls.playFromMediaId`, que abre no carro ou guarda (`idleCallback`)
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None (phases 1-2 done: uses T1 `videoIdFromUrl`)
**Reuses**: `videoIdFromUrl`, `sharedSession`, `idleCallback`/`pendingUrl`, `OfflineRetry.onLoaded`
**Requirement**: CAST-01, CAST-02, CAST-03, CAST-04, CAST-05

**Done when**:

- [x] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [x] Walk: toque num vídeo no celular abre no DHU; celular fica no Início sem tocar; com o app do carro fechado, o vídeo abre ao abrir o app no carro

**Tests**: none
**Gate**: build

**Commit**: `feat: send videos tapped on the phone to the car instead of playing them`

---

### T6: Barra de controle no celular

**What**: barra nativa embaixo da WebView da `LoginActivity`, ligada a um `MediaController` da sessão: capa, título, canal, tocar/pausar, anterior, próximo, `SeekBar` com `clock()` do tempo atual e da duração (atualizada a cada 1 s, seek ao soltar), mensagem de erro da sessão no lugar do canal; escondida sem metadata
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T5
**Reuses**: `sharedSession`, `clock`
**Requirement**: REMOTE-01, REMOTE-02, REMOTE-03, REMOTE-04, REMOTE-05, REMOTE-06, REMOTE-07, REMOTE-11, REMOTE-12, CAST-02, CAST-06

**Done when**:

- [x] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [x] Walk: cada controle tem efeito no DHU; barra some antes do primeiro vídeo

**Tests**: none
**Gate**: build

**Commit**: `feat: add the remote control bar to the phone`

---

### T7: Menu de proporção, qualidade e velocidade na barra

**What**: botão "⋮" na barra abre um `PopupMenu` com "Proporção" (chama `carLink.cycleAspect`), "Qualidade" e "Velocidade" (submenus com as opções de `QUALITY`/`SPEED`; salva com `setChoice` e chama `carLink.applyPlayback`)
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T6
**Reuses**: `carLink`, `QUALITY`, `SPEED`, `setChoice`
**Requirement**: REMOTE-08, REMOTE-09, REMOTE-10

**Done when**:

- [ ] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`
- [ ] Walk: cada item muda o vídeo do carro (lido via DevTools)

**Tests**: none
**Gate**: build

**Commit**: `feat: change aspect, quality and speed of the car video from the phone`

---

## Checks

**C1** - Toque num link de vídeo no celular, com o app aberto no carro, abre o vídeo no carro e o celular fica na página (CAST-01, AC 1)
Proof: walk - toque num vídeo do Início no celular: DHU em `/watch?v=<id>`; celular continua no Início

**C2** - Com o app do carro fechado, o vídeo fica guardado, a barra avisa "Abra o YouTube Auto no carro para tocar" e o vídeo abre ao abrir o app no carro (CAST-02, AC 2)
Proof: walk - app do carro fechado, toque no celular: aviso na barra; abrir o app no DHU: o vídeo abre

**C3** - O celular nunca reproduz vídeo, nem prévias do feed (CAST-03, AC 3)
Proof: walk - DevTools no celular: nenhum `<video>` com `paused` false depois de rolar o Início e tocar em vídeos

**C4** - Navegação do celular para `/watch` ou `/shorts` volta e abre no carro (CAST-04, AC 4)
Proof: walk - `location.href` para um `/watch` no celular via DevTools: celular volta, DHU abre o vídeo

**C5** - Id do vídeo vem de `/watch?v=`, `/shorts/` e `youtu.be/`; o resto não tem id (CAST-01, AC 1)
Proof: run unit | CAST-01 video id from watch shorts and youtu-be links PASSED

**C6** - Short abre no carro como `https://m.youtube.com/watch?v=<id>` (CAST-05, AC 5)
Proof: run unit | CAST-05 a short opens as a watch url PASSED

**C7** - Barra mostra capa, título e canal do vídeo do carro (REMOTE-01, AC 6)
Proof: walk - screenshot do celular com a barra e o vídeo do DHU

**C8** - Vídeo do carro muda e a barra acompanha em até 2 s (REMOTE-02, AC 7)
Proof: walk - "próximo" no DHU: barra do celular mostra o novo título

**C9** - Tocar/pausar da barra alterna o carro e o ícone segue o estado (REMOTE-03, AC 8)
Proof: walk - DevTools no carro: `paused` alterna; ícone da barra muda

**C10** - "Anterior" volta o vídeo do carro ao início (REMOTE-04, AC 9)
Proof: walk - `currentTime` do carro perto de 0 depois do toque

**C11** - "Próximo" pula para o próximo do player do carro (REMOTE-05, AC 10)
Proof: walk - id do vídeo do carro muda depois do toque

**C12** - Tempo atual e duração em `m:ss` / `h:mm:ss`, atualizados a cada 1 s (REMOTE-06, AC 11)
Proof: run unit | REMOTE-06 clock formats m-ss and h-mm-ss PASSED

**C13** - A barra mostra o tempo do carro andando (REMOTE-06, AC 11)
Proof: walk - dois screenshots com 2 s de intervalo: tempo avança e bate com o `currentTime` do carro

**C14** - Soltar a barra de tempo leva o carro à posição (REMOTE-07, AC 12)
Proof: walk - arrastar até a metade: `currentTime` do carro perto da metade da duração

**C15** - "Proporção" no celular avança o ciclo no carro sem mudar o padrão (REMOTE-08, AC 13)
Proof: walk - `yta.aspect()` do carro muda; `aspect` em `settings.xml` inalterado

**C16** - Qualidade escolhida no celular é aplicada e salva (REMOTE-09, AC 14)
Proof: walk - `getPlaybackQuality()` do player do carro e `quality` em `settings.xml` com o valor escolhido

**C17** - Velocidade escolhida no celular é aplicada e salva (REMOTE-10, AC 15)
Proof: walk - `playbackRate` do vídeo do carro e `speed` em `settings.xml` com o valor escolhido

**C18** - Com o app aberto no carro, sem vídeo enviado nem tocando, a barra fica escondida (REMOTE-11, AC 16)
Proof: walk - processo novo, app aberto no DHU no Início, celular aberto: sem barra

**C19** - Sem o app aberto no carro, a barra avisa "Os vídeos tocam só no carro. Abra o YouTube Auto no Android Auto." (REMOTE-12, AC 17)
Proof: walk - DHU em outro app, processo novo, celular aberto: barra com o aviso

**C20** - Tocar num vídeo sem o app aberto no carro mostra "Vídeo guardado: ele toca quando você abrir o YouTube Auto no carro" (CAST-06, AC 18)
Proof: walk - mesmo cenário de C19, toque num vídeo: aviso rápido na tela do celular

---

## Handoff

**Settled mid-build:** user: "talvez seja uma boa ideia, nao conectado no android Auto, avisar no app do celular que o vídeo não será reproduzido ali." (AC 17, 18). Achado no walk de T5: vídeo aberto no carro sem toque na tela do carro (celular, card de mídia) começava mudo; o `PAGE_JS` desliga o mudo nos primeiros 5 s de reprodução de cada vídeo (critério 1 pede o vídeo tocando no carro). Achados no walk de T6: (a) vídeo carregado por `loadUrl` começa a tocar antes do `PAGE_JS` ser injetado, então o evento `play` se perde e a sessão ficava sem metadata nem estado; o `PAGE_JS` passou a acompanhar tocar/pausar e metadata também por consulta a cada 500 ms; (b) `document.title` é "YouTube" logo após o carregamento: esse título provisório é ignorado; o canal vem de `getVideoData().author` quando a página não informa; (c) o Android Auto mantém o serviço e a tela do carro vivos com o app fechado, então "app aberto no carro" passou a ser "player do carro existe" (`carPlaying()`), e um vídeo enviado nesse estado mostra "Abra o YouTube Auto no carro para tocar"
**Dispatched:** none yet
**Walked:** pending
