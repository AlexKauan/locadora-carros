# Sistema de Gerenciamento de Aluguel de Carros (Terminal + Arquivos)

Este projeto implementa um sistema simples de locadora de carros que roda no terminal e salva tudo em arquivos CSV.

Ele segue a descrição e o diagrama UML fornecidos para as entidades principais:

- `Usuario` (abstrata)
- `Funcionario`
- `Cliente`
- `Carro`
- `Aluguel`
- `Cargo` (enum)
- `Status` (enum)

Além disso, existem apenas classes auxiliares para:

- Entrada e validação de dados no terminal (`Console`)
- Persistência em arquivos CSV (`ArquivoCsv`)
- Ponto de entrada e menus (`Main`)

## Como executar

Abra um terminal na pasta do projeto e rode:

```bash
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
java -cp out Projeto.locadora.app.Main
```

Se você recompilar várias vezes, pode existir lixo de compilações antigas dentro de `out/`. Se algo ficar estranho, apague a pasta `out/` e compile novamente.
