import pathlib

from setuptools import setup, find_packages

with open("README.md", "r") as fh:
    long_description = fh.read()

HERE = pathlib.Path(__file__).parent

setup(
    name="dead-salmon-brain",
    version="0.0.2",
    description="Dead Salmon Brain is a cluster computing system for analysing A/B experiments",
    license="Apache License v2.0",
    author="Dead Salmon Brain",
    author_email="deadsalmonbrain@gmail.com",
    packages=find_packages(exclude=["*.tests", "*.tests.*", "tests.*", "tests"]),
    long_description=long_description,
    long_description_content_type="text/markdown",
    python_requires=">=3.6",
    install_requires=["pyspark>=2.3.0", "numpy"],
    tests_require=["pytest"],
    project_urls={
        "Source code": "https://github.com/Salmon-Brain/dead-salmon-brain/tree/main/python",
    },
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: Apache Software License",
    ],
)
