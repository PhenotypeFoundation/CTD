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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
public class getMeasurements {

    private String strSessionToken;
    private String strAssayToken;

    public String[] getMeasurements() throws NoImplementationException, SerializerException, Exception401Unauthorized, Exception307TemporaryRedirect, Exception500InternalServerError, Exception403Forbidden, Exception404ResourceNotFound, Exception400BadRequest {

        // Check if the minimal parameters are set
        if(getAssayToken()==null || getSessionToken()==null){
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
        ArrayList<String> probesets = new ArrayList<String>();

//        Ticket ticket = null;
//        Integer ssa_id = null;
//        Query q = session.createQuery("from Ticket where password='" + getSessionToken() + "'");
//        ticket = (Ticket) q.uniqueResult();
//
//        Iterator it1 = ticket.getStudySampleAssaies().iterator();
//        while (it1.hasNext()) {
//            StudySampleAssay ssa = (StudySampleAssay) it1.next();
//            String xref = ssa.getXREF();
//            if (getAssayToken().equals(xref)) {
//                ssa_id = ssa.getId();
//            }
//        }
//
//        if (ssa_id != null) {
            String strQ2 = "SELECT DISTINCT ca.probeset"
                        + " FROM expression ex,chip_annotation ca,study_sample_assay ssa"
                        + " WHERE ex.study_sample_assay_id=ssa.id"
                        + " AND ssa.X_REF='" + getAssayToken() + "'"
                        + " AND ex.chip_annotation_id=ca.id;";
            SQLQuery sql2 = session.createSQLQuery(strQ2);
            //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, strQ);
            Iterator it2 = sql2.list().iterator();
            while (it2.hasNext()) {
                String probeset = (String) it2.next();
                probesets.add(probeset);
            }
//        }

        // Close hibernate session
        session.close();

        // If no probesets are found, then a 404 is thrown
        if(probesets.isEmpty()) {
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
        strReturn[1] = trans.serializeToJsonString(probesets);

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
