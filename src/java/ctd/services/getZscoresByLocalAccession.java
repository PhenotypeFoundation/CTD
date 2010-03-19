/* Copyright 2010 Wageningen University, Division of Human Nutrition.
 * Drs. R. Kerkhoven, robert.kerkhoven@wur.nl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ctd.services;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;
import ctd.model.StudySampleAssay;
import ctd.model.Ticket;
import ctd.ws.model.ProbeSetExpression;
import ctd.ws.model.ProbeSetZscore;
import ctd.ws.model.ZscoresDataSet;
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
public class getZscoresByLocalAccession {
    private String password;
    private String reference;




    public String getZscoresByLocalAccession() throws NoImplementationException, SerializerException {
        String message = "";
        ArrayList<ProbeSetExpression> array = new ArrayList<ProbeSetExpression>();
        ArrayList<ProbeSetZscore> array_new = new ArrayList<ProbeSetZscore>();
        
        ZscoresDataSet zs = new ZscoresDataSet();
        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Ticket ticket = null;
        Integer ssa_id = null;
        Query q1 = session.createQuery("from Ticket where password='" + getPassword() + "'");
        ticket = (Ticket) q1.uniqueResult();

        Iterator it1 = ticket.getStudySampleAssaies().iterator();
        while (it1.hasNext()) {
            StudySampleAssay ssa = (StudySampleAssay) it1.next();
            String name = ssa.getXREF();
            if (name != null) {
                if (name.equals(getReference())) {
                    ssa_id = ssa.getId();
                }
            }
        }

        //get the expression data
        if (ssa_id != null) {
            SQLQuery sql = session.createSQLQuery("SELECT expression.expression,probeset FROM expression,chip_annotation WHERE study_sample_assay_id="+ssa_id.toString()+" AND expression.chip_annotation_id=chip_annotation.id;");
            Iterator it2 = sql.list().iterator();
            while (it2.hasNext()) {
                Object[] data = (Object[]) it2.next();
                String probeset = (String) data[1];
                Double value = (Double) data[0];
                ProbeSetExpression pse = new ProbeSetExpression();
                pse.setLog2Value(value);
                pse.setProbeSetName(probeset);
                array.add(pse);
            }
        }
        //get the standard deviation and average. (from the log2 transformed expression values.)
        if (ssa_id!=null){
            Query q2 = session.createQuery("FROM StudySampleAssay WHERE id="+ssa_id.toString());
            StudySampleAssay ssa = (StudySampleAssay) q2.uniqueResult();
            Double average = ssa.getAverage();
            Double std = ssa.getStd();
            zs.setAverage(average);
            zs.setStandardDeviation(std);

            //convert raw data array to new one with z-scores
            for (int i=0;i<array.size();i++){
                ProbeSetExpression pse = array.get(i);
                Double value = pse.getLog2Value();
                String probeset = pse.getProbeSetName();

                //z-score
                Double zscore = (value - average) / std;
                ProbeSetZscore psz = new ProbeSetZscore();
                psz.setProbeSetName(probeset);
                psz.setZScore(zscore);
                array_new.add(psz);
            }
            zs.setProbeSetZscoreList(array_new);

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
        message = trans.serializeToString(zs);


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
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }
}
