/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ctd.services;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;
import ctd.model.Protocol;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author kerkh010
 */
public class getProtocols {


    private String wsPassword;

    public String getProtocols() throws NoImplementationException, SerializerException{
        String message = "";
        HashMap<String,String> protocols = new HashMap<String,String>();

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Query q1 = session.createQuery("from Protocol");
        Iterator it1 = q1.iterate();
        while (it1.hasNext()){
            Protocol pr = (Protocol) it1.next();
            String name = pr.getName();
            String description = pr.getDescription();
            protocols.put(name, description);
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
        message = trans.serializeToString(protocols);

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


}
