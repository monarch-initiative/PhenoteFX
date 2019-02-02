.. _installation:

============
Installation
============

-------------------------
Use the prebuilt Jar file
-------------------------

Simply download the latest release of PhenoteFX from the GitHub releases page at
https://github.com/monarch-initiative/PhenoteFX/releases.

.. note::

    This is the recommended way of installing for normal users.




.. _install_from_source:

-------------------
Install from Source
-------------------

You only need to install from source if you want to contribute to the development of
PhenoteFX yourself. To use PhenoteFX for annotation, it is recommended to download
the prebuilt version.

Prerequisites
=============

For building PhenoteFX, you will need

#. `Java JDK 8 or higher <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ for compiling OntoLib,
#. `Maven 3 <http://maven.apache.org/>`_ for building PhenoteFX, and
#. `Git <http://git-scm.com/>`_ for getting the sources.


Git Checkout
============

The following command downloads the sources of PhenoteFX.

.. code-block:: console

   # git clone https://github.com/monarch-initiative/PhenoteFX.git
   # cd PhenoteFX

Building
========

You can build PhenoteFX using ``mvn package``.

.. code-block:: console

    $ mvn package

You shuld now be able to start the app with the following command.

.. code-block:: console

    $ java -jar target/PhenoteFX.jar




Maven Proxy Settings
====================

If you are behind a proxy, you may encounter problems with Maven downloading dependencies.
Edit the ``settings.xml`` file in your ``.m2`` maven directory to adjust the settings for your proxy server.

.. code-block:: console

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

