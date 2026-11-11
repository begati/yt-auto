# car-polish proofs

**HEAD**: 3826fc0
**Tree**: clean
**Shell**: /bin/sh
**Result**: 19 pass, 0 fail

| Check | Proof | Result |
| --- | --- | --- |
| C1 | `run unit / PLAYBTN-01 shown on a watch page playing or paused PASSED` | pass |
| C2 | `run unit / PLAYBTN-02 shown off the watch page while a video plays PASSED` | pass |
| C3 | `run unit / PLAYBTN-03 hidden on home search and feeds when nothing plays PASSED` | pass |
| C4 | `walk - screenshot do DHU no Início, barra de ações aberta` | pass (Walked) |
| C5 | `walk - DevTools no carro: um <video> fora do #movie_player tocando no Início; barra sem Tocar/Pausar` | pass (Walked) |
| C6 | `run unit / PLAYBTN-05 leaving the watch page paused hides the button PASSED` | pass |
| C7 | `walk - pausar um vídeo no DHU, tocar no botão da página inicial: barra sem Tocar` | pass (Walked) |
| C8 | `walk - screenshots do launcher do DHU e da gaveta do celular` | pass (Walked) |
| C9 | `walk - screenshot da gaveta do celular com o ícone ao lado do YouTube e do YT Music` | pass (Walked) |
| C10 | `walk - cmd uimode night no, app reaberto no DHU: DevTools do carro matchMedia('(prefers-color-scheme: dark)').` | pass (Walked) |
| C11 | `walk - cmd uimode night no, app reaberto no celular: DevTools do celular com prefers-color-scheme: dark; scree` | pass (Walked) |
| C12 | `walk - cmd uimode night no: screenshot das Configurações escuras` | pass (Walked) |
| C13 | `walk - day/night no console do DHU, com CAR.SYS : setting night mode false/true no logcat do celular; DevTools` | pass (Walked) |
| C16 | `walk - screenshots da gaveta do celular, da barra de título do app e do launcher do DHU` | pass (Walked) |
| C17 | `! grep -rn "YouTube Auto" app/src && grep -q '"Abra o YT Auto no carro para tocar"' app/src/main/java/dev/bega` | pass |
| C18 | `./gradlew assembleDebug && test -f app/build/outputs/apk/debug/yt-auto-0.5-debug.apk` | pass |
| C19 | `walk - DHU: vídeo aberto = Pausar com paused false; toque em Pausar = Tocar com paused true; toque em Tocar = ` | pass (Walked) |
| C20 | `walk - DHU no Início (/), DevTools do carro #movie_player video.play(): barra com Pausar; .pause(): barra sem ` | pass (Walked) |
| Build | `./gradlew assembleDebug` | pass |

## Output

`Run unit` - exit 0

```

VideoFillTest > ASPECT-06 options are Ajustar Preencher Esticar in order PASSED

VideoFillTest > FILL-04 shown action strips do not shrink the page PASSED

VideoFillTest > ASPECT-07 default is contain PASSED

VideoFillTest > ASPECT-04 tapping leaves the saved default unchanged PASSED

VideoFillTest > ASPECT-09 changing the default drops the tapped mode PASSED

VideoFillTest > ASPECT-01 tapping cycles contain cover fill contain PASSED

BUILD SUCCESSFUL in 1s
22 actionable tasks: 1 executed, 21 up-to-date
```

`! grep -rn "YouTube Auto" app/src && grep -q '"Abra o YT Auto no carro para tocar"' app/src/main/java/dev/bega` - exit 0

```
(no output)
```

`./gradlew assembleDebug && test -f app/build/outputs/apk/debug/yt-auto-0.5-debug.apk` - exit 0

```
> Task :app:mergeExtDexDebug UP-TO-DATE
> Task :app:mergeLibDexDebug UP-TO-DATE
> Task :app:mergeProjectDexDebug UP-TO-DATE
> Task :app:mergeDebugJniLibFolders UP-TO-DATE
> Task :app:mergeDebugNativeLibs NO-SOURCE
> Task :app:stripDebugDebugSymbols NO-SOURCE
> Task :app:validateSigningDebug UP-TO-DATE
> Task :app:writeDebugAppMetadata UP-TO-DATE
> Task :app:writeDebugSigningConfigVersions UP-TO-DATE
> Task :app:packageDebug UP-TO-DATE
> Task :app:createDebugApkListingFileRedirect UP-TO-DATE
> Task :app:assembleDebug UP-TO-DATE

BUILD SUCCESSFUL in 757ms
33 actionable tasks: 33 up-to-date
```
