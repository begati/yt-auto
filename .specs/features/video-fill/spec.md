# Video Fill Specification

**Approved**: 2026-11-11 - user: "aprovado"

## Problem Statement

Na página do vídeo, a WebView é encolhida para a área visível que o host do Android Auto informa. Isso deixa
uma margem preta em volta do vídeo e encolhe o vídeo toda vez que as barras de botões aparecem. Além disso o
vídeo sempre usa `object-fit:contain`, sem opção de ocupar a tela inteira.

## Goals

- [ ] Na página do vídeo em tela cheia, o vídeo ocupa a superfície inteira do carro, sem margem, e os botões ficam por cima.
- [ ] O motorista alterna a proporção do vídeo (Ajustar / Preencher / Esticar) com um botão no carro.
- [ ] A proporção padrão é escolhida nas Configurações do carro e do celular.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Superfície inteira fora da página do vídeo (listas, pesquisa, canais) | Usuário escolheu "só na página do vídeo": listas continuam dentro da área visível para nada ficar sob botões/cards |
| Tela cheia nativa do player (`onShowCustomView`) | Já ocupa o `FrameLayout` inteiro hoje; a proporção dessa view não é controlada por CSS |
| Teste instrumentado de WebView/CSS e do template do carro | Projeto testa só na JVM (decisão anterior, "só JVM"); verificação visual no carro/DHU |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| O que é a "borda" | A margem entre a superfície do carro e a área visível do host | Resposta do usuário: "Margem da área visível" | y - user: "Margem da área visível (Recomendado)" |
| Onde o vídeo ocupa a superfície inteira | Só em `/watch` com a opção "Tela cheia ao abrir vídeo" ligada | Resposta do usuário | y - user: "Só na página do vídeo (Recomendado)" |
| Modos do botão | Ciclo Ajustar (`contain`) → Preencher (`cover`) → Esticar (`fill`) → Ajustar | Resposta do usuário | y - user: "Ajustar / Preencher / Esticar" |
| O botão muda o padrão salvo? | Não: vale até o processo do app terminar; o padrão só muda nas Configurações | Resposta do usuário | y - user: "Não, só até fechar o app (Recomendado)" |
| Padrão de fábrica da proporção | Ajustar (`contain`) | É o comportamento atual | y - user: "aprovado" |
| Mudar a proporção nas Configurações com uma escolha do botão ativa | A escolha do botão é descartada e vale o novo padrão | Quem acabou de escolher um padrão espera vê-lo aplicado | y - user: "aprovado" |
| Onde fica o botão | Barra de ações do mapa (ícone, ao lado da engrenagem); a barra principal já tem o máximo de 4 ações | Limite do `NavigationTemplate` | y - user: "aprovado" |
| Retorno ao tocar | Toast do carro com o nome do modo ("Ajustar", "Preencher", "Esticar") | Botão do mapa só tem ícone; sem toast o motorista não sabe o modo | y - user: "aprovado" |
| Botão fora da página do vídeo | Visível sempre; muda o modo, que se aplica ao próximo vídeo | Esconder exigiria reconstruir o template a cada navegação | y - user: "aprovado" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Vídeo sem margem, botões por cima ⭐ MVP

**User Story**: Como motorista, quero que o vídeo ocupe a tela inteira do carro, para não perder espaço com margem nem ver o vídeo encolher quando os botões aparecem.

**Why P1**: É a reclamação principal (itens 1 e 2 do pedido).

**Acceptance Criteria**:

1. WHILE a página é `/watch` e a opção "Tela cheia ao abrir vídeo" está ligada the app SHALL dispor a WebView sobre a superfície inteira do carro, ignorando a área visível do host
2. WHILE a página não é `/watch` the app SHALL dispor a WebView dentro da área visível do host, como hoje
3. WHILE a opção "Tela cheia ao abrir vídeo" está desligada the app SHALL dispor a WebView dentro da área visível do host, inclusive em `/watch`
4. WHEN a navegação troca entre `/watch` e outra página THEN the app SHALL refazer a disposição da WebView pela regra dos critérios 1-3

**Independent Test**: no DHU, abrir um vídeo: sem margem preta; tocar na tela para mostrar os botões: o vídeo não muda de tamanho.

---

### P1: Botão de proporção ⭐ MVP

