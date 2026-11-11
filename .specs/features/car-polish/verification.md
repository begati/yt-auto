# Car Polish verification

**Verdict**: PASS (round 2: findings closed by 3826fc0; round 1 was FAIL)
**Proofs**: `proofs.md` at 3826fc0 (round 2); round 1 at 620c3a4
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | `CarPolishTest.kt` `PLAYBTN-01 shown on a watch page playing or paused` | partly - fails if `/watch` stops counting (`playing`-only mutant), but asserts only that the button is present, not AC 1's label (Tocar paused / Pausar playing) |
| C2 | `CarPolishTest.kt` `PLAYBTN-02 shown off the watch page while a video plays` | partly - holds for the pure function (`videoIdFromUrl`-only mutant fails it), but `playing=true` is handed in; the page layer that T3 narrowed to `#movie_player video` is never shown reporting a miniplayer |
| C3 | `CarPolishTest.kt` `PLAYBTN-03 hidden on home search and feeds when nothing plays` | yes - Início, Inscrições, Histórico, canal, empty and a search whose query contains `watch?v=` (kills a `contains("watch?v=")` mutant); own probes at HEAD: `/watch` with no id, `/watch?v=short`, `/playlist?...&v=ID`, non-YouTube host, `about:blank` all hidden; `#t=`, `app=desktop&v=`, `&list=&index=` shown |
| C4 | walk - DHU Início, action bar open | yes - Handoff records Voltar, Início, Pesquisar on Início (opened by an SPA tap on YouTube's own Início tab) |
| C5 | walk - synthetic `<video>` outside `#movie_player` playing on Início | yes - before T3 the capture-phase `play` listener would report it and show the button; read at HEAD: the car page's only `<video>` is inside `#movie_player` |
| C6 | `CarPolishTest.kt` `PLAYBTN-05 leaving the watch page paused hides the button` | yes for the function (same two values as C1/C3); the transition itself (`doUpdateVisitedHistory` vs `toggleShown`) is C7's walk |
| C7 | walk - pause on the DHU, car home button | yes - exercises the case the Handoff found (home button sets `pageUrl` before `loadUrl`) and the `toggleShown` comparison |
| C8 | walk - DHU launcher and phone drawer screenshots | yes - vectors match: gradient `#FF4B3E` to `#A3001B`, white rim/spokes/hub with play cut out, inside the 66dp zone |
| C9 | walk - phone drawer beside YouTube and YT Music | yes - no white background, no rounded-rect or circle-with-play silhouette |
| C10 | walk - phone `night no`, car DevTools `prefers-color-scheme: dark` | yes - re-read at HEAD: `cmd uimode night` = no, car page (800 wide) `matchMedia(...dark)` true |
| C11 | walk - phone `night no`, phone DevTools dark | yes - re-read at HEAD: phone page `matchMedia(...dark)` true with night mode no |
| C12 | walk - Configurações screenshot dark | yes - `AppTheme` parent is non-DayNight `Theme.DeviceDefault` |
| C13 | walk - car DevTools before/after day/night switch | yes - round 2: logcat `CAR.SYS : setting night mode false/true` prova a troca chegando ao celular. Round 1: no - the Handoff says the DHU `day`/`night` commands had no visible effect and the switch was not observed reaching the app; same `timeOrigin` would hold whether or not the switch happened |
| C16 | walk - drawer, title bar, DHU launcher show "YT Auto" | yes - `android:label` on `<application>`, no activity overrides it |
| C17 | `! grep -rn "YouTube Auto" app/src && grep -q '"Abra o YT Auto no carro para tocar"' ...` | yes - `git grep "YouTube Auto\|youtube-auto"` outside `.specs` is empty |
| C18 | `./gradlew assembleDebug && test -f app/build/outputs/apk/debug/yt-auto-0.5-debug.apk` | yes - the old `youtube-auto-0.5-debug.apk` is gone from the output dir, so Gradle clears stale APKs and a reverted `archivesName` would fail `test -f` |
| C19 | walk - Pausar/Tocar/Pausar com `paused` false/true/false (Walked) | yes - round 2, author walk: rótulo e estado do vídeo principal lidos juntos |
| C20 | walk - `#movie_player video` tocando em `/` = Pausar, pausado = some (Walked) | yes - round 2, author walk: lê o estado do player principal na página, não a função pura |
| C21 | walk - Início e vídeo em tela cheia e no split sem faixa no topo (Walked) | yes - author walk depois do fechamento (regressão relatada pelo usuário, sem Verifier novo): cabeçalho do YouTube em y=0 e `innerHeight` 400 |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| C13 passes without the day/night switch reaching the app: AC 11 is unproven | tasks.md Handoff T6: "o DHU não mostra efeito visível dos comandos day/night, então a troca real ... não foi observada chegando ao app" | make the switch observable during the walk (e.g. log `carContext.isDarkMode` / `onCarConfigurationChanged` temporarily, or a DHU night-mode setting that visibly changes the host UI), then repeat the `timeOrigin`/`currentTime` read; or take the limit to the user | 3826fc0 - C13: logcat `CAR.SYS : setting night mode false/true` mostra a troca chegando ao celular com o vídeo tocando; mesmo `timeOrigin`, `currentTime` 491 → 496 → 501, escuro |
| AC 2 (miniplayer) has no proof at the page layer: C2 hands `playing=true` to the pure function, and T3 now reports only `#movie_player video`, so a miniplayer video outside `#movie_player` would hide the button with C2 green | `YoutubeCarAppService.kt:110-112,140` (`#movie_player` filter); `CarPolishTest.kt:19-23`; no walk of a video kept playing after an SPA move to Início (the car home button does a full `loadUrl`, which ends playback) | add a walk check: play a video on the DHU, go to Início with YouTube's own tab, read `!!document.querySelector('#movie_player video:not([paused])')` / bar shows Pausar | 3826fc0 - C20 (walk): o carro não tem miniplayer (Voltar SPA pausa); `#movie_player video` tocando em `/` = Pausar, pausado = some |
| AC 1's label is not in any check: C1 asserts only that the button is present, not Tocar when paused and Pausar when playing | `CarPolishTest.kt:11-16`; label at `YoutubeCarAppService.kt:685`; only the Handoff prose mentions "Pausar pela barra = Tocar" | add the label to C4/C7's walk claim (or a check row: open a video = Pausar, pause from the bar = Tocar) | 3826fc0 - C19 (walk): Pausar/Tocar/Pausar com `paused` false/true/false |
| Reported state and controls now read different videos: `yta.playing` follows `#movie_player video`, but Tocar/Pausar, card play/pause, restart and seek act on `document.querySelector('video')`; a preview earlier in the DOM would be toggled instead of the main player | `YoutubeCarAppService.kt:171-174,576` vs `:140`; at HEAD the car's Início DOM puts `#movie_player` before the feed cards, so not reachable today | use `#movie_player video` in `JS_PLAY`, `JS_PAUSE`, `JS_TOGGLE`, `JS_RESTART` and `onSeekTo`, same selector as the poll | 3826fc0 - código: JS_PLAY/PAUSE/TOGGLE/RESTART e onSeekTo usam `#movie_player video`; walk do C19 alterna o vídeo principal |
