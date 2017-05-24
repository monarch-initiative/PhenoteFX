package org.monarch.hphenote.io;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by peter on 23.05.17.
 * A class to parse the MedGen file.
 * Note that I checked that the encoding of this file
 * <pre>
 * file -i MedGen_HPO_OMIM_Mapping.txt
 * MedGen_HPO_OMIM_Mapping.txt: text/plain; charset=us-ascii
 * </pre>
 * @author Peter Robinson
 */
public class MedGenParser {
    /** Key: e.g., 613962, Value e.g., ACTIVATED PI3K-DELTA SYNDROME */
    private Map<Integer,String> omimId2NameMap;

    public MedGenParser(){
        this.omimId2NameMap = new HashMap<>();
        File dir = org.monarch.hphenote.gui.Platform.getHPhenoteDir();
        String basename="MedGen_HPO_OMIM_Mapping.txt.gz";
        File medgenpath = new File(dir + File.separator + basename);
        parseFile(medgenpath);
    }

    public Map<Integer,String> getOmimId2NameMap() { return omimId2NameMap; }



    private void parseFile(File path) {
        try {
            InputStream fileStream = new FileInputStream(path);
            InputStream gzipStream = new GZIPInputStream(fileStream);
            Reader decoder = new InputStreamReader(gzipStream, java.nio.charset.StandardCharsets.US_ASCII);
            BufferedReader br = new BufferedReader(decoder);
            String line=null;
            while ((line=br.readLine())!=null){
                if (line.startsWith("#"))
                    continue; // skip header
                String F[] = line.split("\\|");
                if (F.length < 2)
                    continue;
                try {
                    Integer id = Integer.parseInt(F[1]);
                    String name = F[2];
                    omimId2NameMap.put(id, name);
                } catch (NumberFormatException n) {
                    n.printStackTrace();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
