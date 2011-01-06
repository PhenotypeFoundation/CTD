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
public class getMeasurements {

    private String password;
    private String assayToken;

    public String getMeasurements() throws NoImplementationException, SerializerException {
        String message = "";
        ArrayList<String> probesets = new ArrayList<String>();

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Ticket ticket = null;
        Integer ssa_id = null;
        Query q = session.createQuery("from Ticket where password='" + getPassword() + "'");
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

        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToJsonString(probesets);

        return message;
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
}
