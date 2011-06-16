package ctd.services;

import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.services.exceptions.*;
import ctd.services.internal.GscfService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author kerkh010
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class getMeasurements {

    private String strSessionToken;
    private String strAssayToken;

    /**
     * This function is used by the REST service getMeasurements to create it's response
     *
     * @return a String in JSON format
     *
     * @throws Exception401Unauthorized This exception is thrown if GSCF indicates that a user is not authorized (getAuthorizationLevel)
     * @throws Exception500InternalServerError This exception is thrown when there is an error in this code
     * @throws Exception403Forbidden This exception is thrown if GSCF indicates that a user is not logged in (isUser)
     * @throws Exception404ResourceNotFound This exception is thrown when the requested assay isn't present in the database
     * @throws Exception400BadRequest This exception is thrown when not enough paremeters are set
     */

    public String[] getMeasurements() throws Exception401Unauthorized, Exception500InternalServerError, Exception403Forbidden, Exception404ResourceNotFound, Exception400BadRequest {

        // Check if the minimal parameters are set
        if(getAssayToken()==null || getSessionToken()==null){
            throw new Exception400BadRequest();
        }

        // Init a GSCF service
        GscfService objGSCFService = new GscfService();

        // Check if the provided sessionToken is valid
        if(!objGSCFService.isUser(getSessionToken())) {
            throw new Exception403Forbidden();
        }

        // Create a hibernate session
        Configuration objConf;
        objConf = new Configuration().configure();
        SessionFactory sessionFactory = objConf.buildSessionFactory();
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

        // If the assay isn't found we throw a 404
        if(strStudyToken.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        // We call GSCF to check if the provided user has access
        HashMap<String,String> objParam = new HashMap();
        objParam.put("studyToken", strStudyToken);
        String[] strGSCFRespons = objGSCFService.callGSCF(getSessionToken(),"getAuthorizationLevel",objParam);
        if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"canRead"))) {
            throw new Exception401Unauthorized();
        }

        // init parameters
        String[] strReturn = new String [2];
        ArrayList<String> probesets = new ArrayList<String>();

        // Create the query that gets the data the user requested
        String strQ2 = "SELECT DISTINCT ca.probeset"
                    + " FROM expression ex,chip_annotation ca,study_sample_assay ssa"
                    + " WHERE ex.study_sample_assay_id=ssa.id"
                    + " AND ssa.X_REF='" + getAssayToken() + "'"
                    + " AND ex.chip_annotation_id=ca.id;";
        SQLQuery sql2 = session.createSQLQuery(strQ2);

        // Iterate over the queryresults and add them to a set
        Iterator it2 = sql2.list().iterator();
        while (it2.hasNext()) {
            String probeset = (String) it2.next();
            probesets.add(probeset);
        }

        // Close hibernate session
        session.close();

        // If no probesets are found, then a 404 is thrown
        if(probesets.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        // Use SKARINGA to transform the results into a valid JSON message
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            strReturn[1] = trans.serializeToJsonString(probesets);
        } catch (Exception e) {
            throw new Exception500InternalServerError("ERROR getMeasurements: "+e.getLocalizedMessage());
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
