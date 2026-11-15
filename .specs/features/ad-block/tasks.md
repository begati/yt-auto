# Ad Block Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: none - inline (one document-start script, one URL, strings)
**Status**: Done (Verifier independente não rodado: usuário pediu para commitar e lançar - "pode comitar tudo, parece que ta tudo funcionando")
**Approved**: 2026-11-15 - user: "sim"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`
**Run js**: `node --test app/src/test/js/page_start.test.mjs`

---

## Test Coverage Matrix

> Generated from codebase, project guidelines, and spec - confirm before Execute. Guidelines found: none - strong defaults applied; `app/build.gradle.kts` (test logging, one line per test).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Document-start page script (`assets/page_start.js`) | unit (node) | 1:1 to ADS-01..05, CC-02 and the two edge cases | `app/src/test/js/*.test.mjs` | `node --test app/src/test/js/page_start.test.mjs` |
| Kotlin pure helpers (flags, strings) | unit (JUnit) | 1:1 to spec ACs | `app/src/test/java/dev/begati/youtubeauto/*Test.kt` | `./gradlew testDebugUnitTest --rerun` |
| WebView wiring, injected CSS, phone start URL | walk (DHU + phone over adb) | each AC observed on the device, the failing side too (L-004) | - | walk |
| README | none | grep proof | - | lone proof |

## Gate Check Commands

> Generated from codebase - confirm before Execute.

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | After tasks with unit tests only | `node --test app/src/test/js/page_start.test.mjs; ./gradlew testDebugUnitTest --rerun` |
| Full | After WebView wiring tasks | quick + walk on DHU |
| Build | After phase completion | `./gradlew assembleDebug` + quick |

---

## Execution Plan

### Phase 1: Anúncios, legendas e conta

```
T1 → T2 → T3 → T4 → T5 → T6 → T7
```

---

## Task Breakdown

### T1: Script de início de documento que remove anúncios

**What**: `page_start.js` com o filtro de `adPlacements`, `adSlots`, `playerAds`, `adBreakHeartbeatParams` em `JSON.parse`, `Response.prototype.json` e `ytInitialPlayerResponse`, lendo `yta.flag('skipAds')` (padrão ligado sem `yta`)
**Where**: `app/src/main/assets/page_start.js` (+ `app/src/test/js/page_start.test.mjs`)
**Depends on**: None
**Reuses**: bridge `yta.flag` de `PAGE_JS`
**Requirement**: ADS-01, ADS-02, ADS-03, ADS-04, ADS-05

**Done when**:

- [x] Os testes node de ADS-01..05 e dos dois edge cases passam: `node --test app/src/test/js/page_start.test.mjs`
- [x] Test count: 7 tests pass

**Tests**: unit
**Gate**: quick

**Commit**: `feat: strip ad data from YouTube player responses`

---

### T2: Injetar o script no início de cada documento do carro

**What**: `WebViewCompat.addDocumentStartJavaScript(wv, <asset>, setOf("https://m.youtube.com"))` na WebView do carro, só quando `WebViewFeature.DOCUMENT_START_SCRIPT` é suportado; dependência `androidx.webkit:webkit`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt` (+ `app/build.gradle.kts`)
**Depends on**: T1
**Reuses**: criação da WebView em `onSurfaceAvailable`
**Requirement**: ADS-06, ADS-07, ADS-08, ADS-09

**Done when**:

- [x] Walk: 5 vídeos seguidos no DHU com a opção ligada, nenhum com `ad-showing`; opção desligada, anúncio volta e é pulado pelo código da v1.2
- [x] Gate check passes: `./gradlew assembleDebug`

**Tests**: none (walk - WebView wiring layer)
**Gate**: full

**Commit**: `feat: block YouTube ads before the player schedules them`

---

### T3: Esconder anúncios do feed

**What**: regra CSS em `PAGE_JS` para `ad-slot-renderer`, `ytm-promoted-sparkles-web-renderer`, `ytm-companion-ad-renderer` sob a classe `yta-noads`, alternada pelo intervalo com `yta.flag('skipAds')`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T2
**Reuses**: estilo e `classList.toggle('yta-watch')` de `PAGE_JS`
**Requirement**: ADS-10, ADS-11

**Done when**:

- [x] Walk: Início/busca no DHU sem blocos de anúncio com a opção ligada; com ela desligada, os blocos aparecem
- [x] Gate check passes: `./gradlew assembleDebug`

**Tests**: none (walk - injected CSS layer)
**Gate**: full

**Commit**: `feat: hide promoted items in the YouTube feed`

---

### T4: Rótulo "Bloquear anúncios"

**What**: `flag_skip_ads` nas 7 línguas com os textos de ADS-12, e teste do rótulo e do padrão ligado
**Where**: `app/src/main/res/values*/strings.xml` (+ `app/src/test/java/dev/begati/youtubeauto/SettingsTest.kt`)
**Depends on**: T3
**Reuses**: `I18nTest`, `FLAGS`
**Requirement**: ADS-12

**Done when**:

- [x] Teste `ADS-12` passa: `./gradlew testDebugUnitTest --rerun`

**Tests**: unit
**Gate**: quick

**Commit**: `feat: rename the ads setting to Block ads`

---

### T5: Preferência de legenda do YouTube no início do documento

**What**: em `PAGE_JS`, trocar a janela de 10 s por descarregar a legenda sempre que ela aparece com "Legendas" desligada
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T4
**Reuses**: T1, T2 (mesmo script e injeção)
**Requirement**: CC-01, CC-02, CC-03

**Done when**:

- [x] Walk: após `pm clear`, primeiro vídeo (PAIbJhS0Pzw, enviado pelo celular) passa do pulo do SponsorBlock em ~81 s sem legenda; com "Legendas" ligada, aparece com legenda
- [x] Gate check passes: `./gradlew assembleDebug`

**Tests**: none (walk - injected page script in PAGE_JS)
**Gate**: full

**Commit**: `fix: keep captions off on a fresh install`

---

### T6: Celular abre o m.youtube.com sem exigir login

**What**: `LoginActivity` carrega `https://m.youtube.com` em vez de `accounts.google.com/ServiceLogin`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T5
**Reuses**: `LoginActivity.onCreate`
**Requirement**: ANON-01, ANON-02, ANON-03, ANON-04

