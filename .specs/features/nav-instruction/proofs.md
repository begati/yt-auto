# nav-instruction proofs

**HEAD**: 3f4492c
**Tree**: clean
**Shell**: /bin/sh
**Result**: 21 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / TURN-01 a navigation notification from waze or maps gives distance and maneuver PASSED` | pass |
| C2 | `run unit / TURN-02 removing the notification hides the instruction PASSED` | pass |
| C3 | `run unit / TURN-03 other apps other categories and empty notifications are ignored PASSED` | pass |
| C4 | `run unit / TURN-04 nothing shows while the switch is off PASSED` | pass |
| C13 | `run unit / TURN-12 the switch is on by default PASSED` | pass |
| C14 | `run unit / CSET-01 car settings are quality speed captions navigation PASSED` | pass |
| C15 | `walk - acesso revogado: ligar a chave no DHU mostra o toast` | pass (Walked) |
| C18 | `run unit / TURN-15 the phone switch reads on only with notification access PASSED` | pass |
| C19 | `walk - DHU com card: desligar a chave no carro esconde o card na hora; religar o mostra; o mesmo pela chave do` | pass (Walked) |
| C20 | `walk - DHU: screenshot do card do Waze com o selo do Waze e do Maps com o selo do Maps` | pass (Walked) |
| C16 | `walk - README.md e README.pt-BR.md com a linha em Features e o print docs/car-turn.png` | pass (Walked) |
| C5 | `walk - celular: ligar "Navegação na tela" abre a tela de acesso a notificações do Android` | pass (Walked) |
| C6 | `walk - DHU com rota no Google Maps: screenshot do overlay com a seta do Maps` | pass (Walked) |
| C7 | `walk - DHU: card sobre a tela preta com a rede desligada; toque em "Podcasts" debaixo do card abre o chip. Cap` | pass (Walked) |
| C8 | `run unit / all languages have the same strings as English PASSED` | pass |
| C9 | `run unit / TURN-09 the last posted instruction wins PASSED` | pass |
| C10 | `run unit / TURN-10 the maneuver words pick the icon PASSED` | pass |
| C12 | `run unit / TURN-11 a title that is not a distance is the maneuver PASSED` | pass |
| C17 | `walk - DHU com card na tela: cmd notification disallow_listener faz o card sumir` | pass (Walked) |
| C11 | `walk - DHU com rota no Waze: screenshot do overlay sobre o vídeo; encerrar a rota o esconde` | pass (Walked) |
| Build | `./gradlew assembleDebug` | pass |

## Output

`Run unit` - exit 0

```

VideoFillTest > ASPECT-06 options are Ajustar Preencher Esticar in order PASSED

VideoFillTest > FILL-04 shown action strips do not shrink the page PASSED

VideoFillTest > ASPECT-08 invalid stored aspect reads as the default PASSED

VideoFillTest > ASPECT-04 the button pick is saved as the default PASSED

VideoFillTest > ASPECT-07 default on a fresh install is cover (fill the screen) PASSED

VideoFillTest > ASPECT-05 the saved aspect applies PASSED

BUILD SUCCESSFUL in 1s
22 actionable tasks: 1 executed, 21 up-to-date
```
