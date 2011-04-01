package ctd.services;

import ctd.services.exceptions.Exception307TemporaryRedirect;
import ctd.services.exceptions.Exception500InternalServerError;
import ctd.services.internal.GscfService;
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

    public void loginGSCF() throws Exception307TemporaryRedirect {
        GscfService objGSCFService = new GscfService();
        String[] strGSCFRespons = new String[2];
        try {
            strGSCFRespons = objGSCFService.callGSCF(getSessionToken(), "isUser", null);
        } catch (Exception500InternalServerError ex) {
            Logger.getLogger(loginGSCF.class.getName()).log(Level.SEVERE, "loginGSCF ERROR: Internal Service Error");
        }
        if (!objGSCFService.isUser(strGSCFRespons[1])) {
            ResourceBundle res = ResourceBundle.getBundle("settings");
            String urlAuthRemote = objGSCFService.urlAuthRemote(strSessionToken, res.getString("ctd.moduleURL") + "/"+getReturnScript());
            throw new Exception307TemporaryRedirect(urlAuthRemote);
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
