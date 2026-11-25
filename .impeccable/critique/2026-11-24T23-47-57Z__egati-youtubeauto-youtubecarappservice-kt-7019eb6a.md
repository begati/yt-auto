---
target: tela do carro
total_score: 26
max_score: 40
na_heuristics: 
p0_count: 0
p1_count: 2
target_identity: "file:/home/begati/dev/youtube-auto/app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt"
target_fingerprint: "sha256:ac8945c311d10d53c975b828d3130049528a9494a1310bbe38087552d734ae84"
target_path: /home/begati/dev/youtube-auto/app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt
timestamp: 2026-11-24T23-47-57Z
slug: egati-youtubeauto-youtubecarappservice-kt-7019eb6a
---
Method: dual-agent (A: design review · B: detector + native mechanical facts)

## Design Health Score
| # | Heuristic | Score | Key Issue |
|---|---|---|---|
| 1 | Visibility of System Status | 3 | Aspect state only in a toast; icon shows current state |
| 2 | Match System / Real World | 3 | "Go to" (Subscriptions, History) lives inside Settings |
| 3 | User Control and Freedom | 3 | Countdown cancellable, back exits fullscreen first; aspect is a blind 3-state cycle |
| 4 | Consistency and Standards | 3 | Panel family shared; title sizes 32/28/24, padding drift end vs drag |
| 5 | Error Prevention | 2 | YouTube kebab menus/chips live with tiny targets; slightly sideways drag seeks |
| 6 | Recognition Rather Than Recall | 2 | Gestures have no on-screen cue; one toast on 3 sessions |
| 7 | Flexibility and Efficiency | 3 | Wheel, media card, phone bar, voice |
| 8 | Aesthetic and Minimalist Design | 2 | 8 chrome controls on the watch page at 800x480 |
| 9 | Error Recovery | 3 | Offline auto-retries with hint; sign-in sent to phone |
| 10 | Help and Documentation | 2 | Tip is a LENGTH_LONG toast; Gestures list buried in Settings |
| **Total** | | **26/40** | **Acceptable** |

## Design Specificity Verdict
Specific in the overlays (turn card with source badge, physical-side half-moon, width-is-the-video drag seek, countdown that names its tap). Panel family coherent but generic Material-dark. Browse is YouTube mobile web at 130%: nothing car-specific there.
Detector: 0 findings, exit 0; Kotlin not scanned, page_start.js has no UI. No browser overlay (native). B false positive: countdown tap exists (onClick :1823).

## Priority Issues
1. [P1] Watch page shows 8 chrome controls (4 action strip + 4 side strip). Fix: side strip only previous/next (+ settings); aspect into Settings or the settings list. (/impeccable distill)
2. [P1] Gestures (double-tap skip, drag seek, bar) invisible after the first-sessions toast. Fix: brief in-surface hint panel on first videos, or a persistent faint edge cue during pause. (/impeccable onboard)
3. [P2] Audio-only while moving off by default and not toggleable from the car (CAR_SETTINGS :309). Fix: add to car settings list; consider first-drive prompt. (/impeccable harden)
4. [P2] Web content targets far under 76dp (kebab menus, chip bar 9+). Fix: page CSS hides ytm-menu-renderer buttons in car, enlarges chips. (/impeccable adapt)
5. [P2] Navigation split: home strip / Go to in Settings / back. Fix: home long-press or Go to as first settings row (current) vs own entry. (/impeccable layout)

## Persona Red Flags
- Jordan: no gesture cue; Go to buried; aspect icon cycles unexplained; tip gone in 3.5 s.
- Casey: 8 strip buttons; tiny kebab hits; two-step bar seek needs a fixed look.
- Moving driver: video by default while moving; cover swallows taps without controls.
- Passenger: SearchTemplate keyboard may lock while moving; voice or phone only.

## Minor Observations
- Skip moon 0x80 black: white text 4.0:1 worst case (lowest on car).
- Panel title sizes 32/28/24; end vs drag padding (28,18,28,22) vs (28,16,28,20); five dp() helper copies.
- Nav card padding left 14/right 20 via setPadding, does not flip in RTL.
- ic_arrow_back has no autoMirrored (Arabic).
- Icon-only Actions carry no title (no accessible label).
- going_home_tap 2.38x English in fr/ru; panel sizes to content, no maxLines.
- docs/car-video.png outdated (crop glyph, no play/pause).

## Questions to Consider
- Should the side strip carry only video verbs, with configuration in one Settings entry?
- Should a car-specific feed (bigger cards, no kebab, one chip row) replace m.youtube.com home?
- Is video-while-moving by default a decision you own or one inherited?
