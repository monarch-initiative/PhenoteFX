# Using a local copy of the HPO for annotations


This is an option for power users that most users should not require.

In many cases, new term requests on the HPO GitHub issue tracker contain both a new term request
as well as a list of diseases that should be annotated with the new term. It would be nice to
annotate the diseases at the same time as we make new terms. However, PhenoteFX uses the release
version of the ``hp.json`` file by default; since this file is updated only about once a month,
we would have to wait to annotate the diseases.

Instead, PhenoteFX is able to use a local copy of the ``hp.json`` file that has been created from the
most up-to-date edit file of the HPO (``hp-edit.owl``).

### How to create a local hp.obo version


Please see the tutorial at http://hpo-workbench.readthedocs.io/en/latest/latest.html
We are working on making this slightly easier.


