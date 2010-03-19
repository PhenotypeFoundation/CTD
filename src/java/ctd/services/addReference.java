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

import ctd.model.StudySampleAssay;
import ctd.model.Ticket;
import java.util.Iterator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author kerkh010
 */
public class addReference {
    private String password;
    private String name_RAWFILE;
    private String reference;

    /**
     *
     * @return message
     */
    public String addReference(){
        String message = "";
        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Query q = session.createQuery("from Ticket where password='"+getPassword()+"'");
        Ticket ticket = (Ticket) q.uniqueResult();
        String ticket_id = ticket.getId().toString();

        Iterator it1 = ticket.getStudySampleAssaies().iterator();
        while (it1.hasNext()){
            StudySampleAssay ssa = (StudySampleAssay) it1.next();
            String name = ssa.getNameRawfile();
            name = name.replace(".CEL", "");
            if (name.equals(getName_RAWFILE())){
                ssa.setXREF(getReference());
            }
        }
        
        Transaction tr = session.beginTransaction();
        session.saveOrUpdate(ticket);
        session.persist(ticket);
        tr.commit();
        session.close();

        message = getReference() +" is added.";

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
     * @return the name_RAWFILE
     */
    public String getName_RAWFILE() {
        return name_RAWFILE;
    }

    /**
     * @param name_RAWFILE the name_RAWFILE to set
     */
    public void setName_RAWFILE(String name_RAWFILE) {
        this.name_RAWFILE = name_RAWFILE;
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
