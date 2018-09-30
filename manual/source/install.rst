.. _installation:

============
Installation
============

-------------------------
Use the prebuilt Jar file
-------------------------

.. note::

    This is the recommended way of installing for normal users.

Simply download the latest release of PhenoteFX from the GitHub releases page at
https://github.com/monarch-initiative/PhenoteFX/releases.


.. _install_from_source:

-------------------
Install from Source
-------------------

.. note::

    You only need to install from source if you want to develop PhenoteFX in Java yourself.

Prerequisites
=============

For building PhenoteFX, you will need

#. `Java JDK 8 or higher <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ for compiling OntoLib,
#. `Maven 3 <http://maven.apache.org/>`_ for building phenol, and
#. `Git <http://git-scm.com/>`_ for getting the sources.


Phenol
======
Phenol is a Java library that we are developing to work with HPO and several other ontologies. Currently, phenol is
not in maven central and so you will need to build the latest version of phenol and use ``mvn install`` to install
phenol to your local .m2 directory before building PhenoteFX. Phenol can be obtained from the
`Phenol GitHub page <https://github.com/monarch-initiative/phenol>`_.

Git Checkout
============

In this tutorial, we will download the PhenoteFX sources and build them in ``~/target``.

.. code-block:: console

   # git clone https://github.com/monarch-initiative/PhenoteFX.git
   # cd PhenoteFX

Maven Proxy Settings
====================

If you are behind a proxy, you will get problems with Maven downloading dependencies.
If you run into problems, make sure to also delete ``~/.m2/repository``.
Then, execute the following commands to fill ``~/.m2/settings.xml``.

.. code-block:: console

    ontolib # mkdir -p ~/.m2
    ontolib # test -f ~/.m2/settings.xml || cat >~/.m2/settings.xml <<END
    <settings>
      <proxies>
       <proxy>
          <active>true</active>
          <protocol>http</protocol>
          <host>proxy.example.com</host>
          <port>8080</port>
          <nonProxyHosts>*.example.com</nonProxyHosts>
        </proxy>
      </proxies>
    </settings>
    END

Building
========

You can build PhenoteFX using ``mvn package``.

.. code-block:: console

    $ mvn package

