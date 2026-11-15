# Ad Block Specification

**Approved**: 2026-11-15 - user: "sim"

Inclui também as legendas que aparecem na primeira instalação e o uso sem conta do YouTube (pedidos do usuário durante a spec).

## Problem Statement

Issue #2: na v1.2 o app só reage ao anúncio depois que ele já carregou e começou (`ad-showing`, conferido a cada
500 ms), então cada vídeo ainda tem uma espera antes de começar. O usuário pede remover os anúncios por completo.
O anúncio só existe porque a resposta do player traz os dados dele (`adPlacements`, `adSlots`, `playerAds`); sem
eles o YouTube não agenda o anúncio.

Legendas: em algumas centrais, logo após instalar o app, os vídeos aparecem com legenda mesmo com "Legendas"
desligada (o padrão). Só some depois de ligar e desligar "Legendas" nas configurações do app.

Conta: o app do celular abre direto na tela de login do Google (`accounts.google.com/ServiceLogin`), sem opção de
seguir sem conta. O carro já carrega o m.youtube.com com ou sem sessão; só o celular obriga o login.

## Goals

- [ ] Com a opção ligada, o vídeo começa sem anúncio e sem a espera do pulo.
- [ ] Numa instalação nova, com "Legendas" no padrão (desligada), o primeiro vídeo já aparece sem legenda.
- [ ] O app pode ser usado sem conta do YouTube, e o login continua disponível para quem quiser.
- [ ] Anúncio que escapar do bloqueio continua sendo pulado como na v1.2.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Aviso "bloqueadores de anúncio não são permitidos" do YouTube | Ainda não visto no m.youtube.com; tratar se aparecer em outra issue |
| Anúncio embutido no próprio stream do vídeo (SSAI) | Não vem em campos separáveis da resposta; o pulo da v1.2 continua cobrindo o que mostrar `ad-showing` |
| WebView do celular (`LoginActivity`) | O celular é só controle remoto e nunca toca vídeo |
| Opção separada para bloquear e para pular | Decisão do usuário: uma só chave |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Abordagem | Remover os dados de anúncio das respostas do player antes da página lê-los, mantendo o pulo atual como reserva | Resposta do usuário | y - user: "Bloquear + pular (Recomendado)" |
| Configuração | A chave existente `skipAds` controla bloqueio e pulo; o rótulo vira "Bloquear anúncios" em todas as línguas; ligada por padrão | Resposta do usuário | y - user: "Mesma chave (Recomendado)" |
| Momento da injeção | Script no início do documento (`WebViewCompat.addDocumentStartJavaScript`, dependência nova `androidx.webkit:webkit`), só na origem `https://m.youtube.com` | `onPageFinished` chega depois de `ytInitialPlayerResponse` já ter sido lido; não há outro jeito nativo de rodar antes dos scripts da página | n |
| Campos removidos | `adPlacements`, `adSlots`, `playerAds`, `adBreakHeartbeatParams`, no topo da resposta do player | Os campos que agendam anúncios no player; o resto da resposta fica intacto | n |
| Onde a resposta passa | `JSON.parse`, `Response.prototype.json` e a variável global `ytInitialPlayerResponse`; também aninhada em `playerResponse` (achado no walk do T2: a navegação do SPA usa `/youtubei/v1/get_watch`, que responde `[{playerResponse}, {watchNextResponse}]`) | Primeira carga traz a resposta no HTML; navegação no SPA busca `/youtubei/v1/player` | n |
| Anúncios no feed (vídeos promovidos no Início e na busca) | Escondidos por CSS com a opção ligada | O pedido é "remover as propagandas por completo"; CSS é uma linha | n |
| Causa das legendas na instalação nova | Confirmada no DHU (pilha de chamadas): com os dados zerados, o próprio `ytmweb` chama `loadModule('captions')` depois de um salto no vídeo (o pulo do SponsorBlock em ~81 s de PAIbJhS0Pzw), fora da janela de 10 s do commit 6824b1f. Num estado já usado não acontece. A hipótese do `localStorage` estava errada: liga/desliga não grava nada lá | Reproduzido duas vezes (@81 e @82); o usuário notou que a legenda liga quando o patrocínio é pulado | y - user: "parece que quando pula patrocinio, legenda ativa" |
| Conserto das legendas | Com "Legendas" desligada, o app descarrega a legenda sempre que ela aparece (a cada 500 ms), o botão CC do player incluído | Resposta do usuário | y - user: "Bloquear sempre" |
| Auto Backup do Android (achado no walk) | Fora desta spec: a reinstalação restaura `app_webview` (cookies da sessão, storage) do backup | Resposta do usuário | y - user: "Fora desta spec" |
| Tela inicial do celular | Abre `https://m.youtube.com` em vez da tela de login; quem quer entrar usa o "Fazer login" do próprio YouTube, que segue para `accounts.google.com` na mesma WebView (o UA sem `; wv` continua) | Uma linha (troca de URL); quem já está logado chega no mesmo lugar que o `continue` de hoje | n |
| Sem conta: Histórico, Inscrições, Assistir mais tarde, playlists | Continuam mostrando "Você não está logado / Faça login no app do celular" no navegador de mídia do Android Auto | Esses feeds só existem com conta; a mensagem já existe e segue verdadeira | n |
| Sem conta: Início | Mostra o que o YouTube mostra para visitante (pode vir vazio pedindo uma pesquisa); a pesquisa funciona | Comportamento do próprio YouTube, fora do controle do app | n |
| README | "Requisitos" passa a dizer que a conta é opcional (en e pt-BR) | O requisito deixa de existir | n |
| Opção desligada | Respostas e feed ficam intactos; vale na próxima resposta, sem recarregar | Mesma leitura ao vivo das outras flags (`yta.flag`) | n |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Vídeo começa sem anúncio ⭐ MVP

