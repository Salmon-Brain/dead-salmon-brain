import os
import pathlib
import re


def get_default(path: str):
    pattern = re.compile("computing-\d+(\.\d+)+.jar")
    files = [file for file in os.listdir(path) if pattern.match(file)]
    return str(pathlib.Path(path, files[0]))


def pytest_addoption(parser):
    parser.addoption("--path", action="store", default=get_default("../../computing/build/libs/"))
