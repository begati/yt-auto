# UI Polish 1.4 Specification

**Approved**: 2026-11-17 - user: "aprovado, pode seguir com as tasks"

## Problem Statement

The impeccable critique of 2026-11-17 (`.impeccable/critique/2026-11-17T23-46-06Z__...md`) scored the app 23/40. The
sign-in path is invisible on both screens (issue #3), the car's main controls are words a driver has to read, YouTube's
own navigation competes with the app's action strip on the car, the car repeats all 11 phone settings, the phone
settings are an ungrouped wall, and the phone remote bar mixes icon families with small, silent, unlabeled buttons.

## Goals

- [ ] A signed-out user learns on both screens that sign-in happens on the phone, and gets a confirmation when it is done.
- [ ] Every control the app draws on the car is an icon, and the action strip is the car's only navigation.
- [ ] Settings are short on the car and grouped and explained on the phone, with an icon on every row.
- [ ] The phone remote bar uses one icon family, 48 dp targets, touch feedback and spoken labels.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| "Só áudio com o carro em movimento" on by default | User: "Manter desligado" |
| Recent searches in the car search screen | Not in the 1.4 plan (critique "minor"); deferred |
| Car connection status in the phone title bar | Not in the 1.4 plan; deferred |
| Hiding sponsored items in the phone feed | Not in the 1.4 plan; deferred |
| Restyling YouTube's page content | Not the app's; only hiding its navigation and sign-in buttons on the car |
| New settings or behaviours | This feature re-presents the existing ones |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Release scope | All P1 and P2 items of the critique | User | y - user: "Tudo (P1 + P2)" |
| Audio-only default | Stays off | User | y - user: "Manter desligado" |
| YouTube navigation on the car | Hidden: top bar and bottom tab bar | User | y - user: "Esconder a navegação do YouTube" |
| Reaching Inscrições / Histórico / Playlists on the car | The home icon opens a list screen: Início, Inscrições, Histórico, Playlists; "Página inicial" still picks the page loaded when the app opens | User | y - user: "Ícone de casa abre um menu (Recomendado)" |
| Car settings | Qualidade do vídeo, Velocidade, Legendas, then a non-tappable row "Mais opções no celular" | User | y - user: "Qualidade, Velocidade, Legendas (Recomendado)" |
| Sign-in tapped on the car | A message screen replaces the toast; YouTube's sign-in buttons are hidden on the car | User | y - user: "Tela de mensagem + esconder botões (Recomendado)" |
| Phone sign-in banner | Shown while signed out, closable with X until the next sign-in or sign-out; on sign-in a confirmation and a car reload | User | y - user: "Fixo até logar, com botão fechar (Recomendado)" |
| "Playlists" page | `https://m.youtube.com/feed/library` | m.youtube.com has no separate playlists feed; the library page lists playlists | y - user: "aprovado, pode seguir com as tasks" |
| Signed in | The WebView cookies hold `SAPISID` for youtube.com | Same test the media browser already uses (`MediaService`) | y - user: "aprovado, pode seguir com as tasks" |
| Phone settings groups | Reprodução (Qualidade, Velocidade, Proporção, Legendas, Tela cheia ao abrir vídeo); Anúncios (Bloquear anúncios, Pular patrocínios); No carro (Só áudio com o carro em movimento, Tocar com outro app na tela, Página inicial, Tamanho do texto) | Critique proposal; groups by what the setting is about | y - user: "aprovado, pode seguir com as tasks" |
| Switch summaries (pt) | Bloquear anúncios: "Pula os anúncios dos vídeos"; Pular patrocínios: "Pula trechos patrocinados marcados pela comunidade"; Só áudio: "Esconde o vídeo enquanto o carro anda (usa o GPS)"; Tela cheia: "Abre o vídeo ocupando a tela do carro"; Tocar com outro app: "Continua tocando com o Maps ou outro app na frente"; Legendas: "Mostra legendas quando o vídeo tem" | Says what each does in the driver's words | y - user: "aprovado, pode seguir com as tasks" |
| New copy in other languages | Written for en, pt, es, fr, it, ru, ar, like every existing string | The app ships 7 languages with no gaps (I18nTest) | y - user: "aprovado, pode seguir com as tasks" |
| Icon source | Material Symbols (outlined, 24 dp) as vector drawables in `res/drawable/`, like the existing `ic_settings.xml` | One family across car and phone | y - user: "aprovado, pode seguir com as tasks" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Guided sign-in ⭐ MVP

**User Story**: As a signed-out user, I want both screens to tell me that sign-in happens on the phone, and to know when it worked, so that I do not get stuck on the car.

**Why P1**: It is where users get stuck (issue #3).

**Acceptance Criteria**:

1. WHEN the car WebView navigates to host `accounts.google.com` THEN the app SHALL keep the current page and open a car message screen with a phone icon, the title "Entre pelo celular", the text "Abra o YT Auto no celular e toque em Entrar. O carro usa a mesma conta." and one action "OK"
2. WHEN the driver taps "OK" on that message screen THEN the app SHALL close it and show the page the driver was on
3. WHILE the car shows a YouTube page the app SHALL hide every link to `accounts.google.com` on it
4. WHILE the user is signed out and has not closed the banner the phone app SHALL show, above the YouTube page, a banner with an account icon, the text "Entre para ver suas inscrições no carro", a button "Entrar" and a close button
5. WHEN the user taps "Entrar" THEN the phone app SHALL open Google's sign-in page for YouTube in its WebView
6. WHEN the user taps the banner's close button THEN the phone app SHALL hide the banner until the user signs in or out again
7. WHEN the user becomes signed in on the phone THEN the phone app SHALL hide the banner, show the toast "Pronto: o carro já usa sua conta", and reload the car page if the car screen is open
8. WHILE the phone WebView shows a page on `accounts.google.com` the phone app SHALL hide the remote bar

**Independent Test**: app data cleared; car: tap a YouTube sign-in link via DevTools (none visible) - message screen, OK returns; phone: banner, Entrar, sign in, toast, car page reloads signed in.

---

### P1: Car navigation by icons ⭐ MVP

**User Story**: As a driver, I want the car's controls to be icons I recognise at a glance, and one navigation instead of two.

**Why P1**: Reading words while driving is slow; two navigations double the choices.

**Acceptance Criteria**:

1. WHILE the car shows a YouTube page the app SHALL hide YouTube's top bar and bottom tab bar
2. The car action strip SHALL show icons with no text: back (`arrow_back`), home (`home`), play or pause (`play_arrow` / `pause`) when shown, and search (`search`)
3. WHEN the driver taps the home icon THEN the app SHALL open a car list screen with Início, Inscrições, Histórico and Playlists, each with an icon (`home`, `subscriptions`, `history`, `video_library`)
4. WHEN the driver picks an entry in that list THEN the app SHALL load its page in the car and close the list
5. The car aspect action SHALL show the icon of the current aspect: Ajustar `fit_screen`, Preencher `crop`, Esticar `open_in_full`
6. WHEN the driver taps the aspect action THEN the app SHALL switch to the next aspect and show the next aspect's icon
7. WHEN the car app opens THEN the app SHALL load the page chosen in "Página inicial", as today

**Independent Test**: DHU: no YouTube header or tabs; four icons; home list loads Inscrições; aspect icon changes on each tap.

---

### P2: Short car settings

**User Story**: As a driver, I want only the settings I change while watching on the car.

**Why P2**: An 11-row list is a scroll task while driving.

**Acceptance Criteria**:

1. The car settings screen SHALL list, in this order, Qualidade do vídeo, Velocidade and Legendas, each with an icon (`high_quality`, `speed`, `closed_caption`)
2. The car settings screen SHALL end with a row "Mais opções no celular" with a phone icon that does nothing when tapped
3. WHEN the driver changes a car setting THEN the app SHALL apply it as today (quality, speed and captions take effect on the playing video)

**Independent Test**: DHU: settings show 4 rows; change speed; the video plays at the new speed.

---

### P2: Grouped phone settings

**User Story**: As a phone user, I want settings grouped by topic with a short explanation and an icon each.

**Why P2**: Nine unrelated rows under "Vídeo" force guessing.

**Acceptance Criteria**:

1. The phone settings SHALL show the groups Reprodução, Anúncios and No carro with the rows listed in the Assumptions table, in that order
2. The phone settings SHALL show an icon on every row: Qualidade `high_quality`, Velocidade `speed`, Proporção `aspect_ratio`, Legendas `closed_caption`, Tela cheia `fullscreen`, Bloquear anúncios `block`, Pular patrocínios `fast_forward`, Só áudio `headphones`, Tocar com outro app `picture_in_picture_alt`, Página inicial `home`, Tamanho do texto `format_size`
3. The phone settings SHALL show under every switch the summary from the Assumptions table
4. The phone settings SHALL keep every key, default and behaviour of today's settings (a value saved by 1.3 is read unchanged by 1.4)

**Independent Test**: phone settings: three groups, icons and summaries; a setting changed in 1.3 keeps its value after installing 1.4.

---

### P2: Phone remote bar

**User Story**: As a one-handed phone user, I want remote buttons I can hit, that respond and that a screen reader names.

**Why P2**: Mixed icon families, ~24 dp targets, no feedback, no labels.

**Acceptance Criteria**:

1. The remote bar SHALL use the icons `skip_previous`, `play_arrow` / `pause`, `skip_next`, `replay_10`, `forward_10` and `tune` (the playback menu that today shows a gear)
2. Every remote bar button SHALL have a touch target of at least 48 × 48 dp
3. WHEN a remote bar button is pressed THEN the app SHALL show the system touch ripple on it
4. Every remote bar button SHALL have a spoken label: Anterior, Tocar or Pausar (following the state), Próximo, Voltar 10 s, Avançar 10 s, Opções de reprodução

**Independent Test**: phone with a video playing in the car: icons, ripple on tap, TalkBack reads each label; Layout Inspector shows 48 dp.

---

### P3: Car overlays

**User Story**: As a driver, I want the offline screen and the audio-only cover to say what is happening with an icon, at the right size on any car display.

**Why P3**: Small wins from the critique's minor list, touching the same icons.

**Acceptance Criteria**:

1. The offline screen SHALL show a `wifi_off` icon above its spinner and the text "Sem conexão. Tentando de novo…", with the second line "Verifique a internet do celular"
2. WHILE the audio-only cover shows and the video has no artwork the app SHALL show a `headphones` icon in the artwork's place
3. The offline screen and the audio-only cover SHALL size their paddings and artwork in dp

**Independent Test**: DHU: phone network off shows the icon and two lines; audio-only cover without artwork shows headphones.

---

## Edge Cases

- IF the user signs out on the phone THEN the app SHALL show the banner again, even if it was closed before
- IF the car screen is not open when the user signs in THEN the app SHALL only show the phone toast
- IF YouTube changes the markup so the top bar or tab bar are not found THEN the car SHALL still show the page and the action strip works as before
- WHEN the home list is open and a video is playing THEN the app SHALL keep the video playing until an entry is picked

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| SIGN-01 | P1 Guided sign-in (AC 1) | Execute | Implementing |
| SIGN-02 | P1 Guided sign-in (AC 2) | Execute | Implementing |
| SIGN-03 | P1 Guided sign-in (AC 3) | Execute | Implementing |
| SIGN-04 | P1 Guided sign-in (AC 4) | Execute | Implementing |
| SIGN-05 | P1 Guided sign-in (AC 5) | Execute | Implementing |
| SIGN-06 | P1 Guided sign-in (AC 6, edge 1) | Execute | Implementing |
| SIGN-07 | P1 Guided sign-in (AC 7, edge 2) | Execute | Implementing |
| SIGN-08 | P1 Guided sign-in (AC 8) | Execute | Implementing |
| NAV-01 | P1 Car navigation (AC 1, edge 3) | Execute | Implementing |
| NAV-02 | P1 Car navigation (AC 2) | Execute | Implementing |
| NAV-03 | P1 Car navigation (AC 3) | Execute | Implementing |
| NAV-04 | P1 Car navigation (AC 4, edge 4) | Execute | Implementing |
| NAV-05 | P1 Car navigation (AC 5) | Execute | Implementing |
| NAV-06 | P1 Car navigation (AC 6) | Execute | Implementing |
| NAV-07 | P1 Car navigation (AC 7) | Execute | Implementing |
| CSET-01 | P2 Car settings (AC 1) | Execute | Implementing |
| CSET-02 | P2 Car settings (AC 2) | Execute | Implementing |
| CSET-03 | P2 Car settings (AC 3) | Execute | Implementing |
| PSET-01 | P2 Phone settings (AC 1) | Execute | Implementing |
| PSET-02 | P2 Phone settings (AC 2) | Execute | Implementing |
| PSET-03 | P2 Phone settings (AC 3) | Execute | Implementing |
| PSET-04 | P2 Phone settings (AC 4) | Execute | Implementing |
| REMOTE-01 | P2 Remote bar (AC 1) | Execute | Implementing |
| REMOTE-02 | P2 Remote bar (AC 2) | Execute | Implementing |
| REMOTE-03 | P2 Remote bar (AC 3) | Execute | Implementing |
| REMOTE-04 | P2 Remote bar (AC 4) | Execute | Implementing |
| OVL-01 | P3 Car overlays (AC 1) | Execute | Implementing |
| OVL-02 | P3 Car overlays (AC 2) | Execute | Implementing |
| OVL-03 | P3 Car overlays (AC 3) | Execute | Implementing |

**Coverage:** 29 total, 0 mapped to tasks, 29 unmapped ⚠️ (Tasks phase next)

---

## Sources

- Impeccable critique 2026-11-17: `.impeccable/critique/2026-11-17T23-46-06Z__egati-youtubeauto-youtubecarappservice-kt-7019eb6a.md` (23/40, P1/P2 list, icon table)
- User answers 2026-11-17: scope "Tudo (P1 + P2)", "Manter desligado", "Esconder a navegação do YouTube", and the four answers in the Assumptions table
- GitHub issue #3 (sign-in on the car)
- `YoutubeCarAppService.kt` - `YoutubeScreen.onGetTemplate`, `PAGE_JS`, `SettingsScreen`, `LoginActivity`, `RemoteBar`, `SettingsActivity.Prefs`, `audioCover`, `offlineView`, `MediaService` (`SAPISID` check)
- Screenshots used by the critique: DHU 800x480 and phone (scratchpad `shots/`)

---

## Success Criteria

- [ ] A first-time user signs in from the phone without the README.
- [ ] No text label in the car action strip; no YouTube header or tabs on the car.
- [ ] A re-run of `/impeccable critique` scores above 23/40.