**User Story**: Como motorista, quero que o vídeo comece direto, sem anúncio nem espera de pulo.

**Why P1**: É o pedido da issue #2.

**Acceptance Criteria**:

1. WHERE "Bloquear anúncios" está ligada, WHEN uma resposta passa por `JSON.parse` com `adPlacements`, `adSlots`, `playerAds` ou `adBreakHeartbeatParams` no topo THEN the app SHALL entregar à página a resposta sem esses campos
2. WHERE "Bloquear anúncios" está ligada, WHEN uma resposta é lida por `Response.prototype.json` com esses campos THEN the app SHALL entregar à página a resposta sem esses campos
3. WHERE "Bloquear anúncios" está ligada, WHEN a página atribui `ytInitialPlayerResponse` com esses campos THEN the app SHALL guardar a resposta sem esses campos
4. The app SHALL manter intactos os demais campos das respostas e as respostas sem campos de anúncio
5. IF "Bloquear anúncios" está desligada THEN the app SHALL entregar as respostas sem alteração
6. The app SHALL rodar o bloqueio no início de cada documento de `https://m.youtube.com`, antes dos scripts da página
7. IF a WebView não suporta script no início do documento THEN the app SHALL abrir a página normalmente, só com o pulo da v1.2
8. WHERE "Bloquear anúncios" está ligada, WHILE o player mostra um anúncio (`ad-showing`) the app SHALL continuar levando o anúncio ao fim e clicando "Pular anúncio", como na v1.2
9. WHEN um vídeo é aberto no carro com "Bloquear anúncios" ligada THEN the app SHALL começar o vídeo sem o player passar por `ad-showing`
10. WHERE "Bloquear anúncios" está ligada, WHEN uma resposta traz a resposta do player aninhada em `playerResponse` (objeto, ou itens de uma lista como a de `/youtubei/v1/get_watch`) THEN the app SHALL entregar essa `playerResponse` sem os campos de anúncio

**Independent Test**: no DHU, abrir 5 vídeos seguidos com a opção ligada: nenhum mostra anúncio; desligar e abrir de novo: anúncios voltam.

---

### P2: Feed sem anúncios

**User Story**: Como motorista, quero o Início e a busca sem vídeos promovidos.

**Why P2**: Completa o "remover por completo"; não causa espera.

**Acceptance Criteria**:

1. WHERE "Bloquear anúncios" está ligada the app SHALL esconder os blocos de anúncio do feed (`ad-slot-renderer`, `ytm-promoted-sparkles-web-renderer`, `ytm-companion-ad-renderer`)
2. IF "Bloquear anúncios" está desligada THEN the app SHALL mostrar os blocos de anúncio do feed

---

### P3: Rótulo da opção

**User Story**: Como usuário, quero que o nome da opção diga o que ela faz.

**Why P3**: Texto apenas.

**Acceptance Criteria**:

1. The app SHALL mostrar a opção `skipAds` como "Block ads", "Bloquear anúncios", "Bloquear anuncios", "Bloquer les annonces", "Blocca annunci", "Блокировать рекламу" e "حظر الإعلانات" (en, pt, es, fr, it, ru, ar), ligada por padrão

---

### P1: Legenda desligada já na instalação nova ⭐ MVP

**User Story**: Como motorista que acabou de instalar o app, quero que os vídeos venham sem legenda, como diz o padrão, sem ter que ligar e desligar a opção.

**Why P1**: Bug visível no primeiro uso; pedido do usuário.

**Acceptance Criteria**:

1. WHEN o primeiro vídeo abre no carro numa instalação nova, com "Legendas" nunca alterada (desligada) THEN the app SHALL mostrar o vídeo sem legenda
2. WHILE "Legendas" está desligada, IF o YouTube liga a legenda a qualquer momento do vídeo (salto do SponsorBlock, anúncio, autoplay sem som, botão CC do player) THEN the app SHALL descarregar a legenda em até 0,5 s
3. WHERE "Legendas" está ligada, WHEN um vídeo abre THEN the app SHALL mostrar o vídeo com legenda, como na v1.2

