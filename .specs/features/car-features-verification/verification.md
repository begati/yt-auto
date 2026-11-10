# Car Features Verification verification

**Verdict**: FAIL
**Proofs**: `proofs.md` at b2f858f
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | `FeedParserTest.kt` `FEED-01 returns id title and channel in page order` | yes - full `FeedVideo` equality, second video nested deeper so order and recursion both count |
| C2 | `FeedParserTest.kt` `FEED-02 keeps only the first occurrence of a videoId` | yes - repeat has a different title, so keeping the last would fail |
| C3 | `FeedParserTest.kt` `FEED-03 caps the list at the first 50 videos` | yes - 51 inputs, asserts the exact first 50 ids in order |
| C4 | `FeedParserTest.kt` `FEED-04 reads simpleText runs and content titles` | yes - three shapes; runs `"Parte "+"um"+"!"` catches any separator |
| C5 | `FeedParserTest.kt` `FEED-05 reads lockup videos by contentId and lockup title` | yes |
| C6 | `FeedParserTest.kt` `FEED-06 ignores lockups that are not videos` | yes - playlist lockup has a title, so dropping the contentType test would include it |
| C7 | `FeedParserTest.kt` `FEED-07 returns empty list without ytInitialData` | yes - video JSON present under another var name, plus `{}` edge case |
| C8 | `FeedParserTest.kt` `FEED-08 skips videoIds without a title` | yes - covers absence of all titles; the `headline` source is not exercised (see Findings) |
| C9 | `FeedParserTest.kt` `FEED-09 channel falls back from shortBylineText to ownerText to empty` | yes - first item has both, so precedence is fixed |
| C10 | `SettingsTest.kt` `SET-01 defaults are 360p home page and 130 percent text` | yes |
| C11 | `SettingsTest.kt` `SET-02 flags default to on` | yes |
| C12 | `SettingsTest.kt` `SET-03 invalid stored choice reads as default` | yes - all three choices |
| C13 | `SettingsTest.kt` `SET-04 saving an invalid choice keeps the previous value` | yes - asserts the read and the stored raw value |
| C14 | `SettingsTest.kt` `SET-05 a saved valid choice is read back` | yes - non-default value for each choice |
| C15 | `SettingsTest.kt` `SET-06 a flag saved off reads off` | yes |
| C16 | `SettingsTest.kt` `SET-07 quality options are exactly the six labels in order` | yes - list equality fixes set, order and count |
| C17 | `SettingsTest.kt` `SET-08 home page and text size options in order` | yes |
| C18 | `SettingsTest.kt` `HOME-01 subscriptions home page labels the button` | yes |
| C19 | `SettingsTest.kt` `HOME-02 history home page labels the button` | yes |
| C20 | `SettingsTest.kt` `HOME-03 default home page labels the button` | yes |
| C21 | `MediaHelpersTest.kt` `ART-01 shrinks the longest side to 320 keeping proportion` | yes - landscape, portrait and the 321x321 boundary |
| C22 | `MediaHelpersTest.kt` `ART-02 keeps art of 320 or less untouched` | yes - includes exactly 320 |
| C23 | `MediaHelpersTest.kt` `THUMB-01 accepts 11 char ids from the allowed alphabet` | yes - every character class, `_` and `-` alone |
| C24 | `MediaHelpersTest.kt` `THUMB-02 rejects ids with slash dot or wrong length` | yes - `/`, `.`, 10, 12, 0, space, non-ASCII letter |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| `headline` as a title source (named in FEED-08 as a title) is held by no test; removing `ytText(v.opt("headline"))` from `collectVideos` keeps every test green | `grep headline app/src/test/...` - 0 hits; throwaway probe `{"videoId":"aaaaaaaaaaa","headline":{"simpleText":"H"}}` -> `FeedVideo(aaaaaaaaaaa, H, "")` passes at b2f858f, so the code is right but unpinned | add a `headline`-only video to the FEED-08 (or FEED-04) test and assert it is returned with that title | `5542b59` - C8 (FEED-08) now asserts a headline-only video comes back with its title |

Own examples run at b2f858f as throwaway JUnit tests (all green, deleted): cap after dedupe (60 ids with 10 repeats -> first 50 distinct), exactly 50 and `[]`, first *titled* occurrence of a repeated id wins, ids with Arabic-Indic digit / trailing or leading `\n` / `%` / full-width letter rejected, `artSize(1000,333)` = 320x107 and `(321,320)` = 320x319, saving `highres` with nothing prior leaves the key absent and reads `medium`. Observation, not a spec rule: `artSize(10000,10)` = 320x0, which `Bitmap.createScaledBitmap` would reject (art dropped, same as the pre-refactor code).