**User Story**: Como motorista, quero alternar a proporção do vídeo com um toque, para preencher a tela quando a proporção do vídeo não bate com a do carro.

**Why P1**: Item 3 do pedido.

**Acceptance Criteria**:

5. WHEN o motorista toca no botão de proporção THEN the app SHALL trocar o modo atual pelo próximo do ciclo Ajustar → Preencher → Esticar → Ajustar
6. WHEN o modo muda THEN the app SHALL mostrar um toast com o nome do novo modo ("Ajustar", "Preencher" ou "Esticar")
7. The app SHALL aplicar ao vídeo de `/watch` o `object-fit` do modo atual: Ajustar = `contain`, Preencher = `cover`, Esticar = `fill`
8. WHEN o motorista toca no botão de proporção THEN the app SHALL manter o padrão salvo nas Configurações inalterado
9. WHILE nenhum modo foi escolhido pelo botão desde que o app abriu the app SHALL usar o padrão salvo nas Configurações

**Independent Test**: no DHU, com um vídeo 16:9 numa tela de outra proporção, tocar o botão três vezes: barras, recorte, distorção, barras de novo.

---

### P1: Proporção padrão nas Configurações ⭐ MVP

**User Story**: Como motorista, quero escolher a proporção padrão, para não precisar tocar no botão em todo vídeo.

**Why P1**: Item 3 do pedido.

**Acceptance Criteria**:

10. The app SHALL oferecer "Proporção do vídeo" nas Configurações do carro e do celular, com as opções "Ajustar", "Preencher" e "Esticar", nessa ordem
11. WHILE nenhuma proporção foi salva the app SHALL usar Ajustar como padrão
12. IF o valor salvo da proporção não é uma das três opções THEN the app SHALL usar Ajustar
13. WHEN a proporção padrão muda nas Configurações THEN the app SHALL descartar o modo escolhido pelo botão e aplicar o novo padrão

**Independent Test**: escolher "Preencher" no celular, abrir um vídeo no carro: vídeo recortado sem tocar no botão.

---

## Edge Cases

- IF o valor salvo da proporção é inválido THEN the app SHALL usar Ajustar (critério 12)
- WHEN o botão é tocado fora de `/watch` THEN the app SHALL mudar o modo, que vale para o próximo vídeo aberto

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| FILL-01 | P1: Vídeo sem margem (AC 1) | Execute | Implementing |
| FILL-02 | P1: Vídeo sem margem (AC 2) | Execute | Implementing |
| FILL-03 | P1: Vídeo sem margem (AC 3) | Execute | Implementing |
| FILL-04 | P1: Vídeo sem margem (AC 4) | Execute | Implementing |
| ASPECT-01 | P1: Botão de proporção (AC 5) | Execute | Implementing |
| ASPECT-02 | P1: Botão de proporção (AC 6) | Execute | Implementing |
| ASPECT-03 | P1: Botão de proporção (AC 7) | Execute | Implementing |
| ASPECT-04 | P1: Botão de proporção (AC 8) | Execute | Implementing |
| ASPECT-05 | P1: Botão de proporção (AC 9) | Execute | Implementing |
| ASPECT-06 | P1: Proporção padrão (AC 10) | Execute | Implementing |
| ASPECT-07 | P1: Proporção padrão (AC 11) | Execute | Implementing |
| ASPECT-08 | P1: Proporção padrão (AC 12) | Execute | Implementing |
| ASPECT-09 | P1: Proporção padrão (AC 13) | Execute | Implementing |

**Coverage:** 13 total, 13 mapped to tasks, 0 unmapped

---

## Sources

- Pedido do usuário (2026-11-11): remover borda, botões sobre o vídeo, botão de proporção + padrão nas Configurações
- Ambiente de teste do usuário (2026-11-11): servidor do Android Auto + telefone `motorola_signature` via USB em modo depuração - verificação visual dos critérios 1-9 e 13 é feita nele
- `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt` - `fitToArea`, `PAGE_JS` (`object-fit:contain`), `NavigationTemplate`

---

## Success Criteria

- [ ] Na página do vídeo não há margem preta entre o vídeo e a borda da tela do carro.
- [ ] Mostrar/esconder os botões não muda o tamanho do vídeo.
- [ ] Os três modos são alcançáveis pelo botão e pelas Configurações.
