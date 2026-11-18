# ui-polish proofs

**HEAD**: 30844b9
**Tree**: dirty - app/build.gradle.kts
**Shell**: /bin/sh
**Result**: 32 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `walk - DHU, DevTools location.href='https://accounts.google.com/ServiceLogin': screenshot of the message scree` | pass (Walked) |
| C2 | `walk - DHU: tap OK, screenshot of the page; location.href same as before` | pass (Walked) |
| C3 | `walk - DHU signed out, Início and /feed/library: DevTools counts a[href*="accounts.google.com"] with a non-zer` | pass (Walked) |
| C4 | `run unit / SIGN-04 banner shows only while signed out and not closed PASSED` | pass |
| C5 | `walk - phone signed out: tap Entrar; DevTools phone page URL on accounts.google.com with service=youtube` | pass (Walked) |
| C6 | `run unit / SIGN-06 closing lasts until the sign-in state changes PASSED` | pass |
| C7 | `walk - user signs in on the phone with the car open: screenshot of the toast; car performance.timeOrigin chang` | pass (Walked) |
| C8 | `walk - phone with the car playing: Entrar, screenshot without remote bar; back, screenshot with it` | pass (Walked) |
| C9 | `walk - DHU: screenshots of Início and a video page without header and tabs; phone page still shows them` | pass (Walked) |
| C10 | `walk - DHU: screenshots of the strip on Início and during playback` | pass (Walked) |
| C11 | `run unit / NAV-03 home menu lists the four pages with icons PASSED` | pass |
| C12 | `walk - DHU with a video playing: open the menu (video still plays), pick Inscrições; DevTools location.pathnam` | pass (Walked) |
| C13 | `run unit / NAV-05 aspect icon follows the aspect PASSED` | pass |
| C14 | `run unit / NAV-06 next aspect has the next icon PASSED` | pass |
| C15 | `run unit / NAV-07 start page is the chosen home page PASSED` | pass |
| C16 | `run unit / CSET-01 car settings are quality speed captions PASSED` | pass |
| C17 | `walk - DHU settings: screenshot of four rows; tapping the last row keeps the screen` | pass (Walked) |
| C18 | `walk - DHU playing: speed 1.5x in car settings; DevTools playbackRate 1.5` | pass (Walked) |
| C19 | `run unit / PSET-01 phone groups and order PASSED` | pass |
| C20 | `run unit / PSET-02 every phone setting has its icon PASSED` | pass |
| C21 | `run unit / PSET-03 every switch has its summary PASSED` | pass |
| C22 | `run unit / PSET-04 keys and defaults unchanged PASSED` | pass |
| C23 | `walk - phone with the car playing: screenshot of the remote bar` | pass (Walked) |
| C24 | `walk - adb shell uiautomator dump: bounds of each remote button at least 48 dp (144 px at density 3)` | pass (Walked) |
| C25 | `walk - screenrecord of a press shows the ripple` | pass (Walked) |
| C26 | `walk - uiautomator dump: content-desc Anterior, Pausar (playing) then Tocar (paused), Próximo, Voltar 10 s, Av` | pass (Walked) |
| C27 | `walk - DHU, phone network cut with svc wifi disable and svc data disable: screenshot` | pass (Walked) |
| C28 | `walk - DHU, audio-only on and moving simulated, a page with no artwork: screenshot` | pass (Walked) |
| C29 | `walk - DHU screenshots of both overlays at the DHU density: artwork 320x180 dp, paddings as in code` | pass (Walked) |
| C30 | `walk - phone signed out: screenshot of the banner above YouTube with icon, text, Entrar and X` | pass (Walked) |
| C31 | `walk - DHU: three taps on the aspect action show open_in_full, fit_screen, crop; pref back to cover` | pass (Walked) |
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

BUILD SUCCESSFUL in 11s
22 actionable tasks: 12 executed, 10 up-to-date
```
