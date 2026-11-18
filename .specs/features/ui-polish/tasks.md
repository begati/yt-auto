# UI Polish 1.4 Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: none - design inline in the tasks. What the app shows is decided by pure data and functions in `YoutubeCarAppService.kt` that JVM tests reach: the car home menu (`HOME_MENU`), the car settings list (`CAR_SETTINGS`), the phone settings layout (`PHONE_GROUPS`, with icon and summary per key), the aspect icon (`aspectIcon`) and the banner rule (`bannerShown`). Screens only render that data: car `HomeMenuScreen`/`SettingsScreen` (ListTemplate), the sign-in `MessageTemplate`, phone `Prefs` and `LoginActivity` banner. YouTube's chrome and sign-in links are hidden by CSS added to the `PAGE_JS` style tag, car only. Icons are Material Symbols outlined 24 dp vector drawables.
**Status**: Approved
**Approved**: 2026-11-17 - user: "aprovado, pode usar sub-agentes"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

---

## Test Coverage Matrix

> Generated from codebase and earlier features' decision ("JVM only", no Robolectric). Guidelines found: none - strong defaults applied; `app/build.gradle.kts` (JUnit 4, `testLogging` one line per test).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Pure data and rules (home menu, car settings, phone settings layout, aspect icon, banner rule) | unit | 1:1 with the ACs they decide; every listed edge case | `app/src/test/java/dev/begati/youtubeauto/UiPolishTest.kt` | `./gradlew testDebugUnitTest` |
| Strings in 7 languages | unit (existing `I18nTest`) | every new key in all languages | `app/src/test/java/dev/begati/youtubeauto/I18nTest.kt` | `./gradlew testDebugUnitTest` |
| Car templates, Views, injected CSS, drawables | none (walk) | walk on DHU + phone, DevTools to read state | - | `./gradlew assembleDebug` |

## Gate Check Commands

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tasks with unit tests | `./gradlew testDebugUnitTest` |
| Build | Tasks without tests (walk) and end of phase | `./gradlew assembleDebug testDebugUnitTest` |

---

## Execution Plan

### Phase 1: Foundation

```
T1 → T2
```

### Phase 2: Car navigation

```
T3 → T4 → T5 → T6
```

### Phase 3: Settings

```
T7 → T8
```

### Phase 4: Phone

```
T9 → T10 → T11
```

### Phase 5: Car overlays

```
T12 → T13
```

---

## Task Breakdown

### T1: Icon set

**What**: Material Symbols outlined 24 dp vector drawables for every icon the spec names (arrow_back, home, play_arrow, pause, search, subscriptions, history, video_library, fit_screen, crop, open_in_full, high_quality, speed, closed_caption, smartphone, aspect_ratio, fullscreen, block, fast_forward, headphones, picture_in_picture_alt, format_size, skip_previous, skip_next, replay_10, forward_10, tune, account_circle, close, wifi_off), named `ic_<name>.xml`; the existing `ic_aspect_ratio`, `ic_settings`, `ic_rewind`, `ic_forward` are replaced by or kept beside them
**Where**: `app/src/main/res/drawable/`
**Depends on**: None
**Reuses**: `ic_settings.xml` format (white fill, 24 dp viewport)
**Requirement**: NAV-02, NAV-03, NAV-05, CSET-01, CSET-02, PSET-02, REMOTE-01, SIGN-01, SIGN-04, OVL-01, OVL-02

**Done when**:

- [x] Every icon above exists as a vector drawable
- [x] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`

**Tests**: none
**Gate**: build

---

### T2: New strings in 7 languages

**What**: every new text of the spec (sign-in message title/text/OK, banner text/Entrar/close, sign-in toast, home menu entries "Playlists", "Mais opções no celular", group titles Reprodução/Anúncios/No carro, six switch summaries, offline second line, remote labels Anterior/Próximo/Avançar 10 s/Opções de reprodução) in en, pt, es, fr, it, ru, ar
**Where**: `app/src/main/res/values*/strings.xml`
**Depends on**: T1
**Reuses**: existing keys (`play`, `pause`, `rewind_10`, `forward_10`, `subscriptions`, `history`, `home_home`, `notice_failed_hint`)
**Requirement**: SIGN-01, SIGN-04, SIGN-07, NAV-03, CSET-02, PSET-01, PSET-03, REMOTE-04, OVL-01

**Done when**:

- [x] `I18nTest` passes with the new keys in all 7 files
- [x] Gate check passes: `./gradlew testDebugUnitTest`

**Tests**: unit
**Gate**: quick

---

### T3: Hide YouTube navigation and sign-in links on the car

**What**: CSS in the `PAGE_JS` style tag hiding YouTube's top bar, bottom tab bar and every `a[href*="accounts.google.com"]` (with its button wrapper) on the car page; the phone page is untouched
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: the `PAGE_JS` style element and selector constants
**Requirement**: NAV-01, SIGN-03

**Done when**:

- [x] DHU: no YouTube header, no tab bar, no "Fazer login" link on Início and `/feed/library`
- [x] Phone page still shows YouTube's header and tabs
- [x] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`

