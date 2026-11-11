# Car Polish Specification

**Approved**: 2026-11-11 - user: "aprovado"; tema revisto 2026-11-11 - user: "vou mudar de ideia, mantenha tema tanto do app quanto no carro sempre em dark. pode ver os demais."

## Problem Statement

Três arestas no app do carro. O botão "Tocar" aparece mesmo sem vídeo, como no Início, onde não faz nada útil.
O ícone é o `ic_media_play` cinza do sistema, sem identidade, e some entre os outros apps da central. E o tema
escuro depende do tema do celular; o usuário quer o app sempre escuro, no celular e no carro.

## Goals

- [ ] O botão Tocar/Pausar só aparece quando há vídeo para controlar.
- [ ] O app tem um ícone próprio e vermelho que, pela forma (volante), não se confunde com o YouTube nem com o YouTube Music.
- [ ] O app fica sempre no tema escuro, no celular e no carro, qualquer que seja o tema do celular ou da central.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Camada monocromática do ícone (ícones temáticos do Android 13+) | Não pedido; sem ela o Android mostra o ícone colorido |
| Tema claro, ou seguir o dia/noite da central | O usuário mudou de ideia: sempre escuro |
| Sobrepor a "Aparência" fixada em "Tema claro" na conta do YouTube | É uma escolha explícita feita dentro do próprio YouTube; o padrão da conta ("Usar tema do dispositivo") fica escuro |
| Trocar o `applicationId` / pacote `dev.begati.youtubeauto` e o nome da pasta do repositório | Trocar o `applicationId` instala outro app e perde login e configurações; a pasta não aparece para o usuário |
| Outros botões da barra de ações (Voltar, Início, Pesquisar) | Não pedido |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Quando o botão Tocar/Pausar aparece | Numa página de vídeo (`/watch`) ou enquanto um vídeo toca fora dela (miniplayer); some no Início, na busca e nas listas sem vídeo | Resposta do usuário | y - user: "Vídeo aberto ou tocando (Recomendado)" |
| Prévia do feed tocando sozinha (muda) no Início do carro | Não conta como "vídeo tocando": o botão continua escondido | Prévia não é um vídeo que o usuário abriu; mostrar "Pausar" no Início é o problema relatado | n |
| Cores do ícone | Gradiente vermelho → vermelho-escuro, sem fundo branco (antes ciano → azul) | Pedido do usuário depois de ver o azul | y - user: "pode fazer o icone vermelho ao inves de azul" |
| Símbolo do ícone | Volante estilizado com um triângulo de play no centro; sem o retângulo arredondado com play (YouTube) e sem o círculo com play (YouTube Music) | Resposta do usuário | y - user: "Volante com play (Recomendado)" |
| Nome do projeto | "YT Auto" em vez de "YouTube Auto": nome do app, mensagens ao usuário, APK (`yt-auto-<versão>-<tipo>.apk`) e projeto Gradle (`yt-auto`) | Pedido do usuário no meio da execução | y - user: "vamos também chamar todo o projeto de YT Auto ao invés de YouTube Auto" |
| Vídeo antigo na barra do celular depois do Início | Sai do vídeo sem nada tocando = a sessão de mídia perde título, capa e posição; a barra do celular some e o card de mídia fica vazio; abrir outro vídeo traz a barra de volta | Pedido do usuário depois do fechamento | y - user: "quando aperto início no android auto, tem que remover o progresso do video que ta tocando lá no yt auto do android" |
| Formato do ícone | Ícone adaptativo em vetor (fundo com gradiente + símbolo branco), usado no celular e no Android Auto | `minSdk` 29 suporta ícone adaptativo; vetor dispensa PNGs por densidade | n |
| Tema do app | Sempre escuro, no celular (página do YouTube e Configurações) e no carro, ignorando o tema do celular e o dia/noite da central | Pedido do usuário | y - user: "vou mudar de ideia, mantenha tema tanto do app quanto no carro sempre em dark" |
| A central troca dia/noite com o app aberto (faróis acesos/apagados) | A página do carro continua escura e o vídeo em reprodução continua tocando, sem recarregar | Pedido do usuário | y - user: "ao acender faróis, central muda tema pra escuro, app tem que ser capaz de suportar isso, mas sem dar reload em vídeo em reprodução" |
| Como forçar o escuro | O tema do app (`AppTheme`) deixa de ser DayNight e a `Presentation` do carro recebe um tema escuro explícito; a WebView passa `prefers-color-scheme: dark` e o YouTube, no padrão "Usar tema do dispositivo", fica escuro. Sem mexer no cookie `PREF` | Medido: só trocar o atributo `darker-dark-theme` não muda o visual da página | n |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Tocar/Pausar só com vídeo ⭐ MVP

**User Story**: Como motorista, quero ver Tocar/Pausar só quando há um vídeo, para não procurar um botão que não faz nada.

**Why P1**: É o primeiro ponto do pedido.

**Acceptance Criteria**:

1. WHILE a página do carro é uma página de vídeo (`/watch`) the app SHALL mostrar na barra de ações "Tocar" com o vídeo pausado e "Pausar" com ele tocando
2. WHILE um vídeo toca no carro fora de `/watch` (miniplayer) the app SHALL mostrar "Pausar" na barra de ações
3. WHILE a página do carro não é `/watch` e nenhum vídeo toca the app SHALL mostrar na barra de ações apenas Voltar, o botão da página inicial e Pesquisar
4. IF só uma prévia do feed toca fora de `/watch` THEN the app SHALL manter o botão Tocar/Pausar escondido
5. WHEN o usuário sai de `/watch` para o Início com o vídeo pausado THEN the app SHALL esconder o botão

