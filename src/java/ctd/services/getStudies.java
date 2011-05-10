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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class getStudies {
    private String strAssayToken;
    private String strSessionToken;

    /***
     * TODO: COMMENT
     * 
     * @return
     * @throws Exception400BadRequest
     * @throws Exception403Forbidden
     * @throws Exception500InternalServerError
     */

    public String getStudies() throws Exception400BadRequest, Exception403Forbidden, Exception500InternalServerError {
        String strReturn = "";

        // Check if the minimal parameters are set
        if(getSessionToken()==null){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getStudies(): strSessionToken==null");
            throw new Exception400BadRequest();
        }

        // Check if the provided sessionToken is valid
        GscfService objGSCFService = new GscfService();
        String[] strGSCFRespons = objGSCFService.callGSCF(getSessionToken(),"isUser",null);
        if(!objGSCFService.isUser(strGSCFRespons[1])) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getStudies(): strSessionToken invalid: "+strSessionToken);
            throw new Exception403Forbidden();
        }
        //Logger.getLogger(getTicket.class.getName()).log(Level.INFO, "getStudies(): strSessionToken is valid: "+strSessionToken);

        strReturn = "<option value='none'>Select a study...</option>";
        objGSCFService = new GscfService();
        strGSCFRespons = objGSCFService.callGSCF(getSessionToken(),"getStudies",null);
        String[] strGSCFRespons2 = objGSCFService.callGSCF(getSessionToken(),"getAssays",null);

        //Logger.getLogger(getStudies.class.getName()).log(Level.SEVERE, "getSt:\nCODE: "+strGSCFRespons[0]+"\n"+strGSCFRespons[1]+"\n-----\nCODE: "+strGSCFRespons2[0]+"\n"+strGSCFRespons2[1]);
        //LinkedList lstStudies = new LinkedList();
        LinkedList lstGSCFResponse = new LinkedList();
        LinkedList lstGSCFResponse2 = new LinkedList();

        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            lstGSCFResponse = (LinkedList) trans.deserializeFromJsonString(strGSCFRespons[1]);
            lstGSCFResponse2 = (LinkedList) trans.deserializeFromJsonString(strGSCFRespons2[1]);
        } catch (DeserializerException ex) {
            Logger.getLogger(getStudies.class.getName()).log(Level.SEVERE, null, ex);
        }

        LinkedList<String> lstParentTokens = new LinkedList();
        for(int i = 0; i < lstGSCFResponse2.size(); i++){
            HashMap<String, String> mapTokens = (HashMap<String, String>) lstGSCFResponse2.get(i);
            lstParentTokens.add(mapTokens.get("parentStudyToken"));
            //Logger.getLogger(getStudies.class.getName()).log(Level.SEVERE, "parSt: "+mapTokens.get("parentStudyToken"));
        }

        String[] arrOptions = new String[lstGSCFResponse.size()];
        for(int i = 0; i < lstGSCFResponse.size(); i++){
            HashMap<String, String> map = (HashMap<String, String>) lstGSCFResponse.get(i);
            if(lstParentTokens.contains(map.get("studyToken"))) {
                String strCode = " ("+map.get("code")+")";
                if(map.get("code")==null) {
                    strCode = "";
                }
                arrOptions[i] = map.get("title").toLowerCase()+"!!SEP!!<option value="+map.get("studyToken")+">"+map.get("title")+strCode+"</option>";
            } else {
                arrOptions[i] = "";
                //Logger.getLogger(getStudies.class.getName()).log(Level.SEVERE, "some are more equal: "+map.get("studyToken"));
            }
        }
        Arrays.sort(arrOptions);
        for(int i = 0; i < lstGSCFResponse.size(); i++){
            if(arrOptions[i].contains("!!SEP!!")) {
                String[] arrSplit = arrOptions[i].split("!!SEP!!");
                strReturn += arrSplit[1];
            }
        }

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