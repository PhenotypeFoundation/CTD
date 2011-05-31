package ctd.services.internal;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.services.exceptions.Exception500InternalServerError;
import ctd.services.getTicket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
        // empty constructor
    }

    /**
    * Call GSCF Service via a REST call
    *
    * @param       String sessionToken Session token for connection to GSCF
    * @param       String restMethod Method to call on GSCF rest controller
    * @param       HashMap<String, String> restParams Parameters to provide to the GSCF rest method
    * @return      the String[] that contains the HTTP-status code
    *              on the first position, and the response to the
    *              query in the second position
    * @throws      Exception500InternalServerError if something goes wrong while
    *              making or processing the REST call to GSCF this exception is thrown
    */
    @Deprecated
    public String[] callGSCF(String sessionToken, String restMethod, HashMap<String, String> restParams) throws Exception500InternalServerError {
        String[] strRet = new String[2];

        if(restParams==null) {
            restParams = new HashMap<String, String>();
        }

        ResourceBundle res = ResourceBundle.getBundle("settings");
        String strModuleVal = res.getString("ctd.moduleURL");
        String strConsumerVal = res.getString("ctd.consumerID");
        if(!restParams.containsKey("consumer")) {
            restParams.put("consumer", strConsumerVal);
        }
        if(!restParams.containsKey("moduleURL")) {
            restParams.put("moduleURL", strModuleVal);
        }

        try {
            // Place the REST call
            URL urlURL = new URL(this.restURL()+restMethod+"/query?token="+sessionToken);
            HttpURLConnection connection = (HttpURLConnection)urlURL.openConnection();

            connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            boolean blnFirst = true;
            if(!(restParams==null) && !restParams.isEmpty()) {
                for (Map.Entry<String, String> entry : restParams.entrySet()) {
                    if(!blnFirst) {
                        wr.write("&");
                    } else {
                        blnFirst = false;
                    }
                    wr.write(entry.getKey()+"="+entry.getValue());
                }
            }
            wr.flush();
            wr.close();

            strRet[0] = connection.getResponseCode()+"";
            strRet[1] = "";
            if(!strRet[0].equals("403")) {
                // Read the response body into a buffer and process it
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String strLine = "";
                while((strLine=rd.readLine())!=null){
                    strRet[1] +=strLine+"\n";
                }
                rd.close();
            }
            connection.disconnect();
            //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "callGSCF result "+strRet[0]+":<br />"+strRet[1]);
        } catch(Exception e) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "callGSCF Internal Error: \n"+e.getLocalizedMessage()+"\n"+e.toString()+"\nSessionToken: ["+sessionToken+"]\nrestMethod: ["+restMethod+"]");
            throw new Exception500InternalServerError(e.getMessage());
        }

        return strRet;
    }

    public LinkedList callGSCF2(String sessionToken, String restMethod, HashMap<String, String> restParams) {

        String[] strRet = new String[2];

        try {
            strRet = this.callGSCF(sessionToken, restMethod, restParams);
        } catch (Exception500InternalServerError e) {
            StackTraceElement[] arrStackTr = Thread.currentThread().getStackTrace();
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "callGSCF2 ERROR ("+arrStackTr[2].toString()+"): "+e.getError());
        }

        LinkedList objJSON = null;
        try {
            ObjectTransformer trans = ObjectTransformerFactory.getInstance().getImplementation();
            objJSON = (LinkedList) trans.deserializeFromJsonString(strRet[1]);
        } catch (Exception e) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "callGSCF2 ERROR (JSON): "+e.getLocalizedMessage());
        }

        return objJSON;

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
     * @param strToken  Session token
     * @param strReturnURL The url that GCSF redirects to if the request is succesful
     * @return          URL to redirect the user to
     */
    public String urlAuthRemote(String strToken, String strReturnURL) {
        ResourceBundle res = ResourceBundle.getBundle("settings");
        return res.getString("gscf.baseURL") + "/login/auth_remote?moduleURL="+res.getString("ctd.moduleURL")+"&consumer="+res.getString("ctd.consumerID")+"&token="+strToken+"&returnUrl="+strReturnURL;
    }

    /**
     * Returns the URL to let the user logout at GSCF
     *
     * @param   strToken  Session token
     * @param   strReturnURL The url that GCSF redirects to if the request is succesful
     * @return  URL to redirect the user to
     */
    public String urlLogoutRemote(String strToken, String strReturnURL) {
        ResourceBundle res = ResourceBundle.getBundle("settings");
        return res.getString("gscf.baseURL") + "/logout/remote?moduleURL="+res.getString("ctd.moduleURL")+"&consumer="+res.getString("ctd.consumerID")+"&token="+strToken+"&returnUrl="+strReturnURL;
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
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Skaringa Exception in GscfService.isUser");
        }

        return blnRet;
    }

    public boolean getAuthorizationLevel(String strJSON, String strLevel){
        boolean blnRet = false;

        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            Map objJSON = (Map) trans.deserializeFromJsonString(strJSON);
            if(objJSON.containsKey(strLevel)) {
                blnRet = objJSON.get(strLevel).toString().equals("true");
            }
        } catch (Exception ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Skaringa Exception in GscfService.getAuthorizationLevel");
        }

        return blnRet;
    }
    /*
    public String getAssayName(String strAssayToken, String strStudyToken, String strSessionToken) {
        String[] strGSCFRespons = new String[2];

        HashMap<String, String> restParams = new HashMap<String, String>();
        restParams.put("assayToken", strAssayToken);
        restParams.put("studyToken",strStudyToken);
        try {
            strGSCFRespons = this.callGSCF(strSessionToken,"getAssays",restParams);
        } catch (Exception500InternalServerError e) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "GscfService: getAssayName ERROR (getAssay): "+e.getError());
        }
        ObjectTransformer trans = null;
        HashMap objJSON = new HashMap();
        try {
            //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "GscfService: "+strGSCFRespons[1]);
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            LinkedList lstJSON = (LinkedList) trans.deserializeFromJsonString(strGSCFRespons[1]);
            objJSON = (HashMap) lstJSON.get(0);
        } catch (Exception e) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "GscfService: getAssayName ERROR (JSON): "+e.getLocalizedMessage());
        }
        return (String) objJSON.get("name");
    }
     * 
     */
}
