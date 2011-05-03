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

    public String getUser() {

        String strUser = "";

        GscfService objGSCFService = new GscfService();
        String[] strGSCFRespons = new String[2];
        try {
            this.loginGSCF();
            strGSCFRespons = objGSCFService.callGSCF(getSessionToken(), "getUser", null);
        } catch (Exception500InternalServerError ex) {
            Logger.getLogger(loginGSCF.class.getName()).log(Level.SEVERE, "loginGSCF ERROR: Internal Service Error");
        } catch (Exception307TemporaryRedirect ex) {
            
        }

        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            Map objJSON = (Map) trans.deserializeFromJsonString(strGSCFRespons[1]);
            if(objJSON.containsKey("username")) {
                strUser = (String) objJSON.get("username").toString();
            }
        } catch (Exception ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Skaringa Exception in loginGSCF.getUser");
        }
        
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String strRet = "You are signed in as <i>"+strUser+"</i> at the General Study Capture Framework (<a href='#'>sign out</a>)";
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
