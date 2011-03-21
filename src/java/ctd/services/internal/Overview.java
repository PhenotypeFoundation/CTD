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
 * This class is used to generate the HTML formatted rows of the overview page
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class Overview {

    private String strOffset;

    public String Overview() {
        String strRet = "";

        // offset is not yet implemented in overview.jsp, but it can be used to
        // split the overview into multiple pages.
        // Now only the first 20 results are given
        if(getOffset()!=null) {
            strOffset = " LIMIT "+strOffset +",20";
        } else {
            strOffset = "";
        }

        // het gscf url in order to be able to refer to study and assay details
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String strGscfHome = res.getString("gscf.baseURL");

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        // Select study_tokens, assay_tokens, the number of rows in study_sample_assay
        // with these 2 keys and the number of rows in expression with these keys
        SQLQuery sql = session.createSQLQuery("SELECT a.study_token, a.X_REF, COUNT(a.id) ,COUNT(b.study_sample_assay_id) FROM study_sample_assay a LEFT OUTER JOIN expression b ON a.id=b.study_sample_assay_id GROUP BY a.id"+strOffset);
        Iterator it2 = sql.list().iterator();
        int iRownr = 1;
        while (it2.hasNext()) {
            Object[] data = (Object[]) it2.next();

            // strClass is used to give even and odd rows a different background color
            String strClass = "odd";
            if(iRownr%2==0) strClass = "even";
            iRownr++;

            strRet += "<tr class=\""+strClass+"\">\n";
            strRet += "\t<td class=\"tdoverview\"><a href='"+strGscfHome+"/study/showByToken/"+(String)data[0]+"'>"+(String)data[0]+"</a></td>\n";
            strRet += "\t<td class=\"tdoverview\"><a href='"+strGscfHome+"/assay/showByToken/"+(String)data[1]+"'>"+(String)data[1]+"</a></td>\n";
            strRet += "\t<td class=\"tdoverview\">"+data[2].toString()+" ("+data[3].toString()+")</td>\n";
            strRet += "</tr>\n";
        }
        session.close();

        return strRet;
    }

     /**
     * This function calls the constructor Overview() of this class and returns the result
     * @return a String containing the table rows
     */
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
