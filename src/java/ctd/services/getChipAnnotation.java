/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ctd.services;


import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;
import ctd.ws.model.ChipAnnotation;
import ctd.ws.model.ProbeSetAnnotation;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author kerkh010
 */
public class getChipAnnotation {

    private String wsPassword;
    private String chipName;


    public String getChipAnnotation() throws NoImplementationException, SerializerException{

        String message = "";
        

        
        ArrayList<ProbeSetAnnotation> apsa = new ArrayList<ProbeSetAnnotation>();

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        SQLQuery q1 = session.createSQLQuery("Select probeset,gene_accession,gene_symbol,gene_description From chip,chip_annotation WHERE chip.id=chip_annotation.chip_id AND chip.name='"+getChipName()+"'");
        Iterator it1 = q1.list().iterator();

        while (it1.hasNext()){
            Object[] data = (Object[]) it1.next();
            ProbeSetAnnotation psa = new ProbeSetAnnotation();
            String probeset = (String) data[0];
            String gene_accession = (String) data[1];
            String gene_symbol = (String) data[2];
            String gene_description = (String) data[3];

            psa.setProbeSet(probeset);
            psa.setGeneAccession(gene_accession);
            psa.setGeneSymbol(gene_symbol);
            psa.setGeneDescription(gene_description);

            apsa.add(psa);
        }

        

        session.close();
        sessionFactory.close();
        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToString(apsa);

        return message;

    }

    /**
     * @return the wsPassword
     */
    public String getWsPassword() {
        return wsPassword;
    }

    /**
     * @param wsPassword the wsPassword to set
     */
    public void setWsPassword(String wsPassword) {
        this.wsPassword = wsPassword;
    }

    /**
     * @return the chipName
     */
    public String getChipName() {
        return chipName;
    }

    /**
     * @param chipName the chipName to set
     */
    public void setChipName(String chipName) {
        this.chipName = chipName;
    }


}
