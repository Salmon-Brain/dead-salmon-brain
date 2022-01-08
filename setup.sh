#!/bin/bash

spark=${spark:-3.1.2}
scala=${scala:-2.12.11}
scala_short=$(echo "$scala" | cut -f1,2 -d'.')
hadoop=${hadoop:-3.2}
version=${version:-0.0.3}
is_install_python_lib=${is_install_python_lib:-false}
is_build=${is_build:-false}


set -x
# shellcheck disable=SC2034
current_dir=$(pwd)

for ARGUMENT in "$@"; do
  KEY=$(echo $ARGUMENT | cut -f1 -d=)
  VALUE=$(echo $ARGUMENT | cut -f2 -d=)

  case "$KEY" in
  spark) spark=${VALUE} ;;
  scala) scala=${VALUE} ;;
  hadoop) hadoop=${VALUE} ;;
  is_install_python_lib) is_install_python_lib=${VALUE} ;;
  is_build) is_build=${VALUE} ;;

  *) ;;
  esac
done

if [[ -z "${JAVA_HOME}" ]]; then
  apt update
  apt-get install openjdk-8-jdk-headless -qq >/dev/null
  echo 'export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64' >>~/.bashrc
  echo JAVA_HOME="${JAVA_HOME}"
else
  echo JAVA_HOME="${JAVA_HOME}"
fi

# shellcheck disable=SC1068
# shellcheck disable=SC2034
spark_path=${SPARK_HOME}

if [[ -z "${SPARK_HOME}" ]]; then
  # shellcheck disable=SC2164
  cd /opt
  apt-get install wget
  wget https://dlcdn.apache.org/spark/spark-"$spark"/spark-"$spark"-bin-hadoop"$hadoop".tgz
  tar zvxf spark-"$spark"-bin-hadoop"$hadoop".tgz
  rm -f spark-"$spark"-bin-hadoop"$hadoop".tgz
  spark_path="/opt/spark-$spark-bin-hadoop$hadoop"
  # shellcheck disable=SC2016
  echo "export SPARK_HOME=$spark_path" >>~/.bashrc
  echo SPARK_HOME="${SPARK_HOME}"
else
  echo SPARK_HOME="${SPARK_HOME}"
fi

# shellcheck disable=SC1090
. ~/.bashrc

jars="$spark_path"/jars
rm -f "$jars/ruleofthumb-*.jar"

if [ "$is_build" = true ]; then
  # shellcheck disable=SC2164
  cd "$current_dir"
  "${current_dir}"/gradlew clean build -PscalaVersion="$scala" -PsparkVersion="$spark" -x test
  # shellcheck disable=SC2034
  path='ruleofthumb/build/libs/'
  # shellcheck disable=SC2012
  # shellcheck disable=SC2034
  # shellcheck disable=SC2196
  # shellcheck disable=SC2154
  file=$(ls path | egrep -v "ruleofthumb_$spark_$scala_short-([0-9]{1,}\.)+[0-9]{1,}.jar")
  cp "$path"/"$file" "$jars"
  else
    # shellcheck disable=SC2164
    cd $jars
    wget https://repo1.maven.org/maven2/ai/salmonbrain/ruleofthumb_"$spark"_"$scala_short"/"$version"/ruleofthumb_"$spark"_"$scala_short"-"$version".jar
fi


# shellcheck disable=SC1072
# shellcheck disable=SC1073
if [ "$is_install_python_lib" = true ]; then
  apt install python3-pip
  pip install pyspark=="$spark" findspark
  if [ "$is_build" = true ]; then
    pip install python/
  else
    pip install dead-salmon-brain=="$version"
  fi
fi