**Tests**: none
**Gate**: build

---

### T4: Icon action strip and aspect icon

**What**: action strip actions become icon-only (`ic_arrow_back`, `ic_home`, `ic_play_arrow`/`ic_pause`, `ic_search`); `internal fun aspectIcon(aspect: String): Int` maps contain/cover/fill to `ic_fit_screen`/`ic_crop`/`ic_open_in_full`, the map action uses it and the screen invalidates after `cycleAspect`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T3
**Reuses**: `cycleAspect`, `ASPECT`, `playToggleShown`
**Requirement**: NAV-02, NAV-05, NAV-06

**Done when**:

- [x] Tests `NAV-05` and `NAV-06` in `UiPolishTest.kt` pass
- [x] Gate check passes: `./gradlew testDebugUnitTest`

**Tests**: unit
**Gate**: quick

---

### T5: Car home menu

**What**: `internal val HOME_MENU` (Início, Inscrições, Histórico, Playlists with their URLs and icons) and a `HomeMenuScreen` ListTemplate the home action pushes; picking an entry loads its URL and pops; the app still opens on "Página inicial"
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T4
**Reuses**: `HOME_PAGE` URLs and labels, `ChoiceScreen` pattern
**Requirement**: NAV-03, NAV-04, NAV-07

**Done when**:

- [x] Tests `NAV-03` and `NAV-07` in `UiPolishTest.kt` pass
- [x] Gate check passes: `./gradlew testDebugUnitTest`

**Tests**: unit
**Gate**: quick

---

### T6: Car sign-in message screen

**What**: `shouldOverrideUrlLoading` pushes a `MessageTemplate` screen (phone icon, "Entre pelo celular", the text, action "OK" that pops) instead of the CarToast
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T5
**Reuses**: `isCarSignIn`
**Requirement**: SIGN-01, SIGN-02

**Done when**:

- [x] DHU: a sign-in navigation (via DevTools) opens the message screen; OK returns to the same page
- [x] `CarSignInTest` still passes
- [x] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`

**Tests**: none
**Gate**: build

---

### T7: Short car settings

**What**: `internal val CAR_SETTINGS` = quality, speed, captions with icons; `SettingsScreen` renders those rows and a last non-clickable "Mais opções no celular" row with the phone icon
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None (earlier phases done)
**Reuses**: `QUALITY`, `SPEED`, `FLAGS` captions, `ChoiceScreen`, `setFlag`
**Requirement**: CSET-01, CSET-02, CSET-03

**Done when**:

- [x] Test `CSET-01` in `UiPolishTest.kt` passes
- [x] Gate check passes: `./gradlew testDebugUnitTest`

**Tests**: unit
**Gate**: quick

---

### T8: Grouped phone settings

**What**: `internal val PHONE_GROUPS` (group title to setting keys, in order) and per-key icon and summary; `SettingsActivity.Prefs` builds the three categories from it, with `setIcon` and switch summaries; keys and defaults unchanged
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T7
**Reuses**: `CHOICES`, `FLAGS`, `list()`/`switch()` builders in `Prefs`
**Requirement**: PSET-01, PSET-02, PSET-03, PSET-04

**Done when**:

- [x] Tests `PSET-01`, `PSET-02`, `PSET-03`, `PSET-04` in `UiPolishTest.kt` pass
- [x] Gate check passes: `./gradlew testDebugUnitTest`

**Tests**: unit
**Gate**: quick

---

### T9: Phone sign-in banner and confirmation

**What**: `internal fun bannerShown(signedIn: Boolean, closedWhileSignedOut: Boolean)`; `LoginActivity` shows the banner (account icon, text, Entrar, close) above the WebView; Entrar loads Google's sign-in for YouTube; close stores the dismissal, cleared on any sign-in state change; on the signed-out to signed-in change it hides the banner, shows the toast and reloads the car page through `carLink`
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None (earlier phases done)
**Reuses**: `SAPISID` cookie check from `MediaService`, `carLink`
**Requirement**: SIGN-04, SIGN-05, SIGN-06, SIGN-07

**Done when**:

- [x] Tests `SIGN-04` and `SIGN-06` in `UiPolishTest.kt` pass
- [x] Gate check passes: `./gradlew testDebugUnitTest`

**Tests**: unit
**Gate**: quick

---

### T10: Hide the remote bar on Google sign-in

**What**: `LoginActivity` hides the remote bar while the main frame is on `accounts.google.com` and shows it again when it leaves
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T9
**Reuses**: `isCarSignIn` (host test)
**Requirement**: SIGN-08

**Done when**:

- [x] Phone: remote bar gone on the Google form, back on YouTube
- [x] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`

