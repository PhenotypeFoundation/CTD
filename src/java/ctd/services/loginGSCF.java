package ctd.services;

import ctd.services.exceptions.Exception400BadRequest;
import ctd.services.exceptions.Exception401Unauthorized;
import ctd.services.exceptions.Exception403Forbidden;
import ctd.services.exceptions.Exception500InternalServerError;
import ctd.services.internal.GscfService;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Taco
 */
public class loginGSCF {
    private String strSessionToken;

    public String loginGSCF(String t) throws Exception400BadRequest, Exception403Forbidden, Exception500InternalServerError {
        strSessionToken = t;
        String strReturn = "";
//        // Check if the provided sessionToken has access to the provided assayToken
//        HashMap<String,String> objParam = new HashMap();
//        objParam.put("assayToken", strAssayToken);
//        String[] strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"getAuthorizationLevel",objParam);
//        if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"canRead"))) {
//
//        }

        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Arrived in loginGSCF()...");

        // Check if the minimal parameters are set
        /*if(strSessionToken==null){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getStudies(): strSessionToken==null");
            throw new Exception400BadRequest();
        }*/

        //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "getStudies(): strSessionToken=="+strSessionToken);

        // Check if the provided sessionToken is valid
        GscfService objGSCFService = new GscfService();
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String strConsumerVal = res.getString("ctd.consumerID");
        HashMap<String, String> restParams = new HashMap<String, String>();
        restParams.put("consumer", strConsumerVal);
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "loginGSCF(): about to call isUser()");
        String[] strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"isUser",restParams);
        Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "loginGSCF(): just called isUser()");
        if(!objGSCFService.isUser(strGSCFRespons[1])) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "loginGSCF(): strSessionToken invalid: "+strSessionToken);
            //throw new Exception403Forbidden();
            String urlAuthRemote = objGSCFService.urlAuthRemote(strSessionToken, res.getString("ctd.moduleURL")+"/upload3.jsp");
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "loginGSCF(): isUser() response was: "+strGSCFRespons[1]+" for sessionToken "+strSessionToken);
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "loginGSCF(): about to call urlAuthRemote: "+urlAuthRemote);
            strReturn = urlAuthRemote;
            return strReturn;
        } else {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "loginGSCF(): strSessionToken is valid: "+strSessionToken);
            strReturn = "yes";
            return strReturn;
        }
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
}
