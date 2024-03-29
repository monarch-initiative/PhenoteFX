# Installation



### Use the prebuilt Jar file


Simply download the latest release of PhenoteFX from the GitHub releases page at
https://github.com/monarch-initiative/PhenoteFX/releases.

!!! 

    This is the recommended way of installing for normal users.




## Install from Source


You only need to install from source if you want to contribute to the development of
PhenoteFX yourself. To use PhenoteFX for annotation, it is recommended to download
the prebuilt version.

### Prerequisites


For building PhenoteFX, you will need

- `Java JDK 21 or higher <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ for compiling PhenoteFX,
- `Maven <http://maven.apache.org/>`_ for building PhenoteFX, and
- `Git <http://git-scm.com/>`_ for getting the sources.


### Git Checkout

The following command downloads the sources of PhenoteFX.

```
git clone https://github.com/monarch-initiative/PhenoteFX.git
```

### Building


You can build PhenoteFX using ``mvn package``.

```bash
cd PhenoteFX
mvn package
```

    

You shuld now be able to start the app with the following command.

```bash
java -jar target/PhenoteFX.jar
```




### Maven Proxy Settings

If you are behind a proxy, you may encounter problems with Maven downloading dependencies.
Edit the ``settings.xml`` file in your ``.m2`` maven directory to adjust the settings for your proxy server.

```agsl
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
```
    
    

