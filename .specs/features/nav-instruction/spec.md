# Nav Instruction Specification

**Approved**: 2026-11-18 - user: "aprovado, pode implementar"

## Problem Statement

O app se registra no Android Auto como app de navegação (é a única categoria que recebe um `Surface`). Com ele na frente,
o Android Auto esconde o card curva a curva do Waze na barra inferior; testado no DHU em 2026-11-18: com o Spotify na
frente o card aparece ("10 m · Siga pela rota destacada"), com este app não. O Waze continua navegando e publica a
instrução numa notificação no celular. Pedido do usuário: "faz isso" (desenhar a instrução por cima do vídeo).

## Goals

- [ ] Com o Waze ou o Google Maps navegando, o motorista vê a próxima instrução (distância e manobra) na tela do app no carro, sem sair do vídeo.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Ler o pacote `androidx.car.app.EXTENSIONS` do Waze | 208 bytes, sem espaço para imagem; ícone vem do texto (TURN-10) |
| Tocar na instrução para abrir o Waze/Maps | Não pedido |
| Outros apps de navegação | Pedido cita Waze e Google Maps |
| Teste instrumentado do listener e da view | Projeto testa só na JVM; as regras ficam em funções puras, a tela é provada no DHU |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Posição | Canto superior esquerdo da área visível do app | Longe dos botões do app (direita) | y - user: "Canto superior esquerdo (Recomendado)" |
| Em quais telas | Em toda tela do app no carro (feed, busca, vídeo), enquanto houver navegação | Navegação importa em qualquer tela | y - user: "Sempre que o app está na tela (Recomendado)" |
| Ativação | Chave "Navegação na tela" no grupo Carro das configurações do celular; mexer nela sem acesso abre a tela de acesso a notificações do Android | Mesmo padrão do "só áudio" que pede GPS | y - user: "Chave nas Configurações (Recomendado)" |
| Padrão e chave no carro | Chave ligada por padrão, também nas configurações do carro (`CAR_SETTINGS`) | Pedido no meio do build | y - user: "botar opção pra ligar e desativar isso no app também, e como padrão, ligado" |
| Ligada sem acesso | No celular, o resumo da chave vira o aviso `nav_access_needed`; no carro, ligar a chave sem acesso mostra um `CarToast` com o mesmo aviso (o carro não abre telas do celular) | Padrão ligado não basta: o acesso só se concede no celular | n - default do agente |
| README | Recurso citado em Features, com print do card no carro, e como conceder o acesso | Pedido | y - user: "faz uns prints do curva a curva também, acho que vale citar isso no readme como recurso" |
| Apps lidos | Waze (`com.waze`) e Google Maps (`com.google.android.apps.maps`) | Pedido | y - user: "Waze e Google Maps" |
| Qual notificação | Do pacote acima com `category=navigation`; distância = `android.title`, manobra = `android.text` | Formato visto no Waze (canal "Waze Navigation Instructions"); o do Maps não foi conferido e é verificado no walk | n - default do agente |
| Seta do Maps | Pintada de branco, como os ícones próprios | No walk o Maps mandou `largeIcon` 144x144 com `tint=0xff000000` (glifo de uma cor); sem pintar, sairia preto no card preto | n - default do agente |
| Seta | Sempre há ícone: o `largeIcon` da notificação quando existe (Maps costuma ter); sem ele (Waze), um ícone próprio do app escolhido pelas palavras da manobra | Waze não traz ícone (`largeIcon=null`, só um `androidx.car.app.EXTENSIONS` de 208 bytes) | y - user: "consegue implementar ícones também? pra nao ficar só texto?" |
| Ícones próprios | Chegada, rotatória, retorno, manter/saída à esquerda, manter/saída à direita, virar à esquerda, virar à direita, seguir em frente (padrão) | Manobras que o texto identifica; o resto segue em frente | n - default do agente |
| Palavras por manobra | Nos 7 idiomas do app (o texto vem no idioma do Waze); ordem: chegada, rotatória, retorno, manter/saída, virar; com duas direções no texto, vale a primeira | Texto como "vire à esquerda e depois à direita" descreve primeiro a manobra próxima | n - default do agente |
| Distância até a manobra | Mostrada em destaque, como o Waze/Maps escreve no título ("500 m", "1,2 km", "0.5 mi"), na unidade configurada no app de navegação, sem conversão; atualiza a cada nova notificação | Visto no aparelho: `android.title=10 m` | y - user: "voce consegue fazer também a instrução da distancia da proxima manobra? ex 500m, 1 milha" |
| Texto longo | Distância em 1 linha; manobra em até 2 linhas com reticências | Legível de relance | n - default do agente |
| Por cima de quê | Acima da página, da tela offline e da capa "só áudio"; não recebe toques | Dirigindo, a capa de só áudio é justamente quando mais importa | n - default do agente |
| Idiomas | Textos novos nos 7 idiomas do app | `I18nTest` exige | n - default do código |
| Visual | Decidido com o skill impeccable no Execute, dentro do estilo já usado nos overlays (escuro translúcido, texto branco) | Pedido: "usando impeccable" | n - default do agente |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Instrução de navegação na tela do app ⭐ MVP

