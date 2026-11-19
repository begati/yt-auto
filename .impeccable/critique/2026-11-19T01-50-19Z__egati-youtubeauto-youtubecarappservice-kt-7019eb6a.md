---
target: tela do carro
total_score: 26
max_score: 40
na_heuristics: 
p0_count: 0
p1_count: 2
target_identity: "file:/home/begati/dev/youtube-auto/app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt"
target_fingerprint: "sha256:e675ad459ea701c7245dc2a4de68ff8a6304ab82a265a4b4bd359b166b7544fb"
target_path: /home/begati/dev/youtube-auto/app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt
timestamp: 2026-11-19T01-50-19Z
slug: egati-youtubeauto-youtubecarappservice-kt-7019eb6a
---
Method: dual-agent (A: design review · B: detector + native mechanical facts)

## Design Health Score
| # | Heuristic | Score | Key Issue |
|---|---|---|---|
| 1 | Visibility of System Status | 3 | No loading state on the first car page load |
| 2 | Match System / Real World | 3 | "Página inicial" title over an "Início" row |
| 3 | User Control and Freedom | 3 | Back follows WebView history, unpredictable after several pages |
| 4 | Consistency and Standards | 2 | Three play/pause controls; host strip covers YouTube's top-right controls and title |
| 5 | Error Prevention | 3 | Sign-in redirect, nav access warning |
| 6 | Recognition Rather Than Recall | 1 | Gestures invisible; only a 3.5 s toast teaches them |
| 7 | Flexibility and Efficiency | 3 | Stacked double taps, voice, phone remote |
| 8 | Aesthetic and Minimalist Design | 3 | Overlays restrained; YouTube title cropped in fill mode |
| 9 | Error Recovery | 3 | Offline names cause and fix; search "Nenhum item" before typing reads as failure |
| 10 | Help and Documentation | 2 | No way to see the gesture tip again in the car |
| **Total** | | **26/40** | **Acceptable** |

## Design Specificity Verdict
Specific: little own UI, one coherent overlay system, each overlay fills a real car gap. Remaining weakness is the collision of YouTube mobile web and host chrome. Detector: 0 findings, exit 0 (nothing scannable in Kotlin/Views). Native facts: overlay text 20-32sp; contrast >= 7:1 on plates except skip label 4.0:1 (bold 20sp, large text, passes 3:1); turn card START/END with RTL pivot; skip bubble LEFT/RIGHT on purpose (physical side); no hard-coded strings; 81 translatable keys in all 7 locales; overlay ImageViews without contentDescription (mostly decorative).

## Priority Issues
1. [P1] Gesture tip fires while a template (Settings) is on top: visible checks the surface, not screenManager.top; burns one of 3 tips. Fix: require screenManager.top === this. (/impeccable harden)
2. [P1] Gestures undiscoverable after the tips: fix with a "Gestos" row in car settings or a faint side hint on first tap. (/impeccable onboard)
3. [P2] Host strip collides with YouTube's player controls; play/pause in three places. Fix: hide YouTube's top-bar controls on watch, or drop play/pause from the strip on watch. (/impeccable distill)
4. [P2] Search empty state shows "Nenhum item" before input. Fix: recent searches or a no-items message. (/impeccable clarify)
5. [P3] Home menu naming drift ("Página inicial" vs "Início"; "Playlists" opens /feed/library). (/impeccable clarify)

## Persona Red Flags
- Jordan: does not know a tap brings up buttons; YouTube feed menus ~24px.
- Sam: overlays not announced; skip label contrast relies on shadow.
- Riley: Back destination depends on WebView history; toast stacks over drag panel.
- Casey: 70-char tip in 3.5 s.
- Driver moving: audio cover may overflow 480px; cover taps give no feedback.

## Minor Observations
- Drag panel 20dp vs turn card 16dp radius, no stated reason.
- "Mais opções" could name what lives on the phone.

## Questions to Consider
- Should the strip on watch keep only back, search, settings?
- Should browsing be a native GridTemplate with the WebView only for playback?
