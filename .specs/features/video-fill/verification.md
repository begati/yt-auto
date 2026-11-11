# Video Fill verification

**Verdict**: PASS (round 2: findings closed; round 1 was FAIL)
**Proofs**: `proofs.md` at 3489ccb
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | walk - vídeo aberto sem margem (Walked round 2) | yes - round 2: critério mudado pelo usuário (superfície inteira em todas as páginas); `frame.addView(wv)` sem layoutParams, `fitToArea` removido (559a070) |
| C2 | walk - Início sem margem (Walked round 2) | yes - round 2: mesmo caminho de C1, a disposição não depende da página |
| C3 | walk - opção desligada no celular, WebView ainda na superfície inteira (Walked round 2) | yes - round 2: a disposição não lê a opção |
| C4 | walk - vídeo, Início, outro vídeo (Walked round 2) | yes - round 2: nenhum relayout na navegação |
| C5 | `VideoFillTest.kt` `ASPECT-01 tapping cycles contain cover fill contain` | yes - full cycle plus wrap and a non-default start |
| C6 | walk - toasts "Preencher", "Esticar", "Ajustar" (Walked line) | yes - toast text is `ASPECT.options.getValue(aspect(...))` after the tap (YoutubeCarAppService.kt:554-555) |
| C7 | walk - Ajustar barras, Preencher recortado, Esticar distorcido (Walked line, `getComputedStyle` read) | yes - CSS `object-fit:var(--yta-fit,contain)` and the poll sets `--yta-fit` from `yta.aspect()` (YoutubeCarAppService.kt:121,125,623) |
| C8 | `VideoFillTest.kt` `ASPECT-04 tapping leaves the saved default unchanged` | yes - whole prefs map compared after two taps |
| C9 | `VideoFillTest.kt` `ASPECT-05 without a tap the saved default applies` | yes - cover and fill |
| C10 | `VideoFillTest.kt` `ASPECT-06 options are Ajustar Preencher Esticar in order` | yes - lista do carro via `CHOICES`; o celular é provado por C14 |
| C11 | `VideoFillTest.kt` `ASPECT-07 default is contain` | yes |
| C12 | `VideoFillTest.kt` `ASPECT-08 invalid stored aspect reads as contain` | yes - one invalid value; probe also showed the cycle from an invalid value is cover, fill, contain |
| C13 | `VideoFillTest.kt` `ASPECT-09 changing the default drops the tapped mode` | yes - round 2: ida e volta coberta (fill, depois contain) e outra chave mantém a escolha; listener `dropAspectTap` chama `tapAfterChange`; walk pelo celular lê `contain` na volta (559a070) |
| C14 | walk - Configurações do celular: "Proporção do vídeo", Esticar chega ao carro como `fill` (Walked round 2) | yes - round 2, author walk (no second Verifier) |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| Button choice is resurrected when the default changes A -> B -> A (AC 13 says discard) | `aspect()` keeps the tap while `tap.first == saved` (YoutubeCarAppService.kt:191-194); probe at HEAD: default contain, tap -> cover, set fill -> `fill`, set contain -> `cover` (expected `contain`) | clear `aspectTap` when the saved aspect changes (car `ChoiceScreen` callback and a prefs listener for the phone), or store a change counter instead of the saved value; extend ASPECT-09 test with the round trip | 559a070 - C13 |
| C10 does not prove the phone's "Proporção do vídeo" | `ASPECT-06` asserts `ASPECT in CHOICES`; phone uses an explicit `addPreference(list(ASPECT))` at YoutubeCarAppService.kt:878, not `CHOICES`; Walked line has no phone step | add a phone step to the walk (option listed with the three labels in order), or build the phone "Vídeo" list from `CHOICES` so the unit test covers both | 559a070 - C14 (walk) |
| Turning "Tela cheia ao abrir vídeo" off on the phone while on `/watch` leaves the WebView over the whole surface (AC 3) until the next navigation | `fitToArea()` runs only on navigation, area change and the car settings callback (YoutubeCarAppService.kt:568,636,659,687); the page's `yta-watch` class follows the flag by polling, so page and layout disagree | refit on a `SharedPreferences` change listener for `fullscreen` (or from the bridge poll) | 559a070 - C3 (critério mudado pelo usuário: a disposição não depende mais da opção) |
| `fillsSurface` and the page disagree on URLs `java.net.URI` rejects | probe: `https://m.youtube.com/watch?v=a%7Cb` (literal pipe) and `?v=a b` -> false, while `location.pathname` is `/watch` and the CSS fills; YoutubeCarAppService.kt:246-247 | compare the path without strict parsing (e.g. `android.net.Uri.parse(url).path` or `url.substringBefore('#').substringBefore('?')` path part) and add such a URL to FILL-01 | 559a070 - C1, C2 (`fillsSurface` removido: a disposição não depende mais da URL) |

