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

        // If no paremetermap is supplied, we create an empty map
        if(restParams==null) {
            restParams = new HashMap<String, String>();
        }

        // Always add the moduleURL and consumerID to the REST call
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
            // Place the REST call, put the sessionToken in the URL
            URL urlURL = new URL(this.restURL()+restMethod+"/query?token="+sessionToken);
            HttpURLConnection connection = (HttpURLConnection)urlURL.openConnection();

            // Add all the paremeters to the POST buffer
            connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            boolean blnFirst = true;
            if(!(restParams==null) && !restParams.isEmpty()) {
                for (Map.Entry<String, String> entry : restParams.entrySet()) {
                    if(!blnFirst) {
                        // Seperate parameters with a "&"
                        wr.write("&");
                    } else {
                        blnFirst = false;
                    }
                    // Write a parameter and it's value
                    wr.write(entry.getKey()+"="+entry.getValue());
                }
            }
            wr.flush();
            wr.close();

            // Get the responsecode
            strRet[0] = connection.getResponseCode()+"";
            strRet[1] = "";

            // If everything is OK (200)
            if(strRet[0].equals("200")) {
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
            // If something goes wrong throw an error
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "callGSCF Internal Error: \n"+e.getLocalizedMessage()+"\n"+e.toString()+"\nSessionToken: ["+sessionToken+"]\nrestMethod: ["+restMethod+"]");
            throw new Exception500InternalServerError(e.getMessage());
        }

        return strRet;
    }

    /***
     * This function is an enhanced version of callGSCF. The old version returned
     * a String that needed to be parsed. This new version parses the return for
     * you and returnes the LinkedList, therefore making the code elsewere cleaner
     *
     * @param sessionToken the sessionToken of the user
     * @param restMethod the REST service we want to call
     * @param restParams the paremeters that should be present in the call
     * @return a LinkedList containing hashmaps with the response of the call
     */
    public LinkedList callGSCF2(String sessionToken, String restMethod, HashMap<String, String> restParams) {

        String[] strRet = new String[2];

        try {
            // Use the old function
            strRet = this.callGSCF(sessionToken, restMethod, restParams);
        } catch (Exception500InternalServerError e) {
            // If an error is catched, find out which method called this function in order to make the error log more informative
            StackTraceElement[] arrStackTr = Thread.currentThread().getStackTrace();
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "callGSCF2 ERROR ("+arrStackTr[2].toString()+"): "+e.getError());
        }

        LinkedList objJSON = null;
        try {
            // Transform the JSON String into a LinkedList of maps
            ObjectTransformer trans = ObjectTransformerFactory.getInstance().getImplementation();
            objJSON = (LinkedList) trans.deserializeFromJsonString(strRet[1]);
        } catch (Exception e) {
            // If an error is catched, find out which method called this function in order to make the error log more informative
            StackTraceElement[] arrStackTr = Thread.currentThread().getStackTrace();
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "callGSCF2 JSON ERROR ("+arrStackTr[2].toString()+"): "+e.getLocalizedMessage());
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

    /***
     * This function checks for a certain sessionToken if it is authenticated in GSCF
     *
     * @param strSessionToken the sessionToken that needs to be checked
     * @return a boolean indicating authentication
     */
    public boolean isUser(String strSessionToken) {
        boolean blnRet = false;

        String[] strGSCFRespons = new String[2];
        try {
            // Call GSCF in order to get data for this sessionToken
            strGSCFRespons = this.callGSCF(strSessionToken, "isUser", null);
        } catch (Exception500InternalServerError ex) {
            // If an error is catched, find out which method called this function in order to make the error log more informative
            StackTraceElement[] arrStackTr = Thread.currentThread().getStackTrace();
            Logger.getLogger(GscfService.class.getName()).log(Level.SEVERE, "isUser ERROR ("+arrStackTr[2].toString()+"): "+ex.getError());
        }

        //if(!(strGSCFRespons[1]==null)) {
            ObjectTransformer trans = null;
            try {
                // Transform the JSON String into a Map
                trans = ObjectTransformerFactory.getInstance().getImplementation();
                Map objJSON = (Map) trans.deserializeFromJsonString(strGSCFRespons[1]);
                // Check if the response contains the authentication key
                if(objJSON.containsKey("authenticated")) {
                    String strAuth = (String) objJSON.get("authenticated").toString();
                    // Check if the user is authenticated
                    if(strAuth.equals("true")) {
                        blnRet = true;
                    }
                }
            } catch (Exception ex) {
                // If an error is catched, find out which method called this function in order to make the error log more informative
                StackTraceElement[] arrStackTr = Thread.currentThread().getStackTrace();
                Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Skaringa Exception in GscfService.isUser ("+arrStackTr[2].toString()+"):"+ex.getLocalizedMessage());
            }
        //}

        return blnRet;
    }

    /**
     * Function that parses the autcome of a JSON getAuthorization message
     *
     * @param strJSON the JSON message
     * @param strLevel the level to check for (isOwner, canRead, canWrite)
     * @return a boolean indicating if the user is authorized
     */
    public boolean getAuthorizationLevel(String strJSON, String strLevel){
        boolean blnRet = false;

        ObjectTransformer trans = null;
        try {
            // Create a SKARINGA instance in order to parse the JSON
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            Map objJSON = (Map) trans.deserializeFromJsonString(strJSON);
            // Check if the message indicates that the user is authorized for a certain level
            if(objJSON.containsKey(strLevel)) {
                blnRet = objJSON.get(strLevel).toString().equals("true");
            }
        } catch (Exception ex) {
             // If an error is catched, find out which method called this function in order to make the error log more informative
            StackTraceElement[] arrStackTr = Thread.currentThread().getStackTrace();
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "Skaringa Exception in GscfService.getAuthorizationLevel ("+arrStackTr[2].toString()+"):"+ex.getLocalizedMessage());
        }

        return blnRet;
    }
}
