# Phone Remote Specification

**Approved**: 2026-11-11 - user: "aprovado"

## Problem Statement

Hoje o app do celular é um YouTube independente: tocar num vídeo nele toca no celular, enquanto o carro toca
outra coisa. O usuário quer que o celular funcione como um Chromecast: escolher o vídeo e controlar a
reprodução pelo celular, com o vídeo tocando só no Android Auto.

## Goals

- [ ] Tocar num vídeo no celular abre o vídeo no carro; o celular nunca toca vídeo.
- [ ] O celular mostra e controla o vídeo do carro: tocar/pausar, anterior, próximo, posição, proporção, qualidade e velocidade.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Fila de vários vídeos (adicionar à fila) | Não pedido; "próximo" usa o próximo do próprio player (playlist ou autoplay) |
| Controle de volume pelo celular | Não pedido; o volume é do carro |
| Controlar outro aparelho que não o próprio carro (Chromecast real, TV) | O pedido é celular → Android Auto |
| Tocar no celular quando o carro não está conectado | Usuário escolheu guardar o vídeo para o carro |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Sem carro conectado | O vídeo tocado no celular fica guardado e abre no carro quando o app for aberto lá; o celular não toca | Resposta do usuário | y - user: "Guarda pro carro" |
| Controles | Tocar/pausar, anterior, próximo, barra de tempo, proporção, qualidade e velocidade | Resposta do usuário | y - user: "Tocar/pausar, anterior, próximo (Recomendado), Barra de tempo para avançar/voltar (Recomendado), Proporção, qualidade e velocidade" |
| Layout | Barra fixa embaixo da tela do celular; o YouTube continua navegável acima | Resposta do usuário | y - user: "Barra fixa embaixo (Recomendado)" |
| Quando a barra aparece | Depois que um vídeo foi enviado ao carro ou o carro está com um vídeo; antes disso, só o YouTube | Sem vídeo não há o que controlar | y - user: "aprovado" |
| O que conta como "tocar num vídeo" | Toque em link de vídeo (`/watch?v=`, `/shorts/<id>`, `youtu.be/<id>`) e qualquer navegação do celular para `/watch` ou `/shorts`; Shorts abrem no carro como `/watch?v=<id>` | O carro já toca `/watch`; Shorts em tela vertical não cabem bem no carro | y - user: "aprovado" |
| Prévias do feed (vídeo que toca sozinho mudo no Início) | Pausadas no celular | "não reproduzindo o vídeo no celular" | y - user: "aprovado" |
| Anterior | Volta ao início do vídeo, como o botão do volante hoje | Mantém o comportamento atual | y - user: "aprovado" |
| Qualidade e velocidade pelo celular | Aplicadas na hora no vídeo do carro e salvas como padrão, como já acontece ao trocar no player | Mesmo comportamento das Configurações e do player | y - user: "aprovado" |
| Proporção pelo celular | Mesmo ciclo do botão do carro (Ajustar → Preencher → Esticar), só até fechar o app, sem mudar o padrão | Mesmo comportamento do botão do carro (spec `video-fill`) | y - user: "aprovado" |
| Anterior/próximo/tocar/pausar/posição | Pela `MediaSession` que o carro já publica; a posição e a duração passam a ser publicadas nela | O volante e o card de mídia do Android Auto ganham a barra de progresso junto | y - user: "aprovado" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Escolher no celular, tocar no carro ⭐ MVP

**User Story**: Como passageiro ou motorista parado, quero escolher o vídeo no celular e vê-lo tocar no carro, sem o celular tocar junto.

**Why P1**: É o núcleo do pedido.

**Acceptance Criteria**:

1. WHEN o usuário toca num link de vídeo no celular WHILE o app está aberto no carro THEN the app SHALL abrir esse vídeo no carro e manter o celular na página em que estava
2. WHEN o usuário toca num link de vídeo no celular WHILE o app não está aberto no carro THEN the app SHALL guardar o vídeo, mostrar na barra "Abra o YouTube Auto no carro para tocar" e abrir o vídeo no carro quando o app for aberto lá
3. The app SHALL nunca reproduzir vídeo no celular, inclusive as prévias do feed
4. IF o celular navega para `/watch` ou `/shorts` por outro caminho (autoplay, link externo) THEN the app SHALL voltar à página anterior no celular e abrir o vídeo no carro
5. WHEN o vídeo é um Short (`/shorts/<id>`) THEN the app SHALL abri-lo no carro como `https://m.youtube.com/watch?v=<id>`

**Independent Test**: com o DHU conectado e o app aberto no carro, tocar num vídeo do Início no celular: o vídeo toca no carro, o celular continua no Início e não toca nada.

