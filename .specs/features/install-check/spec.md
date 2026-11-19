# Install Check Specification

**Approved**: 2026-11-19 - user: "aprovado, segue para as tarefas"
**Amendment (P1b)**: 2026-11-19 - user: "sim, pode implementar"

## Problem Statement

O Android Auto do carro esconde a tela de vídeo de apps cujo instalador registrado não é a Play Store; o carro mostra
só o player de mídia ("Abra o YT Auto pela lista de apps do carro"). O KingInstaller grava a Play Store como
instalador; uma instalação por `adb install`, pelo instalador do Android ou abrindo o APK por cima apaga isso. Visto
pelo usuário no carro em 2026-11-19: "quando vc instalou o apk diretamente no celular sem passar pelo kingstaller,
começou a dar o mesmo problema descrito no README". O app não avisa nada, e o README só fala de Fontes desconhecidas.

## Goals

- [ ] Quem tem o YT Auto instalado sem o KingInstaller descobre isso no celular, com o passo para resolver, antes de chegar no carro.
- [ ] O README explica o problema na ordem em que ele costuma acontecer.

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Detectar o caso "instalado pelo KingInstaller e o carro esconde mesmo assim" | O app não tem como saber se o Android Auto listou a tela de vídeo; o player de mídia sozinho também é um uso normal (navegador de mídia). Fica no README |
| Instalar ou reinstalar o app sozinho | Só o usuário instala pelo KingInstaller |
| Mudar os campos de origem que o KingInstaller não troca (`initiatingPackageName`, `packageSource`) | Não dá para mudar de dentro do app; nada indica hoje que o Android Auto lê esses campos |

---

## Assumptions & Open Questions

| Assumption / decision | Chosen default | Rationale | Confirmed? |
| --------------------- | -------------- | --------- | ---------- |
| Onde avisar | Faixa no topo do app no celular e notificação quando o Android Auto conecta | Decisão do usuário | y - user: "Faixa no app + notificação (Recomendado)" |
| Como detectar | Instalador registrado (`getInstallSourceInfo().installingPackageName`) diferente de `com.android.vending` | É o campo que o KingInstaller troca; visto no celular: `adb install` deixa vazio, KingInstaller grava `com.android.vending` | y - user: "aprovado, segue para as tarefas" |
| Texto da faixa e da notificação | Título `install_title` "Reinstale pelo KingInstaller" e linha `install_text` "Instalado de outro jeito, o YT Auto aparece no carro só como player, sem o vídeo." | Revisão do impeccable (clarify): a ação primeiro; o efeito em palavras do motorista ("só como player, sem o vídeo"), sem o termo interno "tela de vídeo" | y - user: "aprovado, segue para as tarefas" |
| Botão da faixa | "Abrir KingInstaller" quando ele está no celular (abre o app); senão "Baixar KingInstaller" (abre a página de releases dele) | Leva direto ao passo | y - user: "aprovado, segue para as tarefas" |
| Fechar a faixa | X ("Ignorar") esconde a faixa e as notificações até a próxima instalação do app (nova data de atualização); reinstalado pelo KingInstaller, some sozinha | Revisão do impeccable: há instalações válidas sem o KingInstaller (Fontes desconhecidas no Android Auto, passo 4 do README, ex. GrapheneOS); um aviso que não fecha vira ruído permanente para elas | y - user: "aprovado, segue para as tarefas" |
| Faixa de login junto | Enquanto a faixa de instalação aparece, a de login não aparece | Revisão do impeccable: uma coisa de cada vez; a instalação bloqueia o carro, o login não | y - user: "aprovado, segue para as tarefas" |
| Ícone da faixa | Ícone próprio (`ic_flag`), não o de conta da faixa de login | As duas faixas não podem parecer o mesmo aviso | y - user: "aprovado, segue para as tarefas" |
| Quando notificar | Quando o Android Auto (`com.google.android.projection.gearhead`) se conecta ao player de mídia do app e o instalador está errado; uma vez por conexão | É quando o motorista percebe o problema | y - user: "aprovado, segue para as tarefas" |
| Permissão de notificação | Pedida (Android 13+) uma vez, logo depois da faixa aparecer, só se ainda não foi concedida; sem permissão, só a faixa | No contexto do aviso que ela serve (revisão do impeccable: nunca um pedido frio); quem não tem o problema nunca vê o pedido | y - user: "aprovado, segue para as tarefas" |
| Notificação | Canal "Instalação" (`install_channel`), título `install_title`, texto `install_text`; tocar abre o YT Auto no celular, com a faixa | Mesmo texto nos dois lugares; a faixa tem o botão do passo | y - user: "aprovado, segue para as tarefas" |
| Idiomas | Textos nos 11 idiomas | `I18nTest` | y - user: "aprovado, segue para as tarefas" |
| README | Seção reescrita nos 3 idiomas, em ordem: 1) ver se o app mostra a faixa; 2) reinstalar pelo KingInstaller (e não atualizar abrindo o APK direto); 3) outros métodos do KingInstaller (Shizuku, Root); 4) Fontes desconhecidas no Android Auto; 5) Personalizar inicializador | O caso mais comum primeiro | y - user: "aprovado, segue para as tarefas" |

**Open questions:** none - all resolved or logged above (required before the spec is confirmed).

---

## User Stories

### P1: Aviso de instalação errada ⭐ MVP

