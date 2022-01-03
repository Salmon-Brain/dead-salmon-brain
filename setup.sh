#!/bin/bash

spark=${spark:-3.1.2}
scala=${scala:-2.12.15}
hadoop=${hadoop:-3.2}
is_install_python=${is_install_python:-false}

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
  is_install_python) is_install_python=${VALUE} ;;

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

# shellcheck disable=SC2164
cd "$current_dir"

"${current_dir}"/gradlew clean build -PscalaVersion="$scala" -PsparkVersion="$spark" -x test


# shellcheck disable=SC1072
# shellcheck disable=SC1073
if [ "$is_install_python" = true ]; then
  apt install python3-pip
  pip install python/
  pip install findspark
fi

# shellcheck disable=SC2034
path='computing/build/libs/'
# shellcheck disable=SC2012
# shellcheck disable=SC2034
# shellcheck disable=SC2196
file=$(ls computing/build/libs/ | egrep -v '*-all.jar')
jars="$spark_path"/jars/
rm -f "$jars"/computing-*.jar
cp "$path"/"$file" "$jars"
