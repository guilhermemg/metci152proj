Tutorial de Uso do Classificador

1. Unzip o arquivo zip recebido
2. Entre na pasta 'metci' criada após deszipar
3. Execute ./classificador.sh com os seguintes parâmetros:
3.1		Caso você queira a opção verbose, insira a opção '-v' nessa posição
3.2		Técnica de classificação: 'NaiveBayes' ou 'RandomTree'
3.3		Folder com os arquivos de treino
3.4 	Folder com os arquivos de teste

Exemplo:

Verbose:
./classificador.sh -v NaiveBayes main/resources/data/train main/resources/data/teste

./classificador.sh -v RandomTree main/resources/data/train main/resources/data/teste

NaoVerbose:
./classificador NaiveBayes main/resources/data/train main/resources/data/teste

./classificador.sh RandomTree main/resources/data/train main/resources/data/teste


==> Os dados utilizados para treinamento e teste estão na pasta 'data'
==> O script fonte de execucao é 'classificador.sh'
==> O código fonte está em 'src'

