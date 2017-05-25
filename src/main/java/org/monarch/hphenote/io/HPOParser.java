package org.monarch.hphenote.io;


import org.monarch.hphenote.model.HPO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a very simple OBO parser that gets a list of HPO terms and IDs. It is intended
 * to enable autocompletion and is not a complete OBO Parser.
 * TODO Replace this with new ontologizer-lib parser when finished
 * @author  Peter Robinson
 * @version 0.0.1 (May 24, 2017)
 */
public class HPOParser extends Parser {

    private Map<String,HPO> hpoMap=null;

    private Map<String,String> hpoName2IDmap=null;

    /** The constructor sets {@link #absolutepath} to
     * the absolute path of the downloaded HPO file (whether or not the file has
     * been downloaded yet; The parser will simply return an empty Map if the file
     * is not available. Note that there is no option as to where the file gets stored
     * (in the .hphenote directory in the home directory of the user).
     */
    public HPOParser() {
        File dir = org.monarch.hphenote.gui.Platform.getHPhenoteDir();
        String basename="hp.obo";
        this.absolutepath = new File(dir + File.separator + basename);
        this.hpoMap=new HashMap<String,HPO>();
        hpoName2IDmap=new HashMap<>();
        inputFile();
    }

    /** @return a Map of HPO terms. THe Map will be initialized but empty if the hp.obo
     * file cannot be parsed.
     */
    public Map<String,HPO> getTerms() {
        return this.hpoMap;
    }

    public Map<String,String> getHpoName2IDmap() { return this.hpoName2IDmap; }

    /**
     * Inputs the hp.obo file and fills {@link #hpoMap} with the contents.
     */
    private void inputFile() {
        if (! inputFileExists())
            return;
        try {
            BufferedReader input = new BufferedReader(new FileReader(absolutepath));
            String line = null;
            boolean interm=false;
            String id=null;
            String name=null;
            while ((line=input.readLine())!=null) {
                if (line.startsWith("[Term]")) {
                    interm=true;
                    continue;
                } else if (interm && line.startsWith("id:")) {
                    id = line.substring(4).trim();
                } else if (interm && line.startsWith("name:")) {
                    name=line.substring(6).trim();
                } else if (interm && line.isEmpty()) {
                    HPO hpo = new HPO();
                    hpo.setHpoId(id);
                    hpo.setHpoName(name);
                    hpoName2IDmap.put(name,id);
                    this.hpoMap.put(id,hpo);
                    id=null;
                    name=null;
                    interm=false;
                }
                //System.out.println(line);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
/*eof*/