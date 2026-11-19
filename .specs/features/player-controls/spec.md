# Player Controls Specification

**Approved**: 2026-11-19 - user: "sim"

## Problem Statement

No vídeo em tela cheia no carro, o app escondia só o play/pause do meio do player do YouTube, e só no modo "tocando".
Anterior e próximo ficavam, e o play voltava pausado, carregando, entre vídeos e em anúncio. O usuário viu os dois
defeitos no DHU HD: "fica o de próximo vídeo e vídeo anterior. muitas vezes o botão de play também aparece".

## Goals

- [ ] No vídeo em tela cheia no carro, nenhum dos três botões do meio do YouTube aparece, em nenhum estado do player.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Controles do topo do YouTube | Já escondidos (commit b6bb860) |
| Barra de progresso e tempo do YouTube | Ficam: o toque na barra pula para o ponto (feature video-controls) |
| Vídeo fora da tela cheia (opção "tela cheia ao abrir" desligada) | Sem a barra nativa por cima do player; fica como o YouTube desenha |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Botões do meio | Esconder anterior, play/pause e próximo em qualquer estado | Play/pause fica no botão nativo e na barra de mídia; anterior/próximo na barra de mídia e no volante | y - user: "Esconder os 3 sempre (Recomendado)" |
| Como esconder | `visibility:hidden` no `player-middle-controls` sob `html.yta-watch` | Não mexe no layout do player; o toque passa para o vídeo | y - user: "sim" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Player sem botões do meio no carro ⭐ MVP

**User Story**: Como motorista, quero um só play/pause (o do Android Auto) no vídeo, sem botões do YouTube no meio da imagem.

**Why P1**: É o defeito relatado.

**Acceptance Criteria**:

1. WHILE um vídeo `/watch` está em tela cheia no carro the app SHALL esconder os botões anterior, play/pause e próximo do meio do player do YouTube nos modos tocando, pausado, carregando, entre vídeos e anúncio
2. WHILE um vídeo `/watch` está em tela cheia no carro the app SHALL manter visíveis a barra de progresso e o tempo do YouTube
3. WHILE a opção "tela cheia ao abrir" está desligada the app SHALL mostrar os botões do meio como o YouTube desenha

**Independent Test**: no DHU, vídeo tocando, pausado pelo botão nativo e logo após um pulo: nenhum botão no meio; a barra de progresso aparece com um toque.

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| PCTL-01 | P1 (AC 1) | Execute | Pending |
| PCTL-02 | P1 (AC 2) | Execute | Pending |
| PCTL-03 | P1 (AC 3) | Execute | Pending |

**Coverage:** 3 total, 3 mapped, 0 unmapped

## Sources

- Relato do usuário no DHU HD (2026-11-19)
- Classes do player lidas por DevTools: `.html5-video-player` com `playing-mode` / `paused-mode`; botões em `player-middle-controls` (`.player-middle-controls-prev-next-button`, `.player-control-play-pause-icon`)

## Checks

**Build**: `./gradlew assembleDebug`

**C1** - Tela cheia: os três botões do meio ficam com `visibility: hidden` tocando, pausado e logo após um pulo (PCTL-01, AC 1)
Proof: walk - DHU HD + DevTools: `getComputedStyle` de `player-middle-controls` e de cada botão é `hidden` com `playing-mode`, `paused-mode` e após `currentTime += 60`; screenshots com os controles mostrados por um toque

**C2** - A barra de progresso e o tempo continuam visíveis (PCTL-02, AC 2)
Proof: walk - DHU HD: screenshot com os controles mostrados tem a barra e "m:ss / m:ss"

**C3** - Sem tela cheia os botões do meio aparecem (PCTL-03, AC 3)
Proof: walk - DHU HD: "tela cheia ao abrir" desligada, controles mostrados: anterior, play e próximo visíveis; opção religada depois

## Handoff

**Walked:** 2026-11-19, orchestrator, DHU 1280x720 (160 dpi) + phone over USB, debug build installed. Reach: open YT Auto on the DHU, Início, tap a video (fullscreen on, the default). C1: DevTools `getComputedStyle` of `player-middle-controls`, the two prev/next buttons and the play/pause button: `hidden` x4 in `paused-mode` (paused by the native play/pause), in `playing-mode,buffering-mode` right after `currentTime += 60` and in `playing-mode`. C2: playing, a tap showed YouTube's controls: "1:12 / 25:20" and the progress bar at the bottom, nothing in the middle (screenshot h3). C3: with the controls shown (shown by a tap), the middle controls and play button read `hidden/hidden` with `html.yta-watch` and `visible/visible` with the class removed, which is the state with "tela cheia ao abrir" off (the page only adds `yta-watch` when that flag is on); the class was put back. Round 2 (after the Verifier, descendants hidden too): video seeked to `duration - 2` and polled every 0.7 s through the end: `playing-mode` then `ended-mode`, all 27 elements in and under `player-middle-controls` hidden or zero-sized in every poll (screenshot i1: black ended screen with only "40:23 / 40:23" and the bar). An ad was not reproduced: the page strips ads before the player reads them (`page_start.js`) and skips the rest; the rule now hides every descendant whatever the player mode, so no state can bring a button back through `visibility:visible`.
