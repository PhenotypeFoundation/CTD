package ctd.services;

import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.services.exceptions.*;
import ctd.services.internal.GscfService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author kerkh010
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class getAssayURL {

    private String strSessionToken;
    private String strAssayToken;

    /**
     * This function is used by the REST service getAssayURL to create it's response
     *
     * @return a String in JSON format
     *
     * @throws Exception401Unauthorized This exception is thrown if GSCF indicates that a user is not authorized (getAuthorizationLevel)
     * @throws Exception500InternalServerError This exception is thrown when there is an error in this code
     * @throws Exception403Forbidden This exception is thrown if GSCF indicates that a user is not logged in (isUser)
     * @throws Exception404ResourceNotFound This exception is thrown when the requested assay isn't present in the database
     * @throws Exception400BadRequest This exception is thrown when not enough paremeters are set
     */

    public String[] getAssayURL() throws Exception400BadRequest, Exception500InternalServerError, Exception403Forbidden, Exception401Unauthorized, Exception404ResourceNotFound {

        // Check if the minimal parameters are set
        if(getAssayToken()==null || getSessionToken()==null){
            throw new Exception400BadRequest();
        }

        // Check if the provided sessionToken is valid
        GscfService objGSCFService = new GscfService();
        if(!objGSCFService.isUser(getSessionToken())) {
            throw new Exception403Forbidden();
        }

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        // Check if the provided sessionToken has access to the provided assayToken
        // This needs to be verified with the studyToken
        String strQ = "SELECT DISTINCT study_token FROM study_sample_assay WHERE X_REF ='"+getAssayToken()+"'";
        SQLQuery sql = session.createSQLQuery(strQ);
        String strStudyToken = "";
        Iterator it1 = sql.list().iterator();
        while (it1.hasNext()) {
            strStudyToken = (String) it1.next();
        }

        // If the study can't be found a 404 is thrown
        if(strStudyToken.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        // Call GSCF in order to get the authorization level for a study
        HashMap<String,String> objParam = new HashMap();
        objParam.put("studyToken", strStudyToken);
        String[] strGSCFRespons = objGSCFService.callGSCF(getSessionToken(),"getAuthorizationLevel",objParam);
        if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"canRead"))) {
            throw new Exception401Unauthorized();
        }

        // init parameters  
        String[] strReturn = new String [2];
        ResourceBundle res = ResourceBundle.getBundle("settings");

        // init paremeters from the settings
        String ftp_username = res.getString("ws.ftp_username");
        String hostname = res.getString("ws.hostname");
        String ftp_folder = res.getString("ws.ftp_folder");

        // Get the folder
        String folder = getAssayToken();

        //location ftp folder
        String link = "sftp://" + ftp_username + "@" + hostname + ":" +ftp_folder + folder + "/";

        // The link is now hardwired to the CTD home because there doesn't excist a page for assay details
        link = res.getString("ctd.moduleURL");

        // Create a map and put the url inside it.
        HashMap<String,String> mapURL = new HashMap<String,String>();
        mapURL.put("url", link);

        // If no URL is present a 404 is thrown
        if(mapURL.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        // Use SKARINGA to transform the results into a valide JSON message
        ObjectTransformer trans = null;
        strReturn[1] = "";
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            strReturn[1] = trans.serializeToJsonString(mapURL);
        } catch (Exception ex) {
            Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "SKARINGA EXCEPTION in getAssayURL: "+ ex.getLocalizedMessage());
        }

        // HTTP response code 200 means 'OK'
        strReturn[0] = "200";
        
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
    public void setAssayToken(String strAssayToken) {
        this.strAssayToken = strAssayToken;
    }
}
