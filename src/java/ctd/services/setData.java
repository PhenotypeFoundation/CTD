package ctd.services;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;
import ctd.model.Ticket;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class setData {

    private String ctdRef;
    private String folder;
    private String password;

    public String setData() {
        String strRet = "";

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration() {}.configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tr = session.beginTransaction();

        Ticket objTicket = new Ticket(ctdRef, folder, password);

        session.saveOrUpdate(objTicket);
        session.persist(objTicket);
        tr.commit();
        session.close();


        getCleanData objCommitData = new getCleanData();
        objCommitData.setCTD_REF(ctdRef);
        objCommitData.setPassword(password);
        try {
            strRet = objCommitData.cleanData();
        } catch (Exception e) {
            strRet = "Saving Failed";
            Logger.getLogger(setData.class.getName()).log(Level.SEVERE, "setData ERROR: Internal Service Error");
        }

        return strRet;
    }
}
