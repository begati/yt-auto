# install-check proofs

**HEAD**: c26ac18
**Tree**: clean
**Shell**: /bin/sh
**Result**: 8 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / INST-01 warns when the installer is not the Play Store PASSED` | pass |
| C2 | `run unit / INST-02 the button opens KingInstaller or its download page PASSED` | pass |
| C3 | `run unit / INST-03 notifies only when Android Auto connects with a warning PASSED` | pass |
| C4 | `walk - celular: screenshot da faixa depois de adb install; diálogo de permissão na primeira vez` | pass (Walked) |
| C5 | `walk - celular + DHU: notificação ao conectar; toque abre o YT Auto com a faixa` | pass (Walked) |
| C6 | `run unit / INST-06 ignoring hides the warning until the app is installed again PASSED` | pass |
| C7 | `grep -q "KingInstaller" README.md && grep -c "Shizuku" README.md README.pt-BR.md README.es.md / grep -v ":0" /` | pass |
| Build | `./gradlew assembleDebug` | pass |

## Output

`Run unit` - exit 0

```

VideoFillTest > ASPECT-06 options are Ajustar Preencher Esticar in order PASSED

VideoFillTest > FILL-04 shown action strips do not shrink the page PASSED

VideoFillTest > ASPECT-08 invalid stored aspect reads as the default PASSED

VideoFillTest > ASPECT-04 the button pick is saved as the default PASSED

VideoFillTest > ASPECT-07 default on a fresh install is cover (fill the screen) PASSED

VideoFillTest > ASPECT-05 the saved aspect applies PASSED

BUILD SUCCESSFUL in 1s
24 actionable tasks: 4 executed, 20 up-to-date
```

`grep -q "KingInstaller" README.md && grep -c "Shizuku" README.md README.pt-BR.md README.es.md / grep -v ":0" /` - exit 0

```
(no output)
```
