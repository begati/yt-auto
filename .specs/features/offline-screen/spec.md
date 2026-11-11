# Offline Screen Specification

**Approved**: 2026-11-11 - user: "aprovado"

## Problem Statement

Sem internet, a WebView do carro mostra a página de erro padrão do Android ("Página da Web não disponível"),
e fica nela: o motorista precisa navegar de novo quando o sinal volta. O app deve mostrar uma tela própria
de carregamento e tentar de novo sozinho até o YouTube carregar.

## Goals

- [ ] A página de erro padrão da WebView nunca aparece no carro por falta de rede.
- [ ] Quando o sinal volta, a página que falhou carrega sozinha, sem toque do motorista.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Erros HTTP do YouTube (404, 500) | Não são falta de rede; a página do YouTube já mostra a própria mensagem |
| Falha de recursos dentro da página (imagem, vídeo, anúncio) | Só a página principal decide se há algo para mostrar |
| Tela de carregamento em carregamentos normais (com rede) | Pedido é para a falta de internet; o YouTube já mostra o próprio carregamento |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Quando mostrar | Toda vez que a página principal falha por erro de rede, não só na abertura | Resposta do usuário | y - user: "Qualquer falha de página (Recomendado)" |
| Como tentar de novo | Assim que o Android avisa que a rede voltou e, por garantia, a cada 5 s | Resposta do usuário | y - user: "A cada 5 s + ao voltar a rede (Recomendado)" |
| App do celular | A tela de login do celular (`LoginActivity`) ganha a mesma tela e as mesmas tentativas, com a mesma lógica compartilhada | Pedido do usuário no meio da execução | y - user: "trata isso também no app do celular" |
| Visual | Fundo preto, animação de carregamento girando e o texto "Sem conexão. Tentando de novo…" | Resposta do usuário | y - user: "Fundo escuro, animação e texto (Recomendado)" |
| O que conta como erro de rede | Só `ERROR_HOST_LOOKUP`, `ERROR_CONNECT`, `ERROR_TIMEOUT`, `ERROR_IO` e `ERROR_UNKNOWN` na página principal; os demais mostram a página de erro da WebView | O Verifier mostrou que um link `intent://` com internet cobria a tela com "Sem conexão" e tentava de novo para sempre | y - user: "Só erros de rede (Recomendado)" |
| Nova tentativa | Recarrega a página que falhou (`reload`), sem empilhar entradas no histórico do "Voltar" | "Voltar" depois de reconectar não deve cair em páginas de erro | y - user: "aprovado" |
| Botões durante a tela | Continuam ativos; "Início" e "Pesquisar" trocam a página que está sendo tentada | Mais simples e útil: o motorista pode desistir da página que falhou | y - user: "aprovado" |
| Onde a tela fica | Ocupa o mesmo espaço da página (inclui o limite do card de mídia, spec `video-fill`) | Consistência com a página | y - user: "aprovado" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Tela de carregamento sem internet ⭐ MVP

**User Story**: Como motorista, quero ver uma tela de carregamento em vez da página de erro do Android quando estou sem sinal, e que o YouTube volte sozinho quando o sinal voltar.

**Why P1**: É o pedido inteiro.

**Acceptance Criteria**:

1. WHEN a página principal falha por erro de rede THEN the app SHALL mostrar, por cima da página, fundo preto, uma animação de carregamento girando e o texto "Sem conexão. Tentando de novo…"
2. WHILE a tela de carregamento está visível the app SHALL recarregar a página que falhou a cada 5 s
3. WHEN o Android avisa que uma rede com internet ficou disponível WHILE a tela de carregamento está visível THEN the app SHALL recarregar a página que falhou imediatamente
4. WHEN a página carrega sem erro THEN the app SHALL esconder a tela de carregamento e parar as tentativas
5. IF a página falha de novo numa tentativa THEN the app SHALL manter a tela de carregamento visível, sem mostrar a página de erro da WebView entre as tentativas
6. IF um recurso que não é a página principal falha THEN the app SHALL manter a página como está, sem a tela de carregamento
7. WHEN o motorista toca "Voltar" depois de reconectar THEN the app SHALL voltar à página anterior à que falhou, sem passar por páginas de erro
9. IF a página principal falha por um erro que não é de rede (link de outro app `intent://`, esquema desconhecido, URL inválida, SSL) THEN the app SHALL mostrar a página de erro da WebView, sem a tela de carregamento nem novas tentativas
8. WHERE a página é a tela do app no celular (login do YouTube) the app SHALL aplicar os critérios 1-6 do mesmo jeito

**Independent Test**: no DHU, desligar Wi-Fi e dados do telefone e abrir o app: tela de carregamento; religar: o Início do YouTube aparece sozinho em até 5 s.

---

## Edge Cases

- WHEN o motorista toca "Início" ou faz uma pesquisa com a tela visível THEN the app SHALL tentar a nova página no lugar da que falhou (critérios 2-4 valem para ela)
- WHEN a tela do carro é recriada (troca de tamanho) com a tela de carregamento visível THEN the app SHALL recarregar `pageUrl` e aplicar os critérios 1-5 de novo

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| OFF-01 | P1 (AC 1) | Execute | Pending |
| OFF-02 | P1 (AC 2) | Execute | Pending |
| OFF-03 | P1 (AC 3) | Execute | Pending |
| OFF-04 | P1 (AC 4) | Execute | Pending |
| OFF-05 | P1 (AC 5) | Execute | Pending |
| OFF-06 | P1 (AC 6) | Execute | Pending |
| OFF-07 | P1 (AC 7) | Execute | Pending |
| OFF-08 | P1 (AC 8) | Execute | Pending |
| OFF-09 | P1 (AC 9) | Execute | Pending |

**Coverage:** 9 total, 9 mapped to tasks, 0 unmapped

---

## Sources

- Pedido do usuário (2026-11-11): "quando o app é iniciado mas não há sinal de internet, abre uma página de erro do próprio webview do android. é possível implementar uma página de carregamento, com uma animação de loading enquanto fica tentando carregar a página do youtube?"
- `YoutubeCarAppService.kt` - `YoutubeScreen.onSurfaceAvailable` (WebView, `WebViewClient`), `fitToArea`
- Ambiente de teste: DHU + telefone via USB; rede cortada com `adb shell svc wifi disable` e `svc data disable` (o ADB por USB continua)

---

## Success Criteria

- [ ] Com o telefone sem rede, o carro nunca mostra a página de erro da WebView.
- [ ] Ao religar a rede, o YouTube aparece sem toque, em até 5 s.
