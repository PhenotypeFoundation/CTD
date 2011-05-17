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

        LinkedList lstGetAssays = objGSCFService.callGSCF2(getSessionToken(),"getAssays",null);

        String strStudyCall = "";
        for(int i = 0; i < lstGetAssays.size(); i++){
            HashMap<String, String> mapTokens = (HashMap<String, String>) lstGetAssays.get(i);

            if(!strStudyCall.equals("")) strStudyCall += "&studyToken=";
            strStudyCall += mapTokens.get("parentStudyToken");
        }

        HashMap<String, String> objParam = new HashMap();
        objParam.put("studyToken",strStudyCall);
        LinkedList lstGetStudies = objGSCFService.callGSCF2(getSessionToken(),"getStudies",objParam);

        String[] arrOptions = new String[lstGetAssays.size()];
        for(int i = 0; i < lstGetStudies.size(); i++){
            HashMap<String, String> map = (HashMap<String, String>) lstGetStudies.get(i);
            String strCode = " ("+map.get("code")+")";
            if(map.get("code")==null) {
                strCode = "";
            }
            arrOptions[i] = map.get("title").toLowerCase()+"!!SEP!!<option value="+map.get("studyToken")+">"+map.get("title")+strCode+"</option>";
        }
        
        Arrays.sort(arrOptions);

        strReturn = "<option value='none'>Select a study...</option>";
        for(int i = 0; i < arrOptions.length; i++){
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