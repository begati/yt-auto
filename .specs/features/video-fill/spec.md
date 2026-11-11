# Video Fill Specification

**Approved**: 2026-11-11 - user: "aprovado"

## Problem Statement

Em todas as páginas, a WebView é encolhida para a área visível que o host do Android Auto informa. Isso deixa
uma margem preta em volta do vídeo e encolhe o vídeo toda vez que as barras de botões aparecem. Além disso o
vídeo sempre usa `object-fit:contain`, sem opção de ocupar a tela inteira.

## Goals

- [ ] Em todas as páginas (vídeo e navegação), a WebView ocupa a superfície inteira do carro, sem margem, e os botões ficam por cima.
- [ ] O motorista alterna a proporção do vídeo (Ajustar / Preencher / Esticar) com um botão no carro.
- [ ] A proporção padrão é escolhida nas Configurações do carro e do celular.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Tela cheia nativa do player (`onShowCustomView`) | Já ocupa o `FrameLayout` inteiro hoje; a proporção dessa view não é controlada por CSS |
| Teste instrumentado de WebView/CSS e do template do carro | Projeto testa só na JVM (decisão anterior, "só JVM"); verificação visual no carro/DHU |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| O que é a "borda" | A margem entre a superfície do carro e a área visível do host | Resposta do usuário: "Margem da área visível" | y - user: "Margem da área visível (Recomendado)" |
| Volta à tela cheia com área antiga | Pedir novo template ao host 500 ms após a superfície voltar; o host reenvia a área real | DHU: o host manda por último a área do modo dividido e só corrige ~10 s depois, quando as barras somem | y - user: "quando eu tiro o controlador de mídia da lateral e jogo o app em toda a tela, tanto o Início quanto o vídeo em si, continuam com o tamanho antigo" |
| Proporção ao trocar de layout | A proporção em uso não muda com o layout | Pedido do usuário | y - user: "ele tem que obedecer a proporção do vídeo que foi configurado ou previamente setado pelo usuário" |
| Card de mídia ao lado do app (modo dividido do Android Auto) | WebView para na borda do card; corte de 1/3 da superfície separa card (recuo 336 de 800 no DHU) de margem/barras (recuo até 88) | Usuário relatou o vídeo cortado sob o card: "o vídeo não se encaixa na nova área disponível pra ele" | y - user: "o vídeo não se encaixa na nova área disponível pra ele, continua sendo renderizado em tela cheia, ficando cortado" |
| Onde a WebView ocupa a superfície inteira | Em todas as páginas; a opção "Tela cheia ao abrir vídeo" só controla se o player ocupa a página. Mudado no meio da execução: a resposta anterior era "Só na página do vídeo (Recomendado)" | Usuário, depois de ver o walk: a tela de navegação também deve ficar em tela cheia | y - user: "o vídeo ficou em tela cheia, mas a tela de navegação dos vídeos, ainda não" |
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

1. WHILE a página é `/watch` the app SHALL dispor a WebView sobre a superfície inteira do carro, ignorando a área visível do host
2. WHILE a página não é `/watch` (início, inscrições, pesquisa, canais) the app SHALL dispor a WebView sobre a superfície inteira do carro, ignorando a área visível do host
3. WHILE a opção "Tela cheia ao abrir vídeo" está desligada the app SHALL manter a WebView sobre a superfície inteira do carro
4. WHEN a navegação troca entre `/watch` e outra página THEN the app SHALL manter a WebView sobre a superfície inteira, sem mudar de tamanho
14. WHILE o host mostra um card ao lado do app (a área visível recua mais de 1/3 da superfície num lado) the app SHALL limitar a WebView à borda do card nesse lado; recuos de até 1/3 (margem do host, barras de botões) SHALL ser cobertos
15. WHILE o host ainda não informou a área visível the app SHALL dispor a WebView sobre a superfície inteira
17. WHEN o app volta do modo dividido para a tela cheia THEN the app SHALL ocupar a superfície inteira em até 1 s, mesmo que o host repita a área do modo dividido
18. WHILE o layout muda entre tela cheia e modo dividido the app SHALL manter no vídeo a proporção em uso (escolhida pelo botão ou o padrão salvo)
16. IF a área visível é vazia, invertida ou não cabe na superfície atual THEN the app SHALL dispor a WebView sobre a superfície inteira

**Independent Test**: no DHU, a lista do Início e um vídeo aberto sem margem preta; tocar na tela para mostrar os botões: o vídeo não muda de tamanho.

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
| FILL-01 | P1: Vídeo sem margem (AC 1) | Execute | Verified |
| FILL-02 | P1: Vídeo sem margem (AC 2) | Execute | Verified |
| FILL-03 | P1: Vídeo sem margem (AC 3) | Execute | Verified |
| FILL-04 | P1: Vídeo sem margem (AC 4) | Execute | Verified |
| FILL-05 | P1: Vídeo sem margem (AC 14) | Execute | Verified |
| FILL-06 | P1: Vídeo sem margem (AC 15) | Execute | Verified |
| FILL-07 | P1: Vídeo sem margem (AC 16) | Execute | Verified |
| FILL-08 | P1: Vídeo sem margem (AC 17) | Execute | Verified |
| FILL-09 | P1: Vídeo sem margem (AC 18) | Execute | Verified |
| ASPECT-01 | P1: Botão de proporção (AC 5) | Execute | Verified |
| ASPECT-02 | P1: Botão de proporção (AC 6) | Execute | Verified |
| ASPECT-03 | P1: Botão de proporção (AC 7) | Execute | Verified |
| ASPECT-04 | P1: Botão de proporção (AC 8) | Execute | Verified |
| ASPECT-05 | P1: Botão de proporção (AC 9) | Execute | Verified |
| ASPECT-06 | P1: Proporção padrão (AC 10) | Execute | Verified |
| ASPECT-07 | P1: Proporção padrão (AC 11) | Execute | Verified |
| ASPECT-08 | P1: Proporção padrão (AC 12) | Execute | Verified |
| ASPECT-09 | P1: Proporção padrão (AC 13) | Execute | Verified |

**Coverage:** 18 total, 18 mapped to tasks, 0 unmapped

---

## Sources

- Pedido do usuário (2026-11-11): remover borda, botões sobre o vídeo, botão de proporção + padrão nas Configurações
- Ambiente de teste do usuário (2026-11-11): servidor do Android Auto + telefone `motorola_signature` via USB em modo depuração - verificação visual dos critérios 1-9 e 13 é feita nele
- `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt` - `fitToArea`, `PAGE_JS` (`object-fit:contain`), `NavigationTemplate`

---

## Success Criteria

- [ ] Em nenhuma página há margem preta entre o vídeo e a borda da tela do carro.
- [ ] Mostrar/esconder os botões não muda o tamanho do vídeo.
- [ ] Os três modos são alcançáveis pelo botão e pelas Configurações.
