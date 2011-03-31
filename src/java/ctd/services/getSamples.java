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
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Taco
 */
public class getSamples {
    private String strAssayToken;
    private String strSessionToken;
    private String strFilename;

    public String getSamples(String t, String assayToken, String filename) throws Exception400BadRequest, Exception403Forbidden, Exception500InternalServerError {
        strSessionToken = t;
        strAssayToken = assayToken;
        strFilename = filename;
        String strReturn = "";
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Arrived in getSamples()... filename="+strFilename);

        // Check if the minimal parameters are set
        if(strSessionToken==null){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strSessionToken==null");
            throw new Exception400BadRequest();
        }
        if(strAssayToken==null){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strAssayToken==null");
            throw new Exception400BadRequest();
        }

        //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getStudies(): strSessionToken=="+strSessionToken);

        // Check if the provided sessionToken is valid
         GscfService objGSCFService = new GscfService();
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String strConsumerVal = res.getString("ctd.consumerID");
        String strModuleVal = res.getString("ctd.moduleURL");
        HashMap<String, String> restParams = new HashMap<String, String>();
        restParams.put("assayToken", strAssayToken);
        restParams.put("consumer", strConsumerVal);
        restParams.put("moduleURL", strModuleVal);
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): about to call isUser()");
        String[] strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"isUser",restParams);
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): just called isUser()");
        if(!objGSCFService.isUser(strGSCFRespons[1])) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strSessionToken invalid: "+strSessionToken);
            throw new Exception403Forbidden();
        }
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): strSessionToken is valid: "+strSessionToken);

        strReturn = "";
        objGSCFService = new GscfService();
        res = ResourceBundle.getBundle("settings");
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): about to call getSamples()");
        strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"getSamples",restParams);
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): just called getSamples(): "+strGSCFRespons[1]);

        LinkedList lstGSCFResponse = new LinkedList();


        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            lstGSCFResponse = (LinkedList) trans.deserializeFromJsonString(strGSCFRespons[1]);
        } catch (DeserializerException ex) {
            Logger.getLogger(getStudies.class.getName()).log(Level.SEVERE, null, ex);
        }

        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): list length: "+lstGSCFResponse.size());

        strReturn += "<table border='1'>";

        try {
            // Open the ZIP file
            ZipFile zf = new ZipFile("C:/temp/uploads/"+strFilename);

            // Enumerate each entry
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
                // Get the entry name
                String zipEntryName = ((ZipEntry)entries.nextElement()).getName();
                System.out.println(zipEntryName);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        for(int i = 0; i < lstGSCFResponse.size(); i++){
            HashMap<String, String> map = (HashMap<String, String>) lstGSCFResponse.get(i);
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): map contains "+map.toString());
            strReturn += "<tr><td class='forbid' style='width:200px'>dunno lollol</td><td style='width:200px'><div class='drag'>"+map.get("name")+" - "+map.get("event")+" - "+map.get("Text on vial")+"</div></td></tr>";
        }
        strReturn += "<tr><td colspan='2' style='height:100px'><div class='drag'>sample4</div><div class='drag'>sample5</div></td></tr>";
        strReturn += "</table>";

        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getSamples(): result: "+strReturn);
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

}
