# Findings — Conformidade

| Campo | Valor |
|---|---|
| Módulo | Conformidade |
| Documento | Findings |
| Versão | 0.1.0 |
| Data | 27-08-2026 |
| Licença | Todos os direitos reservados — ver [LICENSE](../../../LICENSE) |

> Achados confirmados (por leitura de código, teste ao vivo, ou os dois)
> sobre este módulo — cada entrada datada.
>
> Checa se o código já existente bate com o requisito que `concept.md`
> já descreve -- nunca o contrário: um achado aqui não muda o que
> `concept.md` diz que deveria existir, só revela onde o código diverge
> disso (a divergência vira pendência em `tasks.md`). Pode acontecer a
> qualquer momento: antes de `architecture.md` existir, ou depois,
> quando a implementação revela algo não previsto no desenho.
>
> Cada entrada segue [a regra de escrita geral](../../README.md#como-escrever):
> âncora explícita, campo `Confirmado por` com valor fixo, resumo
> simples, depois detalhe técnico.

## Índice
- [Achados](#achados)
- [Controle de versão](#controle-de-versão)

## Achados

### 2026-08-27-funcoes-novas-testadas-isoladamente

**Confirmado por:** teste ao vivo.

*Em resumo:* as três funções/lógicas novas escritas para as ADRs 0001,
0002 e 0003 funcionam como desenhado, testadas fora do fluxo real de
um gancho (sem simular uma chamada de ferramenta), rodando o código
direto contra casos concretos.

*Em detalhe técnico:*
- `first_unread_mandatory_doc` (já existente, reaproveitada pela ADR
  0001): registro vazio devolve o primeiro documento da lista;
  registro com os seis documentos devolve vazio. Testado numa pasta de
  rascunho isolada, com `CLAUDE_PROJECT_DIR` apontando pra lá, sem
  tocar no estado real da sessão.
- `paths_equal` (ADR 0002): três casos -- mesma pasta com letra de
  unidade em caixa diferente (`H:/...` vs `h:/...`, considerados
  iguais); pastas realmente diferentes (consideradas diferentes);
  barra invertida do Windows de um lado só (considerado igual ao
  caminho com barra normal). Os três bateram o esperado.
- Heurística de esquema embutido (ADR 0003, bloco `awk` dentro de
  `pre_commit_hygiene.sh`): bloco com `description` dentro de um
  esquema válido (`type`+`required`+`properties`) detectado; o mesmo
  bloco sem `description` não gerou alarme falso.

### 2026-08-27-sintaxe-e-json-validos

**Confirmado por:** teste ao vivo.

*Em resumo:* os quatro scripts alterados/criados hoje (`lib/common.sh`,
`pre_mandatory_reading_guard.sh`, `pre_commit_hygiene.sh`,
`stop_fact_check.sh`) têm sintaxe de shell válida, e `settings.json`
continua um JSON válido depois da edição.

*Em detalhe técnico:* `bash -n <arquivo>` (checagem de sintaxe sem
executar) rodado nos quatro, um de cada vez -- os quatro devolveram
código de saída 0. `jq empty settings.json` confirmou JSON bem
formado.

## Controle de versão

| Versão | Data | Alteração | Origem da alteração |
|---|---|---|---|
| 0.1.0 | 27-08-2026 | Criação inicial -- dois achados registrados. | Criação inicial do módulo |