**Done when**:

- [x] Walk: após `pm clear`, o celular abre o m.youtube.com sem login; pesquisa e vídeo enviado tocam no carro; "Fazer login" leva ao Google e volta logado (login completo não feito no walk; usuário: "parece que ta tudo funcionando")
- [x] Gate check passes: `./gradlew assembleDebug`

**Tests**: none (walk - WebView wiring layer)
**Gate**: full

**Commit**: `feat: let the app be used without a YouTube account`

---

### T7: README com conta opcional

**What**: "Requirements"/"Requisitos" dizem que a conta do YouTube é opcional
**Where**: `README.md` (+ `README.pt-BR.md`)
**Depends on**: T6
**Reuses**: -
**Requirement**: ANON-05

**Done when**:

- [x] Os dois READMEs trazem "optional"/"opcional" na linha da conta

**Tests**: none
**Gate**: build

**Commit**: `docs: YouTube account is optional`

---

## Checks

**C1** - Com a opção ligada, `JSON.parse` entrega a resposta sem `adPlacements`, `adSlots`, `playerAds`, `adBreakHeartbeatParams` (ADS-01, AC 1)
Proof: run js | ✔ ADS-01 JSON.parse drops the ad fields

**C2** - Com a opção ligada, `Response.prototype.json` entrega a resposta sem esses campos (ADS-02, AC 2)
Proof: run js | ✔ ADS-02 Response.json drops the ad fields

**C3** - Com a opção ligada, `ytInitialPlayerResponse` atribuído fica sem esses campos (ADS-03, AC 3)
Proof: run js | ✔ ADS-03 ytInitialPlayerResponse is stored without the ad fields

**C4** - Os demais campos e as respostas sem anúncio ficam intactos (ADS-04, AC 4)
Proof: run js | ✔ ADS-04 other fields and ad-free responses stay intact

**C5** - Com a opção desligada, as respostas passam sem alteração (ADS-05, AC 5)
Proof: run js | ✔ ADS-05 with the flag off responses pass unchanged

**C6** - Resposta que não é objeto passa sem alteração (edge case 1)
Proof: run js | ✔ EDGE non-object responses pass unchanged

**C7** - Sem `yta`, a opção vale como ligada (edge case 2)
Proof: run js | ✔ EDGE without yta the flag reads as on

**C23** - `playerResponse` aninhada (objeto ou lista, como `get_watch`) fica sem os campos de anúncio (ADS-13, AC 10)
Proof: run js | ✔ ADS-13 a nested playerResponse loses the ad fields, alone or inside a list

