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

        if(getAssayToken()==null || getSessionToken()==null){
            throw new Exception400BadRequest();
        }

        GscfService objGSCFService = new GscfService();
        String[] strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"isUser",null);
        if(!objGSCFService.isUser(strGSCFRespons[1])) {
            throw new Exception403Forbidden();
        }

        HashMap<String,String> objParam = new HashMap();
        objParam.put("assayToken", strAssayToken);
        strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"getAuthorizationLevel",objParam);
        if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1]).equals("isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1]).equals("canRead") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1]).equals("canWrite"))) {
            throw new Exception401Unauthorized();
        }

        String[] strReturn = new String [2];
        ArrayList<String> probesets = new ArrayList<String>();

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Ticket ticket = null;
        Integer ssa_id = null;
        Query q = session.createQuery("from Ticket where password='" + getSessionToken() + "'");
        ticket = (Ticket) q.uniqueResult();

        Iterator it1 = ticket.getStudySampleAssaies().iterator();
        while (it1.hasNext()) {
            StudySampleAssay ssa = (StudySampleAssay) it1.next();
            String xref = ssa.getXREF();
            if (getAssayToken().equals(xref)) {
                ssa_id = ssa.getId();
            }
        }

        if (ssa_id != null) {
            SQLQuery sql = session.createSQLQuery("SELECT chip_annotation.probeset FROM expression,chip_annotation WHERE study_sample_assay_id=" + ssa_id.toString() + " AND expression.chip_annotation_id=chip_annotation.id;");
            Iterator it2 = sql.list().iterator();
            while (it2.hasNext()) {
                String probeset = (String) it2.next();
                probesets.add(probeset);
            }
        }
        session.close();

        if(probesets.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
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