**Tests**: none
**Gate**: build

---

### T11: Remote bar icons, targets, ripple and labels

**What**: `RemoteBar` buttons use the new icons, `tune` for the menu, 48 dp minimum size, `selectableItemBackgroundBorderless` background and a `contentDescription` each (play/pause follows the state)
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T10
**Reuses**: `RemoteBar.dp()`, strings `play`, `pause`, `rewind_10`, `forward_10`
**Requirement**: REMOTE-01, REMOTE-02, REMOTE-03, REMOTE-04

**Done when**:

- [x] Phone with the car playing: new icons, ripple on tap, `uiautomator dump` shows each label and bounds of at least 48 dp
- [x] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`

**Tests**: none
**Gate**: build

---

### T12: Offline screen icon and hint

**What**: `offlineView` shows `ic_wifi_off` above the spinner, keeps "Sem conexão. Tentando de novo…" and adds the second line; paddings in dp
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None (earlier phases done)
**Reuses**: `notice_failed_hint` wording if it matches, otherwise the T2 string
**Requirement**: OVL-01, OVL-03

**Done when**:

- [x] DHU with the phone network off: icon and both lines
- [x] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`

**Tests**: none
**Gate**: build

---

### T13: Audio cover fallback icon and dp sizes

**What**: `audioCover` shows `ic_headphones` when there is no artwork; artwork size and paddings in dp
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T12
**Reuses**: `updateCover`
**Requirement**: OVL-02, OVL-03

**Done when**:

- [ ] DHU with the cover forced on (flag on, moving simulated) and no artwork: headphones icon
- [ ] Gate check passes: `./gradlew assembleDebug testDebugUnitTest`

**Tests**: none
**Gate**: build

---

## Checks

**C1** - A car navigation to `accounts.google.com` keeps the page and opens the message screen with the phone icon, "Entre pelo celular", the text and "OK" (SIGN-01, AC 1)
Proof: walk - DHU, DevTools `location.href='https://accounts.google.com/ServiceLogin'`: screenshot of the message screen; DevTools `location.href` unchanged

**C2** - "OK" closes the message screen and shows the same page (SIGN-02, AC 2)
Proof: walk - DHU: tap OK, screenshot of the page; `location.href` same as before

**C3** - No link to `accounts.google.com` is visible on the car (SIGN-03, AC 3)
Proof: walk - DHU signed out, Início and `/feed/library`: DevTools counts `a[href*="accounts.google.com"]` with a non-zero box: 0

**C4** - Signed out and not closed, the phone shows the banner with icon, text, Entrar and close (SIGN-04, AC 4)
Proof: run unit | SIGN-04 banner shows only while signed out and not closed PASSED

**C5** - Entrar opens Google's sign-in for YouTube in the phone WebView (SIGN-05, AC 5)
Proof: walk - phone signed out: tap Entrar; DevTools phone page URL on `accounts.google.com` with `service=youtube`

**C6** - Close hides the banner until the next sign-in or sign-out (SIGN-06, AC 6, edge 1)
Proof: run unit | SIGN-06 closing lasts until the sign-in state changes PASSED

**C7** - Signing in hides the banner, shows "Pronto: o carro já usa sua conta" and reloads the open car page (SIGN-07, AC 7, edge 2)
Proof: walk - user signs in on the phone with the car open: screenshot of the toast; car `performance.timeOrigin` changes and the car page shows the account avatar

