/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;
import ctd.model.Ticket;
import ctd.ws.model.TicketClient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author kerkh010
 */
public class getExperiments {

    private String password;

    public String getExperiments() throws SerializerException {


        String message = "";
        ArrayList<TicketClient> tickets = new ArrayList<TicketClient>();
        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");

        String password_client = getPassword();


        if (webservice_password.equals(password_client)) {
            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction tr = session.beginTransaction();
            Query q1 = session.createQuery("from Ticket");
            Iterator it1 = q1.iterate();
            while (it1.hasNext()) {
                Ticket ticket = (Ticket) it1.next();
                TicketClient tc = new TicketClient();
                tc.setTitle(ticket.getTitle());
                tc.setPassword(ticket.getPassword());
                tc.setCtdRef(ticket.getCtdRef());
                tc.setFolder(ticket.getFolder());

                if (ticket.getTitle() != null) {
                    tickets.add(tc);
                }
            }
        }

        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToString(tickets);

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