15. WHEN o carro sai do vídeo para uma página sem vídeo tocando (ex.: botão Início) THEN the app SHALL tirar o vídeo da barra do celular (capa, título e progresso) e do card de mídia

**Independent Test**: no DHU, abrir o app no Início: sem Tocar; abrir um vídeo: aparece Pausar; voltar ao Início com o vídeo pausado: o botão some.

---

### P1: Ícone próprio ⭐ MVP

**User Story**: Como motorista, quero achar o app rápido na central sem confundi-lo com o YouTube ou o YouTube Music.

**Why P1**: É o segundo ponto do pedido.

**Acceptance Criteria**:

6. The app SHALL usar como ícone, no celular e no Android Auto, um volante estilizado branco com um triângulo de play no centro, sobre um fundo em gradiente vermelho → vermelho-escuro
7. The app SHALL ter um ícone sem fundo branco, sem a forma de retângulo arredondado com play e sem a de círculo com play

**Independent Test**: instalar o APK; screenshot da gaveta de apps do celular e do launcher do DHU mostra o ícone novo ao lado do YouTube e do YT Music.

---

### P1: App sempre escuro ⭐ MVP

**User Story**: Como motorista, quero o app sempre no tema escuro, no celular e no carro, sem depender do tema do celular ou da central.

**Why P1**: É o terceiro ponto do pedido, revisto pelo usuário.

**Acceptance Criteria**:

8. The app SHALL mostrar a página do YouTube no carro com o tema escuro, com o celular no tema claro
9. The app SHALL mostrar a página do YouTube no celular com o tema escuro, com o celular no tema claro
10. The app SHALL mostrar as Configurações do celular com o tema escuro, com o celular no tema claro
11. WHILE um vídeo toca no carro, WHEN a central troca entre dia e noite (ex.: faróis acesos) THEN the app SHALL manter a página escura e o vídeo tocando, sem recarregar a página

**Independent Test**: celular no claro (`adb shell cmd uimode night no`): a página do carro, a do celular e as Configurações ficam escuras.

---

### P1: Nome "YT Auto" ⭐ MVP

**User Story**: Como usuário, quero ver o app chamado "YT Auto" em todo lugar.

**Why P1**: Pedido do usuário no meio da execução.

**Acceptance Criteria**:

12. The app SHALL se chamar "YT Auto" no launcher do celular, no launcher do Android Auto e na barra de título do celular
13. The app SHALL escrever "YT Auto", nunca "YouTube Auto", nas mensagens ao usuário (avisos do celular e erro do card de mídia)
14. The build SHALL gerar o APK como `yt-auto-<versão>-<tipo>.apk`

**Independent Test**: instalar o APK `yt-auto-0.5-debug.apk`; a gaveta do celular e o launcher do DHU mostram "YT Auto".

---

## Edge Cases

- WHEN o app do carro abre direto num vídeo guardado pelo celular THEN the app SHALL mostrar o botão "Tocar" ou "Pausar" desde o início (é `/watch`)

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| PLAYBTN-01 | P1: Tocar/Pausar só com vídeo (AC 1) | Execute | Verified |
| PLAYBTN-02 | P1: Tocar/Pausar só com vídeo (AC 2) | Execute | Verified |
| PLAYBTN-03 | P1: Tocar/Pausar só com vídeo (AC 3) | Execute | Verified |
| PLAYBTN-04 | P1: Tocar/Pausar só com vídeo (AC 4) | Execute | Verified |
| PLAYBTN-05 | P1: Tocar/Pausar só com vídeo (AC 5) | Execute | Verified |
| ICON-01 | P1: Ícone próprio (AC 6) | Execute | Verified |
| ICON-02 | P1: Ícone próprio (AC 7) | Execute | Verified |
| THEME-01 | P1: App sempre escuro (AC 8) | Execute | Verified |
| THEME-02 | P1: App sempre escuro (AC 9) | Execute | Verified |
| THEME-03 | P1: App sempre escuro (AC 10) | Execute | Verified |
| THEME-04 | P1: App sempre escuro (AC 11) | Execute | Verified |
| NAME-01 | P1: Nome "YT Auto" (AC 12) | Execute | Verified |
| NAME-02 | P1: Nome "YT Auto" (AC 13) | Execute | Verified |
| NAME-03 | P1: Nome "YT Auto" (AC 14) | Execute | Verified |
| PLAYBTN-06 | P1: Tocar/Pausar só com vídeo (AC 15) | Execute | Verified |

**Coverage:** 15 total, 15 mapped to tasks, 0 unmapped

---

## Sources

- Pedido do usuário (três pontos: botão Tocar, ícone, tema escuro) - define o escopo
- `app/src/main/java/dev/begati/youtubeauto/YoutubeCarAppService.kt:674` - botão Tocar/Pausar sempre presente hoje
- `app/src/main/AndroidManifest.xml` - ícone atual `@android:drawable/ic_media_play`

---

## Success Criteria

- [ ] No Início do carro, a barra de ações não tem Tocar.
- [ ] O ícone novo aparece no launcher do DHU e na gaveta do celular.
- [ ] Com o celular no claro, o app fica escuro no celular e no carro.
