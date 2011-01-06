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
import java.util.HashMap;
import java.util.ResourceBundle;
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
public class getAssayURL {

    private String password;
    

    public String getAssayURL() throws NoImplementationException, SerializerException {

        String message = "";

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");

        String webservice_password = res.getString("ws.password");
        String ftp_username = res.getString("ws.ftp_username");
        String hostname = res.getString("ws.hostname");
        String ftp_folder = res.getString("ws.ftp_folder");

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Ticket ticket = null;
        Integer ssa_id = null;
        Query q = session.createQuery("from Ticket where password='" + getPassword() + "'");
        ticket = (Ticket) q.uniqueResult();
        session.close();

        String folder = ticket.getFolder();

        //location ftp folder
        String link = "sftp://" + ftp_username + "@" + hostname + ":" +ftp_folder + folder + "/";

        HashMap<String,String> url = new HashMap<String,String>();

        url.put("url", link);

        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToJsonString(url);

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
