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

        if(strAssayToken==null || strSessionToken==null){
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
        ArrayList<ProbeSetAnnotation> metadata = new ArrayList<ProbeSetAnnotation>();

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        String strMeasurementQuery = "";
        if(!getMeasurementToken().equals("")) {
            strMeasurementQuery += "AND chip_annotation.probeset IN(" + getMeasurementToken() + ")";
        }

        SQLQuery sql = session.createSQLQuery("SELECT chip.name,chip_annotation.gene_accession,chip_annotation.gene_symbol,chip_annotation.gene_description FROM ticket,study_sample_assay, expression, chip,chip_annotation WHERE ticket.password='" + getPassword() + "' AND ticket.id=study_sample_assay.ticket_id AND study_sample_assay.X_REF='" + getAssayToken() + "' AND expression.study_sample_assay_id=study_sample_assay.id AND expression.chip_annotation_id=chip_annotation.id "+strMeasurementQuery+" AND chip_annotation.chip_id=chip.id;");
        Iterator it2 = sql.list().iterator();
        while (it2.hasNext()) {
            ProbeSetAnnotation ca = new ProbeSetAnnotation();
            Object[] annotation = (Object[]) it2.next();
            String chipname = (String) annotation[0];
            String geneaccession = (String) annotation[1];
            String genesymbol = (String) annotation[2];
            String geneannotation = (String) annotation[3];

            ca.setChipName(chipname);
            ca.setGeneAccession(geneaccession);
            ca.setGeneDescription(geneannotation);
            ca.setGeneSymbol(genesymbol);
            ca.setProbeSet(getAssayToken());
            metadata.add(ca);
        }

        session.close();

        if(metadata.isEmpty()) {
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
    public String getPassword() {
        return strSessionToken;
    }

    /**
     * @param strSessionToken the strSessionToken to set
     */
    public void setPassword(String strSessionToken) {
        this.strSessionToken = strSessionToken;
    }
}
