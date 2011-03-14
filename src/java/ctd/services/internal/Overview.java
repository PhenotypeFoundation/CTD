package ctd.services.internal;

import ctd.services.getTicket;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class Overview {

    private String strOffset;

    public String Overview() {
        String strRet = "";

        if(strOffset==null) {
            strOffset = "0";
        }

        ResourceBundle res = ResourceBundle.getBundle("settings");
        String strGscfHome = res.getString("gscf.baseURL");

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        SQLQuery sql = session.createSQLQuery("SELECT a.study_token, a.X_REF, COUNT(a.id) ,COUNT(b.study_sample_assay_id) FROM study_sample_assay a LEFT OUTER JOIN expression b ON a.id=b.study_sample_assay_id GROUP BY a.id LIMIT "+strOffset+", 20");
        Iterator it2 = sql.list().iterator();
        while (it2.hasNext()) {
            Object[] data = (Object[]) it2.next();
            strRet += "<tr>";
            strRet += "<td><a href='"+strGscfHome+"/study/showByToken/"+(String)data[0]+"'>"+(String)data[0]+"</a></td>";
            strRet += "<td><a href='"+strGscfHome+"/assay/showByToken/"+(String)data[1]+"'>"+(String)data[1]+"</a></td>";
            strRet += "<td>"+data[2].toString()+" ("+data[3].toString()+")</td>";
            strRet += "</tr>";
        }
        session.close();

        return strRet;
    }

    public String getContent() {
        return this.Overview();
    }


    /**
     * @return the strOffset
     */
    public String getOffset() {
        return strOffset;
    }

    /**
     * @param strOffset the strOffset to set
     */
    public void setOffset(String strOffset) {
        this.strOffset = strOffset;
    }
}
