#!/bin/bash

#       Script para execucao de um classificador de emails
# como spam ou não, utilizando um classificador NaiveBayes
# e a linguagem java.

# uso: ./classificador [-v] TecnicaX folder_treino folder_teste > out.txt
#    saida: 
#               precision: xx
#               recall:    xx
#               f-measure: xx
#

function show_help (){
    echo -e "Classificador\n"
    echo "usage: ./classificador.sh [-v verbose] "
    echo "                                      ClassificationTecnique: (NaiveBayes, RandomTree)"
    echo "                                      TrainFolder"
    echo "                                      TestFolder"
}

# 1) ler folder_treino
# 2) preprocessar todos os arquivos de treino
# 3) criar um arquivo intermediario dataset_treino.arff 
# 4) treinar um classificador com o dataset_treino.arff
# 5) salvar o modelo criado a partir do treinamento em um arquivo modelo.dat
#
# 6) ler folder_teste
# 7) preprocessar todos os arquivos de teste
# 8) criar um arquivo intermediario dataset_test.arff
# 9) carregar o modelo contido no arquivo modelo.dat
# 10) tentar classificar os emails no dataset_test.arff usando este modelo


# USO DO SCRIPT:
# prompt$ ./classificador [-v] ClassificationAlgo main/resources/data/train main/resources/data/teste

HOME_FOLDER="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

MAIN_CLASS=metci.weka.classifier.Main
JAR_FILE=EmailClassifier-0.0.1-SNAPSHOT-jar-with-dependencies.jar
LOCAL_JAR=${HOME_FOLDER}/${JAR_FILE}

#unzip EmailClassifier-0.0.1-SNAPSHOT-dist.zip

#cd metci

# load options from command line

OPTIND=1         # Reset in case getopts has been used previously in the shell.

#while getopts "c:h?e:j:m:w:" opt; do
#    case "$opt" in
#    h|\?)
#        show_help
#        exit 0
#        ;;
#    esac
#done

shift $((OPTIND-1))

[ "$1" = "--" ] && shift

# load options from command line - end

if [ "$#" -eq 4 ]; then
    # VERBOSE OPTION
        #java -cp "${HOME_FOLDER}/lib/*" ${MAIN_CLASS} -v NaiveBayes main/resources/data/train main/resources/data/teste
        java -cp "${HOME_FOLDER}/lib/*" ${MAIN_CLASS} $1 $2 $3 $4
fi

if [ "$#" -eq 3 ]; then
        # NOT VERBOSE OPTION
        #java -cp "${HOME_FOLDER}/lib/*" ${MAIN_CLASS} NaiveBayes main/resources/data/train main/resources/data/teste
        java -cp "${HOME_FOLDER}/lib/*" ${MAIN_CLASS} $1 $2 $3
fi














 
