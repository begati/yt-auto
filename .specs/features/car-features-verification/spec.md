# Car Features Verification Specification

**Approved**: 2026-11-10 - user: "aprovado, pode seguir"

## Problem Statement

As funcionalidades feitas em 2026-11-10 (tela cheia, controles, sessão de mídia, histórico/inscrições,
thumbnails, qualidade e configurações) foram entregues sem nenhum teste. Esta spec fixa, em critérios
testáveis na JVM, as regras de lógica pura que elas contêm, para que uma regressão quebre um teste.
O que depende de WebView, do YouTube real ou do Android Auto fica fora (escopo "só JVM", escolhido pelo usuário).

## Goals

- [ ] Cada regra de lógica pura listada abaixo tem um teste JUnit que falha se a regra quebrar.
- [ ] `./gradlew testDebugUnitTest` roda verde sem emulador nem rede.
- [ ] Nenhuma mudança de comportamento no app: só extração de funções para torná-las testáveis.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Tela cheia em `/watch`, pular anúncio, qualidade aplicada no player, página "visível" em segundo plano, metadata enviada pela página (JS em `PAGE_JS`) | Precisa de WebView: teste instrumentado, recusado pelo usuário ("só JVM") |
| Templates do carro (barra de ações, engrenagem, `SettingsScreen`, `ChoiceScreen`), botões do volante, card de mídia, ré | Precisa do host Android Auto ou DHU; roteiro manual |
| `MediaSession`/`PlaybackState`, `MediaBrowserService`, `ThumbProvider.openFile`, `Uri.encode` | Classes do framework Android não executam na JVM sem Robolectric (não pedido) |
| Download real do feed e das thumbnails (rede, cookies) | Teste JVM sem rede; o parsing do HTML é coberto |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Como testar código hoje `private` e preso a tipos Android | Extrair funções puras `internal` no mesmo arquivo: parser do feed (retorna id/título/canal), leitura/gravação de configurações recebendo `SharedPreferences`, cálculo do tamanho da capa, validação do ID de vídeo, rótulo do botão home | Menor mudança que deixa a lógica alcançável pelo JUnit sem Robolectric | y - user: "aprovado, pode seguir" |
| `SharedPreferences` no teste | Implementação em memória escrita no próprio teste | É uma interface; evita dependência nova | y - user: "aprovado, pode seguir" |
| Biblioteca JSON nos testes | `testImplementation("org.json:json")`, versão fixa | Na JVM o `org.json` do android.jar é stub e lança exceção | y - user: "pode adicionar as dependências" |
| Framework de teste | JUnit 4 (`testImplementation("junit:junit:4.13.2")`) | Padrão do Android Gradle Plugin para testes locais | y - user: "pode adicionar as dependências" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Histórico e inscrições extraídos corretamente ⭐ MVP

**User Story**: Como motorista, quero que as listas de Histórico e Inscrições mostrem os vídeos certos, para escolher o que tocar pelo card de mídia.

**Why P1**: O parsing do HTML do YouTube é a regra mais frágil e mais densa do app.

**Acceptance Criteria**:

1. WHEN o HTML contém `var ytInitialData = ` seguido de JSON com vídeos THEN o parser SHALL retornar cada vídeo com id, título e canal, na ordem em que aparecem no JSON
2. WHEN o mesmo `videoId` aparece mais de uma vez THEN o parser SHALL manter apenas a primeira ocorrência
3. WHEN o JSON tem mais de 50 vídeos THEN o parser SHALL retornar exatamente os 50 primeiros
4. WHEN o título vem como `simpleText`, como `runs` (vários trechos) ou como `content` THEN o parser SHALL usar esse texto, com os `runs` concatenados sem separador
5. WHEN o vídeo vem como `lockupViewModel` com `contentType` `LOCKUP_CONTENT_TYPE_VIDEO` THEN o parser SHALL usar `contentId` como id e `metadata.lockupMetadataViewModel.title.content` como título
6. IF um `lockupViewModel` tem `contentType` diferente de `LOCKUP_CONTENT_TYPE_VIDEO` THEN o parser SHALL ignorá-lo
7. IF o HTML não contém `var ytInitialData = ` THEN o parser SHALL retornar lista vazia
8. IF um objeto tem `videoId` mas nenhum título (`title`, `headline` ou título do lockup) THEN o parser SHALL ignorá-lo
9. WHEN o vídeo tem `shortBylineText` THEN o parser SHALL usá-lo como canal, senão `ownerText`, senão texto vazio

**Independent Test**: HTML de exemplo no teste, sem rede.

---

### P1: Configurações com padrões e validação

**User Story**: Como motorista, quero que as configurações tenham padrões sensatos e nunca fiquem num valor inválido, no carro e no celular.

**Why P1**: Todas as telas de configuração e o comportamento da página dependem dessas leituras.

**Acceptance Criteria**:

