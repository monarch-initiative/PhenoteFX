# PhenoteFX
PhenoteFX is a Java app that is designed to help create and maintain Human Phenotype Ontology annotation files.

## Requirements
HPO Phenote requires Java 1.8 version 60 or better.

## Building and Running HPO Phenote
HPO Phenote is provided as a maven project.
```
$ mvn clean package
$ java -jar target/HPhenote.jar
```

## Important notes
PhenoteFX is in the alpha testing stage. Please report any malfunctions to Peter. 
Note that the current version of hp.obo (Nov 11,2017) contains two format errors that need to be repaired manually before HPhenote can start. This is being fixed in the next release.


## Roadmap
* Version of PhenoteFX for other phenotype ontology annotations (this will need decision on annotation file formats)
* Extend HPOA format and revise interface of PhenoteFX