**C8** - O script roda no início dos documentos de `https://m.youtube.com`, antes da página (ADS-06, AC 6)
Proof: walk - chrome://inspect no carro: `window.__ytaStart` existe e `ytInitialPlayerResponse.adPlacements` é `undefined` na primeira carga de `/watch`

**C9** - Sem suporte a script no início do documento a página abre e só o pulo funciona (ADS-07, AC 7)
Proof: walk - build com o suporte forçado a falso: página abre, anúncio aparece e é levado ao fim pelo pulo da v1.2

**C10** - Anúncio que escapa continua sendo levado ao fim e pulado (ADS-08, AC 8)
Proof: walk - com o bloqueio forçado a falso e a opção ligada, o anúncio é levado ao fim (mesma corrida de C9)

**C11** - Vídeo aberto no carro com a opção ligada começa sem `ad-showing` (ADS-09, AC 9)
Proof: walk - 5 vídeos seguidos no DHU: nenhum mostra anúncio; desligando a opção, anúncios voltam

**C12** - Com a opção ligada, o feed não mostra blocos de anúncio (ADS-10, P2 AC 1)
Proof: walk - Início e busca no DHU com a opção ligada: `document.querySelectorAll('ad-slot-renderer,ytm-promoted-sparkles-web-renderer,ytm-companion-ad-renderer')` todos com `display:none`

**C13** - Com a opção desligada, os blocos de anúncio do feed aparecem (ADS-11, P2 AC 2)
Proof: walk - mesma busca com a opção desligada: os blocos aparecem

**C14** - Rótulo da opção nas 7 línguas e padrão ligado (ADS-12, P3 AC 1)
Proof: run unit | ADS-12 the ads setting reads Block ads in every language and is on by default PASSED

**C15** - Instalação nova com "Legendas" no padrão: primeiro vídeo sem legenda (CC-01, AC 1)
Proof: walk - `pm clear`, login, vídeo com legendas automáticas no DHU: sem legenda

**C16** - Com "Legendas" desligada, legenda ligada pelo YouTube depois de um salto é descarregada em até 0,5 s (CC-02, AC 2)
Proof: walk - `pm clear`, PAIbJhS0Pzw enviado pelo celular, `loadModule` instrumentado: depois do pulo do SponsorBlock em ~81 s a faixa volta a vazio em até 0,5 s e a tela do DHU fica sem legenda

**C17** - Com "Legendas" ligada, vídeo aparece com legenda (CC-03, AC 3)
Proof: walk - "Legendas" ligada no celular, mesmo vídeo no DHU: com legenda

**C18** - O celular abre o m.youtube.com sem a tela de login (ANON-01, AC 1)
Proof: walk - `pm clear` e abrir o app: m.youtube.com sem login

**C19** - Sem conta, pesquisa e envio de vídeo ao carro funcionam (ANON-02, AC 2)
Proof: walk - sem conta: pesquisa no carro toca; vídeo tocado no celular toca no carro

**C20** - "Fazer login" abre o Google na mesma tela e volta logado (ANON-03, AC 3)
Proof: walk - tocar "Fazer login" no celular, entrar: volta ao m.youtube.com com o avatar da conta

**C21** - Com conta, o celular abre logado e o carro segue como na v1.2 (ANON-04, AC 4)
Proof: walk - reabrir o app logado: m.youtube.com com a conta; Histórico no Android Auto lista vídeos

**C22** - README en e pt-BR dizem que a conta é opcional (ANON-05, AC 5)
Proof: `grep -qi "account.*optional" README.md && grep -qi "conta.*opcional" README.pt-BR.md`

---

## Handoff

