# Big Buttons Specification

**Approved**: 2026-11-18 - user: "aprovado, segue para as tarefas"

## Problem Statement

Um usuário com central multimídia de 7" reclamou que os ícones ficaram pequenos. As sobreposições do app já foram
aumentadas, mas os botões da action strip (voltar, home, play/pause, busca, proporção, configurações) são desenhados
pelo Android Auto, que decide o tamanho: o app não consegue aumentá-los. A saída é o app desenhar os próprios botões,
grandes, sobre a superfície do carro.

## Goals

- [ ] Voltar, home, play/pause, busca e proporção ficam em botões do app com área de toque de pelo menos 76 × 76 dp
  (mínimo das diretrizes de design de carro do Google) numa barra no rodapé da tela do carro.
- [ ] Nada que a barra nativa fazia se perde: cada botão faz exatamente o que a ação nativa correspondente faz hoje.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Configurações como botão próprio | O `NavigationTemplate` exige uma ação nativa ("Action strip must contain at least one action"); configurações, a menos usada, fica como essa ação |
| Opção para ligar/desligar os botões grandes | Decisão do usuário: "Sem opção" |
| Aumentar os ícones das telas de lista/menu (home, busca, configurações) | São templates desenhados pelo Android Auto |
| Botões novos (avançar/voltar 10 s, próximo vídeo) | Só os botões que já existem mudam de lugar e tamanho |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Quais botões viram próprios | Voltar, home, play/pause, busca e proporção; configurações fica nativo, sozinho na action strip, e a map action strip sai | Template exige 1 ação nativa | y - user: "5 próprios + config nativo (Recomendado)" |
| Posição | Barra horizontal no rodapé da tela do carro | Alcance do polegar; não cobre o card de curva (topo esquerdo) | y - user: "Barra inferior (Recomendado)" |
| Visibilidade | Com vídeo tocando numa página `/watch`, a barra some 5 s depois do último toque e volta com um toque; fora disso fica sempre visível | Igual ao hábito da barra nativa, sem cobrir o vídeo à toa | y - user: "Somem no vídeo (Recomendado)" |
| Opção nas configurações | Nenhuma: sempre botões próprios | Decisão do usuário | y - user: "Sem opção" |
| Centrais sem toque na superfície | Em host com Car API < 5 (sem `onClick` da superfície) a barra própria não aparece e a action strip nativa volta como hoje | O app não recebe o toque nesses hosts; botão que não responde é pior que botão pequeno | y - user: "aprovado, segue para as tarefas" |
| Toque com a barra escondida | Mostra a barra e o toque segue para a página como hoje (o vídeo, o toque duplo e a barra de progresso continuam funcionando) | Não muda nenhum gesto existente | y - user: "aprovado, segue para as tarefas" |
| Página sob a barra fixa | Fora do vídeo (barra sempre visível) a página termina acima da barra; no vídeo a barra flutua por cima | Lista com o último item escondido atrás da barra não teria como ser tocada | y - user: "aprovado, segue para as tarefas" |
| Tamanho | Alvo de toque de 76 × 76 dp por botão, ícone de 40 dp | 76 dp é o mínimo das diretrizes de carro do Google; 40 dp acompanha a seta de 72 dp do card de curva | y - user: "aprovado, segue para as tarefas" |
| Aparência | Mesma família das sobreposições atuais: fundo preto translúcido com cantos arredondados, ícones brancos, sombra leve; ripple ao tocar | Visual já estabelecido no card de curva e no painel de arrastar | y - user: "aprovado, segue para as tarefas" |
| Play/pause | Aparece nas mesmas condições de hoje (`playToggleShown`: tocando, ou numa página de vídeo) e troca o ícone com o estado | Comportamento atual | y - user: "aprovado, segue para as tarefas" |
| Rótulos | Cada botão tem `contentDescription` com as strings que já existem (`back`, `home_home`, `pause`/`play`, `search`, `aspect_title`) | Acessibilidade; sem strings novas | y - user: "aprovado, segue para as tarefas" |
| Camadas | A barra fica por cima do vídeo, da capa de só áudio e da tela offline | A barra nativa ficava por cima de tudo; no modo só áudio ainda é preciso pausar e voltar | y - user: "aprovado, segue para as tarefas" |
| Idioma da direita para a esquerda | A ordem dos botões espelha em árabe | Padrão do Android para layouts RTL | y - user: "aprovado, segue para as tarefas" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Botões grandes no rodapé ⭐ MVP

**User Story**: Como motorista com uma central de 7", quero botões grandes para voltar, ir para o início, pausar,
pesquisar e mudar a proporção, para acertar o toque sem tirar os olhos da estrada por mais tempo.

**Why P1**: É a reclamação inteira.

**Acceptance Criteria**:

