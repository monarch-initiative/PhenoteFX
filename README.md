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

## Note on hp.obo format
There is currently an error in the translation of hp-edit.owl to hp.obo.
When the hp.obo file is opened by PhenoteFX, the following error message will be seen
```$xslt
java.lang.IllegalStateException: Failed to parse at line 13956 due to extraneous input '"' expecting {BooleanValue, '}', '[', ']', '=', ',', ';', ' ', Word, QuotedString, Esc2}
```
This is because the comment of  HP:0001428 begins with a quotation mark. Open the
file, go to line 13956 and remove the quotation mark.
There is a second similar error at line 117054 (HP:0031248).
Remove the {EXACT=""} from the end of the line
```$xslt
synonym: "Itchy palm" EXACT layperson [] {EXACT=""}
```
Then we are good to go!