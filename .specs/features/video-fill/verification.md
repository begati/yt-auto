# Video Fill verification

**Verdict**: FAIL (round 3: three findings open; round 2 was PASS, round 1 FAIL)
**Proofs**: `proofs.md` at c5ed75a
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | `VideoFillTest.kt` `FILL-01 margins and hidden strips are covered` | yes - round 3: asserts the exact bounds (0,0,800,400) for the DHU margin rect (12,12,788,388), so passing the raw area through fails it; the bounds take no URL, so `/watch` needs no case of its own (VideoFillTest.kt:70-71, YoutubeCarAppService.kt:253). AC 1 still says "ignorando a área visível", which AC 14 now overrides beside a card |
| C2 | walk - Início sem margem (Walked round 2) | yes - round 2: mesmo caminho de C1, a disposição não depende da página |
| C3 | walk - opção desligada no celular, WebView ainda na superfície inteira (Walked round 2) | yes - round 2: a disposição não lê a opção |
| C4 | `VideoFillTest.kt` `FILL-04 shown action strips do not shrink the page` | yes - round 3: the test covers the strips showing on a tap (12,88,712,388 -> whole surface), not navigation itself; navigation cannot resize because `webViewBounds` takes no URL and `fitToArea` runs only on surface build and area change (YoutubeCarAppService.kt:672,699) |
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
| C15 | `VideoFillTest.kt` `FILL-05 a media card beside the app limits the page` | partly - round 3: the right card (DHU rect), the left card and the bottom card hold exact bounds; the top card branch has no case, and no rect sits near the 1/3 cut, so a threshold of w/4 or `>=` passes (VideoFillTest.kt:80-84). Probe at HEAD: the cut is right (left 266 covered, 267 limits; top 300 -> 0,300,800,400) |
| C16 | `VideoFillTest.kt` `FILL-06 no visible area yet fills the surface` | yes - round 3: null -> (0,0,800,400). Area before the surface is kept and applied at build (YoutubeCarAppService.kt:672); after, on the change (699); a size rebuild runs `fitToArea` on the new WebView |
| C17 | walk - modo dividido do DHU, vídeo em Ajustar à esquerda do card (Walked round 3) | yes - round 3, author walk with measured rects; the split rect (22,34,464,376) is the one FILL-05 asserts. Native fullscreen (custom view) was not walked |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| Button choice is resurrected when the default changes A -> B -> A (AC 13 says discard) | `aspect()` keeps the tap while `tap.first == saved` (YoutubeCarAppService.kt:191-194); probe at HEAD: default contain, tap -> cover, set fill -> `fill`, set contain -> `cover` (expected `contain`) | clear `aspectTap` when the saved aspect changes (car `ChoiceScreen` callback and a prefs listener for the phone), or store a change counter instead of the saved value; extend ASPECT-09 test with the round trip | 559a070 - C13 |
| C10 does not prove the phone's "Proporção do vídeo" | `ASPECT-06` asserts `ASPECT in CHOICES`; phone uses an explicit `addPreference(list(ASPECT))` at YoutubeCarAppService.kt:878, not `CHOICES`; Walked line has no phone step | add a phone step to the walk (option listed with the three labels in order), or build the phone "Vídeo" list from `CHOICES` so the unit test covers both | 559a070 - C14 (walk) |
| Turning "Tela cheia ao abrir vídeo" off on the phone while on `/watch` leaves the WebView over the whole surface (AC 3) until the next navigation | `fitToArea()` runs only on navigation, area change and the car settings callback (YoutubeCarAppService.kt:568,636,659,687); the page's `yta-watch` class follows the flag by polling, so page and layout disagree | refit on a `SharedPreferences` change listener for `fullscreen` (or from the bridge poll) | 559a070 - C3 (critério mudado pelo usuário: a disposição não depende mais da opção) |
| `fillsSurface` and the page disagree on URLs `java.net.URI` rejects | probe: `https://m.youtube.com/watch?v=a%7Cb` (literal pipe) and `?v=a b` -> false, while `location.pathname` is `/watch` and the CSS fills; YoutubeCarAppService.kt:246-247 | compare the path without strict parsing (e.g. `android.net.Uri.parse(url).path` or `url.substringBefore('#').substringBefore('?')` path part) and add such a URL to FILL-01 | 559a070 - C1, C2 (`fillsSurface` removido: a disposição não depende mais da URL) |
| C15 does not pin the 1/3 cut nor the top card | VideoFillTest.kt:80-84 - insets 12, 88 (covered) and 200, 336 (limited); any threshold between 88 and 199 on h, or `>=`, passes; the `t > h / 3` branch (YoutubeCarAppService.kt:257) has no limited case | add rects at the cut (left 266 covered, 267 limited on 800) and a top card (e.g. top 300 -> 0,300,800,400) | |
| A native fullscreen video goes under the media card (AC 14) | `onShowCustomView` adds the custom view with default MATCH_PARENT params (YoutubeCarAppService.kt:658); `fitToArea` sizes only the WebView (702-710) | give the custom view the same bounds (or put both in a child frame sized by `fitToArea`) | |
| A rect that does not fit the surface collapses or inverts the WebView | probe at HEAD: empty rect (0,0,0,0) -> 0x0 page; inverted (500,12,300,388) -> width -200; `area` survives `teardown` (684-694), so a card rect (336,..,788) on a rebuilt 300-wide surface -> width -36 until the host resends; LayoutParams -1/-2 would read as MATCH_PARENT/WRAP_CONTENT | treat an empty or inverted rect as no area, clamp to the surface, and reset `area` when the size changes (or rely on the host resending it and say so in a test) | |
