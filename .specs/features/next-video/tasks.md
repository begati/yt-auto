# Next Video Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: inline (rules in pure `internal fun`s tested on the JVM; the media browser's playable items carry the playlist in their `mediaId`; the page keeps reporting the end through `yta.ended`, the car screen decides)
**Status**: Approved
**Approved**: 2026-11-24 - user: "sim"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

---

## Test Coverage Matrix

> Generated from codebase, project guidelines, and spec - confirm before Execute. Guidelines found: `app/build.gradle.kts` (`testLogging`, one line per passed test), earlier specs (JVM only, no Robolectric).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Pure rules (`internal fun` in `YoutubeCarAppService.kt`) | unit | 1:1 with the ACs that decide something; the invalid-id edge | `app/src/test/java/dev/begati/youtubeauto/NextVideoTest.kt` | `./gradlew testDebugUnitTest --rerun` |
| Wiring (media browser items, car screen end handling) | none (walk) | Walk on the DHU + DevTools | - | `./gradlew assembleDebug` |

## Gate Check Commands

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tasks with unit tests | `./gradlew testDebugUnitTest --rerun` |
| Build | Wiring tasks | `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun` |

---

## Execution Plan

### Phase 1: Media browser keeps the playlist

```
T1 → T2
```

### Phase 2: No wait for autoplay

```
T3 → T4
```

---

## Task Breakdown

### T1: Playlist in the media id and the watch URL

**What**: `internal fun playlistMediaId(videoId: String, parentId: String): String` - `"<video>@<list>"` for `watch_later` (WL), `liked` (LL) and `list:<id>`, the bare video id otherwise; `watchUrl(mediaId)` opens `watch?v=<video>&list=<list>` when the part after `@` matches `PLAYLIST_ID`, `watch?v=<video>` otherwise; with `NextVideoTest`.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: `watchUrl`, `PLAYLIST_ID`, `LIST_PREFIX`, `FEEDS`
**Requirement**: NEXT-01, NEXT-02, NEXT-03

**Done when**:

- [x] One test per AC, name starting with its ID
- [x] Gate check passes: `./gradlew testDebugUnitTest --rerun`

**Tests**: unit
**Gate**: quick

---

### T2: Media browser items carry their playlist

**What**: `fetchFeed` builds each playable item's `mediaId` with `playlistMediaId(v.id, parentId)`; thumbnails keep the bare video id.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T1
**Reuses**: `fetchFeed`, `onLoadChildren`
**Requirement**: NEXT-01

**Done when**:

- [ ] Walk: a video played from a playlist in the DHU media browser opens with `list=` and the second item starts in under 1 s after the first ends
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build

---

### T3: Rule for the early next

**What**: `internal fun earlyNextDue(ended: Boolean, autonav: Boolean?, url: String, sent: Boolean): Boolean` - true only when ended, autonav is `true`, the URL has no `list` parameter and no next was sent for this end; with `NextVideoTest`.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: `endCountdownDue` (same inputs)
**Requirement**: NEXT-04, NEXT-05, NEXT-06, NEXT-07

**Done when**:

- [ ] One test per AC, name starting with its ID
- [ ] Gate check passes: `./gradlew testDebugUnitTest --rerun`

**Tests**: unit
**Gate**: quick

---

### T4: Car screen starts the next video at the end

**What**: `setEnded` runs `JS_NEXT` when `earlyNextDue(...)` holds and remembers it was sent until the end clears; the end countdown keeps running as before.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T3
**Reuses**: `setEnded`, `js`, `JS_NEXT`
**Requirement**: NEXT-04, NEXT-08

**Done when**:

- [ ] Walk: with autoplay on, a video opened without `list=` and seeked near its end is followed by another video within 2 s
- [ ] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build

---

## Checks

**C1** - A video from Watch later, Liked or a playlist node opens with `&list=<playlist>` (NEXT-01, AC 1)
Proof: run unit | NEXT-01 a video from a playlist node opens with its playlist PASSED

**C2** - A video from History, Subscriptions or the phone remote opens as `watch?v=<video>` (NEXT-02, AC 2)
Proof: run unit | NEXT-02 a video from a feed or the phone opens without a playlist PASSED

**C3** - An invalid playlist id opens the video without a playlist (NEXT-03, AC 3)
Proof: run unit | NEXT-03 an invalid playlist id opens the video alone PASSED

**C4** - Outside a playlist with autoplay on, the end starts the next video at once (NEXT-04, AC 1)
Proof: run unit | NEXT-04 outside a playlist with autoplay on the end starts the next video PASSED

**C5** - In a playlist the app does not start the next video (NEXT-05, AC 2)
Proof: run unit | NEXT-05 in a playlist the app leaves the next video to YouTube PASSED

**C6** - With autoplay off or unknown the app does not start the next video (NEXT-06, AC 3)
Proof: run unit | NEXT-06 with autoplay off or unknown the app does not start the next video PASSED

**C7** - One end starts the next video at most once (NEXT-07, AC 4)
Proof: run unit | NEXT-07 one end starts the next video at most once PASSED

**C8** - If no next video starts, the end countdown still goes home (NEXT-08, Edge Cases)
Proof: walk - DHU + DevTools: with autoplay on and the next call made inert, the video stays ended and the countdown appears at 10 s and opens the start page

---

## Handoff

**Settled mid-build:** none
**Dispatched:** none
**Walked:** none
