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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Taco Steemers
 * @author Tjeerd van Dijk
 */
public class getAssays {
    private String strStudyToken;
    private String strSessionToken;

    /***
     * TODO COMMENT
     * 
     * @return
     * @throws Exception400BadRequest
     * @throws Exception403Forbidden
     * @throws Exception500InternalServerError
     */

    public String getAssays() throws Exception400BadRequest, Exception403Forbidden, Exception500InternalServerError {
        String strReturn = "";
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Arrived in getAssays()...");

        // Check if the minimal parameters are set
        if(strSessionToken==null){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): strSessionToken==null");
            throw new Exception400BadRequest();
        }
        if(strStudyToken==null){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): strStudyToken==null");
            throw new Exception400BadRequest();
        }

        // Check if the provided sessionToken is valid
        GscfService objGSCFService = new GscfService();
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String strConsumerVal = res.getString("ctd.consumerID");
        String strModuleVal = res.getString("ctd.moduleURL");
        HashMap<String, String> restParams = new HashMap<String, String>();
        restParams.put("consumer", strConsumerVal);
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): about to call isUser()");
        String[] strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"isUser",restParams);
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): just called isUser()");
        if(!objGSCFService.isUser(strGSCFRespons[1])) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): strSessionToken invalid: "+strSessionToken);
            throw new Exception403Forbidden();
        }
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): strSessionToken is valid: "+strSessionToken);

        strReturn = "<option value='none'>Select an assay...</option>";
        objGSCFService = new GscfService();
        restParams = new HashMap<String, String>();
        restParams.put("studyToken", strStudyToken);
        restParams.put("consumer", strConsumerVal);
        restParams.put("moduleURL", strModuleVal);
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): about to call getAssays()");
        strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"getAssays",restParams);
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): just called getAssays(): "+strGSCFRespons[0]+" and "+strGSCFRespons[1]);

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

        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): list length: "+lstGSCFResponse.size());

        for(int i = 0; i < lstGSCFResponse.size(); i++){
            HashMap<String, String> map = (HashMap<String, String>) lstGSCFResponse.get(i);
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): map contains "+map.toString());
            strReturn += "<option value="+map.get("assayToken")+">"+map.get("name")+"</option>";
        }

        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getAssays(): result: "+strReturn);
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
    public String getStudyToken() {
        return strStudyToken;
    }

    /**
     * @param studyToken the strStudyToken to set
     */
    public void setStudyToken(String studyToken) {
        this.strStudyToken = studyToken;
    }

}
