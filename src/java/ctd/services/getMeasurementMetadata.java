package ctd.services;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;

import ctd.model.StudySampleAssay;
import ctd.model.Ticket;
import ctd.services.exceptions.*;
import ctd.services.getTicket;
import ctd.services.internal.GscfService;
import ctd.ws.model.ProbeSetAnnotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
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

    public String[] getMeasurementMetadata() throws SerializerException, Exception401Unauthorized, Exception500InternalServerError, Exception403Forbidden, Exception400BadRequest, Exception404ResourceNotFound {

        // Check if the minimal parameters are set
        if(strAssayToken==null || strSessionToken==null){
            throw new Exception400BadRequest();
        }

        // Check if the provided sessionToken is valid
        GscfService objGSCFService = new GscfService();
        String[] strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"isUser",null);
        if(!objGSCFService.isUser(strGSCFRespons[1])) {
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
        HashMap<String,String> objParam = new HashMap();
        objParam.put("studyToken", strStudyToken);
        strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"getAuthorizationLevel",objParam);
        if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"canRead"))) {
            throw new Exception401Unauthorized();
        }

        // init parameters
        String[] strReturn = new String [2];
        ArrayList<ProbeSetAnnotation> metadata = new ArrayList<ProbeSetAnnotation>();

        // If the optional parameter measurementToken is set, then we prepare
        // an extra condition for the query
        String strMeasurementQuery = "";
        if(!getMeasurementToken().equals("")) {
            strMeasurementQuery += " AND ca.probeset IN(" + getMeasurementToken() + ") ";
        }

        SQLQuery sql2 = session.createSQLQuery("SELECT DISTINCT ca.probeset,ca.gene_accession,ca.gene_symbol,ca.gene_description"
                + " FROM study_sample_assay ssa, expression ex, chip c,chip_annotation ca"
                + " WHERE ssa.X_REF='" + getAssayToken() + "'"
                + " AND ex.study_sample_assay_id=ssa.id"
                + " AND ex.chip_annotation_id=ca.id "
                + " AND ca.chip_id=c.id"
                + strMeasurementQuery + ";");
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
            //ca.setProbeSet(getAssayToken());
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
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }

        // HTTP response code 200 means 'OK'
        strReturn[0] = "200";
        strReturn[1] = trans.serializeToJsonString(metadata);
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
     * @return the strMeasurementToken
     */
    public String getMeasurementToken() {
        String strRet = "";
        for(int i=0; i<strMeasurementToken.size(); i++) {
            if(!strRet.equals("")) {
                strRet += ",";
            }
            strRet += "'" + strMeasurementToken.get(i) + "'";
        }
        return strRet;
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
