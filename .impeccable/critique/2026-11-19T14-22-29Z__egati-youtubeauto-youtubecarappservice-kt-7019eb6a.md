---
target: tela do carro
total_score: 26
max_score: 40
na_heuristics: 
p0_count: 0
p1_count: 2
target_identity: "file:/home/begati/dev/youtube-auto/app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt"
target_fingerprint: "sha256:2c55ae1d1dfe2486ff26242a7480f29a81f537eff7af6b0c78d0990b1223d3b2"
target_path: /home/begati/dev/youtube-auto/app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt
timestamp: 2026-11-19T14-22-29Z
slug: egati-youtubeauto-youtubecarappservice-kt-7019eb6a
---
Method: dual-agent (A: design review · B: detector + native mechanical facts)

## Design Health Score
| # | Heuristic | Score | Key Issue |
|---|---|---|---|
| 1 | Visibility of System Status | 3 | Countdown shows a bar but no seconds left |
| 2 | Match System / Real World | 3 | Aspect uses a crop glyph; state only in a toast |
| 3 | User Control and Freedom | 2 | Countdown does not say how to stay (tap goes home now by user decision) |
| 4 | Consistency and Standards | 3 | Overlay tokens drift: scrim 0x80/0xCC/0xE0, radius 16/20dp, bar 4/6dp |
| 5 | Error Prevention | 2 | Sideways drag seeks; a sloppy scroll can seek |
| 6 | Recognition Rather Than Recall | 2 | Gestures taught by a toast and a nested Gestos row |
| 7 | Flexibility and Efficiency | 3 | Wheel, voice, phone remote, recent searches, share |
| 8 | Aesthetic and Minimalist Design | 3 | Overlays restrained; YouTube feed dense at 800x480 |
| 9 | Error Recovery | 3 | Offline names cause and action |
| 10 | Help and Documentation | 2 | GesturesScreen is a 45-word paragraph |
| **Total** | | **26/40** | **Acceptable** |

## Design Specificity Verdict
Specific where it counts, borrowed elsewhere (Principle 1). Detector: 0 findings, nothing scannable (Kotlin/Views). Native: overlay text >= 20sp; plate contrast >= 7:1 except skip label 4.0:1 (large bold text, shadow); 86 keys in all 10 translations, placeholders match, no car string > 1.6x English; turn card START/END, skip LEFT/RIGHT by design.

## Priority Issues
1. [P1] End countdown: no "how to stay" text; card over the audio cover art. Fix: second line naming the action; anchor below the cover subtitle. (/impeccable clarify, /impeccable layout)
2. [P1] Gesture discoverability: GesturesScreen paragraph. Fix: 3-row list, one short line per gesture. (/impeccable distill)
3. [P2] Aspect button on non-video pages. Fix: side strip aspect only on /watch. (/impeccable distill)
4. [P2] Host strips cover feed content at 800x480. Fix: accept (auto-hide) or pad. (/impeccable layout)
5. [P3] Overlay token drift. Fix: one scrim, radius, bar height. (/impeccable polish)

## Persona Red Flags
- Jordan: gestures unseen; crop icon reads as crop.
- Riley: side strip changes shape as next appears; tap during countdown.
- Casey: 8 similar grey circles.
- Driver moving: countdown leads to silence for podcast listeners.

## Minor Observations
- PRODUCT.md says 7 languages; now 11.
- Old car-turn capture.

## Questions to Consider
- Does keeping native strips honor the 7" floor principle?
- Go home at the end, or keep YouTube's suggestion for audio listeners?
