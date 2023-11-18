# For developers

PhenoteFX is a JavaFX application.  See the [installation](installation.md) instructions for information on how to run
PhenoteFX as a JAR file. The following text describes how to create native images with GraalVM


### GraalVM







### mkdocs

To generate the documentaiton locally, create a Python virtural environment and install mkdoc and packages as follows, and
then start the local mkdocs server.


```bash
python3 -m venv venv
source venv/bin/activate
pip install --upgrade pip
pip install mkdocs-material
pip install mkdocs-material[imaging]
pip install pillow cairosvg
pip install mkdocs-material-extensions
pip install mkdocstrings[python]
mkdocs serve
```