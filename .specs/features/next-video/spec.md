# Next Video Specification

**Approved**: 2026-11-24 - user: "aprovado, segue"

## Problem Statement

A user asked (Threads, 2026-11-24) for "uma opção para escolher o tempo pra iniciar o próximo vídeo nas playlists,
atualmente acho que tá com 8 segundos ou mais". Measured on the DHU (2026-11-24): a playlist opened in the video
screen already moves to its next item in under 0.5 s, in front and behind Waze. The wait comes from two other paths:
a video played from the Android Auto media browser opens as `watch?v=<id>` without its playlist, so the playlist is
lost; and outside a playlist, with YouTube's autoplay on, YouTube waits about 10.5 s before its pick starts.

## Goals

- [ ] A video played from a playlist in the media browser continues with that playlist's next item, in order.
- [ ] Outside a playlist, with autoplay on, the next video starts without YouTube's ~10 s wait.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| A setting for the wait time | User decision: start at once, no setting ("Sim, começa direto") |
| A "next in N s" card with cancel | User decision: start at once ("Sim, começa direto") |
| Turning YouTube's autoplay on or off | It belongs to the user's YouTube account; the app follows it |
| Playlists opened in the video screen | YouTube already moves on in under 0.5 s (measured) |
| History and Subscriptions in the media browser | They are feeds, not playlists: no order to keep |
| Autoplay on start ("dar play sozinho") | The other request in the same comment; unclear, asked separately |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Scope | Both paths: media browser keeps the playlist, and autoplay outside a playlist starts at once | User answer | y - user: "Os dois (Recomendado)" |
| Warning before the next video | None: the next video starts directly; pause or back stop it | User answer | y - user: "Sim, começa direto (Recomendado)" |
| Which media browser nodes are playlists | Watch later (`WL`), Liked (`LL`) and each node under Playlists (`list:<id>`) | Those nodes are YouTube playlists; History and Subscriptions are feeds | y - user: "aprovado, segue" |
| Autoplay state unknown | Keep YouTube's own behavior (no early next); the existing 10 s rule then applies | Starting a video the user may have turned off is worse than waiting | y - user: "aprovado, segue" |
| Inside a playlist | The app does not call next: YouTube already does, a second call would skip an item | Measured: YouTube moves on in under 0.5 s | y - user: "aprovado, segue" |
| Where the rule runs | Pure Kotlin functions (`watchUrl`, the end rule) the unit tests reach; the page only reports the end, as today | Rule 6: rules in a tested layer | y - user: "aprovado, segue" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Media browser keeps the playlist ⭐ MVP

**User Story**: As a driver, when I play a video from a playlist in the Android Auto media list, I want the playlist to go on to its next item, so I don't have to pick each video.

**Why P1**: It is the "playlists" in the request; today the playlist is lost.

**Acceptance Criteria**:

1. WHEN a video is played from Watch later, Liked or a playlist under Playlists in the media browser THEN the app SHALL open it with that playlist (`watch?v=<video>&list=<playlist>`)
2. WHEN a video is played from History, Subscriptions or the phone remote THEN the app SHALL open it as `watch?v=<video>`, as today
3. IF the playlist id of a played item is not a valid playlist id THEN the app SHALL open the video without a playlist

**Independent Test**: on the DHU, play the first video of a playlist from the media browser, seek near its end: the playlist's second video starts in under 1 s.

---

### P2: No wait for autoplay outside a playlist

**User Story**: As a driver watching a video outside a playlist with autoplay on, I want the next video to start as soon as this one ends.

**Why P2**: Same wait, outside playlists; independent of P1.

**Acceptance Criteria**:

1. WHEN a video outside a playlist ends with YouTube's autoplay on THEN the app SHALL start the next video at once
2. IF the video is in a playlist THEN the app SHALL not start the next video itself
3. IF YouTube's autoplay is off or unknown THEN the app SHALL not start the next video itself
4. WHILE a video stays ended the app SHALL start the next video at most once for that end
5. IF the player's own video is not the URL's video (an ad) THEN the app SHALL not start the next video itself  <!-- added by the verification finding -->

**Independent Test**: on the DHU with autoplay on, a video opened without `list=` and seeked near its end: another video starts within 2 s.

---

## Edge Cases

- IF the next video does not start after an early next THEN the app SHALL fall back to the existing end countdown (10 s rule, spec video-end)

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| NEXT-01 | P1 (AC 1) | Tasks | Implementing |
| NEXT-02 | P1 (AC 2) | Tasks | Implementing |
| NEXT-03 | P1 (AC 3) | Tasks | Implementing |
| NEXT-04 | P2 (AC 1) | Tasks | Implementing |
| NEXT-05 | P2 (AC 2) | Tasks | Implementing |
| NEXT-06 | P2 (AC 3) | Tasks | Implementing |
| NEXT-07 | P2 (AC 4) | Tasks | Implementing |
| NEXT-08 | Edge Cases | Tasks | Implementing |
| NEXT-09 | P2 (AC 5) | Tasks | Implementing |

**Coverage:** 9 total, 9 mapped to tasks, 0 unmapped

## Sources

- User request on Threads (screenshot `/home/begati/Área de trabalho/photo_2026-11-24_19-51-18.jpg`), quoted in the Problem Statement
- DHU measurements (2026-11-24, CDP on the debug WebView): playlist `PLZ1...HMGG` in the video screen: next item at < 0.5 s, in front and behind Waze; `watch?v=kXYiU_JCYtU` without `list=`, autoplay on: ended at 4.5 s, next at 15.0 s; autoplay off: stays ended
- `watchUrl` (`YoutubeCarAppService.kt:445`) builds `watch?v=<id>` only; `fetchFeed` sets `mediaId = videoId`

## Success Criteria

- [ ] No playlist played from the media browser stops or jumps out of the playlist after one video
- [ ] Autoplay outside a playlist starts the next video within 2 s of the end