1. WHEN nada foi salvo THEN a leitura SHALL retornar qualidade `medium`, página inicial `https://m.youtube.com` e tamanho do texto `130`
2. WHEN nada foi salvo THEN as flags `skipAds`, `fullscreen` e `background` SHALL ser `true`
3. IF o valor salvo de uma escolha não está entre as opções (ex.: qualidade `tiny`) THEN a leitura SHALL retornar o padrão dessa escolha
4. IF o app tenta salvar um valor fora das opções (ex.: qualidade `highres`) THEN o valor salvo anterior SHALL permanecer
5. WHEN um valor válido é salvo THEN a leitura seguinte SHALL retornar esse valor
6. WHEN uma flag é salva como `false` THEN a leitura seguinte SHALL retornar `false`
7. The system SHALL oferecer as qualidades `Automática`, `240p`, `360p`, `480p`, `720p`, `1080p`, nessa ordem e só elas
8. The system SHALL oferecer as páginas iniciais `Início`, `Inscrições`, `Histórico` e os tamanhos `100%`, `115%`, `130%`, `150%`, nessa ordem

**Independent Test**: `SharedPreferences` em memória no teste.

---

### P2: Botão home com o nome da página inicial

**User Story**: Como motorista, quero que o botão leve o nome da página para onde ele vai.

**Why P2**: Usabilidade; o app funciona sem isso.

**Acceptance Criteria**:

1. WHEN a página inicial salva é `https://m.youtube.com/feed/subscriptions` THEN o rótulo do botão home SHALL ser `Inscrições`
2. WHEN a página inicial salva é `https://m.youtube.com/feed/history` THEN o rótulo do botão home SHALL ser `Histórico`
3. WHEN nenhuma página inicial foi salva THEN o rótulo do botão home SHALL ser `Início`

---

### P2: Capa e thumbnails seguras

**User Story**: Como motorista, quero capas que sempre carreguem, sem estourar o limite do Android, e sem expor arquivos do celular.

**Why P2**: Falha aqui só tira a imagem, mas o ID inválido é fronteira de segurança do provider exportado.

**Acceptance Criteria**:

1. WHEN a capa tem o maior lado acima de 320 px THEN o redimensionamento SHALL reduzi-la proporcionalmente até o maior lado ter 320 px
2. IF a capa tem o maior lado de 320 px ou menos THEN o redimensionamento SHALL manter o tamanho original
3. The system SHALL aceitar como ID de vídeo apenas textos de exatamente 11 caracteres entre `A-Z`, `a-z`, `0-9`, `_` e `-`
4. IF o ID pedido ao provider contém `/`, `.` ou tem tamanho diferente de 11 THEN o sistema SHALL recusá-lo

---

## Edge Cases

- IF o JSON do feed está vazio (`{}`) THEN o parser SHALL retornar lista vazia (coberto por FEED-07/FEED-08 com JSON sem vídeos)
- WHEN a capa é quadrada de 321 px THEN o resultado SHALL ser 320x320 (fronteira de ART-01)

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| FEED-01 | P1: Histórico e inscrições | T2 | Verified |
| FEED-02 | P1: Histórico e inscrições | T2 | Verified |
| FEED-03 | P1: Histórico e inscrições | T2 | Verified |
| FEED-04 | P1: Histórico e inscrições | T2 | Verified |
| FEED-05 | P1: Histórico e inscrições | T2 | Verified |
| FEED-06 | P1: Histórico e inscrições | T2 | Verified |
| FEED-07 | P1: Histórico e inscrições | T2 | Verified |
| FEED-08 | P1: Histórico e inscrições | T2 | Verified |
| FEED-09 | P1: Histórico e inscrições | T2 | Verified |
| SET-01 | P1: Configurações | T3 | Verified |
| SET-02 | P1: Configurações | T3 | Verified |
| SET-03 | P1: Configurações | T3 | Verified |
| SET-04 | P1: Configurações | T3 | Verified |
| SET-05 | P1: Configurações | T3 | Verified |
| SET-06 | P1: Configurações | T3 | Verified |
| SET-07 | P1: Configurações | T3 | Verified |
| SET-08 | P1: Configurações | T3 | Verified |
| HOME-01 | P2: Botão home | T4 | Verified |
| HOME-02 | P2: Botão home | T4 | Verified |
| HOME-03 | P2: Botão home | T4 | Verified |
| ART-01 | P2: Capa e thumbnails | T5 | Verified |
| ART-02 | P2: Capa e thumbnails | T5 | Verified |
| THUMB-01 | P2: Capa e thumbnails | T6 | Verified |
| THUMB-02 | P2: Capa e thumbnails | T6 | Verified |

**Coverage:** 24 total, 24 mapped to tasks, 0 unmapped

## Sources

- Conversa de 2026-11-10 - pedidos do usuário que geraram cada funcionalidade
- `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt` em `58007a2` - comportamento atual

---

## Success Criteria

- [ ] `./gradlew testDebugUnitTest` verde com um teste por critério
- [ ] `./gradlew assembleDebug` verde depois da extração
