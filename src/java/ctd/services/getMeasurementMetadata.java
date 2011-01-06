/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;

import ctd.model.StudySampleAssay;
import ctd.model.Ticket;
import ctd.ws.model.ProbeSetAnnotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author kerkh010
 */
public class getMeasurementMetadata {

    private String password;
    private String assayToken;
    private String measurementToken;

    public String getMeasurementMetadata() throws SerializerException {
        String message = "";
        ArrayList<ProbeSetAnnotation> metadata = new ArrayList<ProbeSetAnnotation>();

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        SQLQuery sql = session.createSQLQuery("SELECT chip.name,chip_annotation.gene_accession,chip_annotation.gene_symbol,chip_annotation.gene_description FROM ticket,study_sample_assay, expression, chip,chip_annotation WHERE ticket.password='" + getPassword() + "' AND ticket.id=study_sample_assay.ticket_id AND study_sample_assay.X_REF='" + getAssayToken() + "' AND expression.study_sample_assay_id=study_sample_assay.id AND expression.chip_annotation_id=chip_annotation.id  AND chip_annotation.probeset='" + getMeasurementToken() + "' AND chip_annotation.chip_id=chip.id;");
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


        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToJsonString(metadata);

        return message;



    }

    /**
     * @return the assayToken
     */
    public String getAssayToken() {
        return assayToken;
    }

    /**
     * @param assayToken the assayToken to set
     */
    public void setAssayToken(String assayToken) {
        this.assayToken = assayToken;
    }

    /**
     * @return the measurementToken
     */
    public String getMeasurementToken() {
        return measurementToken;
    }

    /**
     * @param measurementToken the measurementToken to set
     */
    public void setMeasurementToken(String measurementToken) {
        this.measurementToken = measurementToken;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
