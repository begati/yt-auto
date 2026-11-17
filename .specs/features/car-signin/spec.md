# Car Sign-in Specification

**Approved**: 2026-11-17 - user: "Aprovado (Recomendado)"

## Problem Statement

Issue #3: a user who starts without an account taps the YouTube profile ("Você" / "Fazer login") on the car
screen, and the car WebView opens the Google sign-in page. The car's virtual display has no keyboard, so the
user is stuck on a form they cannot fill. Sign-in only works on the phone app (cookies are shared with the car).

## Goals

- [ ] The car never shows the Google sign-in page.
- [ ] A user who tries to sign in on the car is told to sign in on the phone app.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Signing in on the car (host keyboard, QR code, device code) | Google sign-in needs a real browser form; the phone app already covers it |
| Changes to the phone app's sign-in | It works: `LoginActivity` lets `accounts.google.com` load |
| Hiding YouTube's "Fazer login" button on the car | The page is YouTube's; blocking the navigation covers every entry point |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| What counts as sign-in | Any main-frame navigation to host `accounts.google.com` (includes redirects and account switching) | Every Google sign-in, account chooser and re-auth form lives there and needs a keyboard | y - user: "Aprovado (Recomendado)" |
| Message | `CarToast` (long) with the existing string `notice_signed_out_hint` ("Faça login no app do celular", already in all 7 languages) | Reuses a translated string; same wording as the signed-out notice | y - user: "Aprovado (Recomendado)" |
| Page after the block | Stays on the page the user was on | Nothing else to show; the toast explains | y - user: "Aprovado (Recomendado)" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Block sign-in on the car ⭐ MVP

**User Story**: As a driver without an account in the app, I want the car to tell me where to sign in instead of opening a form I cannot type into.

**Why P1**: It is the whole issue.

**Acceptance Criteria**:

1. WHEN the car WebView navigates to a URL on host `accounts.google.com` THEN the app SHALL cancel the navigation and keep the current page
2. WHEN the app cancels a sign-in navigation on the car THEN the app SHALL show a car toast with the text of `notice_signed_out_hint` ("Faça login no app do celular" in Portuguese)
3. IF the car WebView navigates to a URL on any other host (m.youtube.com, www.google.com, youtube.com) THEN the app SHALL let it load as before
4. WHERE the WebView is the phone app's (`LoginActivity`) the app SHALL let `accounts.google.com` load as before

**Independent Test**: on the DHU with the app signed out, tap "Fazer login" on the car page: the page stays and the toast appears; on the phone, "Fazer login" opens the Google form.

---

## Edge Cases

- IF the URL is not parseable or has no host THEN the app SHALL let the WebView handle it as before

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| SIGNIN-01 | P1 (AC 1) | Execute | Implementing |
| SIGNIN-02 | P1 (AC 2) | Execute | Implementing |
| SIGNIN-03 | P1 (AC 3) | Execute | Implementing |
| SIGNIN-04 | P1 (AC 4) | Execute | Implementing |

**Coverage:** 4 total, 4 mapped to tasks, 0 unmapped

---

## Sources

- GitHub issue #3 (pablosguajardo, 2026-11-17): "If you start in anonymous mode, you cannot log in afterwards. I go to the home screen and select my user profile. It says I'm not logged in. The login screen appears, but it won't let me type—the keyboard is missing."
- User request (2026-11-17): "manda ver, implementa e responde a issue.... temos celular com adb conectado, use o tlc"
- `YoutubeCarAppService.kt` - car `OfflineRetry` client (`YoutubeScreen.onSurfaceAvailable`), `SearchScreen` comment (virtual display has no IME), `LoginActivity`
- Test setup: DHU + phone over USB (`adb devices`: 0091479724)

---

## Success Criteria

- [ ] Tapping "Fazer login" on the car never shows the Google form.
- [ ] The toast tells the user to sign in on the phone.