**User Story**: Como usuário que instalou ou atualizou o YT Auto sem o KingInstaller, quero ser avisado no celular, para reinstalar antes de chegar no carro.

**Why P1**: É o problema relatado.

**Acceptance Criteria**:

1. WHEN o app abre no celular e o instalador registrado não é `com.android.vending` THEN the app SHALL mostrar no topo a faixa com `install_title`, `install_text`, o botão de ação e o X, no lugar da faixa de login
2. IF o instalador registrado é `com.android.vending` THEN the app SHALL não mostrar a faixa
3. WHERE o KingInstaller está instalado THEN the app SHALL mostrar o botão "Abrir KingInstaller", que abre o KingInstaller
4. WHERE o KingInstaller não está instalado THEN the app SHALL mostrar o botão "Baixar KingInstaller", que abre `https://github.com/fcaronte/KingInstaller/releases`
5. WHEN o Android Auto se conecta ao player de mídia do app e o instalador registrado não é `com.android.vending` THEN the app SHALL mostrar uma notificação com `install_title` e `install_text`
6. WHEN o motorista toca na notificação THEN the app SHALL abrir o YT Auto no celular
7. WHILE a permissão de notificação não foi concedida (Android 13+) the app SHALL pedi-la uma vez, ao mostrar a faixa
8. WHEN o usuário toca no X da faixa THEN the app SHALL esconder a faixa e não notificar até a próxima instalação do app
9. WHEN o Android Auto abre uma sessão do carro e o instalador registrado não é `com.android.vending` THEN the app SHALL mostrar a mesma notificação, no máximo uma vez a cada 10 minutos somando os dois caminhos (achado no walk: o Android Auto não liga o player de mídia ao conectar; requisito novo pelo Verifier)

**Independent Test**: com o app instalado por `adb install`, o celular mostra a faixa e o DHU conectando dispara a notificação; reinstalado pelo KingInstaller, nada aparece.

---

### P1b: Aviso quando o KingInstaller foi usado e o método falhou

**User Story**: Como usuário que já instalou pelo KingInstaller, quero que o aviso me diga para trocar de método, e não que eu "instalei de outro jeito".

**Why P1b**: Pergunta do usuário (2026-11-19): "fica claro pro usuário que ele pôde ter instalado via kingstaller mas o método selecionado não funcionou a contento?". Teste com um app comum no celular: a origem da instalação (`originatingPackageName`) volta `null` para apps sem permissão de sistema, então o app não sabe se o KingInstaller foi usado; sabe se ele está no celular.

**Acceptance Criteria**:

1. WHERE o KingInstaller está no celular and o instalador registrado não é `com.android.vending` THEN the app SHALL mostrar na faixa e na notificação o título `install_title_king` "O YT Auto não ficou registrado como da Play Store" e o texto `install_text_king` "Reinstale pelo KingInstaller. Se já reinstalou, escolha outro método (Shizuku ou Root)."
2. WHERE o KingInstaller não está no celular and o instalador registrado não é `com.android.vending` THEN the app SHALL manter `install_title` e `install_text`

**Independent Test**: com o KingInstaller no celular e o app instalado por `adb install`, a faixa e a notificação mostram o texto novo; desinstalar o KingInstaller não é preciso para o caso 2 (coberto pela regra).

---

### P2: README que resolve

**User Story**: Como usuário com o carro mostrando só o player de mídia, quero um roteiro na ordem certa.

**Why P2**: Pedido: "ajustar o README, a informação que tá lá hoje não resolve".

**Acceptance Criteria**:

1. The README (en, pt-BR, es) SHALL explicar na seção do erro que a causa mais comum é o app instalado ou atualizado sem o KingInstaller, e SHALL listar os passos na ordem da linha "README" das Assumptions

**Independent Test**: leitura dos três READMEs.

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| INST-01 | P1 (AC 1-2) | Tasks | Pending |
| INST-02 | P1 (AC 3-4) | Tasks | Pending |
| INST-03 | P1 (AC 5-6) | Tasks | Pending |
| INST-04 | P1 (AC 7) | Tasks | Pending |
| INST-06 | P1 (AC 8) | Tasks | Pending |
| INST-07 | P1 (AC 9) | Tasks | Pending |
| INST-08 | P1b (AC 1-2) | Tasks | Pending |
| INST-05 | P2 (AC 1) | Tasks | Pending |

**Coverage:** 8 total, 0 mapped to tasks, 5 unmapped ⚠️

## Sources

- Relato do usuário no carro (2026-11-19), citado no Problem Statement
- `dumpsys package dev.begati.youtubeauto` depois da instalação pelo KingInstaller: `installerPackageName=com.android.vending`, `initiatingPackageName=com.google.android.packageinstaller`, `originatingPackageName=com.example.kinginstaller`, `packageSource=4`
- Teste com um app sem permissões (2026-11-19, depois da reinstalação pelo KingInstaller): `installing=com.android.vending initiating=com.google.android.packageinstaller originating=null source=4`
- Revisão do impeccable (clarify + Android): ação primeiro no texto, sem jargão; aviso fechável por haver instalações válidas sem KingInstaller; uma faixa por vez; ícone próprio; permissão pedida no contexto do aviso
- Memória do projeto: `adb install` deixa `installerPackageName=null`; o DHU lista o app, o carro esconde