1. WHERE o host tem Car API 5 ou mais THEN the app SHALL mostrar uma barra no rodapé da tela do carro com os botões voltar, home, play/pause (quando `playToggleShown`), busca e proporção, nessa ordem
2. The app SHALL dar a cada botão da barra uma área de toque de pelo menos 76 × 76 dp na densidade da tela do carro
3. WHERE o host tem Car API 5 ou mais THEN the app SHALL deixar na action strip nativa só o botão de configurações e SHALL não definir a map action strip
4. WHEN o motorista toca em voltar THEN the app SHALL sair da tela cheia nativa do vídeo, se ela estiver aberta, e senão voltar uma página no WebView
5. WHEN o motorista toca em home THEN the app SHALL abrir a lista "Página inicial" (`HomeMenuScreen`)
6. WHEN o motorista toca em play/pause THEN the app SHALL alternar a reprodução do vídeo e SHALL trocar o ícone para o novo estado
7. WHEN o motorista toca em busca THEN the app SHALL abrir a tela de pesquisa (`SearchScreen`)
8. WHEN o motorista toca em proporção THEN the app SHALL passar para a próxima proporção (Ajustar, Preencher, Esticar), mostrar o aviso com o nome dela e trocar o ícone do botão
9. WHEN o motorista toca fora dos botões THEN the app SHALL tratar o toque como hoje (página, toque duplo, barra de progresso, arrastar)
10. IF o host tem Car API menor que 5 THEN the app SHALL não mostrar a barra própria e SHALL manter a action strip e a map action strip nativas como hoje

**Independent Test**: no DHU, cada botão da barra faz o que a ação nativa fazia; `uiautomator`/Layout Inspector mostra
cada botão com 76 dp ou mais.

---

### P1: Barra some durante o vídeo

**User Story**: Como motorista assistindo um vídeo parado, quero que a barra saia da frente e volte com um toque.

**Why P1**: Barra fixa cobriria o rodapé do vídeo o tempo todo.

**Acceptance Criteria**:

1. WHILE um vídeo toca numa página `/watch` the app SHALL esconder a barra 5 s depois do último toque na tela do carro
2. WHEN o motorista toca na tela com a barra escondida THEN the app SHALL mostrar a barra e SHALL entregar o toque à página como hoje
3. WHILE nenhum vídeo toca, ou a página não é `/watch`, the app SHALL manter a barra visível
4. WHILE a barra fica sempre visível the app SHALL terminar a página acima da barra, sem conteúdo atrás dela
5. The app SHALL mostrar a barra por cima do vídeo, da capa de só áudio e da tela offline

**Independent Test**: no DHU, com vídeo tocando, a barra some em 5 s e volta num toque; pausado, ela fica.

---

## Edge Cases

- IF o vídeo é pausado com a barra escondida THEN the app SHALL mostrar a barra
- WHEN a área visível muda (card de mídia ao lado) THEN the app SHALL manter a barra dentro da área da página
- IF a tela do carro é recriada (nova superfície) THEN the app SHALL recriar a barra com o estado atual de play/pause e proporção

---

## Design Brief

- **Job:** controle de um toque, em movimento, de olhos quase na estrada. Modo Operate: acerto do toque > expressão.
- **Direção:** mesma família das sobreposições existentes (card de curva, painel de arrastar): placa preta
  translúcida de cantos arredondados, ícones brancos, nada de cor nova. A barra é uma placa só, centralizada no
  rodapé, com margem da borda; os botões têm o mesmo peso, play/pause no centro do grupo.
- **Feedback:** ripple no toque; entrada e saída da barra em fade curto (~200 ms), sem deslizar sobre o vídeo.
- **Não pode:** cobrir o card de curva; parecer um controle do YouTube (para não confundir com o player da página);
  ícone menor que 40 dp.

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| BTN-01 | P1: Botões grandes no rodapé (AC 1) | Tasks | Pending |
| BTN-02 | P1: Botões grandes no rodapé (AC 2) | Tasks | Pending |
| BTN-03 | P1: Botões grandes no rodapé (AC 3) | Tasks | Pending |
| BTN-04 | P1: Botões grandes no rodapé (AC 4-8) | Tasks | Pending |
| BTN-05 | P1: Botões grandes no rodapé (AC 9) | Tasks | Pending |
| BTN-06 | P1: Botões grandes no rodapé (AC 10) | Tasks | Pending |
| BTN-07 | P1: Barra some durante o vídeo (AC 1-3) | Tasks | Pending |
| BTN-08 | P1: Barra some durante o vídeo (AC 4) | Tasks | Pending |
| BTN-09 | P1: Barra some durante o vídeo (AC 5) | Tasks | Pending |
| BTN-10 | Edge Cases | Tasks | Pending |

**Coverage:** 10 total, 0 mapped to tasks, 10 unmapped ⚠️

## Sources

- Reclamação de um usuário com central de 7": ícones pequenos (relatada pelo usuário em 2026-11-18)
- `NavigationTemplate.Builder` (car-app 1.4.0): "Action strip for this template must be set"; `ActionStrip.Builder`: "Action strip must contain at least one action"
- `onClick` da superfície: só em hosts com Car API 5 ou mais (comentário em `YoutubeCarAppService.kt:1607`)

## Success Criteria

- [ ] O usuário da central de 7" consegue usar voltar, home, pausar, busca e proporção sem errar o toque
- [ ] Nenhum gesto atual (toque duplo, toque na barra de progresso, arrastar) deixa de funcionar