**Settled mid-build:** T5 - causa real das legendas achada no walk (`loadModule('captions')` do próprio YouTube depois do pulo do SponsorBlock, só com dados zerados); usuário escolheu "Bloquear sempre"; CC-02 e T5 reescritos; Auto Backup fora desta spec ("Fora desta spec"). Walk do T2 achou o caminho do SPA (`/youtubei/v1/get_watch`, `[{playerResponse}, ...]`) sem filtro; virou ADS-13 / C23, com teste, antes do commit do T2
**Dispatched:** none
**Walked:** DHU 800x480 + telefone via USB; estado lido pelo DevTools da WebView do carro (`adb forward tcp:9222 localabstract:webview_devtools_remote_<pid>`, script CDP `Runtime.evaluate`). Conta logada era Premium (sem anúncios): `pm clear` deixou o app sem conta, onde o YouTube manda só pós-roll (`AD_PLACEMENT_KIND_END`); anúncio provocado com `currentTime = duration - 4` e `ad-showing` amostrado a cada 250 ms por 25 s.
- T2 / C8: página do carro com `window.__ytaStart` 1, `JSON.parse` embrulhado e `ytInitialPlayerResponse` como accessor (definido antes do `var` da página, que é não-configurável).
- C11 controle (opção desligada via `shared_prefs/settings.xml` + `force-stop`): `ytInitialPlayerResponse` com `playerAds`, `adPlacements`, `adBreakHeartbeatParams`; pós-roll de dQw4w9WgXcQ visível 50 amostras (12,5 s).
- C11 (opção ligada): dQw4w9WgXcQ, kJQP7kiw5Fk, 9bZkp7q19f0, JGwWNGJdvx8, OPf0YbXqDm0 = 0 amostras com `ad-showing`, sem campos de anúncio; o fim do vídeo leva direto ao próximo (autoplay pelo SPA). Primeira rodada achou `getPlayerResponse()` do vídeo do autoplay com os campos (via `get_watch`): corrigido em 02e8467 (ADS-13); segunda rodada: os 5 vídeos do autoplay com `ad: []`.
- T3 / C12, C13: sem conta (e com Premium) o YouTube não mostra anúncios no feed nem na busca (buscas "carro novo", "celular", "iphone", "seguro auto": nenhuma tag de anúncio). Prova com os três elementos (`ad-slot-renderer`, `ytm-promoted-sparkles-web-renderer`, `ytm-companion-ad-renderer`) inseridos via DevTools na busca "carro": opção ligada = `html.yta-noads`, `display` none nos três; opção desligada (prefs + `force-stop`) = sem a classe, `display` inline nos três. Ressalva: as tags vêm do markup conhecido do m.youtube.com, não de um anúncio real observado.
- T5 / C15, C16, C17 (causa): `pm clear` + PAIbJhS0Pzw enviado pelo celular, com `loadModule`/`unloadModule`/`setOption` do player instrumentados (pilha) e a faixa amostrada a cada 250 ms: build antigo = `loadModule ["captions"]` vindo de `_.aa.publish` do `ytmweb` em @81, logo após o pulo do SponsorBlock, faixa `pt` (asr) ligada e na tela até o fim (duas vezes: @81 e @82; uma terceira em estado restaurado do Auto Backup, também @82). Estado já usado: 150 s sem ligar. Usuário notou: "parece que quando pula patrocinio, legenda ativa".
- T5 (conserto, build novo): `pm clear`, mesmo envio; tempo 61 → 89 (pulo), `loadModule ["captions"]` em @81 seguido de `unloadModule` do app no mesmo instante, a faixa nunca aparece na amostragem de 250 ms; screenshot do DHU em 100 s sem legenda (C15, C16). Com `captions` true em `shared_prefs` + `force-stop`, mesmo vídeo: faixa `pt`, 2 segmentos, legenda na tela do DHU em 11 s (C17).
- T5 (investigação inicial, sem conclusão): estado limpo (`pm clear`), sem conta, vídeo PAIbJhS0Pzw (legendas automáticas em pt) aberto por toque na busca e por `location.href`: `getOption('captions','track')` vazio e nenhum `.ytp-caption-segment` em 16 s. `loadModule`/`unloadModule('captions')` mostra/esconde a legenda (`asr` pt) sem mudar nada no `localStorage` (só existe `yt-player-caption-persistence`). Hipótese da spec não confirmada; bug não reproduzido no DHU - pergunta ao usuário.
- T6 / C18, C19: `pm clear`, app aberto no celular = m.youtube.com sem conta ("Que tal uma pesquisa para começar?"), sem tela do Google; busca "flow podcast" no celular, toque no 1º vídeo = carro em `/watch?v=PAIbJhS0Pzw` tocando, sem `SAPISID`; celular continua em `/results`. C20 parcial: aba "Você" > "Fazer login" = `accounts.google.com/v3/signin/identifier` dentro do próprio app (LoginActivity no topo, barra do controle remoto embaixo); falta o usuário entrar com a conta.
- C9/C10: build com o `if` da injeção forçado a falso (revertido em seguida): `__ytaStart` ausente, página abre, campos presentes, pós-roll visível só 3 amostras (0,75 s) - levado ao fim pelo pulo da v1.2.
