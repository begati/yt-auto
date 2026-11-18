# Gesture Hint Specification

**Approved**: 2026-11-18 - user: "sim"

## Problem Statement

Os gestos da tela do carro (feature `video-controls`: toque duplo, toque na barra, arrastar para o lado) não têm
botão nem rótulo. Quem não lê o README não descobre que existem. Pedido do usuário: "faz a dica no app na primeira vez".

## Goals

- [ ] Na primeira vez que um vídeo toca no carro, o motorista vê uma dica dos gestos, uma única vez.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Tutorial com várias telas ou animação dos gestos | Pedido é uma dica; tela extra no carro distrai quem dirige |
| Opção para rever a dica | O README explica os gestos (seção "Na tela do carro") |
| Teste instrumentado do toast | Projeto testa só na JVM; a regra fica numa função pura, o toast é provado no walk |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Quando mostrar | Quando o vídeo de uma página `/watch` começa a tocar no carro pela primeira vez | É quando os gestos passam a valer | y - user: "sim" |
| Quantas vezes | Uma vez por instalação (marca salva nas preferências; limpar dados mostra de novo) | Pedido: "na primeira vez" | y - user: "sim" |
| Forma | `CarToast` longo (~3,5 s), o mesmo aviso do Android Auto que o app já usa ("Patrocínio pulado", proporção) | Nativo do host, não cobre o vídeo, some sozinho | y - user: "sim" |
| Texto (pt) | "Dica: toque duas vezes no vídeo para pular 10 s, ou arraste para o lado para avançar" | Os dois gestos mais úteis; o toque na barra é o do YouTube e se descobre sozinho | y - user: "sim" |
| Idiomas | Os 7 do app (en, pt, es, fr, it, ru, ar) | `I18nTest` exige todas as strings em todos | y - user: "sim" |
| Quem já usa o app | Também vê a dica uma vez depois de atualizar | Os gestos são novos para todos | y - user: "sim" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Dica dos gestos na primeira vez ⭐ MVP

**User Story**: Como motorista, quero saber que posso dar dois toques ou arrastar no vídeo, para usar os controles sem ter lido o README.

**Why P1**: É o pedido inteiro.

**Acceptance Criteria**:

1. WHEN um vídeo de uma página `/watch` começa a tocar no carro e a dica nunca foi mostrada THEN the app SHALL mostrar um `CarToast` longo com o texto `gesture_hint` no idioma do celular
2. WHEN a dica é mostrada THEN the app SHALL salvar que ela foi mostrada, e SHALL não mostrá-la de novo, nem depois de reiniciar o app
3. IF o vídeo toca fora de `/watch` (prévia no feed, miniplayer) THEN the app SHALL não mostrar a dica nem marcá-la como mostrada

**Independent Test**: com a marca apagada, abrir um vídeo no DHU mostra a dica; abrir outro vídeo não mostra.

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| HINT-01 | P1: Dica (AC 1) | Execute | Pending |
| HINT-02 | P1: Dica (AC 2) | Execute | Pending |
| HINT-03 | P1: Dica (AC 3) | Execute | Pending |

**Coverage:** 3 total, 0 mapped, 3 unmapped ⚠️ (Tasks skipped: one step, checks below)

---

## Sources

- Pedido do usuário, 2026-11-18: "faz a dica no app na primeira vez"
- `.specs/features/video-controls/spec.md` - os gestos

---

## Success Criteria

- [ ] A dica aparece uma única vez, no primeiro vídeo tocado no carro.

---

## Checks

**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

**C1** - O primeiro vídeo em `/watch` pede a dica (HINT-01, AC 1)
Proof: run unit | HINT-01 the first video on watch asks for the hint PASSED

**C2** - Depois de mostrada, a dica não volta, nem com as preferências relidas (HINT-02, AC 2)
Proof: run unit | HINT-02 the hint shows only once PASSED

**C3** - Vídeo fora de `/watch` não mostra nem gasta a dica (HINT-03, AC 3)
Proof: run unit | HINT-03 a video off watch neither shows nor uses up the hint PASSED

**C4** - O toast aparece no carro com o texto no idioma do celular (HINT-01, AC 1)
Proof: walk - DHU com a marca apagada: screenshot do toast ao abrir um vídeo; segundo vídeo sem toast

---

## Handoff

**Settled mid-build:** none
**Dispatched:** Verifier at HEAD aba2790 (diff e5041bd..aba2790); no screens sub-agent (no capture)
**Walked:** 2026-11-18, orchestrator, DHU 800x480 + phone 0091479724, debug build installed over the previous one with data kept (`gestureHintShown` absent from `shared_prefs/settings.xml` before). Reach: Início, tap a feed title. C4: the first video on `/watch` shows the car toast "Dica: toque duas vezes no vídeo para pular 10 s, ou arraste para o lado para avançar" (two lines, whole text) for ~3 s, then it goes; prefs now hold `gestureHintShown=true`. `history.back()` to Início, tap another video: it plays on `/watch` (`paused` false) and no toast shows in screenshots over 8 s.
