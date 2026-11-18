# UI Polish verification

**Verdict**: PASS (round 2: both round 1 findings closed by 4735366; round 1 was FAIL)
**Proofs**: `proofs.md` at 317006d
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | walk - DHU DevTools `location.href='https://accounts.google.com/ServiceLogin?service=youtube'` (Walked 2026-11-17) | yes - message screen with phone icon, title, text and OK recorded, `location.href` stayed `/feed/library`; code: `shouldOverrideUrlLoading` pushes `SignInScreen` once (`screenManager.top !is SignInScreen`) and returns true |
| C2 | walk - DHU tap OK (Walked) | yes - OK returned to `/feed/library`; OK is `screenManager.pop()` |
| C3 | walk - DHU signed out, Início and `/feed/library`, `a[href*="accounts.google.com"]` with a box (Walked) | yes - 2 in the DOM, 0 with a box on library, 0 on Início |
| C4 | `UiPolishTest` `SIGN-04 banner shows only while signed out and not closed` | no - asserts only the visibility rule (`bannerShown`, first launch); passes with any icon, text or buttons in the banner. The contents were seen in the walk, but C4's proof does not cite it |
| C5 | walk - phone signed out, tap Entrar (Walked) | yes - `accounts.google.com` with `service=youtube` |
| C6 | `UiPolishTest` `SIGN-06 closing lasts until the sign-in state changes` | yes - closed stays closed while signed out, cleared by sign-in and by sign-out (edge 1); own example: upgrade from 1.3 already signed in gives no toast or banner, and sign-out shows it |
| C7 | walk - user signs in with the car open on `/watch` (Walked) | yes - banner gone, car `performance.timeOrigin` changed and `LOGGED_IN` true; the toast was seen by the user, not captured; `carLink` is cleared on the car screen's destroy so a closed car only gets the toast (edge 2) |
| C8 | walk - phone with car playing, Entrar (Walked) | yes - remote bar gone on `accounts.google.com`; `doUpdateVisitedHistory` toggles `barBox` on every main-frame navigation |
| C9 | walk - DHU Início and video page, topbar and pivot bar height 0; phone still shows them (Walked) | yes - CSS is in `PAGE_JS` (car only), not `PHONE_JS` |
| C10 | walk - DHU strip on Início and during playback (Walked) | yes - back, home, search / back, home, pause, search; every `Action` has only `setIcon` |
| C11 | `UiPolishTest` `NAV-03 home menu lists the four pages with icons` | yes - URLs, title ids and icons asserted in order |
| C12 | walk - DHU video playing, home menu, Inscrições (Walked) | yes - `video.paused` false while the list is open, `/feed/subscriptions` loaded, list closed |
| C13 | `UiPolishTest` `NAV-05 aspect icon follows the aspect` | yes - all three aspects; own example: an invalid stored aspect reads as `cover` and shows `ic_crop` |
| C14 | `UiPolishTest` `NAV-06 next aspect has the next icon` | no - proves the next key and its icon, but would pass if the strip were never re-rendered (the `invalidate()` added in the click listener is untested); the walk saw the icon change on three taps but C14's proof does not cite it |
| C15 | `UiPolishTest` `NAV-07 start page is the chosen home page` | yes - three home pages; own example: a non-empty last page is kept |
| C16 | `UiPolishTest` `CSET-01 car settings are quality speed captions` | yes - keys and icons in order; titles come from the unchanged `CHOICES`/`FLAGS` |
| C17 | walk - DHU settings, four rows, last row tapped (Walked) | yes - row has no click listener |
| C18 | walk - DHU playing, Velocidade 1,5x, `playbackRate` 1.5 (Walked) | yes |
| C19 | `UiPolishTest` `PSET-01 phone groups and order` | yes |
| C20 | `UiPolishTest` `PSET-02 every phone setting has its icon` | yes - every shown key and the full key set |
| C21 | `UiPolishTest` `PSET-03 every switch has its summary` | yes - ids per flag; pt texts checked by hand against the Assumptions table, all 7 locales have 75 translatable strings |
| C22 | `UiPolishTest` `PSET-04 keys and defaults unchanged` | yes - literal 1.3 keys, defaults and option keys (match 86fedf5), every setting shown once, a 1.3 value read back; new prefs `signedIn` and `bannerClosed` do not collide with setting keys |
| C23 | walk - phone with car playing, remote bar icons (Walked) | yes |
| C24 | walk - `uiautomator dump`, 144x144 px at density 3 (Walked) | yes |
| C25 | walk - `input motionevent DOWN` on Voltar 10 s, ripple drawn (Walked) | yes - `selectableItemBackgroundBorderless` |
| C26 | walk - `uiautomator dump` content-desc (Walked) | yes - Pausar to Tocar seen by the batch 2 worker; `contentDescription` follows the state in `update` |
| C27 | walk - DHU, phone network cut (Walked) | yes |
| C28 | walk - DHU, mock GPS, audio-only, no artwork (Walked) | yes - `ic_headphones` tinted white, tint cleared when artwork returns |
| C29 | walk - DHU overlays, artwork 160x90 dp, paddings (Walked) | yes - both use `ctx.resources.displayMetrics.density` of the car's virtual display context |
| C30 | walk - phone signed out, banner with account icon, text, Entrar and X (Walked) | yes - round 2, author: added to close the C4 finding; the Walked line records the banner contents |
| C31 | walk - DHU, three taps show open_in_full, fit_screen, crop (Walked) | yes - round 2, author: added to close the C14 finding; the redraw after `invalidate()` was seen |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| C4's test passes with the wrong banner contents (icon, "Entre para ver suas inscrições no carro", Entrar, close) | `UiPolishTest` `SIGN-04` asserts only `bannerShown`/`signInChanged`; the contents appear only in the `**Walked:**` line, which C4's proof does not cite | make C4's proof also cite the walk (banner with account icon, text, Entrar, X), or move the banner's string and icon ids into data a test asserts || 4735366 - C30 |
| C14's test passes if the aspect action never shows the new icon after a tap | `UiPolishTest` `NAV-06` checks `nextAspect` and `aspectIcon` only; the icon change depends on `invalidate()` in the map action's click listener | make C14's proof also cite the walk (open_in_full, fit_screen, crop across three taps), which already records it || 4735366 - C31 |
