package ctd.services;

import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.services.exceptions.Exception307TemporaryRedirect;
import ctd.services.exceptions.Exception500InternalServerError;
import ctd.services.internal.GscfService;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Taco Steemers
 * @author Tjeerd van Dijk
 */
public class loginGSCF {

    private String strSessionToken;
    private String strReturnScript = "";

    /***
     * This function checkes is a certain strSessionToken is authenticated in GSCF.
     * When this is not the case it throws a redirectexception containing a url
     * to GSCF. This url also contains a returnurl with strReturnScript.
     * 
     * @throws Exception307TemporaryRedirect
     */

    public void loginGSCF() throws Exception307TemporaryRedirect {
        // Create a GSCF service
        GscfService objGSCFService = new GscfService();

        // Check if the user is logged in in GSCF
        if (!objGSCFService.isUser(getSessionToken())) {
            // If the user is not logged in, get some settings
            ResourceBundle res = ResourceBundle.getBundle("settings");
            // Create the URL we need to redirect CTD to in order to enable the user to log in
            String urlAuthRemote = objGSCFService.urlAuthRemote(getSessionToken(), res.getString("ctd.moduleURL") + "/"+getReturnScript());
            // Throw the redirect
            throw new Exception307TemporaryRedirect(urlAuthRemote);
        }
    }


    /***
     * This function gets the username of the current user from GSCF and transformes
     * it into a message that is shown in the bar at the top of CTD
     *
     * @return the String containing the message for the bar at the top of CTD
     */
    public String getUser() {

        String strUser = "";

        // Create a GSCF service
        GscfService objGSCFService = new GscfService();

        String[] strGSCFRespons = new String[2];
        try {
            // Call GSCF in order to get the username
            strGSCFRespons = objGSCFService.callGSCF(getSessionToken(), "getUser", null);
        } catch (Exception500InternalServerError ex) {
            Logger.getLogger(loginGSCF.class.getName()).log(Level.SEVERE, "loginGSCF ERROR: Internal Service Error \n"+ex.getError());
        }

        // If a 503 response is received, GSCF is probably offline
        if(strGSCFRespons[0].equals("503")) {
            return "ERROR, GSCF OFFLINE";
        }

        // If a 200 response code (OK) is reveived, we try to get the username
        if(strGSCFRespons[0].equals("200")) {
            ObjectTransformer trans = null;
            try {
                trans = ObjectTransformerFactory.getInstance().getImplementation();
                // Translate the JSON into an object
                Map objJSON = (Map) trans.deserializeFromJsonString(strGSCFRespons[1]);
                // Get the username
                if(objJSON.containsKey("username")) {
                    strUser = (String) objJSON.get("username").toString();
                }
            } catch (Exception ex) {
                Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Skaringa Exception in loginGSCF.getUser");
            }
        }

        // Use the username in order to create a HTML message
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String strRet = "You are signed in as <i>"+strUser+"</i> at the General Study Capture Framework (<a href='"+objGSCFService.urlLogoutRemote(strSessionToken, res.getString("ctd.moduleURL") + "/index.jsp")+"'>sign out</a>)";
        if(strUser.equals("")) {
            strRet = "You are not signed in (<a href='"+objGSCFService.urlAuthRemote(strSessionToken, res.getString("ctd.moduleURL") + "/index.jsp")+"'>sign in</a>) at the General Study Capture Framework";
        }

        return strRet;
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
     * @return the strSessionToken
     */
    public String getReturnScript() {
        return strReturnScript;
    }

    /**
     * @param strSessionToken the strSessionToken to set
     */
    public void setReturnScript(String strReturnScript) {
        this.strReturnScript = strReturnScript;
    }
}
