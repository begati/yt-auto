# car-signin proofs

**HEAD**: 205abed
**Tree**: clean
**Shell**: /bin/sh
**Result**: 5 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / SIGNIN-01 accounts google com is blocked on the car PASSED` | pass |
| C2 | `walk - app data cleared, DHU: tap "Fazer login" on the car page; screenshot with the toast and the YouTube pag` | pass (Walked) |
| C3 | `run unit / SIGNIN-03 other hosts and bad urls are not blocked PASSED` | pass |
| C4 | `walk - phone: "Fazer login" in YT Auto opens accounts.google.com; screenshot of the form` | pass (Walked) |
| Build | `./gradlew assembleDebug` | pass |

## Output

`Run unit` - exit 0

```
> Task :app:compileDebugUnitTestKotlin UP-TO-DATE
> Task :app:preDebugUnitTestBuild UP-TO-DATE
> Task :app:javaPreCompileDebugUnitTest UP-TO-DATE
> Task :app:compileDebugUnitTestJavaWithJavac NO-SOURCE
> Task :app:processDebugJavaRes UP-TO-DATE
> Task :app:processDebugUnitTestJavaRes UP-TO-DATE

> Task :app:testDebugUnitTest

CarSignInTest > SIGNIN-01 accounts google com is blocked on the car PASSED

CarSignInTest > SIGNIN-03 other hosts and bad urls are not blocked PASSED

BUILD SUCCESSFUL in 2s
22 actionable tasks: 1 executed, 21 up-to-date
```