**Independent Test**: limpar os dados do app (`adb shell pm clear dev.begati.youtubeauto`), entrar na conta, abrir no carro um vídeo com legendas automáticas: aparece sem legenda.

---

### P1: Usar sem conta do YouTube ⭐ MVP

**User Story**: Como usuário sem conta (ou que não quer entrar), quero abrir o app e assistir no carro sem fazer login.

**Why P1**: Pedido do usuário; hoje o login é obrigatório no celular.

**Acceptance Criteria**:

1. WHEN o app do celular abre THEN the app SHALL carregar `https://m.youtube.com`, sem passar pela tela de login do Google
2. WHILE não há conta logada the app SHALL permitir pesquisar e tocar vídeos no carro e enviar vídeos do celular para o carro
3. WHEN o usuário toca "Fazer login" no m.youtube.com do celular THEN the app SHALL abrir o login do Google na mesma tela e, após entrar, voltar ao m.youtube.com logado
4. WHILE há conta logada the app SHALL abrir o m.youtube.com já logado, com o mesmo comportamento da v1.2 no carro
5. The app SHALL mostrar em "Requisitos" do README (en e pt-BR) que a conta do YouTube é opcional

**Independent Test**: `adb shell pm clear dev.begati.youtubeauto`, abrir o app: m.youtube.com sem login; pesquisar e enviar um vídeo ao carro: toca. Tocar "Fazer login", entrar: volta logado.

---

## Edge Cases

- IF a resposta não é um objeto (número, texto, lista, `null`) THEN the app SHALL entregá-la sem alteração
- IF o objeto `yta` ainda não existe quando uma resposta passa THEN the app SHALL tratar a opção como ligada (o padrão)

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| ADS-01 | P1 (AC 1) | Execute | Implementing |
| ADS-02 | P1 (AC 2) | Execute | Implementing |
| ADS-03 | P1 (AC 3) | Execute | Implementing |
| ADS-04 | P1 (AC 4) | Execute | Implementing |
| ADS-05 | P1 (AC 5) | Execute | Implementing |
| ADS-06 | P1 (AC 6) | Execute | Implementing |
| ADS-07 | P1 (AC 7) | Execute | Implementing |
| ADS-08 | P1 (AC 8) | Execute | Implementing |
| ADS-09 | P1 (AC 9) | Execute | Implementing |
| ADS-10 | P2 (AC 1) | Execute | Implementing |
| ADS-11 | P2 (AC 2) | Execute | Implementing |
| ADS-12 | P3 (AC 1) | Execute | Implementing |
| ADS-13 | P1 (AC 10) | Execute | Implementing |
| CC-01 | P1 Legenda (AC 1) | Execute | Implementing |
| CC-02 | P1 Legenda (AC 2) | Execute | Implementing |
| CC-03 | P1 Legenda (AC 3) | Execute | Implementing |
| ANON-01 | P1 Sem conta (AC 1) | Execute | Implementing |
| ANON-02 | P1 Sem conta (AC 2) | Execute | Implementing |
| ANON-03 | P1 Sem conta (AC 3) | Execute | Implementing |
| ANON-04 | P1 Sem conta (AC 4) | Execute | Implementing |
| ANON-05 | P1 Sem conta (AC 5) | Execute | Implementing |

**Coverage:** 21 total, 0 mapped to tasks, 20 unmapped ⚠️

---

## Sources

- Issue #2 "Propagandas" (2026-11-14): "Seria possivel remover as propagandas por completo? Mesmo na v1.2, ele pulando elas, ainda tem aquele tempo de espera."
- `YoutubeCarAppService.kt` - `PAGE_JS` (pulo de anúncio, `SKIP_AD_BUTTONS`), `FLAGS` (`skipAds`), WebView do carro em `onSurfaceAvailable`
- Pedido do usuário (2026-11-15): "tem central que assim que vc instala o app no cel, o vídeo aparece legenda, mesmo com o padrão sendo legenda desligado, tem que entrar na config do app, ligar de desligar a legenda."
- Pedido do usuário (2026-11-15): "consegue tirar a obrigatoriedade de uma conta do youtube quando abre o app? o usuário poder usar de forma anonima" / "add isso no tlc tb na spec"
- `LoginActivity.onCreate` - `loadUrl("https://accounts.google.com/ServiceLogin?service=youtube&continue=https://m.youtube.com")`; `MediaService` / `feedNotice` (aviso "Você não está logado")
- Commit 6824b1f - pulo da v1.2 (issue #1)

---

## Success Criteria

- [ ] Com a opção ligada, nenhum de 5 vídeos seguidos no DHU mostra anúncio.
- [ ] Após `pm clear`, o primeiro vídeo no DHU aparece sem legenda.
- [ ] Após `pm clear`, o celular abre o m.youtube.com sem login e um vídeo enviado toca no carro.
- [ ] Testes de unidade e `./gradlew assembleDebug` verdes.
