# Previous / Next Specification

**Approved**: 2026-11-19 - user: "sim"

## Problem Statement

A spec `player-controls` escondeu os botões anterior e próximo do meio do player do YouTube no vídeo em tela cheia do
carro. Pausar continua no botão nativo, mas anterior e próximo ficaram só no volante e no card de mídia. Pedido do
usuário: "o video perdeu o botao próximo e voltar, nem todo mundo tem controle da central com volante. pode valer a
pena esse botao no app, só que nem sempre tem próximo vídeo a ser exibido, tratar isso tb".

## Goals

- [ ] No vídeo do carro, anterior e próximo voltam como botões nativos do Android Auto, e próximo só aparece quando há um próximo vídeo.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Voltar a mostrar os botões do meio do YouTube | Decisão do usuário: botões na barra lateral nativa |
| Fila própria de vídeos | O próximo é o que o YouTube tem (playlist, mix ou sugestão) |
| Botões fora da página de vídeo | Início, busca e listas não têm vídeo atual |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Onde | Barra lateral nativa (map action strip), na ordem anterior, próximo, proporção, configurações | Cabe: o Android Auto aceita até 4 ações nela; estilo e tamanho do sistema | y - user: "Barra lateral nativa (Recomendado)" |
| Sem próximo vídeo | O botão próximo some | Sem botão morto | y - user: "Some (Recomendado)" |
| Anterior sem vídeo anterior | Volta ao início do vídeo; com vídeo anterior (playlist), vai a ele | Mesmo comportamento do volante e do card de mídia (o volante passou a usar a mesma regra, achado do Verifier) | y - user: "Volta ao início do vídeo (Recomendado)" |
| De onde vem "há próximo / há anterior" | Dos botões anterior/próximo do player do YouTube na página (ainda no DOM, só escondidos): habilitado = existe; a página avisa o app quando muda | É o que o YouTube faria ao tocar neles | y - user: "sim" |
| Quando aparecem | Em qualquer página de vídeo (`/watch`), tocando, pausado ou terminado | O vídeo atual é o ponto de partida dos dois | y - user: "sim" |
| Ação do próximo | `nextVideo()` do player, o mesmo do volante | Já usado pelo botão do volante (`JS_NEXT`) | y - user: "sim" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Anterior e próximo no vídeo do carro ⭐ MVP

**User Story**: Como motorista sem botões de mídia no volante, quero anterior e próximo na tela do carro, para trocar de vídeo sem procurar o card de mídia.

**Why P1**: É o pedido.

**Acceptance Criteria**:

1. WHILE a página é um vídeo `/watch` the app SHALL mostrar na barra lateral nativa, nesta ordem, anterior, próximo (quando houver), proporção e configurações
2. IF o YouTube não tem próximo vídeo THEN the app SHALL não mostrar o botão próximo
3. WHEN o motorista toca em próximo THEN the app SHALL abrir o próximo vídeo do YouTube
4. WHEN o motorista toca em anterior e o YouTube tem vídeo anterior THEN the app SHALL abrir o vídeo anterior
5. WHEN o motorista toca em anterior e o YouTube não tem vídeo anterior THEN the app SHALL voltar o vídeo atual ao início
6. WHILE a página não é um vídeo the app SHALL mostrar na barra lateral só configurações (era "proporção e configurações"; mudado em 2026-11-19 pela spec critique-fixes, FIX-04)
7. WHEN o YouTube passa a ter ou deixa de ter próximo vídeo THEN the app SHALL atualizar a barra lateral

**Independent Test**: no DHU, num vídeo de mix: anterior e próximo na barra; próximo troca de vídeo; num vídeo solto,
anterior volta ao início; no Início, só proporção e configurações.

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| PN-01 | P1 (AC 1) | Tasks | Pending |
| PN-02 | P1 (AC 2) | Tasks | Pending |
| PN-03 | P1 (AC 3) | Tasks | Pending |
| PN-04 | P1 (AC 4) | Tasks | Pending |
| PN-05 | P1 (AC 5) | Tasks | Pending |
| PN-06 | P1 (AC 6) | Tasks | Pending |
| PN-07 | P1 (AC 7) | Tasks | Pending |

**Coverage:** 7 total, 0 mapped to tasks, 7 unmapped ⚠️

## Design Brief

- **Job:** trocar de vídeo de relance, sem volante. Modo Operate.
- **Direção:** botões nativos do Android Auto (ícones `skip_previous` e `skip_next`, já no app), na coluna lateral
  junto da proporção e das configurações; o host desenha tamanho, estilo e quando aparecem e somem.
- **Não pode:** botão que não faz nada (próximo sem próximo vídeo); mudar a barra superior (voltar, home, play/pause, busca).

## Sources

- Pedido do usuário (2026-11-19)
- `ActionsConstraints.ACTIONS_CONSTRAINTS_MAP` (car-app 1.4.0): até 4 ações
- DevTools no DHU: `.player-middle-controls-prev-next-button` ×2 no DOM (anterior, próximo); vídeo de mix: os dois
  habilitados; vídeo solto: anterior com `icon-disable`, próximo habilitado (sugestão)
