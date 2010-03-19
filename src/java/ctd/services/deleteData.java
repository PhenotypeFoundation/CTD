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
import ctd.ws.model.DeleteDataResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author kerkh010
 */
public class deleteData {

    private String password;
    private String wsPassword;

    public String deleteData() throws NoImplementationException, SerializerException {

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");

        ArrayList<Integer> array = new ArrayList<Integer>();
        DeleteDataResult ddr = new DeleteDataResult();

        if (webservice_password.equals(getWsPassword())) {
            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            Query q = session.createQuery("from Ticket where password='" + getPassword() + "'");
            Ticket ticket = (Ticket) q.uniqueResult();

            //if password is incorrect, stop and give error message.
            if (ticket == null) {
                ddr.setErrorMessage("password from this ticket is not found in the database.");
            }
            //Delete, if ticket is retrieved.
            if (ticket != null) {
                //gather study_sampel_assay_ids to be deleted in the expression table
                Iterator it1 = ticket.getStudySampleAssaies().iterator();
                while (it1.hasNext()) {
                    StudySampleAssay ssa = (StudySampleAssay) it1.next();
                    Integer id = ssa.getId();
                    array.add(id);
                }
                //Delete ticket and its connected samples
                Transaction tx = (Transaction) session.beginTransaction();
                session.delete(ticket);
                tx.commit();
                ddr.setMessage("Data from this ticket is deleted.");
            }
            session.close();
        }

        if (webservice_password.equals(getWsPassword()) == false) {
            ddr.setErrorMessage("wrong password for using webservice, change the settings.properties file.");
        }

        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        String text = trans.serializeToString(ddr);
        return text;
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
}
