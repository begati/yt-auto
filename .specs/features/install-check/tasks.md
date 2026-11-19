# Install Check Tasks

## Execution Protocol (MANDATORY -- do not skip)

Implement these tasks with the `tlc-spec-driven` skill: **activate it by name and follow its Execute flow and Critical Rules.** Do not search for skill files by filesystem path. The skill is the source of truth for the full flow (per-task cycle, sub-agent delegation, the walk, check_proofs, the Verifier and the screens sub-agent).

**If the skill cannot be activated, STOP and tell the user - do not proceed without it.**

---

**Design**: inline (regras em `internal fun` puras testadas na JVM; faixa no `LoginActivity` no padrão da faixa de login; notificação a partir do `MediaService`)
**Status**: Approved
**Approved**: 2026-11-19 - user: "sim"
**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

---

## Test Coverage Matrix

> Generated from codebase, project guidelines, and spec - confirm before Execute. Guidelines found: `app/build.gradle.kts` (`testLogging`), specs anteriores ("só JVM", sem Robolectric).

| Code Layer | Required Test Type | Coverage Expectation | Location Pattern | Run Command |
| ---------- | ------------------ | -------------------- | ---------------- | ----------- |
| Regras puras (`installWarning`, `kingAction`, `notifyInstall`) | unit | 1:1 com os ACs que decidem algo (quando avisar, qual botão, quando notificar, X) | `app/src/test/java/dev/begati/youtubeauto/InstallCheckTest.kt` | `./gradlew testDebugUnitTest --rerun` |
| Views, notificação, permissão | none (walk) | Walk no celular + DHU | - | `./gradlew assembleDebug` |
| Strings | unit (existente) | `I18nTest` exige os 11 idiomas | `I18nTest.kt` | `./gradlew testDebugUnitTest --rerun` |
| READMEs | none | - | - | build gate only |

## Gate Check Commands

| Gate Level | When to Use | Command |
| ---------- | ----------- | ------- |
| Quick | Tarefas com testes unitários | `./gradlew testDebugUnitTest --rerun` |
| Build | Tarefas de View/docs | `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun` |

---

## Execution Plan

### Phase 1: Aviso de instalação

```
T1 → T2 → T3 → T4
```

---

## Task Breakdown

### T1: Regras do aviso

**What**: `internal fun installWarning(installer: String?, updatedAt: Long, ignoredAt: Long)` (instalador ≠ `com.android.vending` e não ignorado para esta instalação); `internal fun kingAction(kingInstalled: Boolean)` (abrir ou baixar); `internal fun notifyInstall(client: String, warning: Boolean)` (só para `com.google.android.projection.gearhead`); com `InstallCheckTest`.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: None
**Reuses**: -
**Requirement**: INST-01, INST-02, INST-03, INST-06

**Done when**:

- [x] Um teste por AC listado, nome começando pelo ID
- [x] Gate check passes: `./gradlew testDebugUnitTest --rerun`

**Tests**: unit
**Gate**: quick
**Commit**: `feat: add the rules for the install warning`

---

### T2: Faixa de instalação no celular

**What**: no `LoginActivity`, a faixa com `ic_flag`, `install_title`, `install_text`, botão (abrir o KingInstaller `com.example.kinginstaller` ou abrir `https://github.com/fcaronte/KingInstaller/releases`) e X ("Ignorar", grava a data da instalação atual); no lugar da faixa de login; pede a permissão de notificação uma vez (Android 13+); strings nos 11 idiomas.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt` (+ `res/values*/strings.xml`, `AndroidManifest.xml`)
**Depends on**: T1
**Reuses**: `signInBanner` (padrão), regras de T1
**Requirement**: INST-01, INST-02, INST-04, INST-06

**Done when**:

- [x] Walk no celular: instalado por `adb install`, a faixa aparece com "Abrir KingInstaller"; o botão abre o KingInstaller; X esconde até reinstalar
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: warn on the phone when the app was not installed with KingInstaller`

---

### T3: Notificação quando o Android Auto conecta

**What**: `MediaService.onGetRoot` com `notifyInstall` verdadeiro posta a notificação (canal `install_channel`, `install_title`, `install_text`, toque abre o `LoginActivity`), uma vez por conexão.
**Where**: `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt`
**Depends on**: T2
**Reuses**: regras de T1, strings de T2
**Requirement**: INST-03

**Done when**:

