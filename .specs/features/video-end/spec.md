# Video End Specification

**Approved**: 2026-11-19 - user: "aprovado, segue para as tarefas"

## Problem Statement

Quando um vídeo termina no carro e nada toca em seguida, a tela fica preta com só "40:23 / 40:23" e a barra no fim
(visto no DHU HD em 2026-11-19, depois que os botões do meio do YouTube foram escondidos). O motorista precisa achar
o home ou o voltar para sair dali. Pedido do usuário: "ao final do video, vai ficar uma tela preta, se nao tiver
nenhum video tocando... talvez valha a pena mostrar uma barra de progresso de uns 5s, levando para o ínicio (padrão
do que o usuário configurou)".

## Goals

- [ ] Um vídeo que termina sem outro em seguida leva sozinho à página inicial escolhida em 5 s, com a contagem à vista.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Botão "Assistir de novo" / "Próximo" na contagem | Pedido é voltar ao início; volante e barra de mídia já têm próximo |
| Ligar/desligar a contagem nas configurações | Não pedido |
| Mudar a reprodução automática do YouTube | É da conta do usuário no YouTube |
| Vídeo fora da tela cheia | A página do YouTube mostra os sugeridos embaixo; não fica tela preta |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Quando aparece | Só quando o vídeo termina e nada toca em seguida | Se a reprodução automática emendar outro vídeo, não há tela preta | y - user: "Só se nada tocar em seguida (Recomendado)" |
| Toque durante a contagem | Abre a página inicial na hora | Decisão do usuário | y - user: "Vai na hora" |
| Como saber se "nada toca em seguida" | Reprodução automática desligada (lida do botão do YouTube quando ele está na página): contagem logo ao terminar. Ligada ou desconhecida: contagem só se o vídeo continuar terminado 10 s depois | O botão de reprodução automática nem sempre está no DOM (ausente no vídeo terminado); 10 s dá tempo ao YouTube de abrir o próximo | y - user: "aprovado, segue para as tarefas" |
| Destino | A página inicial escolhida nas configurações ("Página inicial": Início, Inscrições ou Histórico) | "padrão do que o usuário configurou" | y - user: "aprovado, segue para as tarefas" |
| Visual | Painel no centro, da mesma família do painel de arrastar: "Voltando para Início" (nome da página escolhida) e uma barra que esvazia em 5 s | Mesmo estilo das sobreposições do carro | y - user: "aprovado, segue para as tarefas" |
| O que cancela | Tocar o vídeo de novo (play nativo, volante, barra de mídia), sair da página do vídeo (voltar, home, busca) | Quem agiu não quer ser levado embora | y - user: "aprovado, segue para as tarefas" |
| Por cima de | Vídeo e capa de só áudio | Com a capa, o motorista também fica parado num vídeo terminado | y - user: "aprovado, segue para as tarefas" |
| Idiomas | Texto nos 7 idiomas | `I18nTest` | y - user: "aprovado, segue para as tarefas" |
| Botão de tela cheia do YouTube | Esconder o botão "Entrar no modo tela cheia" (canto inferior direito, `.ytwPlayerBottomControlsFullscreenButtonWrapper`) no vídeo em tela cheia do carro, em qualquer estado; com "tela cheia ao abrir" desligada ele fica | Pedido do usuário: "adiciona também pra tirar o botao do webview do youtube de esticar o vídeo"; o vídeo do carro já ocupa a tela e a proporção é o botão nativo | y - user: "adiciona também pra tirar o botao do webview do youtube de esticar o vídeo" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Volta ao início no fim do vídeo ⭐ MVP

**User Story**: Como motorista, quando o vídeo acaba e nada mais toca, quero voltar sozinho à minha página inicial, sem procurar um botão.

**Why P1**: É o pedido inteiro.

**Acceptance Criteria**:

1. WHEN um vídeo em tela cheia termina com a reprodução automática desligada THEN the app SHALL mostrar a contagem de 5 s
2. WHEN um vídeo em tela cheia termina com a reprodução automática ligada ou desconhecida e continua terminado 10 s depois THEN the app SHALL mostrar a contagem de 5 s
3. IF outro vídeo começa a tocar antes da contagem THEN the app SHALL não mostrar a contagem
4. WHILE a contagem corre the app SHALL mostrar "Voltando para <página inicial>" e uma barra que esvazia em 5 s
5. WHEN a contagem chega ao fim THEN the app SHALL abrir a página inicial escolhida nas configurações
6. WHEN o motorista toca a tela durante a contagem THEN the app SHALL abrir a página inicial na hora
7. IF o vídeo volta a tocar ou a página sai do vídeo durante a contagem THEN the app SHALL esconder a contagem e não abrir a página inicial

**Independent Test**: no DHU, vídeo pulado para o fim com a reprodução automática desligada: o painel conta 5 s e abre Início; de novo, tocando no painel, abre na hora; de novo, apertando play, o painel some.

---

### P2: Sem o botão de tela cheia do YouTube

**User Story**: Como motorista, não quero o botão de tela cheia do YouTube no vídeo do carro, que já ocupa a tela e tem a proporção no botão nativo.

**Why P2**: Pedido junto com a contagem; independente dela.

**Acceptance Criteria**:

1. WHILE um vídeo `/watch` está em tela cheia no carro the app SHALL esconder o botão de tela cheia do YouTube e tudo dentro dele, em qualquer estado do player
2. WHILE a opção "tela cheia ao abrir" está desligada the app SHALL mostrar o botão de tela cheia como o YouTube desenha

**Independent Test**: no DHU, controles mostrados por um toque no vídeo tocando e no vídeo terminado: só o tempo e a barra, sem o ícone no canto.

---

## Edge Cases

- IF a página inicial escolhida é Inscrições ou Histórico THEN the app SHALL usar o nome dela no texto e abri-la
- IF o carro está com a capa de só áudio THEN the app SHALL mostrar a contagem por cima da capa

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| END-01 | P1 (AC 1) | Tasks | Pending |
| END-02 | P1 (AC 2) | Tasks | Pending |
| END-03 | P1 (AC 3) | Tasks | Pending |
| END-04 | P1 (AC 4) | Tasks | Pending |
| END-05 | P1 (AC 5) | Tasks | Pending |
| END-06 | P1 (AC 6) | Tasks | Pending |
| END-07 | P1 (AC 7) | Tasks | Pending |
| END-08 | Edge Cases | Tasks | Pending |
| END-09 | P2 (AC 1) | Tasks | Pending |
| END-10 | P2 (AC 2) | Tasks | Pending |

**Coverage:** 10 total, 0 mapped to tasks, 10 unmapped ⚠️

## Sources

- Pedido do usuário (2026-11-19), citado no Problem Statement
- DHU HD: vídeo terminado em tela cheia = `ended-mode`, `getPlayerState() === 0`, tela preta com tempo e barra; botão de reprodução automática (`.ytwAutonavToggleButtonHost`, aria-label "Reprodução automática desativada") fora do DOM nesse estado

## Success Criteria

- [ ] Nenhum vídeo terminado deixa o carro numa tela preta por mais de 15 s