**C8** - The remote bar is hidden on `accounts.google.com` and back on YouTube (SIGN-08, AC 8)
Proof: walk - phone with the car playing: Entrar, screenshot without remote bar; back, screenshot with it

**C9** - The car page hides YouTube's top bar and bottom tab bar (NAV-01, AC 1)
Proof: walk - DHU: screenshots of Início and a video page without header and tabs; phone page still shows them

**C10** - The car action strip shows only icons: back, home, play/pause, search (NAV-02, AC 2)
Proof: walk - DHU: screenshots of the strip on Início and during playback

**C11** - The home menu lists Início, Inscrições, Histórico and Playlists with their URLs and icons (NAV-03, AC 3)
Proof: run unit | NAV-03 home menu lists the four pages with icons PASSED

**C12** - Picking an entry loads its page and closes the list (NAV-04, AC 4, edge 4)
Proof: walk - DHU with a video playing: open the menu (video still plays), pick Inscrições; DevTools `location.pathname` `/feed/subscriptions`

**C13** - The aspect action shows the current aspect's icon (NAV-05, AC 5)
Proof: run unit | NAV-05 aspect icon follows the aspect PASSED

**C14** - Tapping the aspect action moves to the next aspect and its icon (NAV-06, AC 6)
Proof: run unit | NAV-06 next aspect has the next icon PASSED

**C15** - The car app opens on "Página inicial" (NAV-07, AC 7)
Proof: run unit | NAV-07 start page is the chosen home page PASSED

**C16** - Car settings list quality, speed and captions with icons, in that order (CSET-01, AC 1)
Proof: run unit | CSET-01 car settings are quality speed captions PASSED

**C17** - The last car settings row is "Mais opções no celular" with the phone icon and does nothing (CSET-02, AC 2)
Proof: walk - DHU settings: screenshot of four rows; tapping the last row keeps the screen

**C18** - A car setting change applies to the playing video (CSET-03, AC 3)
Proof: walk - DHU playing: speed 1.5x in car settings; DevTools `playbackRate` 1.5

**C19** - Phone settings show Reprodução, Anúncios and No carro with the rows in order (PSET-01, AC 1)
Proof: run unit | PSET-01 phone groups and order PASSED

**C20** - Every phone settings row has its icon (PSET-02, AC 2)
Proof: run unit | PSET-02 every phone setting has its icon PASSED

**C21** - Every switch shows its summary (PSET-03, AC 3)
Proof: run unit | PSET-03 every switch has its summary PASSED

**C22** - Keys and defaults are those of 1.3 (PSET-04, AC 4)
Proof: run unit | PSET-04 keys and defaults unchanged PASSED

**C23** - The remote bar uses skip_previous, play_arrow/pause, skip_next, replay_10, forward_10 and tune (REMOTE-01, AC 1)
Proof: walk - phone with the car playing: screenshot of the remote bar

**C24** - Every remote bar button is at least 48 × 48 dp (REMOTE-02, AC 2)
Proof: walk - `adb shell uiautomator dump`: bounds of each remote button at least 48 dp (144 px at density 3)

**C25** - Pressing a remote bar button shows the ripple (REMOTE-03, AC 3)
Proof: walk - screenrecord of a press shows the ripple

**C26** - Every remote bar button has its spoken label, play/pause following the state (REMOTE-04, AC 4)
Proof: walk - `uiautomator dump`: content-desc Anterior, Pausar (playing) then Tocar (paused), Próximo, Voltar 10 s, Avançar 10 s, Opções de reprodução

**C27** - The offline screen shows the wifi_off icon, the first line and "Verifique a internet do celular" (OVL-01, AC 1)
Proof: walk - DHU, phone network cut with `svc wifi disable` and `svc data disable`: screenshot

**C28** - The audio cover without artwork shows the headphones icon (OVL-02, AC 2)
Proof: walk - DHU, audio-only on and moving simulated, a page with no artwork: screenshot

**C29** - Offline screen and audio cover size paddings and artwork in dp (OVL-03, AC 3)
Proof: walk - DHU screenshots of both overlays at the DHU density: artwork 160x90 dp, paddings as in code

---

## Handoff

**Settled mid-build:** batch sub-agents - user: "aprovado, pode usar sub-agentes"
**Dispatched:** batch 1 (phases 1-2, T1-T6) at `86fedf5`, returned (ed5cb0e); batch 2 (phases 3-5, T7-T13) at `ed5cb0e`
**Walked:** not yet. C7 needs the user to type their Google sign-in on the phone.
