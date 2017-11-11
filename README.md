# hphenote
HPO Phenote is a Java FX designed to help create and maintain Human Phenotype Ontology annotation files.

## Requirements
HPO Phenote requires Java 1.8 version 60 or better.

## Building and Running HPO Phenote
HPO Phenote is provided as a maven project.
```
$ mvn clean package
$ java -jar target/HPhenote.jar
```

Note that the current version of hp.obo (Nov 11,2017) contains two format errors that need to be repaired manually before HPhenote can start. This is being fixed in the next release.
