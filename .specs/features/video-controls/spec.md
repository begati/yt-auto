# Video Controls Specification

**Approved**: 2026-11-18 - user: "aprovado, pode seguir"

## Problem Statement

Issue #4 (GitHub): "Please add Video controls on the cars touchscreen (double tap to skip 10s, use the youtube
slider to skip parts of the video etc.)". No carro, o duplo toque do YouTube não funciona e a barra de progresso
não pula para o ponto tocado. Causa medida no DHU (Car App Library 1.4): o host do Android Auto entrega o duplo
toque como `onScale(x, y, 2.0)` (zoom de mapa) e não repassa nenhum dos dois toques à página; um toque simples na
barra chega só como DOWN/UP no mesmo instante, que o player ignora; um arrasto chega como `onScroll(dx, dy)`, sem a
posição onde começou. Os demais controles do YouTube (pausar, anterior, próximo) já funcionam com toque simples.

## Goals

- [ ] Duplo toque no vídeo pula 10 s para trás ou para frente, como no app do YouTube.
- [ ] Tocar na barra de progresso do YouTube pula para aquele ponto.
- [ ] Arrastar na horizontal sobre o vídeo avança ou volta, mostrando o tempo de destino.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Arrastar a bolinha da barra do YouTube | O host não informa onde o arrasto começou; o arrasto horizontal sobre o vídeo cobre o mesmo uso (resposta do usuário) |
| Gestos durante anúncios | O app já corre os anúncios até o fim ("Bloquear anúncios"); o gesto age no vídeo que estiver tocando |
| Pinça (zoom) | Não pedido; continua sem efeito |
| Gestos fora de `/watch` (Início, busca, canais) | Lá o arrasto continua rolando a página e o duplo toque não faz nada, como hoje |
| Teste instrumentado de WebView/Views | Projeto testa só na JVM; as regras ficam em funções puras testadas, a tela é provada no walk do DHU |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Divisão da tela no duplo toque | Metade esquerda do player volta 10 s, metade direita avança 10 s | Resposta do usuário | y - user: "Metades (Recomendado)" |
| Barra de progresso | Toque na barra pula para o ponto; arrasto horizontal sobre o vídeo avança/volta, largura do player = vídeo inteiro, com o tempo de destino na tela | Resposta do usuário | y - user: "Toque + arrasto (Recomendado)" |
| Retorno do duplo toque | Bolha no lado tocado com seta e "10 s", somando em duplos toques seguidos (20 s, 30 s), some ~0,6 s depois do último | Resposta do usuário | y - user: "Bolha na lateral (Recomendado)" |
| Janela para somar duplos toques | 1 s entre um duplo toque e o seguinte, mesmo lado | Mesma sensação do YouTube; a bolha ainda está na tela | n - padrão do agente |
| Quando o arrasto vira avanço | O primeiro `onScroll` do gesto decide: mais horizontal que vertical vira avanço; senão rola a página até o fim do gesto | Mantém a rolagem vertical da página com "Tela cheia ao abrir vídeo" desligado | n - padrão do agente |
| Fim do arrasto | 300 ms sem `onScroll`; o vídeo pula para o destino nesse momento (não a cada evento) | O host não manda "soltou"; pular a cada evento recarrega o buffer dezenas de vezes | n - padrão do agente |
| Barra com controles escondidos | Toque onde a barra fica, com os controles escondidos, só mostra os controles (como hoje) | A barra não está visível; pular sem ver o alvo surpreende | n - padrão do agente |
| Texto da bolha em outros idiomas | "%d s" traduzido como nas strings `rewind_10`/`forward_10` (ex.: "10 с", "10 ث") | O app tem 7 idiomas | n - padrão do agente |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Duplo toque pula 10 s ⭐ MVP

**User Story**: Como passageiro (ou motorista parado), quero dar dois toques no vídeo para pular 10 s, como no app do YouTube.

**Why P1**: Primeiro item da issue.

**Acceptance Criteria**:

1. WHEN o host entrega um duplo toque (`onScale` com fator 2,0) na metade direita do player numa página `/watch` THEN the app SHALL avançar o vídeo principal 10 s
2. WHEN o host entrega um duplo toque na metade esquerda do player numa página `/watch` THEN the app SHALL voltar o vídeo principal 10 s
3. IF o salto passa do início ou do fim do vídeo THEN the app SHALL parar em 0 ou na duração
4. WHEN um duplo toque pula o vídeo THEN the app SHALL mostrar no lado tocado uma bolha com a seta do sentido e "10 s"
5. WHEN outro duplo toque no mesmo lado chega até 1 s depois do anterior THEN the app SHALL pular mais 10 s e a bolha SHALL mostrar o total acumulado (20 s, 30 s, ...)
6. WHEN passam 0,6 s sem novo duplo toque THEN the app SHALL esconder a bolha
7. IF o host entrega `onScale` com fator diferente de 2,0 (pinça) THEN the app SHALL ignorá-lo
8. IF o duplo toque cai fora do player ou fora de `/watch` THEN the app SHALL não pular o vídeo nem mostrar a bolha

