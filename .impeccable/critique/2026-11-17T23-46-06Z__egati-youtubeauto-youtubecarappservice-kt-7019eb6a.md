---
target: YT Auto phone + Android Auto UI
total_score: 23
max_score: 40
na_heuristics: 
p0_count: 0
p1_count: 3
target_identity: "file:/home/begati/dev/youtube-auto/app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt"
target_fingerprint: "sha256:5023f292e5fbe53c5620d9f6d8ff8fb79c083a8d849f6469e9a953c10b458e70"
target_path: /home/begati/dev/youtube-auto/app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt
timestamp: 2026-11-17T23-46-06Z
slug: egati-youtubeauto-youtubecarappservice-kt-7019eb6a
---
# YT Auto: critique (phone + Android Auto), 2026-11-17

Method: dual-agent (A: design review · B: detector + mechanical native checks)

## Heuristics: 23/40 (Acceptable)
1 System status 2 (no signed-in state; aspect state hidden; black video without signal)
2 Real world 3 ("SponsorBlock", "Tocar com outro app na tela" unclear; "Voltar" vs "Voltar 10 s")
3 Control 3
4 Consistency 2 (two gears on phone doing different things; car strip text-only vs map icon-only; Início duplicates YouTube tab)
5 Error prevention 2 (YouTube sign-in CTAs still tappable on car; audio-only while moving defaults OFF; full settings reachable while driving)
6 Recognition 2 (aspect cycle; empty search "Nenhum item"; toast-only sign-in hint)
7 Flexibility 3
8 Minimalist 2 (4 text pills cover header and end screen; FABs over "Reproduzir agora")
9 Error recovery 3
10 Help 1 (no first run, no explanation phone=account/remote, car=screen)

## Specificity
Car: correct use of templates; generic but right. Phone: under-designed; RemoteBar assembled (Holo ic_media_* + Material vectors, no ripple, second gear). Product idea "phone is account and remote, car is screen" appears only in phone_notice.
Detector: 0 findings in all targets; it cannot scan .kt/.xml (only page_start.js in scope). Mechanical: 0 contentDescription in the app (RemoteBar icon buttons), RemoteBar targets ~24dp (transparent background drops padding), raw px in audioCover/offlineView (K:1136-1144, 1447), hardcoded colors, string concat ": " at K:1347 (RTL), translations complete (55 x 7).

## Priority issues
- [P1] Sign-in path invisible on both screens; car toast is transient and the YouTube CTA stays. Fix: car MessageTemplate (smartphone icon, "Entre pelo celular", OK); hide YouTube sign-in buttons on car when signed out; phone banner when signed out (account_circle, "Entrar"); confirmation after sign-in; hide RemoteBar on accounts.google.com. (/impeccable onboard, clarify)
- [P1] Car action strip text-only: slow glance, covers content, "Voltar" ambiguous, Tocar/Pausar width shift. Fix: icon actions arrow_back, home, play_arrow/pause, search. (/impeccable clarify)
- [P1] Safety default: "Só áudio com o carro em movimento" OFF. Fix: default ON + location rationale on first run. Product decision. (/impeccable harden)
- [P2] RemoteBar: two gears, mixed icon families, ~24dp targets, no ripple, no contentDescription. Fix: tune icon, Material transport + replay_10/forward_10, selectableItemBackgroundBorderless, 48dp min, contentDescription. (/impeccable polish)
- [P2] Car settings duplicate phone (11 rows while driving); aspect icon static. Fix: trim to Qualidade/Velocidade/Legendas/Só áudio + "Mais opções no celular"; row icons; aspect icon per state. (/impeccable distill)
- [P2] Phone settings: 9 unrelated rows in "Vídeo", no summaries, no icons. Fix: Reprodução / Anúncios / No carro, summaries, icons. (/impeccable clarify)

## Personas
Jordan: no "sign in here, then open car"; toast vanishes; two gears; SponsorBlock jargon; no sign-in confirmation.
Casey: RemoteBar in thumb zone but tiny targets; ±10s at edges; RemoteBar over Google form; share-to-car undiscoverable.
Driver: words not glyphs; FABs over end-screen button; same-shape pills in glare; video while moving by default; black screen without status.

## Minor
Offline overlay without icon/cause; audio cover px sizes, empty art; search empty state; title bar carries no state; sponsored banner in phone feed while "Bloquear anúncios" ON; update dialog assumes KingInstaller knowledge.

## Icons (Material Symbols)
Car strip: arrow_back, home/subscriptions/history, play_arrow/pause, search. Aspect: fit_screen/crop/open_in_full. Settings rows: high_quality, speed, aspect_ratio, block, fast_forward, headphones, fullscreen, picture_in_picture_alt, closed_caption, home, format_size. RemoteBar: tune, skip_previous, play_arrow/pause, skip_next, replay_10, forward_10. States: wifi_off, headphones, smartphone, account_circle, playlist_remove, history.

## Questions
1. Hide YouTube's chrome on the car so the action strip is the only navigation?
2. Phone first screen = status (account, car connected, now playing) instead of YouTube?
3. Settings on the car at all while moving?
