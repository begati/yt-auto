# Nav Instruction verification

**Verdict**: FAIL
**Proofs**: `proofs.md` at ca7cddd (HEAD is now 11cdfe3, docs-only; rerun `check_proofs.py` at the closing HEAD)
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | `NavInstructionTest` `TURN-01 a navigation notification from waze or maps gives distance and maneuver` | yes - exact title/text for both packages, at the pure-rule layer; the listener's EXTRA_TITLE/EXTRA_TEXT read is covered only by the C11 walk (out of scope by spec) |
| C2 | `NavInstructionTest` `TURN-02 removing the notification hides the instruction` | yes - `current` null after remove |
| C3 | `NavInstructionTest` `TURN-03 other apps other categories and empty notifications are ignored` | yes - each of package, category null/other, blank title+text |
| C4 | `NavInstructionTest` `TURN-04 nothing shows while the switch is off` | partly - `navToShow` returns null when off, but nothing re-evaluates the screen when the switch changes (F1), so the card stays up with the switch off and no test can see it |
| C5 | walk - phone switch opens the access screen | yes - walked; code at `YoutubeCarAppService.kt:2406` matches |
| C6 | walk - Maps arrow over the card | yes - walked (Maps arrow tinted white) |
| C7 | walk - card over the audio cover and offline screen, touches pass | partly - the walk record says the audio-only cover was not reached; offline screen and pass-through were walked. Code: `navView` is the last child, no sibling has elevation, so it does draw above the cover (F6) |
| C8 | `I18nTest` `all languages have the same strings as English` | yes - key parity; the four new strings were read in all 7 files and are translated |
| C9 | `NavInstructionTest` `TURN-09 the last posted instruction wins` | yes - order, re-post and fallback after removal |
| C10 | `NavInstructionTest` `TURN-10 the maneuver words pick the icon` | partly - left/right covered in 7 languages, but keep/roundabout/U-turn/arrival only in pt/en; own examples break in ru/ar and on "you turn" (F3, F4) |
| C11 | walk - Waze card over the video, hidden at route end | yes - walked, `docs/car-turn.png` |
| C12 | `NavInstructionTest` `TURN-11 a title that is not a distance is the maneuver` | partly - the length case uses 14 characters (comment says 13), so the 12/13 bound is not pinned; `{0,12}` would pass too (F5) |
| C13 | `NavInstructionTest` `TURN-12 the switch is on by default` | yes - `flag` true with empty prefs, and `UiPolishTest` FLAGS default map |
| C14 | `UiPolishTest` `CSET-01 car settings are quality speed captions navigation` | yes - the key is in `CAR_SETTINGS`; the car toggle writes the same pref through the shared generic `SettingsScreen` code |
| C15 | walk - car toast / phone summary without access | yes - walked; code at `:1923` and `:2436` |
| C16 | walk - README line and `docs/car-turn.png` | yes - both READMEs have the Features line, the screenshot row and the access hint; png present |
| C17 | walk - `disallow_listener` hides the card | yes - walked; `onListenerDisconnected` clears the feed |
| C18 | `NavInstructionTest` `TURN-15 the phone switch reads on only with notification access` | added after round 1 (F7), judged by the author in round 2: all four stored/access combinations asserted |
| C19 | walk - switch change shows or hides the card at once | added after round 1 (F1), walked in round 2 |
| C20 | walk - source badge on the maneuver icon | added after round 1 (user request), walked in round 2; badge cut and missing after reinstall found by the user and the walk, fixed in 294e1a0 |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| F1 (Major) Switching "Navegação na tela" off (car or phone) leaves the card on screen until the nav app posts again; switching on does not show it until then (AC 4 / AC 12) | `YoutubeCarAppService.kt:1111-1118` the car settings callback calls `updateCover`/`invalidate` but not `showNav`; no prefs listener for a phone change; `showNav` only runs on notification events (`:2094`, `:2098`, `:2104`). Waze stopped at a light posts nothing new | call `showNav()` in the car settings callback and on a `navOverlay` prefs change (or on screen resume); test it at the screen-wiring level or move the decision into a function the callback calls || f85ad82 - car callback and a prefs listener call `showNav()`; C19 walk (car off hid the card, phone on showed it) |
| F2 (Minor) Hide/show race: a removal followed within 150 ms by a new post (route recalculation, Waze re-post) leaves the card hidden while a turn is current | `:1485` hide animates `view.alpha` with `withEndAction { GONE }`; the next `showNav` sees `VISIBLE` (end action pending), takes the update branch, never cancels `view.animate()`, so the end action hides it | `view.animate().cancel()` and restore `alpha = 1f` in the non-null branch, or key the show branch on a `shownTurn == null` state rather than on `visibility` || f85ad82 - non-null branch cancels the fade and restores alpha; C11 walk |
| F3 (Minor) `u.turn` has no word boundary: "When you turn left" picks U_TURN; ARRIVE/ROUNDABOUT/U_TURN match anywhere in the text, so "Turn left toward destination" -> ARRIVE, "Vire à direita na Rua do Retorno" -> U_TURN, "вокруг квартала направо" -> ROUNDABOUT | probe at HEAD of `maneuverOf`; regexes at `:2117-2119` | `\bu.?turn\b`, and word boundaries on `destination`/`retorno`/`круг` (e.g. `\bкруг`/`кругов`); add these as TURN-10 cases || f85ad82, 84bb4b5 - whole-word matching (Android-safe word edges); C10 `TURN-10` cases "When you turn left", "Rua do Retorno", "вокруг квартала" |
| F4 (Minor) Russian and Arabic keep/side words missing: "Съезд справа" and "Держитесь левой стороны" -> STRAIGHT; Arabic "الزم اليسار" (keep left) -> LEFT | probe at HEAD; `:2121-2123` lack `справа`/`слева`/`левой`/`правой` and `الزم` | add the words and one keep/roundabout/U-turn/arrival case per language to TURN-10 (today only pt/en hold those) || f85ad82 - ru/ar words added; C10 `TURN-10` keep/roundabout/U-turn/arrival per language |
| F5 (Minor) TURN-11 length bound not pinned; vulgar-fraction distances are not distances | `NavInstructionTest.kt:30` uses a 14-char title (comment says 13); probe: "123456789012" -> distance, 13 chars -> maneuver (correct), but "¼ mi" / "½ mi" -> shown as maneuver with no distance (`:2060` requires `\p{Nd}` first) | assert exactly 12 vs 13 characters; decide whether a leading `\p{No}` (¼ ½) counts as a distance - US Maps/Waze may write miles that way || f85ad82 - 12 vs 13 pinned, ¼ ½ ¾ count as distances; C12 `TURN-11` |
| F6 (Minor) C7's proof says "screenshot over the audio-only cover", the walk did not reach the cover | `**Walked:**` line: "Not reached: the audio-only cover"; code places `navView` after `cover` (`:1231-1232`) with no elevation on siblings, so the claim is code-backed only | walk it once with a mocked/fake GPS speed, or reword C7's proof to what was walked || f85ad82 - C7 proof reworded to what was walked (offline screen, pass-through; cover by layer order) |
| F7 (Minor) Phone switch with no access can never be turned off, and off->on without access returns from the grant with the switch still off | `:2406-2410` returns `false` for any change without access: a user who declines the feature keeps the "tap to allow" summary forever; a user turning it on grants access and comes back to an off switch (mirror of the walk bug fixed in 7f972cd) | return `true` when the new value is `false`; when turning on without access, accept the change (`true`) and open the grant || f85ad82 - turning off always works, on without access opens the grant and stays on, switch reads off without access; C18 `TURN-15` |
