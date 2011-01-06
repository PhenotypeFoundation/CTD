/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ctd.services;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;
import ctd.model.Chip;
import ctd.ws.model.ChipParameters;
import java.util.ArrayList;
import java.util.Iterator;
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
public class getJsonChips {

    private String wsPassword;

    public String getChips() throws NoImplementationException, SerializerException {

        String message = "";
        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");


        ArrayList<ChipParameters> chips = new ArrayList<ChipParameters>();

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        String password_client = getWsPassword();
        if (webservice_password.equals(password_client)) {
            Query q1 = session.createQuery("from Chip");
            Iterator it1 = q1.list().iterator();

            while (it1.hasNext()) {
                Chip ch = (Chip) it1.next();
                String name = ch.getName();

                ChipParameters cp = new ChipParameters();
                cp.setName(name);

                chips.add(cp);
            }
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
        message = trans.serializeToJsonString(chips);

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
