package ctd.services;

import ctd.services.exceptions.Exception401Unauthorized;
import java.util.HashMap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tjeerd
 */
public class getStudies {
    private String strAssayToken;

    public String getStudies() {

//        // Check if the provided sessionToken has access to the provided assayToken
//        HashMap<String,String> objParam = new HashMap();
//        objParam.put("assayToken", strAssayToken);
//        String[] strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"getAuthorizationLevel",objParam);
//        if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"canRead"))) {
//
//        }

        String strRet = "<option value='none'>Select a study...</option>";
        strRet += "<option value='test'>Test bla</option>";
        return strRet;
    }

}
