# LESSONS - auto-maintained by scripts/lessons.py

> Machine-owned. Do NOT hand-edit. Changes are overwritten on the next `lessons.py` write.
> Canonical state lives in `.specs/lessons.json`. Edit lessons only via the script.
> promote_threshold=2 distinct features · window_days=45 · quarantine_threshold=2

## Confirmed (load these at Plan/Checks)

Corroborated across multiple features. Safe to apply as guidance.

### L-004 - A 'does not react to X failing' check must make X fail in the walk; observing the happy path proves nothing.
- signal: `weak_test` · recurrence: 2 feature(s) · scope: `webview` · harmful: 0
- features: offline-screen, car-polish
- evidence: OFF-06 / C6 (webview) (+1 more)
- last seen: 2026-11-11T17:15:45Z

## Candidates (under observation - do NOT load as guidance yet)

Seen once or not yet corroborated. Tracked, not trusted.

### L-001 - When a criterion lists alternative input sources (fallback chain), give each alternative its own asserted case; one green case leaves the others unpinned.
- signal: `weak_test` · recurrence: 1 feature(s) · scope: `parser` · harmful: 0
- features: car-features-verification
- evidence: FEED-08 / C8 (parser)
- last seen: 2026-11-10T22:34:43Z

### L-002 - When a criterion says a state is discarded, test the round trip (change away and back); masking it against the current value passes a one-way test.
- signal: `weak_test` · recurrence: 1 feature(s) · scope: `settings` · harmful: 0
- features: video-fill
- evidence: ASPECT-09 / C13 (settings)
- last seen: 2026-11-11T15:19:41Z

### L-003 - A setting shown on both car and phone needs a proof for each surface; the phone list is built separately from CHOICES.
- signal: `ac_gap` · recurrence: 1 feature(s) · scope: `settings` · harmful: 0
- features: video-fill
- evidence: ASPECT-06 / C10 (settings)
- last seen: 2026-11-11T15:19:41Z

### L-005 - A retry-forever loop needs its trigger narrowed to the errors a retry can cure; list the excluded error codes in the spec.
- signal: `ac_gap` · recurrence: 1 feature(s) · scope: `webview` · harmful: 0
- features: offline-screen
- evidence: OFF-09 (webview)
- last seen: 2026-11-11T16:01:23Z

### L-006 - A unit test must call the function the production path calls; grep for callers before pointing a check at a helper.
- signal: `weak_test` · recurrence: 1 feature(s) · scope: `webview` · harmful: 0
- features: phone-remote
- evidence: CAST-05 / C6 (webview)
- last seen: 2026-11-11T16:38:30Z

### L-007 - Never build Android framework objects in top-level vals of a file the JVM tests load; use lazy or a function.
- signal: `gate_fail` · recurrence: 1 feature(s) · scope: `jvm-tests` · harmful: 0
- features: phone-remote
- evidence: waitingForCar (jvm-tests)
- last seen: 2026-11-11T16:38:30Z

### L-008 - A pure-function test of page state needs a walk that drives the real page into that state; the function cannot see what the page reports.
- signal: `weak_test` · recurrence: 1 feature(s) · scope: `webview` · harmful: 0
- features: car-polish
- evidence: PLAYBTN-02 / C2 (webview)
- last seen: 2026-11-11T17:15:45Z

### L-009 - When narrowing which element the page reports state from, point every control at the same selector.
- signal: `spec_deviation` · recurrence: 1 feature(s) · scope: `webview` · harmful: 0
- features: car-polish
- evidence: YoutubeCarAppService.kt:171 (webview)
- last seen: 2026-11-11T17:15:45Z

### L-010 - A theme given to a Presentation or window brings its decor too; pick a NoActionBar variant and walk the screen edges after a theme change.
- signal: `gate_fail` · recurrence: 1 feature(s) · scope: `webview` · harmful: 0
- features: car-polish
- evidence: THEME-01 / C21 (webview)
- last seen: 2026-11-11T17:18:32Z

### L-011 - To read a host from a WebView URL use java.net.URL (or Uri.host), not java.net.URI: Chromium leaves | { } ^ unescaped in queries and URI throws on them; test with such a URL.
- signal: `weak_test` · recurrence: 1 feature(s) · scope: `webview` · harmful: 0
- features: car-signin
- evidence: C1 / SIGNIN-01 (webview)
- last seen: 2026-11-17T23:34:16Z

### L-012 - A screen criterion that names contents (icon, text, buttons) needs a walk or a data test of those contents; a visibility-rule unit test alone does not prove it.
- signal: `weak_test` · recurrence: 1 feature(s) · scope: `ui` · harmful: 0
- features: ui-polish
- evidence: C4 / SIGN-04 (ui)
- last seen: 2026-11-18T00:34:07Z

### L-013 - When a car template must redraw after an action, cite the walk that sees the redraw; a unit test of the next state misses a missing invalidate().
- signal: `weak_test` · recurrence: 1 feature(s) · scope: `car-template` · harmful: 0
- features: ui-polish
- evidence: C14 / NAV-06 (car-template)
- last seen: 2026-11-18T00:34:07Z

### L-014 - A gesture's starting value (time, position) is read from the page when the gesture starts, not from a polled copy; a pure formula test does not cover where its input comes from.
- signal: `ac_gap` · recurrence: 1 feature(s) · scope: `car-gestures` · harmful: 0
- features: video-controls
- evidence: CTRL-14/CTRL-18 (car-gestures)
- last seen: 2026-11-18T15:11:02Z

### L-015 - A walk proof that names screenshots and a measured value needs those captured (screenshot loop during the user's gesture), not the user's verdict.
- signal: `weak_test` · recurrence: 1 feature(s) · scope: `walk` · harmful: 0
- features: video-controls
- evidence: C15 (walk)
- last seen: 2026-11-18T15:11:03Z

### L-016 - Anything shown on the car screen in reaction to playback must check the video is on screen (surface attached, no audio cover): background play keeps the page running behind other apps.
- signal: `ac_gap` · recurrence: 1 feature(s) · scope: `car-screen` · harmful: 0
- features: gesture-hint
- evidence: HINT-04 (car-screen)
- last seen: 2026-11-18T15:25:56Z

## Quarantined (failed when applied - ignore)

A confirmed lesson that recurred alongside failure. Kept for the maintainer to review.

_none_
