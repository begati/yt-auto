# Critique Fixes Specification

**Approved**: 2026-11-19 - user: "aprovado, segue para as tarefas"

## Problem Statement

A terceira crítica do impeccable (2026-11-19, 26/40, `.impeccable/critique/2026-11-19T14-22-29Z__…`) apontou cinco
problemas na tela do carro. Pedido do usuário: "Todos", começando pela contagem do fim do vídeo, implementados com o TLC.

## Goals

- [ ] Fechar os cinco problemas da crítica sem mudar o que o usuário já decidiu (toque na contagem vai na hora; contagem igual com a capa de só áudio; barras nativas do Android Auto).

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Margem para as barras do Android Auto em 800x480 | Decisão do usuário: "Aceitar (Recomendado)" - as barras somem sozinhas; margem faria a página pular |
| Toque na contagem cancelar | Decisão anterior do usuário (spec video-end): "Vai na hora" |
| Seguir a reprodução automática do YouTube com a capa de só áudio | Decisão do usuário: "Manter a contagem" |
| Opacidade do card de curva | Fica 0xE0 de propósito: fica sobre vídeo claro a viagem toda (comentário no código) |
| Meia-lua do toque duplo | É um degradê no lado tocado, não um painel; fica como está |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Segunda linha da contagem | "Toque para ir agora" (string `going_home_tap`), menor e 70% branca, abaixo do título | Diz o que o toque faz, que hoje é invisível | y - user: "aprovado, segue para as tarefas" |
| Contagem sobre a capa de só áudio | O painel desce para o rodapé (centralizado, 24 dp da borda) enquanto a capa aparece; sem a capa, fica no centro | Não cobre a arte e o título da capa | y - user: "aprovado, segue para as tarefas" |
| Tela Gestos | Lista de 3 linhas, cada uma com ícone, título curto e uma linha de detalhe: toque duplo (±10 s), arrastar para o lado (avançar ou voltar), toque na barra (ir ao ponto); sem ação ao tocar | Leitura de relance, em vez de um parágrafo de 45 palavras | y - user: "aprovado, segue para as tarefas" |
| Strings da tela Gestos | 6 strings novas nos 11 idiomas; `gestures_text` sai | Sem string morta | y - user: "aprovado, segue para as tarefas" |
| Proporção fora do vídeo | Some: fora de `/watch` a barra lateral fica só com configurações | Proporção só afeta o vídeo; muda o AC 6 da spec prev-next | y - user: "aprovado, segue para as tarefas" |
| Visual das sobreposições | Painéis do centro (contagem e arrastar) com cantos de 16 dp (como o card de curva) e barra de 6 dp nos dois | Uma só medida de canto e de barra | y - user: "aprovado, segue para as tarefas" |
| PRODUCT.md | Idiomas: 11 (en, pt-BR, es, fr, it, de, pl, tr, id, ru, ar) | Desatualizado desde as traduções novas | y - user: "aprovado, segue para as tarefas" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Contagem que diz o que fazer ⭐ MVP

**User Story**: Como motorista, quero saber na contagem do fim do vídeo que um toque me leva já, e não ter a capa de só áudio coberta.

**Why P1**: Problema P1 da crítica.

**Acceptance Criteria**:

1. WHILE a contagem corre the app SHALL mostrar, abaixo de "Voltando para <página>", a linha "Toque para ir agora" no idioma do app
2. WHILE a contagem corre com a capa de só áudio visível the app SHALL mostrar o painel no rodapé, abaixo do texto da capa
3. WHILE a contagem corre sem a capa de só áudio the app SHALL mostrar o painel no centro

**Independent Test**: no DHU, fim de vídeo: painel no centro com as duas linhas; com a capa de só áudio, o painel no rodapé.

---

### P1: Gestos em lista

**User Story**: Como motorista, quero ver os gestos em três linhas curtas, para entender de relance.

**Why P1**: Problema P1 da crítica.

**Acceptance Criteria**:

1. WHEN o motorista abre "Gestos" nas configurações do carro THEN the app SHALL mostrar uma lista com três linhas, nesta ordem: toque duplo, arrastar para o lado, toque na barra
2. The app SHALL dar a cada linha um ícone, um título e uma linha de detalhe, nos 11 idiomas

**Independent Test**: no DHU, Configurações > Gestos mostra as três linhas.

---

### P2: Proporção só no vídeo

**User Story**: Como motorista, não quero um botão de proporção onde ele não faz nada.

**Why P2**: Problema P2 da crítica.

**Acceptance Criteria**:

1. WHILE a página não é um vídeo the app SHALL mostrar na barra lateral só configurações
2. WHILE a página é um vídeo the app SHALL manter anterior, próximo (quando houver), proporção e configurações

**Independent Test**: no DHU, Início com só configurações na lateral; vídeo com os quatro.

---

### P3: Visual único das sobreposições

**User Story**: Como motorista, quero que os painéis do app pareçam da mesma família.

**Why P3**: Problema P3 da crítica.

**Acceptance Criteria**:

1. The app SHALL desenhar o painel da contagem e o painel de arrastar com cantos de 16 dp e barra de 6 dp
2. The PRODUCT.md SHALL listar os 11 idiomas do app

**Independent Test**: no DHU, painel de arrastar e contagem com os mesmos cantos e barra.

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| FIX-01 | Contagem (AC 1) | Tasks | Pending |
| FIX-02 | Contagem (AC 2-3) | Tasks | Pending |
| FIX-03 | Gestos (AC 1-2) | Tasks | Pending |
| FIX-04 | Proporção (AC 1-2) | Tasks | Pending |
| FIX-05 | Visual (AC 1) | Tasks | Pending |
| FIX-06 | Visual (AC 2) | Tasks | Pending |

**Coverage:** 6 total, 0 mapped to tasks, 6 unmapped ⚠️

## Sources

- Crítica do impeccable em `.impeccable/critique/2026-11-19T14-22-29Z__egati-youtubeauto-youtubecarappservice-kt-7019eb6a.md`
- Respostas do usuário (2026-11-19): "Contagem do fim (Recomendado)", "Todos", "Manter a contagem", "Aceitar (Recomendado)", "tenta usar o tlc pra implementar"
