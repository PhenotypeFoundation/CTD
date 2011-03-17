package ctd.services;

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import com.skaringa.javaxml.SerializerException;
import ctd.model.Ticket;
import ctd.services.exceptions.*;
import ctd.services.internal.GscfService;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author kerkh010
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class getAssayURL {

    private String strSessionToken;
    private String strAssayToken;


    public String[] getAssayURL() throws NoImplementationException, SerializerException, Exception400BadRequest, Exception500InternalServerError, Exception403Forbidden, Exception401Unauthorized, Exception404ResourceNotFound {

        if(getAssayToken()==null || getSessionToken()==null){
            throw new Exception400BadRequest();
        }

        GscfService objGSCFService = new GscfService();
        String[] strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"isUser",null);
        if(!objGSCFService.isUser(strGSCFRespons[1])) {
            throw new Exception403Forbidden();
        }

        HashMap<String,String> objParam = new HashMap();
        objParam.put("assayToken", strAssayToken);
        strGSCFRespons = objGSCFService.callGSCF(strSessionToken,"getAuthorizationLevel",objParam);
        if (!(objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"isOwner") || objGSCFService.getAuthorizationLevel(strGSCFRespons[1],"canRead"))) {
            throw new Exception401Unauthorized();
        }

        String[] strReturn = new String [2];

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");

        String ftp_username = res.getString("ws.ftp_username");
        String hostname = res.getString("ws.hostname");
        String ftp_folder = res.getString("ws.ftp_folder");

//        //open hibernate connection
//        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
//        Session session = sessionFactory.openSession();
//
//        Ticket ticket = null;
//        Query q = session.createQuery("from Ticket where password='" + getSessionToken() + "'");
//        ticket = (Ticket) q.uniqueResult();
//        session.close();
//
//        String folder = ticket.getFolder();
        String folder = getAssayToken();

        //location ftp folder
        String link = "sftp://" + ftp_username + "@" + hostname + ":" +ftp_folder + folder + "/";

        HashMap<String,String> url = new HashMap<String,String>();

        url.put("url", link);

        if(url.isEmpty()) {
            throw new Exception404ResourceNotFound();
        }

        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        strReturn[0] = "200";
        strReturn[1] = trans.serializeToJsonString(url);

        return strReturn;
    }

    /**
     * @return the strSessionToken
     */
    public String getSessionToken() {
        return strSessionToken;
    }

    /**
     * @param strSessionToken the strSessionToken to set
     */
    public void setSessionToken(String strSessionToken) {
        this.strSessionToken = strSessionToken;
    }

    /**
     * @return the strAssayToken
     */
    public String getAssayToken() {
        return strAssayToken;
    }

    /**
     * @param strAssayToken the strAssayToken to set
     */
    public void setAssayToken(String strAssayToken) {
        this.strAssayToken = strAssayToken;
    }
}
