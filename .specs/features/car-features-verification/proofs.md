# car-features-verification proofs

**HEAD**: 152b6e0
**Tree**: clean
**Shell**: /bin/sh
**Result**: 25 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / FEED-01 returns id title and channel in page order PASSED` | pass |
| C2 | `run unit / FEED-02 keeps only the first occurrence of a videoId PASSED` | pass |
| C3 | `run unit / FEED-03 caps the list at the first 50 videos PASSED` | pass |
| C4 | `run unit / FEED-04 reads simpleText runs and content titles PASSED` | pass |
| C5 | `run unit / FEED-05 reads lockup videos by contentId and lockup title PASSED` | pass |
| C6 | `run unit / FEED-06 ignores lockups that are not videos PASSED` | pass |
| C7 | `run unit / FEED-07 returns empty list without ytInitialData PASSED` | pass |
| C8 | `run unit / FEED-08 skips videoIds without a title PASSED` | pass |
| C9 | `run unit / FEED-09 channel falls back from shortBylineText to ownerText to empty PASSED` | pass |
| C10 | `run unit / SET-01 defaults are 360p home page and 130 percent text PASSED` | pass |
| C11 | `run unit / SET-02 flags default to on PASSED` | pass |
| C12 | `run unit / SET-03 invalid stored choice reads as default PASSED` | pass |
| C13 | `run unit / SET-04 saving an invalid choice keeps the previous value PASSED` | pass |
| C14 | `run unit / SET-05 a saved valid choice is read back PASSED` | pass |
| C15 | `run unit / SET-06 a flag saved off reads off PASSED` | pass |
| C16 | `run unit / SET-07 quality options are exactly the six labels in order PASSED` | pass |
| C17 | `run unit / SET-08 home page and text size options in order PASSED` | pass |
| C18 | `run unit / HOME-01 subscriptions home page labels the button PASSED` | pass |
| C19 | `run unit / HOME-02 history home page labels the button PASSED` | pass |
| C20 | `run unit / HOME-03 default home page labels the button PASSED` | pass |
| C21 | `run unit / ART-01 shrinks the longest side to 320 keeping proportion PASSED` | pass |
| C22 | `run unit / ART-02 keeps art of 320 or less untouched PASSED` | pass |
| C23 | `run unit / THUMB-01 accepts 11 char ids from the allowed alphabet PASSED` | pass |
| C24 | `run unit / THUMB-02 rejects ids with slash dot or wrong length PASSED` | pass |
| Build | `./gradlew assembleDebug` | pass |

## Output

`Run unit` - exit 0

```

SettingsTest > HOME-02 history home page labels the button PASSED

SettingsTest > SET-04 saving an invalid choice keeps the previous value PASSED

SettingsTest > SET-05 a saved valid choice is read back PASSED

SettingsTest > SET-03 invalid stored choice reads as default PASSED

SettingsTest > SET-08 home page and text size options in order PASSED

SettingsTest > SET-02 flags default to on PASSED

BUILD SUCCESSFUL in 1s
22 actionable tasks: 1 executed, 21 up-to-date
```
