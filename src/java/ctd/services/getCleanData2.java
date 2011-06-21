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

import com.skaringa.javaxml.NoImplementationException;
import com.skaringa.javaxml.ObjectTransformer;
import com.skaringa.javaxml.ObjectTransformerFactory;
import ctd.model.Chip;
import ctd.model.ChipAnnotation;
import ctd.model.StudySampleAssay;
import ctd.model.Ticket;

import ctd.statistics.Statistics;
import ctd.ws.model.CleanDataResult;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileFilter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import org.apache.commons.io.*;

/**
 * @author kerkh010
 * @author Tjeerd van Dijk
 * @author Taco Steemers
 */
public class getCleanData2 {

    private String password;
    private String CTD_REF;
    private String studyToken;
    private HashMap<String, String> objMatches;

    public String cleanData(){
        String message = "";
        String timestamp = new java.util.Date().getTime()+"";


        try{
            CleanDataResult result = new CleanDataResult();
            String error_message = "";
            //get parameters.
            ResourceBundle res = ResourceBundle.getBundle("settings");
            ResourceBundle cdf_list = ResourceBundle.getBundle("cdf");
            //Base directory ftp folder: Here the temporary subfolders are found for each set of CEL-files, and the final assaytoken-based folder.
            String ftp_folder = res.getString("ws.upload_folder");
            String rscript_cleandata = res.getString("ws.rscript_cleandata");
            String rscript = res.getString("ws.rscript");
            //db
            String db_username = res.getString("db.username");
            String db_password = res.getString("db.password");
            String db_database = res.getString("db.database");

            //retrieve the information on the assignment from the database
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction tr = session.beginTransaction();
            Query q = session.createQuery("from Ticket where password='" + getPassword() + "' AND ctd_REF='" + getCTD_REF() + "'");
            Ticket ticket = null;
            String closed = "";
            if (q.list().size() != 0) {
                ticket = (Ticket) q.list().get(0);
                closed = ticket.getClosed();
            }
            if (ticket == null) {
                error_message = "Ticket password and CTD_REF don't match.";
            }
            if (closed.equals("yes")) {
                error_message = "Ticket is already used for normalization of these CEL-files.";
                ticket = null;
            }

            if (ticket != null) {
                //get the folder
                String folder = ticket.getFolder();
                String zip_folder = ftp_folder + folder;
                //get contents
                File dir = new File(zip_folder);

                //find the zip file.
                File[] files = dir.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.isFile();
                    }
                });                
                String cel_zip_file = "";
                String zip_file = "";
                String gct_file = "";
                for (int i = 0; i < files.length; i++) {
                    String file = files[i].getName();
                    if (file.contains("zip")) {
                        // Add the timestamp to the zip
                        files[i].renameTo(new File(zip_folder+"/"+timestamp+"_zip.zip"));
                        file = timestamp+"_zip.zip";
                        
                        cel_zip_file = file;
                        zip_file = zip_folder + "/" + cel_zip_file;
                        gct_file = zip_folder + "/" + timestamp + "_gctfile";
                    }
                }
                Process p3 = Runtime.getRuntime().exec("chmod 777 " + zip_file);

