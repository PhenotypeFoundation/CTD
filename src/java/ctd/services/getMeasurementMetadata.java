package ctd.services;

import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.services.exceptions.*;
import ctd.services.internal.GscfService;
import ctd.ws.model.ProbeSetAnnotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
public class getMeasurementMetadata {

    private String strSessionToken;
    private String strAssayToken;
    private LinkedList<String> strMeasurementToken = new LinkedList<String>();

    /**
     *
     * @return
     * @throws SerializerException
     * @throws Exception401Unauthorized
     * @throws Exception500InternalServerError
     * @throws Exception403Forbidden
     * @throws Exception400BadRequest
     * @throws Exception404ResourceNotFound
     */

    public String[] getMeasurementMetadata() throws Exception401Unauthorized, Exception500InternalServerError, Exception403Forbidden, Exception400BadRequest, Exception404ResourceNotFound {

        // Check if the minimal parameters are set
        if(strAssayToken==null || strSessionToken==null){
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

        // If the assay isn't found we throw a 404
        if(strStudyToken.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        // Call GSCF to check a if a user is authorized for the requested data
        HashMap<String,String> objParam = new HashMap();
        objParam.put("studyToken", strStudyToken);
        String[] strGSCFRespons = objGSCFService.callGSCF(getSessionToken(),"getAuthorizationLevel",objParam);
        if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"canRead"))) {
            throw new Exception401Unauthorized();
        }

        // init parameters
        String[] strReturn = new String [2];
        ArrayList<ProbeSetAnnotation> metadata = new ArrayList<ProbeSetAnnotation>();

        // If the optional parameter measurementToken is set, then we prepare
        // an extra condition for the query
        String strMeasurementQuery = getMeasurementToken();
        if(!strMeasurementQuery.isEmpty()) {
            strMeasurementQuery = " AND ca.probeset IN(" + strMeasurementQuery + ") ";
        }

        // Create a query to collect all data that is requested
        SQLQuery sql2 = session.createSQLQuery("SELECT DISTINCT ca.probeset,ca.gene_accession,ca.gene_symbol,ca.gene_description"
                + " FROM study_sample_assay ssa, expression ex, chip c,chip_annotation ca"
                + " WHERE ssa.X_REF='" + getAssayToken() + "'"
                + " AND ex.study_sample_assay_id=ssa.id"
                + " AND ex.chip_annotation_id=ca.id "
                + " AND ca.chip_id=c.id"
                + strMeasurementQuery + ";");

        // Iterate over the data in order to add them to the lists that are used to create to maps
        Iterator it2 = sql2.list().iterator();
        while (it2.hasNext()) {
            ProbeSetAnnotation ca = new ProbeSetAnnotation();
            Object[] annotation = (Object[]) it2.next();
            String strProbeset = (String) annotation[0];
            String strGeneaccession = (String) annotation[1];
            String strGenesymbol = (String) annotation[2];
            String strGeneannotation = (String) annotation[3];

            // All available metadata of a measurement is reported
            ca.setProbeSet(strProbeset);
            ca.setGeneAccession(strGeneaccession);
            ca.setGeneDescription(strGeneannotation);
            ca.setGeneSymbol(strGenesymbol);
            metadata.add(ca);
        }

        // Close hibernate session
        session.close();

        // If no data is found, then a 404 is thrown
        if(metadata.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        // Use SKARINGA to transform the results into a valide JSON message
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
            strReturn[1] = trans.serializeToJsonString(metadata);
        } catch (Exception ex) {
            throw new Exception500InternalServerError("ERROR getMeasurementMetadata: "+ex.getLocalizedMessage());
        }

        // HTTP response code 200 means 'OK'
        strReturn[0] = "200";
        
        return strReturn;
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

    /**
     * The measurementTokens are collected in a list. This function transformes
     * this list to a String
     * @return the strMeasurementToken
     */
    public String getMeasurementToken() {
        StringBuffer strRet = new StringBuffer();
        strRet.append("");

        // Boolean used to check for the first item
        boolean hasMeasurementToken = false;

        for(int i=0; i<strMeasurementToken.size(); i++) {
            if(hasMeasurementToken) {
                // Seperate items with a comma
                strRet.append( "," );
            } else {
                hasMeasurementToken = true;
            }

            // Add the measurementtokens (with surrounding quotes)
            strRet.append( "'" ).append( strMeasurementToken .get(i) ).append( "'" );
        }
        return strRet.toString();
    }

    /**
     * @param strMeasurementToken the strMeasurementToken to set
     */
    public void setMeasurementToken(String measurementToken) {
        this.strMeasurementToken.add(measurementToken);
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
}
