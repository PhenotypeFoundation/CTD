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
import ctd.services.internal.responseComparator;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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
    private boolean blnError = false;

    /***
     * This function gets all available Assaytokens from GSCF that can be linked
     * to a specific assay. It also gets a filename of a zip containing .cel files
     *
     * @return the table with filenames and sampletokens
     * @throws Exception400BadRequest this exception is thrown if no sessionToken or assayToken is set
     * @throws Exception403Forbidden this exception is thrown if the sessionToken is invalide
     * @throws Exception500InternalServerError this exception is thrown if there is some kind of unknown error
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

        LinkedList lstGetSamples = objGSCFService.callGSCF2(getSessionToken(),"getSamples",restParams);
        Collections.sort(lstGetSamples, new responseComparator("name"));

        strReturn = "";
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
            Logger.getLogger(getSamples.class.getName()).log(Level.SEVERE, "ERROR getSamples: "+e.getMessage());
        }

        if(lstGetSamples.size()<lstFilenames.size()) {
            blnError = true;
            return "<b>There are more files in the submitted .zip than there are available samples.</b><br />Go to the study in <a href='"+res.getString("gscf.baseURL")+"/assay/showByToken/"+getAssayToken()+"'>GSCF</a> and add more samples.<br />";
        }
        if(lstFilenames.size()==0) {
            blnError = true;
            return "<b>There are either no files in the submitted .zip, or the .zip is corrupted.</b><br/>No data has been processed!</br>Please make sure your .zip contains cel-files and is readable before you upload it.<br />";
        }

        for(int i=0; i<lstFilenames.size(); i++) {
            String strColor = "#DDEFFF";
            if(i%2==0) {
                strColor = "#FFFFFF";
            }
            HashMap<String, String> mapSamples = (HashMap<String, String>) lstGetSamples.removeFirst();

            String fn = lstFilenames.get(i);

            strReturn += "<tr><td class='mark fs_fontsize' style='width:50%; background-color:"+strColor+";'>"+fn+"<input type='hidden' class='matching_file' value='"+fn+"'/></td><td class='fs_fontsize' style='width:50%; background-color:"+strColor+";'><div class='drag' style='padding: 3px'>"+mapSamples.get("name")+" - "+mapSamples.get("event")+" - "+mapSamples.get("Text on vial")+"<input type='hidden' class='matching_sample' value='"+mapSamples.get("sampleToken")+"'/></div></td></tr>";
        }

        if(lstGetSamples.size()>0){
            strReturn += "<tr class='fs_th'><td class='mark' colspan='2'><br />The following samples are not matched with a file.</td></tr>";
        }

        while(lstGetSamples.size()>0){
            HashMap<String, String> map = (HashMap<String, String>) lstGetSamples.removeFirst();
            strReturn += "<tr><td colspan='2' class='fs_fontsize'>"
                    + "<div class='drag' style='padding: 3px'>"+map.get("name")+" - "+map.get("event")+" - "+map.get("Text on vial")
                    + "<input type='hidden' class='matching_sample' value='"+map.get("sampleToken")+"'/></div>"
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

    /**
     * @return the blnError
     */
    public boolean getError() {
        return blnError;
    }

}
