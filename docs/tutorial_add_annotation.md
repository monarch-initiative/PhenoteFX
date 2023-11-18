# Adding annotations


The PhenoteFX app requires each annotation to have a citation from PubMed. Therefore, the first thing to do is to 
grab the pubmed id from the article you are curating.

For this example, we will use data from 
[Klopocki E, et al. (2008_) A microduplication of the long range SHH limb regulator (ZRS) 
is associated with triphalangeal thumb-polysyndactyly syndrome. J Med Genet. 45:370-5](https://pubmed.ncbi.nlm.nih.gov/18178630/){:target="_blank"}.

The corresponding ID that PhenoteFX expects is 
[PMID:18178630](https://pubmed.ncbi.nlm.nih.gov/18178630/){:target="_blank"}
(no space in between PMID: and the number, but PhenoteFX will 
automatically remove spaces when you paste the id into the dialog).

<figure markdown>
![PhenoteFX PMID](img/PhenotePMID.png){ width="400" }
<figcaption>PhenoteFX - specifying the PubMed id </figcaption>
</figure>



Paste the PMID into the ``source `` field, as shown above.


Now we can add the first annotation. In this case, we will add the mode of inheritance as 
[Autosomal dominant inheritance HP:0000006](https://hpo.jax.org/app/browse/term/HP:0000006){:target="_blank"}. Note that if
we type some letters into the ``HPO Term`` field, PhenoteFX will autocomplete the term if it can. 

<figure markdown>
![PhenoteFX PMID](img/PhenoteAutocomplete.png){ width="600" }
<figcaption>PhenoteFX - Autocomplete</figcaption>
</figure>


If you hit the enter button, the term will be chosen. Then click on the ``Add annotation`` button. 


<figure markdown>
![PhenoteFX PMID](img/PhenoteAnnot1.png){ width="800" }
<figcaption>PhenoteFX - Autosomal dominant</figcaption>
</figure>

This adds the annotation to the curation file. The PMID is now stored as the default value and does not need to be changed unless
you want to switch to a new citation.


<figure markdown>
![PhenoteFX PMID](img/triphalangeal.png){ width="400" }
<figcaption>PhenoteFX - Features</figcaption>
</figure>



We can see the clinical features to annotate in Table 1 of 
[PMID:18178630](https://pubmed.ncbi.nlm.nih.gov/18178630/){:target="_blank"}. For instance, 11 of 12 affected 
individuals had 
[Triphalangeal thumb HP:0001199](https://hpo.jax.org/app/browse/term/HP:0001199){:target="_blank"}.
We can enter the frequency data as follows (and click the ``Add annotation`` button to enter the annotation).
Note that in this case, the context of the article implies that if a ``+`` is not shown inthe table, the individual 
did not have the feature in question. This may not always be the case and the curator should read the article to be 
able to accurately curate the data.

<figure markdown>
![PhenoteFX PMID](img/triphalangeal-annot.png){ width="400" }
<figcaption>PhenoteFX - Annotating triphalangeal thumb</figcaption>
</figure>


We continue entering features in this way. The table states that five individuals had ``syndactly``. However, the text 
states that individuals in the family had ``cutaneous/osseous syndactyly of fingers III–V or IV/V``. We do not have further
details, and we typically curate with as much detail as possible given the data in the original publication. To search
for a more detailed syndactyly term, we can use the HPO ontology browser on the top right of PhenoteFX.


<figure markdown>
![PhenoteFX PMID](img/PhenoteTree.png){ width="400" }
<figcaption>PhenoteFX - Ontology Browser</figcaption>
</figure>


From this, we choose the term 
[Finger syndactyly HP:0006101](https://hpo.jax.org/app/browse/term/HP:0006101){:target="_blank"}.

<figure markdown>
![PhenoteFX PMID](img/PhenoteAnnot2.png){ width="400" }
<figcaption>PhenoteFX - annotations</figcaption>
</figure>



The above figure shows the appearance of PhenoteFX after we have entered several annotations. When we are finished,
we can use the Save or Save and Close items in the File menu to save our work - see [filemenu](tutorial_file.md).