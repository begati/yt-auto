# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users

- **Motorista parado:** esperando alguém, carregando o carro, no estacionamento; assiste a vídeos na tela do carro.
- **Passageiro:** controla e assiste pela tela do carro ou pelo celular.
- **Motorista em movimento:** ouve como áudio/podcast e faz ações rápidas (pausar, pular, voltar) de relance, com o
  modo só áudio opcional cobrindo o vídeo.

O usuário instala o app por fora da Play Store (APK + KingInstaller), então tem algum conforto técnico, mas usa o
carro como qualquer motorista.

## Product Purpose

Levar o YouTube à tela do carro pelo Android Auto, com a conta, inscrições e histórico do usuário, e transformar o
celular em controle remoto. Sucesso: abrir, achar e controlar um vídeo no carro sem atrito, em qualquer central.

## Positioning

O Google não permite apps de vídeo no Android Auto. O YT Auto projeta o site mobile do YouTube numa superfície de
navegação (`NavigationTemplate`) e acrescenta o que o carro não dá: gestos de vídeo, curva a curva do Waze/Maps por
cima do vídeo, controle pelo celular, mídia no navegador do Android Auto, bloqueio de anúncios e SponsorBlock.

## Operating Context

- Tela do carro: de centrais genéricas de 7" (800x480, densidade baixa) até telas grandes de fábrica. A 7" é o piso:
  tudo que o app desenha tem de ser legível e tocável nela.
- Toque de relance, braço esticado, carro vibrando; às vezes em movimento.
- O Android Auto desenha templates, action strip e cards; o app só controla a superfície (WebView + Views próprias).
- Controles paralelos: botões do volante, card de mídia do Android Auto, barra no celular, voz ("Ok Google").

## Capabilities and Constraints

- Ícones e tamanhos da action strip e dos templates são do host; o app não os aumenta.
- `NavigationTemplate` exige uma ação nativa na action strip.
- Toque na superfície (`onClick`) só em hosts com Car API 5 ou mais.
- Distribuição só por APK no GitHub; o Android Auto esconde o app sem o KingInstaller (ou Fontes desconhecidas).
- 7 idiomas (en, pt-BR, es, fr, it, ru, ar), com RTL em árabe.

## Brand Commitments

- Nome: YT Auto. Aviso de segurança e de uso por conta e risco no README é obrigatório.
- Decisão do usuário (2026-11-18): manter a action strip nativa do Android Auto em vez de botões próprios.

## Evidence on Hand

- Screenshots em `docs/` (busca, vídeo, curva no carro; app e configurações no celular; KingInstaller).
- Reclamação real: usuário de central de 7" achou os ícones pequenos (2026-11-18).
- Sem depoimentos, métricas de uso ou números de downloads além do badge do GitHub; não inventar.

## Product Principles

1. **Nativo por padrão, próprio quando o nativo falha.** Seguir o Android Auto e o YouTube mobile; desenhar só o que
   o sistema não dá ou o que o nativo não resolve em tela pequena.
2. **A central de 7" é o piso.** Toda sobreposição é dimensionada para 800x480 em densidade baixa e escala para cima.
3. **De relance.** Uma ação, um toque, leitura em menos de um segundo; nada que exija olhar fixo.
4. **Não atrapalhar o vídeo.** Sobreposições somem sozinhas ou ficam nos cantos; nada cobre o vídeo sem motivo.

## Accessibility & Inclusion

- `contentDescription` em todo botão desenhado pelo app; textos nos 7 idiomas; layout espelhado em RTL.
- Alvo de toque no carro: 76 dp mínimo (diretriz de carro do Google) para controles próprios.