**Independent Test**: no DHU com vídeo aberto, dois toques rápidos à direita: `currentTime` sobe ~10 s e a bolha aparece à direita.

---

### P1: Toque na barra de progresso ⭐ MVP

**User Story**: Como passageiro, quero tocar num ponto da barra do YouTube para ir direto àquele trecho.

**Why P1**: Segundo item da issue ("use the youtube slider").

**Acceptance Criteria**:

9. WHILE os controles do YouTube estão visíveis, WHEN o host entrega um toque sobre a barra de progresso THEN the app SHALL pular o vídeo para a fração da duração igual à posição do toque na largura da barra
10. IF o toque cai fora da área da barra THEN the app SHALL repassar o toque à página como hoje
11. IF o vídeo não tem duração conhecida (ao vivo, carregando) THEN the app SHALL repassar o toque à página como hoje

**Independent Test**: no DHU, um toque mostra os controles; um toque no meio da barra leva `currentTime` para ~50% da duração.

---

### P1: Arrasto horizontal avança ou volta ⭐ MVP

**User Story**: Como passageiro, quero arrastar o dedo na horizontal sobre o vídeo para avançar ou voltar vendo para onde vou.

**Why P1**: Substitui arrastar a bolinha da barra, que o host não permite (resposta do usuário).

**Acceptance Criteria**:

12. WHILE a página é `/watch` com duração conhecida, WHEN um arrasto começa mais horizontal que vertical THEN the app SHALL tratar o gesto inteiro como avanço/retrocesso, sem rolar a página
13. WHILE o arrasto horizontal dura THEN the app SHALL mostrar no centro o tempo de destino e a duração ("m:ss / m:ss", ou "h:mm:ss" a partir de 1 h) e a diferença com sinal ("+1:20", "-0:35")
14. The app SHALL calcular o destino como o tempo no início do gesto mais (deslocamento horizontal acumulado / largura do player) × duração, entre 0 e a duração; dedo para a direita avança
15. WHEN passam 300 ms sem `onScroll` no gesto horizontal THEN the app SHALL pular o vídeo para o destino e esconder o indicador
16. WHEN um arrasto começa mais vertical que horizontal THEN the app SHALL rolar a página como hoje
17. IF a página não é `/watch` ou a duração não é conhecida THEN the app SHALL rolar a página como hoje

**Independent Test**: no DHU, arrastar da esquerda para a direita metade da largura: o indicador mostra +50% da duração e, ao soltar, `currentTime` vai para lá.

---

## Edge Cases

- IF o player muda de tamanho (card de mídia ao lado) THEN the app SHALL usar a área atual da WebView para as metades, a barra e a largura do arrasto (coberto por 1, 2, 9, 14: medidos na hora do gesto)
- IF a página navega para outro vídeo durante o arrasto THEN the app SHALL descartar o gesto sem pular (coberto por 15: o pulo vale só para o mesmo vídeo do início do gesto)

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| CTRL-01 | P1: Duplo toque (AC 1) | Tasks | Pending |
| CTRL-02 | P1: Duplo toque (AC 2) | Tasks | Pending |
| CTRL-03 | P1: Duplo toque (AC 3) | Tasks | Pending |
| CTRL-04 | P1: Duplo toque (AC 4) | Tasks | Pending |
| CTRL-05 | P1: Duplo toque (AC 5) | Tasks | Pending |
| CTRL-06 | P1: Duplo toque (AC 6) | Tasks | Pending |
| CTRL-07 | P1: Duplo toque (AC 7) | Tasks | Pending |
| CTRL-08 | P1: Duplo toque (AC 8) | Tasks | Pending |
| CTRL-09 | P1: Barra (AC 9) | Tasks | Pending |
| CTRL-10 | P1: Barra (AC 10) | Tasks | Pending |
| CTRL-11 | P1: Barra (AC 11) | Tasks | Pending |
| CTRL-12 | P1: Arrasto (AC 12) | Tasks | Pending |
| CTRL-13 | P1: Arrasto (AC 13) | Tasks | Pending |
| CTRL-14 | P1: Arrasto (AC 14) | Tasks | Pending |
| CTRL-15 | P1: Arrasto (AC 15) | Tasks | Pending |
| CTRL-16 | P1: Arrasto (AC 16) | Tasks | Pending |
| CTRL-17 | P1: Arrasto (AC 17) | Tasks | Pending |

**Coverage:** 17 total, 0 mapped to tasks, 17 unmapped ⚠️ (tasks come after approval)

---

## Sources

- GitHub issue #4 "Video controls" - o pedido
- Medição no DHU 2026-11-18 (log `YTAPROBE` temporário): toque simples = `onClick`; duplo toque = `onScale(x, y, 2.0)` sem `onClick`; toque na barra não muda `currentTime`

---

## Success Criteria

- [ ] Duplo toque, toque na barra e arrasto horizontal mudam `currentTime` no DHU como descrito, sem quebrar toque simples e rolagem fora de `/watch`.
