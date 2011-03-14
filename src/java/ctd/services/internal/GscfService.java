package ctd.services.internal;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.services.exceptions.Exception500InternalServerError;
import ctd.services.getTicket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This service provides functions that enable REST calls to communicate with GSCF
 * @author Tjeerd van Dijk
 * @author Taco Steemers
  */
public class GscfService {

    public GscfService() {

    }

    /**
    * Call GSCF Service via a secure call
    *
    * @param       sessionToken Session token for connection to GSCF
    * @param       restMethod Method to call on GSCF rest controller
    * @param       restParams Parameters to provide to the GSCF rest method
    *
    * @return      String
    */
    public String[] callGSCF(String sessionToken, String restMethod, HashMap<String, String> restParams) throws Exception500InternalServerError {
        String[] strRet = new String[2];

        String strParam = "";
        if(!restParams.isEmpty()) {
            for (Map.Entry<String, String> entry : restParams.entrySet()) {
                strParam = "&"+entry.getKey()+"="+entry.getValue();
            }
        }

        try {
            URL urlURL = new URL(this.restURL()+restMethod+"/query?sessionToken="+sessionToken+strParam);
            HttpURLConnection connection = (HttpURLConnection)urlURL.openConnection();
            strRet[0] = connection.getResponseCode()+"";

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String strLine = "";
            strRet[1] = "";
            while((strLine=rd.readLine())!=null){
                strRet[1] +=strLine+"\n";
            }
            connection.disconnect();
        } catch(Exception e) {
            // If something goes wrong we throw an exception
            throw new Exception500InternalServerError(e.getMessage());
        }

        return strRet;
    }
    /**
     * Base URL of GSCF Rest Controller/API
     *
     * @return url String
     */
    private String restURL() {
        ResourceBundle res = ResourceBundle.getBundle("settings");
        return res.getString("gscf.baseURL") + "/rest/";
    }

    /**
     * Returns the URL to let the user login at GSCF
     *
     * @param params    Parameters of the action called
     * @param strToken  Session token
     * @return          URL to redirect the user to
     */
    public String urlAuthRemote(String strToken, String strReturnURL) {
        ResourceBundle res = ResourceBundle.getBundle("settings");
        return res.getString("gscf.baseURL") + "/login/auth_remote?moduleURL="+res.getString("ctd.moduleURL")+"&consumer="+res.getString("ctd.consumerID")+"&token="+strToken+"&returnURL="+strReturnURL;
    }

    public boolean isUser(String strJSON) {
        boolean blnRet = false;

        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            Map objJSON = (Map) trans.deserializeFromJsonString(strJSON);
            if(objJSON.containsKey("authenticated")) {
                String strAuth = (String) objJSON.get("authenticated").toString();
                if(strAuth.equals("true")) {
                    blnRet = true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Skaringa Exception in GscfService");
        }



        return blnRet;
    }

    public String getAuthorizationLevel(String strJSON){
        String strRet = "";

        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            Map objJSON = (Map) trans.deserializeFromJsonString(strJSON);
            if(objJSON.containsKey("bla")) {
                strRet = (String) objJSON.get("bla");
            }
        } catch (Exception ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Skaringa Exception in GscfService");
        }

        return strRet;
    }

}
