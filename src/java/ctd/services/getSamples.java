package ctd.services;

import com.skaringa.javaxml.DeserializerException;
import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.services.exceptions.Exception400BadRequest;
import ctd.services.exceptions.Exception401Unauthorized;
import ctd.services.exceptions.Exception403Forbidden;
import ctd.services.exceptions.Exception500InternalServerError;
import ctd.services.internal.GscfService;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class getSamples {
    private String strAssayToken;
    private String strSessionToken;
    private String strFilename;

    /***
     * This function gets all available Assaytokens from GSCF that can be linked
     * to a specific assay. It also gets a filename of a zip containing .cel files
     *
     * TODO: comment
     *
     * @return the table with filenames and sampletokens
     * @throws Exception400BadRequest
     * @throws Exception403Forbidden
     * @throws Exception500InternalServerError
     */

    public String getSamples() throws Exception400BadRequest, Exception403Forbidden, Exception500InternalServerError {
        String strReturn = "";

        // Check if the minimal parameters are set
        if(getSessionToken()==null){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strSessionToken==null");
            throw new Exception400BadRequest();
        }
        if(getAssayToken()==null){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strAssayToken==null");
            throw new Exception400BadRequest();
        }

        // Check if the provided sessionToken is valid
        GscfService objGSCFService = new GscfService();
        ResourceBundle res = ResourceBundle.getBundle("settings");
        HashMap<String, String> restParams = new HashMap<String, String>();
        restParams.put("assayToken", getAssayToken());

        String[] strGSCFRespons = objGSCFService.callGSCF(getSessionToken(),"isUser",restParams);
        if(!objGSCFService.isUser(strGSCFRespons[1])) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strSessionToken invalid: "+getSessionToken());
            throw new Exception403Forbidden();
        }

        strReturn = "";
        objGSCFService = new GscfService();
        res = ResourceBundle.getBundle("settings");
        strGSCFRespons = objGSCFService.callGSCF(getSessionToken(),"getSamples",restParams);

        LinkedList lstGSCFResponse = new LinkedList();

        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getSamples.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            lstGSCFResponse = (LinkedList) trans.deserializeFromJsonString(strGSCFRespons[1]);
        } catch (DeserializerException ex) {
            Logger.getLogger(getSamples.class.getName()).log(Level.SEVERE, null, ex);
        }

        strReturn += "<table>";
        strReturn += "<tr class='fs_th'><th class='mark'>Filenames</th><th class='mark'>Samplenames</th></tr>";

        LinkedList<String> lstFilenames = new LinkedList<String>();
        try {
            // Open the ZIP file
            ZipFile zf = new ZipFile(res.getString("ws.upload_folder")+strFilename);

            // Enumerate each entry
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
                // Get the entry name
                String zipEntryName = ((ZipEntry)entries.nextElement()).getName();
                if(!zipEntryName.equals(".") && !zipEntryName.equals("..")){
                    lstFilenames.add(zipEntryName);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        //From samples to filenames
        HashMap<String, String> results = new HashMap<String, String>();
        HashMap<String, Integer> sampletokens = new HashMap<String, Integer>();
        boolean[] used = new boolean[lstFilenames.size()];
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): lstGSCFResponsesize: "+lstGSCFResponse.size());
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): lstFilenamessize: "+lstFilenames.size());
        for(int i = 0; i < lstGSCFResponse.size() && i<lstFilenames.size(); i++){
            HashMap<String, String> map = (HashMap<String, String>) lstGSCFResponse.get(i);
            String name = map.get("name").replace(" ", "").toLowerCase();
            String event = map.get("event").replace(" ", "").toLowerCase();
            String subject = map.get("subject").replace(" ", "").toLowerCase();
            int highest_match = -1;
            int highest_match_score = -1;
            for(int j = 0; j < lstFilenames.size(); j++){
                if(!used[j]){
                    String fn = lstFilenames.get(j).replace(" ", "").toLowerCase();
                    int score = 0;
                    if(fn.contains(name)){
                        score+=3;
                    }
                    if(fn.contains(event)){
                        score+=1;
                    }
                    if(fn.contains(subject)){
                        score+=2;
                    }
                    if(score>highest_match_score){
                        highest_match = j;
                        highest_match_score = score;
                    }
                }
            }
            used[highest_match]=true;
            results.put(lstGSCFResponse.get(i).toString(),lstFilenames.get(highest_match));
            sampletokens.put(lstGSCFResponse.get(i).toString(), i);
        }

        Iterator it = results.entrySet().iterator();
        int i = 0;
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): resultssize: "+results.size());
        while(it.hasNext()){
            String strColor = "#DDEFFF";
            if(i%2==0) {
                strColor = "#FFFFFF";
            }
            i++;

            Map.Entry<String, String> kv = (Map.Entry<String, String>) it.next();
            //HashMap<String, String> map = (HashMap<String, String>) lstGSCFResponse.get(i);
            String fn = kv.getValue();
            HashMap<String, String> map = (HashMap<String, String>) lstGSCFResponse.get(sampletokens.get(kv.getKey()));
            strReturn += "<tr><td class='mark' style='width:50%; background-color:"+strColor+"; font-size:small;'>"+fn+"<input type='hidden' value='"+fn+"'/></td><td style='width:50%; background-color:"+strColor+"; font-size:small;'><div class='drag' style='padding: 3px'>"+map.get("name")+" - "+map.get("event")+" - "+map.get("Text on vial")+"<input type='hidden' value='"+map.get("sampleToken")+"'/></div></td></tr>";
        }

        //Remove used tokens from lstGSCFResponse
        Iterator it2 = sampletokens.entrySet().iterator();
        while(it2.hasNext()){
            Map.Entry<String, Integer> kv = (Map.Entry<String, Integer>) it2.next();
            for(int j = 0; j < lstGSCFResponse.size(); j++){
                if(kv.getKey().equals(lstGSCFResponse.get(j).toString())){
                    lstGSCFResponse.remove(j);
                }
            }
        }

        // Add remainder of samples
        boolean blnRemainingSamples = false;
        if(lstGSCFResponse.size()>0){
            strReturn += "<tr class='fs_th mark'><td class='mark' colspan='2'><br />The following sampletokens are not matched with a file.</td></tr>";
        }
        while(lstGSCFResponse.size()>0){
            HashMap<String, String> map = (HashMap<String, String>) lstGSCFResponse.pop();
            strReturn += "<tr><td colspan='2' style='font-size:small;'>"
                    + "<div class='drag' style='padding: 3px'>"+map.get("name")+" - "+map.get("event")+" - "+map.get("Text on vial")+"</div>"
                    + "</td></tr>";
        }
        strReturn += "</table>";

        return strReturn;
    }

    /**
     * @return the strSessionToken
     */
    public String getSessionToken() {
        return strSessionToken;
    }

    /**
     * @param strSessionToken the strSessionToken to set
     */
    public void setSessionToken(String strSessionToken) {
        this.strSessionToken = strSessionToken;
    }

    /**
     * @return the strAssayToken
     */
    public String getAssayToken() {
        return strAssayToken;
    }

    /**
     * @param strAssayToken the strAssayToken to set
     */
    public void setAssayToken(String assayToken) {
        this.strAssayToken = assayToken;
    }

    /**
     * @return the strFilename
     */
    public String getFilename() {
        return strFilename;
    }

    /**
     * @param strFilename the strFilename to set
     */
    public void setFilename(String strFilename) {
        this.strFilename = strFilename;
    }

}
