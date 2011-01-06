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
import ctd.ws.model.CombatInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
public class getJsonCombatNormalization {

    private String wsPassword;
    private String password_ticket1;
    private String password_ticket2;
    private String password_ticket3;
    private String password_ticket4;
    private String password_ticket5;

    public String getCombatNormalization() throws SerializerException, FileNotFoundException, IOException, NoImplementationException {

        String message = "";
        CombatInfo info = new CombatInfo();

        //init parameters
        ResourceBundle res = ResourceBundle.getBundle("settings");
        String webservice_password = res.getString("ws.password");
        String ftp_root = res.getString("ws.ftp_folder");
        String ftp_username = res.getString("ws.ftp_username");
        String hostname = res.getString("ws.hostname");
        String rscript = res.getString("ws.rscript_combat");

        String password_client = getWsPassword();

        if (webservice_password.equals(password_client)) {

            //open hibernate connection
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();

            ///Get the xrefs of the samples belonging to ticket-experiment 1 and 2.
            Ticket ticket1 = null;
            Ticket ticket2 = null;
            Ticket ticket3 = null;
            Ticket ticket4 = null;
            Ticket ticket5 = null;

            String folder1 = null;
            String folder2 = null;
            String folder3 = null;
            String folder4 = null;
            String folder5 = null;

            String gctfile1 = null;
            String gctfile2 = null;
            String gctfile3 = null;
            String gctfile4 = null;
            String gctfile5 = null;

            String subfolder = "";

            if (getPassword_ticket1() != null) {
                Query q1 = session.createQuery("from Ticket where password='" + getPassword_ticket1() + "'");
                ticket1 = (Ticket) q1.uniqueResult();
                folder1 = ticket1.getFolder();
                gctfile1 = ftp_root + folder1 + "/gctfile_" + folder1 + ".gct";
                subfolder = subfolder + folder1;
            }

            if (getPassword_ticket2() != null) {
                Query q2 = session.createQuery("from Ticket where password='" + getPassword_ticket2() + "'");
                ticket2 = (Ticket) q2.uniqueResult();
                folder2 = ticket2.getFolder();
                gctfile2 = ftp_root + folder2 + "/gctfile_" + folder2 + ".gct";
                subfolder = subfolder + "_" + folder2;
            }

            if (getPassword_ticket3() != null) {
                Query q1 = session.createQuery("from Ticket where password='" + getPassword_ticket3() + "'");
                ticket3 = (Ticket) q1.uniqueResult();
                folder3 = ticket3.getFolder();
                gctfile3 = ftp_root + folder3 + "/gctfile_" + folder3 + ".gct";
                subfolder = subfolder + "_" + folder3;
            }

            if (getPassword_ticket4() != null) {
                Query q1 = session.createQuery("from Ticket where password='" + getPassword_ticket4() + "'");
                ticket4 = (Ticket) q1.uniqueResult();
                folder4 = ticket4.getFolder();
                gctfile4 = ftp_root + folder4 + "/gctfile_" + folder4 + ".gct";
                subfolder = subfolder + "_" + folder4;
            }

            if (getPassword_ticket5() != null) {
                Query q1 = session.createQuery("from Ticket where password='" + getPassword_ticket5() + "'");
                ticket5 = (Ticket) q1.uniqueResult();
                folder5 = ticket5.getFolder();
                gctfile5 = ftp_root + folder5 + "/gctfile_" + folder5 + ".gct";
                subfolder = subfolder + "_" + folder5;
            }

            //Create subfolder
            //create subdir
            String new_folder = ftp_root + subfolder;
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


            //Copy all gct-files to the combat folder.
            if (folder1 != null) {
                String command = "cp " + gctfile1 + " " + new_folder;
                child = Runtime.getRuntime().exec(command);
            }
            if (folder2 != null) {
                String command = "cp " + gctfile2 + " " + new_folder;
                child = Runtime.getRuntime().exec(command);
            }
            if (folder3 != null) {
                String command = "cp " + gctfile3 + " " + new_folder;
                child = Runtime.getRuntime().exec(command);
            }
            if (folder4 != null) {
                String command = "cp " + gctfile4 + " " + new_folder;
                child = Runtime.getRuntime().exec(command);
            }
            if (folder5 != null) {
                String command = "cp " + gctfile5 + " " + new_folder;
                child = Runtime.getRuntime().exec(command);
            }

            //////////////////////////////////////////////////////////////////
            //Do a system call to normalize with ComBAT.
            String args = "Rscript --vanilla " + rscript + " -w" + new_folder+" -o"+new_folder+"/Combat.out";
            Process p = Runtime.getRuntime().exec(args);

            String link = "sftp://" + ftp_username + "@" + hostname + ":" + new_folder+"/Combat.out.gct";
            info.setLocationFtpFile(link);

            String command4 = "chown cleandata " + new_folder+"/Combat.out.gct";
            child = Runtime.getRuntime().exec(command4);

            File f = new File(new_folder+"/Combat.out.gct");
            while (f.exists()==false){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(getJsonCombatNormalization.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            String command5 = "chmod 755 " + new_folder+"/Combat.out.gct";
            child = Runtime.getRuntime().exec(command5);

            //close hibernate.
            session.close();
        }

        ////////////////////
        //SKARINGA
        ObjectTransformer trans = null;
        try {
            trans = ObjectTransformerFactory.getInstance().getImplementation();
        } catch (NoImplementationException ex) {
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, null, ex);
        }
        message = trans.serializeToJsonString(info);

        return message;

    }

    /**
     * @return the password_ws
     */
    public String getWsPassword() {
        return wsPassword;
    }

    /**
     * @param password_ws the password_ws to set
     */
    public void setWsPassword(String password) {
        this.wsPassword = password;
    }

    /**
     * @return the password_ticket1
     */
    public String getPassword_ticket1() {
        return password_ticket1;
    }

    /**
     * @param password_ticket1 the password_ticket1 to set
     */
    public void setPassword_ticket1(String password_ticket1) {
        this.password_ticket1 = password_ticket1;
    }

    /**
     * @return the password_ticket2
     */
    public String getPassword_ticket2() {
        return password_ticket2;
    }

    /**
     * @param password_ticket2 the password_ticket2 to set
     */
    public void setPassword_ticket2(String password_ticket2) {
        this.password_ticket2 = password_ticket2;
    }

    /**
     * @return the password_ticket3
     */
    public String getPassword_ticket3() {
        return password_ticket3;
    }

    /**
     * @param password_ticket3 the password_ticket3 to set
     */
    public void setPassword_ticket3(String password_ticket3) {
        this.password_ticket3 = password_ticket3;
    }

    /**
     * @return the password_ticket4
     */
    public String getPassword_ticket4() {
        return password_ticket4;
    }

    /**
     * @param password_ticket4 the password_ticket4 to set
     */
    public void setPassword_ticket4(String password_ticket4) {
        this.password_ticket4 = password_ticket4;
    }

    /**
     * @return the password_ticket5
     */
    public String getPassword_ticket5() {
        return password_ticket5;
    }

    /**
     * @param password_ticket5 the password_ticket5 to set
     */
    public void setPassword_ticket5(String password_ticket5) {
        this.password_ticket5 = password_ticket5;
    }
}
