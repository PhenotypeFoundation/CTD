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
import ctd.ws.model.AssayInfo;
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
public class getJsonExperimentInfo {

    private String ticketPassword;
    

    public String getJsonExperimentInfo() throws NoImplementationException, SerializerException {
        String message = "";
        ArrayList<AssayInfo> array = new ArrayList<AssayInfo>();
        
        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Ticket ticket = null;
        
        Query q1 = session.createQuery("from Ticket where password='" + getTicketPassword() + "'");
        ticket = (Ticket) q1.uniqueResult();

        Iterator it1 = ticket.getStudySampleAssaies().iterator();
        while (it1.hasNext()) {
            AssayInfo ai = new AssayInfo();
            StudySampleAssay ssa = (StudySampleAssay) it1.next();
            String x_ref_name = ssa.getXREF();
            String raw_name = ssa.getNameRawfile();
            Double avg = ssa.getAverage();
            Double std = ssa.getStd();

            ai.setAverage(avg);
            ai.setNameRawfile(raw_name);
            ai.setXREF(x_ref_name);
            ai.setStd(std);

            array.add(ai);
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
        message = trans.serializeToJsonString(array);


        return message;
    }

    /**
     * @return the password
     */
    public String getTicketPassword() {
        return ticketPassword;
    }

    /**
     * @param password the password to set
     */
    public void setTicketPassword(String password) {
        this.ticketPassword = password;
    }

   
}
