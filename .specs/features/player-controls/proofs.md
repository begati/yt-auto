# player-controls proofs

**HEAD**: 03b7b6a
**Tree**: clean
**Shell**: /bin/sh
**Result**: 4 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `walk - DHU HD + DevTools: getComputedStyle de player-middle-controls e de cada botão é hidden com playing-mode` | pass (Walked) |
| C2 | `walk - DHU HD: screenshot com os controles mostrados tem a barra e "m:ss / m:ss"` | pass (Walked) |
| C3 | `walk - DHU HD: "tela cheia ao abrir" desligada, controles mostrados: anterior, play e próximo visíveis; opção ` | pass (Walked) |
| Build | `./gradlew assembleDebug` | pass |