                //////////////////////////////////////////////////////////////////
                //Do a system call to normalize. R. (zip_folder zip_file gct_file rscript)
                String args = rscript+" --verbose --vanilla " + rscript_cleandata + " -i" + zip_file + " -o" + gct_file + " -w" + zip_folder;
                Logger.getLogger(getTicket.class.getName()).log(Level.INFO, timestamp+": Running: "+args);
                Process p = Runtime.getRuntime().exec(args);
                // Check if CEL files are unzipped allready
                // This is done by checking every 5 seconds for the existence of a .chip file
                // This is a bad way of doing this, in future versions of CTD
                // the output of the R scripts should be parsed
                boolean do_loop = true;
                while (do_loop) {
                    File dir2 = new File(zip_folder);
                    String[] files2 = dir2.list();
                    //Check if CEL files are allready there
                    for (int i = 0; i < files2.length; i++) {
                        String file = files2[i];
                        if (file.endsWith("chip")) {
                            do_loop = false;
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(getCleanData.class.getName()).log(Level.SEVERE, null, timestamp+": "+ex);
                            }
                        }
                    }
                }
                Logger.getLogger(getTicket.class.getName()).log(Level.INFO, timestamp+": rscript has finished.");
                File dir2 = new File(zip_folder);
                String[] files2 = dir2.list();
                String chip_file = "";
                String chip_file_db = "";
                ArrayList<String> unziped_files = new ArrayList<String>();
                for (int i = 0; i < files2.length; i++) {
                    String file = files2[i];
                    if (file.endsWith("CEL")) {
                        unziped_files.add(file);
                    }
                    if (file.endsWith("chip")) {
                        chip_file = file;
                        chip_file_db = chip_file.split("_CDF_")[1];
                        File fileFile = new File(chip_file);
                        fileFile.renameTo(new File(zip_folder+"/"+chip_file_db)); //Making the file correspond to the database entry. Duplicates can be safely overwritten, and will be.
                    }
                }

                //Check if all CEL files are derived from the same chip.
                //This is essential for normalization.
                //initiate check hashmap. This map contains all the unique chip definition file names. There should be only one per analysis.
                ArrayList<StudySampleAssay> map = new ArrayList<StudySampleAssay>();
                for (int i = 0; i < unziped_files.size(); i++) {
                    String cel_file = unziped_files.get(i);

                    StudySampleAssay ssa = new StudySampleAssay();
                    // Open the file that is the first
                    // command line parameter
                    //String cel_file_path = zip_folder + "/" + cel_file;
                    String name = cel_file;
                    ssa.setNameRawfile(name);
                    ssa.setXREF(getCTD_REF());
                    map.add(ssa);
                }
                ticket.getStudySampleAssaies().addAll(map);
                session.saveOrUpdate(ticket);
                session.persist(ticket);
                tr.commit();
                session.close();

                //Storage chip definition file (CDF), creation gct file and database storage.
                SessionFactory sessionFactory1 = new Configuration().configure().buildSessionFactory();
                Session session1 = sessionFactory1.openSession();
                
                //check if cdf (chip definition file) is allready stored, if not, store it.
                List<ChipAnnotation> chip_annotation = null;
                Query q2 = session1.createQuery("from Chip Where Name='" + chip_file_db + "'");
                if (q2.uniqueResult() != null) {
                    Chip chip = (Chip) q2.list().get(0);
                    chip_annotation = chip.getChipAnnotation();
                }
                if (q2.uniqueResult() == null) {
                    //Add this chip and its annotation
                    Chip chip_new = new Chip();
                    chip_new.setName(chip_file_db);

                    //read chip file
                    String chip_file_path = zip_folder + "/" + chip_file;
                    chip_annotation = readChip(chip_file_path);

                    //Store the whole
                    chip_new.getChipAnnotation().addAll(chip_annotation);

                    Transaction tr1 = session1.beginTransaction();
                    session1.save(chip_new);
                    session1.persist(chip_new);
                    tr1.commit();
                    session1.close();
                }

                //create the temp file for storage of the data_insert file.
                String data_file = zip_folder + "/expression.txt";
                FileOutputStream out = null;
                PrintStream pr = null;
                out = new FileOutputStream(data_file);
                pr = new PrintStream(out);

                //create array data input file for the database table, find correct foreign keys.
                //get the study_sample_assay id and the probeset ids.
                SessionFactory sessionFactory2 = new Configuration().configure().buildSessionFactory();
                Session session2 = sessionFactory2.openSession();

                //Get the cip_annotation_id
                Query q3 = session2.createQuery("from Chip Where Name='" + chip_file_db + "'");
                Chip chip = (Chip) q3.list().get(0);
                chip_annotation = chip.getChipAnnotation();
                Iterator it2 = chip_annotation.iterator();
                //for speed, put the chip annotation id in a hashmap
                HashMap<String, String> chip_annotation_ids = new HashMap<String, String>();
                while (it2.hasNext()) {
                    ChipAnnotation ca = (ChipAnnotation) it2.next();
                    String id = ca.getId().toString();
                    String ps = ca.getProbeset();
                    chip_annotation_ids.put(ps, id);
                }

                //Create the .gct-files
                try {

                    Query qt = session2.createQuery("from Ticket where password='" + getPassword() + "' AND ctd_REF='" + getCTD_REF() + "'");

                    ticket = null;

                    if (qt.list().size() != 0) {
                        ticket = (Ticket) qt.list().get(0);
                    }

                    Iterator it3 = ticket.getStudySampleAssaies().iterator();
                    while (it3.hasNext()) {
                        StudySampleAssay ssa = (StudySampleAssay) it3.next();
                        String name_raw_file = ssa.getNameRawfile();
                        String sampleToken = getSampletokens().get(name_raw_file);
                        String ssa_id = ssa.getId().toString();
                        error_message = error_message + name_raw_file;

                        String gct_file_generated = gct_file + ".gct";
                        ArrayList<Double> values = writeFile(pr, chip_annotation_ids, ssa_id, gct_file_generated, name_raw_file.replaceAll(".CEL", ""));

                        Statistics stat = new Statistics();
                        stat.setData(values);
                        Double average = stat.getAverage();
                        Double std = stat.getSTD();

                        ssa.setXREF(getCTD_REF());
                        ssa.setAverage(average);
                        ssa.setStudyToken(getStudytoken());
                        ssa.setSampleToken(sampleToken);
                        ssa.setStd(std);
                    }


                } catch (IOException e) {
                    Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, timestamp+": ERROR IN getCleanData2: "+e.getMessage()+"  "+e.getLocalizedMessage());
                }
                pr.close();
                out.close();

                //update ticket
                Transaction tr2 = session2.beginTransaction();
                session2.update(ticket);
                session2.persist(ticket);
                tr2.commit();
                session2.close();

                //import the data into the database
                String u = "--user=" + db_username;
                String passw = "--password=" + db_password;
                String[] commands = new String[]{"mysqlimport", u, passw, "--local", db_database,data_file};
                Process p4 = Runtime.getRuntime().exec(commands);
                message = message + " RMA and GRSN on the CEL-files is done, data is stored.";

                //close the ticket when finished, normalization can only be performed once by the client.
                CloseTicket();
                
                //Remove zip and data file (expression.txt)
                File fileFolderOld = new File(zip_folder);
                File fileFolderDest = new File(res.getString("ws.upload_folder")+getCTD_REF());
                File[] listOfFiles = fileFolderOld.listFiles();
                for(int i = 0; i < listOfFiles.length; i++){
                    if(listOfFiles[i].getPath().toLowerCase().endsWith(".zip") || listOfFiles[i].getPath().toLowerCase().endsWith("expression.txt")){
                        try{
                            listOfFiles[i].delete();
                        } catch(Exception e) {
                            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, timestamp+": ERROR IN getCleanData2 (try to delete): "+e.toString());
                        }
                    } else {
                        try{
                            FileUtils.copyFileToDirectory(listOfFiles[i],fileFolderDest,false);
                            listOfFiles[i].delete();
                        } catch(Exception e) {
                            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, timestamp+": ERROR IN getCleanData2 (try to copy): "+e.toString());
                        }
                    }
                }

                // Remove temporary folder
                try{
                    fileFolderOld.delete();
                } catch(Exception e){
                    Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, timestamp+": ERROR IN getCleanData2: "+e.toString());
                }

                // --------------------------------------------
                // This piece of code is added in order to cleanup all the files
                // of aborted upload procedures. It checks for these old folders
                // (more than a day old and a temporaty name (which is just a number
                // from 1 upwards. It is assumed that a temporary folder has a
                // name shorter than 10 chars) and removes these files and folders
                File folderData = new File(res.getString("ws.upload_folder"));
                long lngTimestamp = new java.util.Date().getTime();
                listOfFiles = folderData.listFiles();
                for(int i=0; i<listOfFiles.length; i++) {
                    if(listOfFiles[i].lastModified()<(lngTimestamp-10000) && listOfFiles[i].getName().length()<10) {
                        // This folder is more than a day old
                        // We know it is a temporary folder because the name is less than 10 chars long
                        File[] lstDelete = listOfFiles[i].listFiles();
                        for(int j=0; j<lstDelete.length; j++) {
                            // Delete all content of the old folder
                            lstDelete[j].delete();
                        }
                        // Delete the old folder
                        if(!listOfFiles[i].delete()) {
                            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "delSample(): Folder deletion failed: "+listOfFiles[i].getName());
                        }
                    }
                }
                // --------------------------------------------
            }

            // set the messages of the response
            result.setErrorMessage(error_message);
            result.setMessage(message);

            // Use SKARINGA in order to create the JSON response
            ObjectTransformer trans = null;
            try {
                trans = ObjectTransformerFactory.getInstance().getImplementation();
                message = trans.serializeToString(result);
            } catch (NoImplementationException ex) {
                Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "SKARINGA ERROR IN getCleanData2: "+ex.getLocalizedMessage());
            }
            
        } catch(Exception e){
            Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, timestamp+": ERROR IN getCleanData2: "+e.toString());
        }
        return message;
    }

    private void CloseTicket() {

        //open hibernate connection
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        Query q = session.createQuery("from Ticket where password='" + getPassword() + "' AND ctd_REF='" + getCTD_REF() + "'");

        Ticket ticket = null;

        if (q.list().size() != 0) {
            ticket = (Ticket) q.list().get(0);
            ticket.setClosed("yes");
        }

        Transaction tr = session.beginTransaction();
        session.saveOrUpdate(ticket);
        session.persist(ticket);
        tr.commit();
        session.close();

    }

    private ArrayList<ChipAnnotation> readChip(String file) throws FileNotFoundException, IOException {
        ArrayList<ChipAnnotation> ann_list = new ArrayList<ChipAnnotation>();
        BufferedReader input = new BufferedReader(new FileReader(file));

        int probe_set_id_index = 0;
        int gene_symbol_index = 0;
        int gene_title_index = 0;

        //read the header
        String line = null;
        line =
                input.readLine();
        String[] columns = line.split("\t");
        for (int i = 0; i <
                columns.length; i++) {
            String name = columns[i];
            if (name.equals("Probe Set Id")) {
                probe_set_id_index = i;
            }

            if (name.equals("Gene Symbol")) {
                gene_symbol_index = i;
            }

            if (name.equals("Gene Title")) {
                gene_title_index = i;
            }

        }

        String probeset = null;
        String gene_symbol = null;
        String gene_description = null;

        int count = 0;
        while ((line = input.readLine()) != null) {
            columns = line.split("\t");
            for (int j = 0; j <
                    columns.length; j++) {
                if (probe_set_id_index == j) {
                    probeset = columns[j];
                }

                if (gene_symbol_index == j) {
                    gene_symbol = columns[j];
                }

                if (gene_title_index == j) {
                    gene_description = columns[j];
                }

            }

            //Extend the chip object with ChipProbeSet
            ChipAnnotation chip_annotation = new ChipAnnotation();

            String accession = probeset.replaceAll("_at", "");
            if (gene_symbol.equals("NA") == false) {
                chip_annotation.setGeneSymbol(gene_symbol);
            }

            chip_annotation.setProbeset(probeset);
            chip_annotation.setGeneAccession(accession);
            if (gene_description.equals("NA") == false) {
                chip_annotation.setGeneAnnotation(gene_description);
            }

            count++;
            ann_list.add(chip_annotation);
        }

        input.close();
        return ann_list;
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
     * @return the CTD_REF
     */
    public String getCTD_REF() {
        return CTD_REF;
    }

    /**
     * @param CTD_REF the CTD_REF to set
     */
    public void setCTD_REF(String CTD_REF) {
        this.CTD_REF = CTD_REF;
    }

    private ArrayList<Double> writeFile(PrintStream pr, HashMap<String, String> chip_annotation_ids, String ssa_id, String gct_file_generated, String name_raw_file) throws FileNotFoundException, IOException {

        BufferedReader input = new BufferedReader(new FileReader(gct_file_generated));
        String line;

        Integer expression_column = null;
        Integer name_column = null;
        Integer description_column = null;
        Boolean do_header = false;
        Boolean do_data = false;
        Boolean start = false;
        ArrayList<Double> values = new ArrayList<Double>();

        while ((line = input.readLine()) != null) {

            String[] columns = line.split("\t");
            if (columns[0].equals("Name")) {
                do_header = true;
                name_column = 0;
            }

            if (do_header) {
                for (int i = 0; i <columns.length; i++) {
                    String header = columns[i].trim();
                    String xx = name_raw_file + ".CEL";
                    if (header.equals(xx)) {
                        expression_column = i;
                        do_data =
                                true;
                    }

//                    if (header.equals("Name")) {
//                        name_column = i;
//                    }
                }
            }
            do_header = false;

            if (start) {
                String probesetid = columns[name_column];

                Double value = Double.valueOf(columns[expression_column]);

                values.add(value);
                String chip_annotation_id = chip_annotation_ids.get(probesetid);
                String expr_line = ssa_id + "\t" + chip_annotation_id + "\t" + value.toString();
                pr.println(expr_line);
            }

            if (do_data) {
                start = true;
            }

        }
        return values;
    }

    /**
     * @param studyToken the studyToken to set
     */
    public void setStudytoken(String studyToken) {
        this.studyToken = studyToken;
    }

    /**
    * @return the studyToken
    */
    public String getStudytoken() {
        return studyToken;
    }

    /**
     * @param objMatches the objMatches to set
     */
    public void setSampletokens(HashMap<String, String> objMatches) {
        this.objMatches = objMatches;
    }

    /**
    * @return the objMatches
    */
    public HashMap<String, String> getSampletokens() {
        return objMatches;
    }
}
