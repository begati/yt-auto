---
target: tela do carro
total_score: 26
max_score: 40
na_heuristics: 
p0_count: 0
p1_count: 2
target_identity: "file:/home/begati/dev/youtube-auto/app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt"
target_fingerprint: "sha256:c1400f7cfb201aad9acea33e538316cd52ed626b7b438f480a9c4d6957c94068"
target_path: /home/begati/dev/youtube-auto/app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt
timestamp: 2026-11-19T01-37-28Z
slug: egati-youtubeauto-youtubecarappservice-kt-7019eb6a
---
Method: dual-agent (A: design review · B: detector + native mechanical facts)

## Design Health Score
| # | Heuristic | Score | Key Issue |
|---|---|---|---|
| 1 | Visibility of System Status | 3 | Offline: icon + spinner (rendered as a dash at low density) + text, no retry cue |
| 2 | Match System / Real World | 3 | "Página inicial" list titles a menu whose first row is "Início" |
| 3 | User Control and Freedom | 3 | Drag-seek always commits on release; aspect cycles only forward |
| 4 | Consistency and Standards | 2 | Two offline messages (YouTube toast + app screen); two play/pause controls on video |
| 5 | Error Prevention | 3 | No cancel for drag-seek; a bump while moving seeks |
| 6 | Recognition Rather Than Recall | 2 | Gestures taught by a one-time toast only |
| 7 | Flexibility and Efficiency | 3 | Wheel, media card, phone remote, voice, stacked double taps |
| 8 | Aesthetic and Minimalist Design | 2 | Up to 6 strip buttons + YouTube chrome over the video; bubble next to the aspect button |
| 9 | Error Recovery | 2 | Offline auto-retries but no manual retry; generic hint |
| 10 | Help and Documentation | 3 | README thorough; in the car a single toast that never returns |
| **Total** | | **26/40** | **Acceptable** |

## Design Specificity Verdict
Behaviour authored for this product; style deliberately neutral (YouTube bubble + Maps-like card), consistent with "native by default". Weak spots are the seams between YouTube web UI, app overlays and Android Auto chrome. Detector: 0 findings, exit 0 (no Kotlin/View support: not a clean signal). Native facts: all overlay text >= 20sp; contrast >= 7:1 on plates in worst case, skip bubble 4.0:1 (bold 20sp + shadow: large text, passes 3:1); overlay ImageViews have no contentDescription (non-touchable, mostly decorative; navBadge meaning not in text); string parity complete.

## Priority Issues
1. [P1] Offline screen mixes error and working state (icon + spinner-as-dash + text), after YouTube's own offline toast. Fix: drop spinner or replace with a retry line; hide YouTube's toast. (/impeccable distill, /impeccable clarify)
2. [P1] Gestures not discoverable after the first tip (takeGestureHint once). Fix: repeat per car session capped, or a "Gestos" row in car settings. (/impeccable onboard)
3. [P2] Too many controls over the video; skip bubble lands next to the map strip. Fix: shift bubble inward by the strip width; consider aspect in settings only. (/impeccable distill, /impeccable layout)
4. [P2] "Mais opções no celular" looks like a tappable row and does nothing. Fix: make it non-row text or toast the path. (/impeccable clarify)
5. [P3] RTL: turn card anchored LEFT/leftMargin. Fix: START. (/impeccable harden)

## Persona Red Flags
- Driver glancing while moving: YouTube progress text ~14px (app cannot change); drag-seek commits on release.
- Jordan: no visible seek controls; one-time tip; "Página inicial" reads like a setting.
- Sam: strip actions icon-only with no title; aspect state icon-only; skip bubble contrast relies on shadow.
- Riley: rapid aspect taps stack CarToasts; sign-in redirect can push over offline overlay.

## Minor Observations
- Skip label "10 s" could be "+10 s"/"-10 s".
- Audio cover 320x180dp tight on 480px height with 2-line title.
- Search opens empty (no recent searches).

## Questions to Consider
- Should the strip play/pause hide while YouTube's player chrome is up?
- Would a corner notice beat a full black takeover when offline, keeping audio/last frame?
