# Car Sign-in Checks

**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun --tests 'dev.begati.youtubeauto.CarSignInTest'`

## Execution Plan

1. [x] Block `accounts.google.com` in the car WebView with a toast (SIGNIN-01..04) - files: `YoutubeCarAppService.kt`, `CarSignInTest.kt` - verify: unit + build + walk - commit: `fix: tell the user to sign in on the phone when the car opens google sign-in`

## Checks

**C1** - A URL on host `accounts.google.com` is a sign-in the car blocks (SIGNIN-01, AC 1)
Proof: run unit | SIGNIN-01 accounts google com is blocked on the car PASSED

**C2** - Tapping "Fazer login" on the car keeps the page and shows the toast (SIGNIN-01, SIGNIN-02, AC 1-2)
Proof: walk - app data cleared, DHU: tap "Fazer login" on the car page; screenshot with the toast and the YouTube page still there, logcat without an accounts.google.com load in the car

**C3** - Other hosts and unparseable URLs are not blocked (SIGNIN-03, AC 3, edge case)
Proof: run unit | SIGNIN-03 other hosts and bad urls are not blocked PASSED

**C4** - The phone app still opens the Google form (SIGNIN-04, AC 4)
Proof: walk - phone: "Fazer login" in YT Auto opens accounts.google.com; screenshot of the form

## Handoff

**Dispatched:** Verifier round 1 at `a7c154a` (no screens sub-agent: no criterion cites a capture), returned FAIL with 1 finding, closed by 82cc898
**Walked:** 2026-11-17: DHU 800x480 + phone 0091479724 over USB, debug build installed, app data cleared (`pm clear`). Car: tap "Você", then the "Fazer login" banner at the top of `/feed/library` (its `href` is `https://accounts.google.com/ServiceLogin?service=youtube...`, read via DevTools); the toast "Faça login no app do celular" appears and DevTools still reads `location.href` `https://m.youtube.com/feed/library` (C2). Phone: YT Auto, "Você", "Fazer login": the page goes to `https://accounts.google.com/v3/signin/identifier?...` and shows Google's "Faça login" form with the e-mail field (C4).
