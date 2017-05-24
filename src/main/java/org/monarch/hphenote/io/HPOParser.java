package org.monarch.hphenote.io;


import org.monarch.hphenote.model.HPO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HPOParser {

    /** Path to the hp.obo file */

    private Map<String,HPO> hpoMap=null;

    public HPOParser() {

        File dir = org.monarch.hphenote.gui.Platform.getHPhenoteDir();
        String basename="hp.obo";
        File path = new File(dir + File.separator + basename);
        this.hpoMap=new HashMap<String,HPO>();
        inputFile(path);
    }

    public Map<String,HPO> getTerms() {
        return this.hpoMap;
    }


    private void inputFile(File path) {
        try {
            BufferedReader input = new BufferedReader(new FileReader(path));
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