**User Story**: Como motorista assistindo um vídeo no carro, quero ver a próxima curva do Waze ou do Maps, para não perder a rota.

**Why P1**: É o pedido inteiro.

**Acceptance Criteria**:

1. WHEN o Waze ou o Google Maps publica ou atualiza uma notificação com `category=navigation` e a chave "Navegação na tela" está ligada THEN the app SHALL mostrar no canto superior esquerdo da área visível, na tela do carro, a distância (`android.title`) e a manobra (`android.text`) dessa notificação
2. WHEN essa notificação é removida (fim da navegação) THEN the app SHALL esconder a instrução
3. IF a notificação vem de outro pacote, não tem `category=navigation`, ou tem título e texto vazios THEN the app SHALL ignorá-la
4. WHILE a chave "Navegação na tela" está desligada the app SHALL não mostrar instrução nenhuma
5. WHEN o usuário muda a chave "Navegação na tela" no celular e o app não tem acesso a notificações THEN the app SHALL abrir a tela de acesso a notificações do Android
6. WHERE a notificação traz `largeIcon` the app SHALL mostrar esse ícone ao lado do texto
7. The instrução SHALL ficar visível também por cima da capa "só áudio" e da tela offline, e SHALL não receber toques
8. WHEN o celular está em qualquer um dos 7 idiomas do app THEN the app SHALL mostrar o título e o resumo da chave nesse idioma
9. IF a notificação não traz `largeIcon` THEN the app SHALL mostrar ao lado do texto o ícone próprio da manobra escolhido pelas palavras de `android.text` (tabela em Assumptions), e o de seguir em frente quando nenhuma palavra casa
10. IF `android.title` não começa com um algarismo ou tem mais de 12 caracteres (não é uma distância, como o Maps antes da primeira manobra: "Siga em direção à R. Roberto Stiegert") THEN the app SHALL mostrar o título como manobra, seguido de `android.text` quando houver, e nenhuma distância
11. The chave "Navegação na tela" SHALL vir ligada numa instalação nova
12. WHEN o motorista usa a chave "Navegação na tela" nas configurações do carro THEN the app SHALL ligar ou desligar a mesma chave do celular
13. IF o motorista liga a chave no carro e o app não tem acesso a notificações THEN the app SHALL mostrar um `CarToast` com o texto `nav_access_needed`
14. WHILE a chave está ligada e o app não tem acesso a notificações the app SHALL mostrar `nav_access_needed` como resumo da chave no celular

**Independent Test**: no DHU, com a chave ligada e o acesso concedido, iniciar uma rota no Waze e abrir um vídeo no app mostra a instrução no canto superior esquerdo; encerrar a rota a esconde.

---

## Edge Cases

- IF o Waze e o Maps navegam ao mesmo tempo THEN the app SHALL mostrar a instrução da notificação publicada por último
- IF o acesso a notificações é revogado com a chave ligada THEN the app SHALL não mostrar instrução (o Android para de entregar notificações)

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| TURN-01 | P1 (AC 1) | Tasks | Pending |
| TURN-02 | P1 (AC 2) | Tasks | Pending |
| TURN-03 | P1 (AC 3) | Tasks | Pending |
| TURN-04 | P1 (AC 4) | Tasks | Pending |
| TURN-05 | P1 (AC 5) | Tasks | Pending |
| TURN-06 | P1 (AC 6) | Tasks | Pending |
| TURN-07 | P1 (AC 7) | Tasks | Pending |
| TURN-08 | P1 (AC 8) | Tasks | Pending |
| TURN-09 | Edge: última publicada | Tasks | Pending |
| TURN-10 | P1 (AC 9) | Tasks | Pending |
| TURN-11 | P1 (AC 10), achado no walk | Execute | Pending |
| TURN-12 | P1 (AC 11) | Execute | Pending |
| TURN-13 | P1 (AC 12) | Execute | Pending |
| TURN-14 | P1 (AC 13) | Execute | Pending |
| TURN-15 | P1 (AC 14) | Execute | Pending |
| TURN-16 | README | Execute | Pending |

**Coverage:** 16 total, 0 mapped to tasks, 16 unmapped ⚠️

---

## Sources

- Pedido do usuário, 2026-11-18: "faz isso, usando impeccable e tlc e vamos testar como fica"
- Teste no DHU, 2026-11-18: card do Waze some com este app na frente; notificação do Waze `channel=Waze Navigation Instructions`, `category=navigation`, `android.title=10 m`, `android.text=Siga pela rota destacada`, `largeIcon=null`, `androidx.car.app.EXTENSIONS` com 208 bytes; publicada só com o Android Auto conectado e o Waze fora da frente
- Pedido do usuário, 2026-11-18: "consegue implementar ícones também? pra nao ficar só texto?"

