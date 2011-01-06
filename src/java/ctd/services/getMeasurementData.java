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
public class getMeasurementData {

    private String password;
    private String assayToken;

    public String getMeasurementData() throws NoImplementationException, SerializerException {
        String message = "";
        ArrayList<Object> total = new ArrayList<Object>();
        ArrayList<String> assaynames = new ArrayList<String>();
        ArrayList<Integer> assayids = new ArrayList<Integer>();
        ArrayList<String> probesetnames = new ArrayList<String>();
        ArrayList<Double> values = new ArrayList<Double>();

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
            String name = ssa.getXREF();
            if (name.equals(getAssayToken())) {
                Integer id = ssa.getId();
                assayids.add(id);
                assaynames.add(name);
            }
        }

        Integer count = 0;
        for (int i = 0; i < assayids.size(); i++) {
            count++;
            Integer assay_id = assayids.get(i);

            SQLQuery sql = session.createSQLQuery("SELECT expression.expression,probeset FROM expression,chip_annotation WHERE study_sample_assay_id=" + assay_id.toString() + " AND expression.chip_annotation_id=chip_annotation.id;");
            Iterator it2 = sql.list().iterator();
            while (it2.hasNext()) {
                Object[] data = (Object[]) it2.next();
                String probeset = (String) data[1];
                if (count==1) {
                    probesetnames.add(probeset);
                }
                Double value = (Double) data[0];
                values.add(value);
            }
        }

        session.close();
        total.add(assaynames);
        total.add(probesetnames);
        total.add(values);

        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToJsonString(total);

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