---

### P1: Barra de controle no celular ⭐ MVP

**User Story**: Como passageiro, quero ver o que está tocando no carro e controlar pelo celular.

**Why P1**: Sem controle, o celular só escolhe o vídeo.

**Acceptance Criteria**:

6. WHILE um vídeo foi enviado ao carro ou o carro está com um vídeo the app SHALL mostrar embaixo da tela do celular uma barra com a capa, o título e o canal do vídeo do carro
7. WHEN o vídeo do carro muda (pelo celular, pelo carro ou pelo autoplay) THEN the app SHALL atualizar capa, título e canal na barra em até 2 s
8. WHEN o usuário toca tocar/pausar na barra THEN the app SHALL alternar a reprodução no carro e o ícone SHALL mostrar o estado do carro
9. WHEN o usuário toca "anterior" na barra THEN the app SHALL voltar o vídeo do carro ao início
10. WHEN o usuário toca "próximo" na barra THEN the app SHALL pular para o próximo vídeo do player do carro
11. WHILE o carro toca um vídeo the app SHALL mostrar na barra o tempo atual e a duração (`m:ss`, ou `h:mm:ss` a partir de 1 hora), atualizados a cada 1 s
12. WHEN o usuário solta a barra de tempo numa posição THEN the app SHALL levar o vídeo do carro para essa posição
13. WHEN o usuário escolhe "Proporção" na barra THEN the app SHALL avançar o ciclo Ajustar → Preencher → Esticar no carro, como o botão do carro, sem mudar o padrão salvo
14. WHEN o usuário escolhe uma qualidade na barra THEN the app SHALL aplicá-la ao vídeo do carro e salvá-la como padrão
15. WHEN o usuário escolhe uma velocidade na barra THEN the app SHALL aplicá-la ao vídeo do carro e salvá-la como padrão
16. WHILE nada foi enviado ao carro e o carro não tem vídeo the app SHALL esconder a barra

**Independent Test**: com um vídeo tocando no carro, usar cada controle da barra no celular e ver o efeito no DHU.

---

## Edge Cases

- WHEN o app do carro é fechado com um vídeo tocando THEN the app SHALL manter a barra com o último vídeo e o estado "pausado"
- IF o usuário toca num link de vídeo sem id reconhecível THEN the app SHALL deixar o celular seguir o link sem tocar vídeo (critério 3 continua valendo)

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| CAST-01 | P1: Escolher no celular (AC 1) | Tasks | Pending |
| CAST-02 | P1: Escolher no celular (AC 2) | Tasks | Pending |
| CAST-03 | P1: Escolher no celular (AC 3) | Tasks | Pending |
| CAST-04 | P1: Escolher no celular (AC 4) | Tasks | Pending |
| CAST-05 | P1: Escolher no celular (AC 5) | Tasks | Pending |
| REMOTE-01 | P1: Barra (AC 6) | Tasks | Pending |
| REMOTE-02 | P1: Barra (AC 7) | Tasks | Pending |
| REMOTE-03 | P1: Barra (AC 8) | Tasks | Pending |
| REMOTE-04 | P1: Barra (AC 9) | Tasks | Pending |
| REMOTE-05 | P1: Barra (AC 10) | Tasks | Pending |
| REMOTE-06 | P1: Barra (AC 11) | Tasks | Pending |
| REMOTE-07 | P1: Barra (AC 12) | Tasks | Pending |
| REMOTE-08 | P1: Barra (AC 13) | Tasks | Pending |
| REMOTE-09 | P1: Barra (AC 14) | Tasks | Pending |
| REMOTE-10 | P1: Barra (AC 15) | Tasks | Pending |
| REMOTE-11 | P1: Barra (AC 16) | Tasks | Pending |

**Coverage:** 16 total, 0 mapped to tasks, 16 unmapped

---

## Sources

- Pedido do usuário (2026-11-11): "quero que o app do celular sirva de controle remoto pros vídeos do android auto ... como se fosse um chromecast, inclusive, não reproduzindo o video no celular, só no android auto."
- `YoutubeCarAppService.kt` - `LoginActivity` (WebView do celular), `YoutubeScreen` (WebView do carro, `js()`), `sharedSession`/`idleCallback`/`pendingUrl` (sessão de mídia e vídeo guardado), `PAGE_JS` (`yta.time`, `yta.meta`)
- Ambiente de teste: DHU + telefone `motorola_signature` via USB

---

## Success Criteria

- [ ] Nenhum vídeo toca no celular em nenhum momento.
- [ ] Todo controle da barra tem efeito visível no DHU em até 2 s.
