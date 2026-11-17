# Car Sign-in verification

**Verdict**: PASS (round 2: round 1 finding closed by 82cc898; round 1 was FAIL)
**Proofs**: `proofs.md` at a7c154a
**Verifier**: independent sub-agent (author != verifier)

## Checks

| Check | Test | Holds |
| --- | --- | --- |
| C1 | `CarSignInTest.kt` `SIGNIN-01 accounts google com is blocked on the car` | yes - asserts `isCarSignIn` true on ServiceLogin, v3 identifier, upper-case host and http; the wiring (`isForMainFrame`, `return true`) is held by the C2 walk. Misses URLs `java.net.URI` rejects, see Findings |
| C2 | walk (`**Walked:**` 2026-11-17, DHU: "Fazer login" href `https://accounts.google.com/ServiceLogin?...`, toast "Faça login no app do celular", `location.href` still `https://m.youtube.com/feed/library`) | yes - the walk reads the page URL after the tap and the toast text; code shows `CarToast.LENGTH_LONG` with `R.string.notice_signed_out_hint` and `return true` only for main-frame sign-in |
| C3 | `CarSignInTest.kt` `SIGNIN-03 other hosts and bad urls are not blocked` | yes - asserts false on the three hosts AC 3 names, the host in a query, a look-alike suffix host, an unparseable string, empty and null |
| C4 | walk (`**Walked:**` 2026-11-17, phone: "Fazer login" reaches `https://accounts.google.com/v3/signin/identifier?...` with the e-mail field) | yes - and by code: the override lives only in `YoutubeScreen`'s anonymous `OfflineRetry`; `LoginActivity` uses a plain `OfflineRetry` subclass with no `shouldOverrideUrlLoading` |

## Findings

| Finding | Evidence | Fix | Closed |
| --- | --- | --- | --- |
| A sign-in URL on `accounts.google.com` whose path or query holds a character `java.net.URI` rejects (`\|`, `{`, `}`, `^`, backtick) is not blocked, so the car opens the form (AC 1 says any URL on that host). Chromium's URL canonicalizer leaves these characters raw in the query, so `request.url.toString()` can carry them, e.g. a `continue=` back to a search for `a\|b` | Throwaway JVM test at a7c154a: `isCarSignIn("https://accounts.google.com/ServiceLogin?continue=https://m.youtube.com/results?search_query=a\|b")`, `...?q={a}` and `...?q=a^b` all return false (`URISyntaxException` swallowed by `runCatching`) | Take the host without strict parsing - pass `request.url.host` from the callback, or parse with `java.net.URL(url).host` - and add such a URL to the SIGNIN-01 test | 82cc898 - C1 (SIGNIN-01 now includes `a\|b` and `{a}^b` URLs) |
