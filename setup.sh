#!/bin/bash

spark=${spark:-3.1.2}
scala=${scala:-2.12.15}
hadoop=${hadoop:-3.2}
is_install_python=${is_install_python:-false}

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
  apt-get install openjdk-8-jdk-headless -qq >/dev/null
  echo 'export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64' >>~/.bashrc
  touch ~/.bashrc
  echo JAVA_HOME="${JAVA_HOME}"
else
  echo JAVA_HOME="${JAVA_HOME}"
fi

if [[ -z "${SPARK_HOME}" ]]; then
  # shellcheck disable=SC2164
  cd /opt
  wget https://dlcdn.apache.org/spark/spark-"$spark"/spark-"$spark"-bin-hadoop"$hadoop".tgz
  tar zvxf spark-"$spark"-bin-hadoop"$hadoop".tgz
  rm -f spark-"$spark"-bin-hadoop"$hadoop".tgz
  # shellcheck disable=SC2016
  echo 'export SPARK_HOME=/opt/spark-"$spark"-bin-hadoop"$hadoop' >>~/.bashrc
  touch ~/.bashrc
  echo SPARK_HOME="${SPARK_HOME}"
else
  echo SPARK_HOME="${SPARK_HOME}"
fi


./gradlew clean build -PscalaVersion="$scala" -PsparkVersion="$spark" -x test


# shellcheck disable=SC1072
# shellcheck disable=SC1073
if [ "$is_install_python" = true ]; then
  pip install python/
fi

# shellcheck disable=SC2034
path='computing/build/libs/'
# shellcheck disable=SC2012
# shellcheck disable=SC2034
# shellcheck disable=SC2196
file=$(ls computing/build/libs/ | egrep -v '*-all.jar')
jars="${SPARK_HOME}"/jars/
rm "$jars"/computing-*.jar
cp "$path"/"$file" "$jars"