- [x] Walk: instalado por `adb install`, DHU conectando mostra a notificação no celular; tocar abre o app com a faixa
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `feat: notify when Android Auto connects to an app not installed with KingInstaller`

---

### T4: README com o roteiro

**What**: seção "The car shows…" dos três READMEs reescrita: causa mais comum primeiro e os 5 passos da spec.
**Where**: `README.md`, `README.pt-BR.md`, `README.es.md`
**Depends on**: T3
**Reuses**: texto atual da seção
**Requirement**: INST-05

**Done when**:

- [x] Os três READMEs têm os 5 passos na ordem da spec
- [x] Gate check passes: `./gradlew assembleDebug && ./gradlew testDebugUnitTest --rerun`

**Tests**: none
**Gate**: build
**Commit**: `docs: explain the car showing only the player in the order it happens`

---

## Checks

**C1** - Instalador ≠ Play Store e não ignorado: aviso; Play Store: nenhum (INST-01, AC 1-2)
Proof: run unit | INST-01 warns when the installer is not the Play Store PASSED

**C2** - Botão: abrir o KingInstaller quando ele está no celular, senão baixar (INST-02, AC 3-4)
Proof: run unit | INST-02 the button opens KingInstaller or its download page PASSED

**C3** - Notifica só quando o Android Auto conecta e há aviso (INST-03, AC 5)
Proof: run unit | INST-03 notifies only when Android Auto connects with a warning PASSED

**C4** - A faixa aparece no lugar da de login, com ícone, título, texto, botão e X; a permissão é pedida uma vez (INST-01, INST-04, AC 1 e 7)
Proof: walk - celular: screenshot da faixa depois de `adb install`; diálogo de permissão na primeira vez

**C5** - Tocar na notificação abre o app (INST-03, AC 6)
Proof: walk - celular + DHU: notificação ao conectar; toque abre o YT Auto com a faixa

**C6** - X esconde até a próxima instalação (INST-06, AC 8)
Proof: run unit | INST-06 ignoring hides the warning until the app is installed again PASSED

**C7** - READMEs com o roteiro em 5 passos, na ordem, dentro da seção do erro (INST-05)
Proof: `for f in README.md README.pt-BR.md README.es.md; do awk '/^### (The car shows|O carro mostra|El coche muestra)/{on=1;next} /^#/{on=0} on && /^[1-5]\. \*\*/{n++; if (substr($0,1,1)!=n) bad=1} END{exit !(n==5 && !bad)}' $f || exit 1; done`

**C8** - Sessão do carro notifica com instalação errada, no máximo uma vez a cada 10 minutos (INST-07, AC 9)
Proof: run unit | INST-07 a car session notifies on a wrong install, at most once per 10 minutes PASSED

---

## Handoff

**Settled mid-build:** found in the walk: Android Auto does not bind the media service on connect (only when the media card is used), so the notification is also posted from the car session, guarded to once per 10 minutes
**Dispatched:** Verifier at HEAD c26ac18 (diff 84e8b3f..HEAD), returned FAIL with 4 findings, all closed in 30b9eb9
**Walked:** 2026-11-19, orchestrator, phone over USB. Reach: `adb install -r` of the debug build (dumpsys: `installerPackageName=null`), KingInstaller installed on the phone, YT Auto opened from the launcher. C4: the strip shows the flag icon, "Reinstale pelo KingInstaller", "Instalado de outro jeito, o YT Auto aparece no carro só como player, sem o vídeo.", "Abrir KingInstaller" and X, with no sign-in banner; Android's "Permitir que o app YT Auto envie notificações?" dialog opened right after (in1) and was answered Permitir for the notification walk. "Abrir KingInstaller" brought `com.example.kinginstaller/.MainActivity` to the front. X saved `installIgnoredAt` = this install's lastUpdateTime and the strip went away (in2). C5: reinstalled by `adb install -r` (new lastUpdateTime, so the warning is back) and the DHU reconnected: Android Auto bound the car app service, not the media service, so the notification also fires on the car session (`onCreateSession`), with a 10-minute guard against the second binding of the same connection; the phone shows "YT Auto - Reinstale pelo KingInstaller - Instalado de outro jeito, o YT Auto aparece no carro só como player, sem o vídeo." (nt1); tapping it brought `dev.begati.youtubeauto/.LoginActivity` to the front.

---

## Phase Execution Map

```
Phase 1:  T1 → T2 → T3 → T4
```
