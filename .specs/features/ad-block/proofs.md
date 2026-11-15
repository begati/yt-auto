# ad-block proofs

**HEAD**: 707fe67
**Tree**: clean
**Shell**: /bin/sh
**Result**: 24 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run js / ✔ ADS-01 JSON.parse drops the ad fields` | pass |
| C2 | `run js / ✔ ADS-02 Response.json drops the ad fields` | pass |
| C3 | `run js / ✔ ADS-03 ytInitialPlayerResponse is stored without the ad fields` | pass |
| C4 | `run js / ✔ ADS-04 other fields and ad-free responses stay intact` | pass |
| C5 | `run js / ✔ ADS-05 with the flag off responses pass unchanged` | pass |
| C6 | `run js / ✔ EDGE non-object responses pass unchanged` | pass |
| C7 | `run js / ✔ EDGE without yta the flag reads as on` | pass |
| C23 | `run js / ✔ ADS-13 a nested playerResponse loses the ad fields, alone or inside a list` | pass |
| C8 | `walk - chrome://inspect no carro: window.__ytaStart existe e ytInitialPlayerResponse.adPlacements é undefined ` | pass (Walked) |
| C9 | `walk - build com o suporte forçado a falso: página abre, anúncio aparece e é levado ao fim pelo pulo da v1.2` | pass (Walked) |
| C10 | `walk - com o bloqueio forçado a falso e a opção ligada, o anúncio é levado ao fim (mesma corrida de C9)` | pass (Walked) |
| C11 | `walk - 5 vídeos seguidos no DHU: nenhum mostra anúncio; desligando a opção, anúncios voltam` | pass (Walked) |
| C12 | `walk - Início e busca no DHU com a opção ligada: document.querySelectorAll('ad-slot-renderer,ytm-promoted-spar` | pass (Walked) |
| C13 | `walk - mesma busca com a opção desligada: os blocos aparecem` | pass (Walked) |
| C14 | `run unit / ADS-12 the ads setting reads Block ads in every language and is on by default PASSED` | pass |
| C15 | `walk - pm clear, login, vídeo com legendas automáticas no DHU: sem legenda` | pass (Walked) |
| C16 | `walk - pm clear, PAIbJhS0Pzw enviado pelo celular, loadModule instrumentado: depois do pulo do SponsorBlock em` | pass (Walked) |
| C17 | `walk - "Legendas" ligada no celular, mesmo vídeo no DHU: com legenda` | pass (Walked) |
| C18 | `walk - pm clear e abrir o app: m.youtube.com sem login` | pass (Walked) |
| C19 | `walk - sem conta: pesquisa no carro toca; vídeo tocado no celular toca no carro` | pass (Walked) |
| C20 | `walk - tocar "Fazer login" no celular, entrar: volta ao m.youtube.com com o avatar da conta` | pass (Walked) |
| C21 | `walk - reabrir o app logado: m.youtube.com com a conta; Histórico no Android Auto lista vídeos` | pass (Walked) |
| C22 | `grep -qi "account.*optional" README.md && grep -qi "conta.*opcional" README.pt-BR.md` | pass |
| Build | `./gradlew assembleDebug` | pass |

## Output

`Run js` - exit 0

```
✔ ADS-02 Response.json drops the ad fields (0.877074ms)
✔ ADS-03 ytInitialPlayerResponse is stored without the ad fields (0.853048ms)
✔ ADS-04 other fields and ad-free responses stay intact (1.231101ms)
✔ ADS-05 with the flag off responses pass unchanged (1.051329ms)
✔ ADS-13 a nested playerResponse loses the ad fields, alone or inside a list (2.037984ms)
✔ EDGE non-object responses pass unchanged (1.86743ms)
✔ EDGE without yta the flag reads as on (0.921004ms)
ℹ tests 8
ℹ suites 0
ℹ pass 8
ℹ fail 0
ℹ cancelled 0
ℹ skipped 0
ℹ todo 0
ℹ duration_ms 80.139994
```

`Run unit` - exit 0

```

VideoFillTest > ASPECT-06 options are Ajustar Preencher Esticar in order PASSED

VideoFillTest > FILL-04 shown action strips do not shrink the page PASSED

VideoFillTest > ASPECT-08 invalid stored aspect reads as the default PASSED

VideoFillTest > ASPECT-04 the button pick is saved as the default PASSED

VideoFillTest > ASPECT-07 default on a fresh install is cover (fill the screen) PASSED

VideoFillTest > ASPECT-05 the saved aspect applies PASSED

BUILD SUCCESSFUL in 1s
22 actionable tasks: 4 executed, 18 up-to-date
```

`grep -qi "account.*optional" README.md && grep -qi "conta.*opcional" README.pt-BR.md` - exit 0

```
(no output)
```