---

## Success Criteria

- [ ] No DHU, a instrução do Waze aparece por cima do vídeo e some ao encerrar a rota.

---

## Execution Plan

1. [x] Regras puras e testes (filtro da notificação, ícone pela manobra, última publicada vence, chave desligada esconde) e a chave `navOverlay` nas configurações do celular, com textos nos 7 idiomas e ícone -> `YoutubeCarAppService.kt`, `NavInstructionTest.kt`, `UiPolishTest.kt` (chave nova nas listas completas), `values*/strings.xml`, `ic_navigation.xml` -> `testDebugUnitTest` -> `feat: read the turn instruction from waze and maps notifications`
2. [x] Listener de notificações e o pedido de acesso ao ligar a chave -> `YoutubeCarAppService.kt`, `AndroidManifest.xml` -> build + testes -> `feat: listen to navigation notifications and ask for access`
3. [x] Overlay no carro com os ícones de manobra (visual pelo impeccable) -> `YoutubeCarAppService.kt`, drawables -> build + walk no DHU -> `feat: show the next turn over the car screen`

## Checks

**Build**: `./gradlew assembleDebug`
**Run unit**: `./gradlew testDebugUnitTest --rerun`

**C1** - Notificação do Waze/Maps com `category=navigation` vira distância (título) e manobra (texto) (TURN-01, AC 1)
Proof: run unit | TURN-01 a navigation notification from waze or maps gives distance and maneuver PASSED

**C2** - Remover a notificação esconde a instrução (TURN-02, AC 2)
Proof: run unit | TURN-02 removing the notification hides the instruction PASSED

**C3** - Outro pacote, outra categoria ou título e texto vazios são ignorados (TURN-03, AC 3)
Proof: run unit | TURN-03 other apps other categories and empty notifications are ignored PASSED

**C4** - Chave desligada não mostra instrução (TURN-04, AC 4)
Proof: run unit | TURN-04 nothing shows while the switch is off PASSED

**C13** - Chave ligada numa instalação nova (TURN-12, AC 11)
Proof: run unit | TURN-12 the switch is on by default PASSED

**C14** - Chave na lista do carro (TURN-13, AC 12)
Proof: run unit | CSET-01 car settings are quality speed captions navigation PASSED

**C15** - Carro sem acesso: toast `nav_access_needed`; celular sem acesso: resumo `nav_access_needed` (TURN-14, TURN-15, AC 13, AC 14)
Proof: walk - acesso revogado: ligar a chave no DHU mostra o toast; o resumo da chave no celular mostra o aviso

**C16** - README cita o recurso com print e o acesso (TURN-16)
Proof: walk - README.md e README.pt-BR.md com a linha em Features e o print `docs/car-turn.png`

**C5** - Ligar a chave sem acesso abre a tela de acesso a notificações (TURN-05, AC 5)
Proof: walk - celular: ligar "Navegação na tela" abre a tela de acesso a notificações do Android

**C6** - `largeIcon` da notificação aparece ao lado do texto (TURN-06, AC 6)
Proof: walk - DHU com rota no Google Maps: screenshot do overlay com a seta do Maps

**C7** - Instrução por cima da capa "só áudio" e da tela offline, sem receber toques (TURN-07, AC 7)
Proof: walk - DHU: screenshot do overlay sobre a capa "só áudio"; toque no overlay chega à página

**C8** - Título e resumo da chave nos 7 idiomas (TURN-08, AC 8)
Proof: run unit | all languages have the same strings as English PASSED

**C9** - Com Waze e Maps navegando, vale a última publicada (TURN-09)
Proof: run unit | TURN-09 the last posted instruction wins PASSED

**C10** - Sem `largeIcon`, o ícone vem das palavras da manobra; sem palavra, seguir em frente (TURN-10, AC 9)
Proof: run unit | TURN-10 the maneuver words pick the icon PASSED

**C12** - Título que não é distância vira a manobra, sem distância (TURN-11, AC 10)
Proof: run unit | TURN-11 a title that is not a distance is the maneuver PASSED

**C11** - Overlay aparece no carro com distância, manobra e ícone e some ao encerrar a rota (TURN-01, TURN-02)
Proof: walk - DHU com rota no Waze: screenshot do overlay sobre o vídeo; encerrar a rota o esconde

---

## Handoff

**Settled mid-build:** user: "faz uns prints do curva a curva também, acho que vale citar isso no readme como recurso... botar opção pra ligar e desativar isso no app também, e como padrão, ligado." (AC 11 a 14, TURN-16)
**Dispatched:** none
**Walked:** pending
