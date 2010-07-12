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
import ctd.model.Ticket;

import ctd.ws.model.TicketClient;
import java.io.File;

import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.UUID;
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
public class getTicket {

    private String password;

    public String getTicket() throws SerializerException {

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");

        String webservice_password = res.getString("ws.password");

        String ftp_folder = res.getString("ws.ftp_folder");
        String prefix_ctd_reference = res.getString("ws.prefix_ticket_reference");
        String prefix_ftp_subfolder = res.getString("ws.prefix_ftp_subfolders");

        TicketClient ticket_client = new TicketClient();
        String new_folder = null;
        String message = "";

        String password_client = getPassword();

        if (webservice_password.equals(password_client)) {
            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction tr = session.beginTransaction();

            Integer id = 1;
            SQLQuery q1 = session.createSQLQuery("Select MAX(id) FROM ticket");
            Iterator it1 = q1.list().iterator();
            while (it1.hasNext()) {
                id = (Integer) it1.next();
                if (id != null) {
                    id++;
                }
                if (id == null) {
                    id = 1;
                }
            }

            ///Get unique password for processing the submitted celfiles
            UUID uuid =null;

            ///check if this uuid is not allready used in this database
            Boolean check = true;
            while (check) {
                uuid = UUID.randomUUID();
                Query q2 = session.createQuery("from Ticket where password='" + uuid.toString()+"'");
                if (q2.list().isEmpty()){
                    check = false;
                }
            }

            //String db_password = password_client+"_"+webservice_password;//uuid.toString();
            String db_password = uuid.toString();
            //ctd reference id
            String ctd_ref = prefix_ctd_reference + String.valueOf(id);
            //ctd ftp subfolder
            String ctd_ftp_folder = prefix_ftp_subfolder + String.valueOf(id);

            //create subdir
            new_folder = ftp_folder + ctd_ftp_folder;
            boolean success = (new File(new_folder)).mkdir();
            //change owner from root to cleandata for this new directory

            //Adjust permissions
            String command1 = "chown cleandata " + new_folder;
            String command2 = "chgrp cleandata " + new_folder;
            String command3 = "chmod 777 " + new_folder;
            Process child;
            try {
                child = Runtime.getRuntime().exec(command1);
                child = Runtime.getRuntime().exec(command2);
                child = Runtime.getRuntime().exec(command3);
            } catch (IOException ex) {
                Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
            }

            Ticket ticket = new Ticket();
            ticket.setFolder(ctd_ftp_folder);
            ticket.setPassword(db_password);
            ticket.setCtdRef(ctd_ref);
            ticket.setClosed("no");

            ticket_client.setCtdRef(ctd_ref);
            ticket_client.setFolder(ctd_ftp_folder);
            ticket_client.setPassword(db_password);

            session.save(ticket);
            session.persist(ticket);
            tr.commit();

            session.close();
            sessionFactory.close();
            session = null;
            sessionFactory = null;

        }
        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToString(ticket_client);

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
}
