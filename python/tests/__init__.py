import os
import pathlib
import re
import subprocess

dir_path = os.path.dirname(os.path.realpath(__file__))
os.chdir(dir_path)

path = "../../computing/build/libs/"

if not os.path.exists(path):
    subprocess.call(["../.././gradlew", "build", "-p", "../../computing/"])

pattern = re.compile("computing-\d+(\.\d+)+.jar")
files = [file for file in os.listdir(path) if pattern.match(file)]
src = str(pathlib.Path(path, files[0]))

__all__ = ["src"]
