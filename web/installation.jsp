<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<table width="700" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td>
    <div style="display:none"><p class="header1">For programmers</p>
        <p>There are two ways to get started with the clean transcriptome database, the easy way is to use the client library to upload CEL-files and download the normalized expression data. Just install the jar as a library in your java project and you are ready to implement the code provided on the example page. Parameters for the server connection are found in the settings.properties file. Contact the local administrator from the server you want to connect with to fill in these details
        <br />
        </p>
     
      
      <table cellpadding="0" cellspacing="0" width="640" border="0">
        <tr>
          <td bgcolor="#0099CC" class="text_header_white">Client installation</td>
        </tr>
      </table>
      <p class="text_normal"><span class="text_header_black">1.</span> Download the client jar-library from the <a target="content" href="download.jsp">download page</a>.</p>
      <p class="text_normal"><span class="text_header_black">2.</span> Edit the settings.properties file in the jar manually. There you find the webservice location, password and sftp parameters. Look at them carefully and manually edit it for the server you want to use. The default server is nbx13.nugo.org/ctd. (<a target="_blank" href="settings.properties">example</a>) </p> </div>
      <table cellpadding="0" cellspacing="0" width="640" border="0">
        <tr>
          <td bgcolor="#0099CC" class="text_header_white">Installation</td>
        </tr>
      </table>
<p class="header2">TomCat webserver</p>
        <p class="text_normal">If the TomCat server is allready on your system, download the <a target="content" href="download.jsp">war-file</a> and deploy it in the webapps folder of the installation directory. Otherwise, download it and follow the installation instructions on the <a target="_blank" href="http://tomcat.apache.org/">project webpage</a>. Another installation protocol has been tested succesfully on an Ubuntu platform (<a target="_blank" href="http://www.howtogeek.com/howto/linux/installing-tomcat-6-on-ubuntu/">link</a>).<br />
        The war file is automatically deployed into the &quot;nugoctdwebapp&quot; folder. A virtual link should be made to the ctd folder where the client has its default webservice location.<br />
         </p>
      <p class="text_normal"><span class="text_header_black">1.</span> Place the war file into the webapps folder of your Tomcat installation, it is automatically deployed/unpacked.<br />
      <span class="text_header_black">2.</span> Create virtual link:<br />
              cd /usr/local/tomcat/webapps<br />
        ln -s nugoctdwebapp ctd<br />
        <span class="text_header_black">3.</span> For establishing a link with the MySQL database edit the username, password and database name in the hibernate.cfg.xml file. It is located in the WEB_INF/classes folder.<br />
        <span class="text_header_black">4.</span> Also edit the settings.properties file. Fill in the correct parameters for the webservice password (also used by the client), location of the ftp folder, the R-script and the connection parameters of the local MySQL database.</p>
<p><span class="header2">GSCF</span></p>
        <p><span class="text_header_black">1.</span><span class="text_normal"> You will need an instance of the Generic Study Capture Framework. If no instance is available you can download and install it (<a target="_blank" href="https://trac.nbic.nl/gscf/">link</a>).</span><br/></p>
        <p><span class="text_header_black">2.</span><span class="text_normal"> Login as an admin to the GSCF instance and add the CTD module via Admin -> Manage Modules.</span></p>
        <p><span class="text_header_black">3.</span><span class="text_normal"> Make sure that the currect properties are set in properties.settings file located at the client.</span></p>
<p><span class="header2">MySQL server</span>    </p>
        <p><span class="text_header_black">1.</span><span class="text_normal"> Download and install the MySQL server if needed. (<a target="_blank" href="http://www.mysql.com/">link</a>)</span><br/>
        <span class="text_header_black">2.</span><span class="text_normal"> Create the database, named &quot;ctd&quot;, and run the <a target="content" href="download.jsp">sql-script</a> to restore the database tables.</span></p>
<p><span class="header2">R</span></p>
        <p class="text_normal"><span class="text_header_black">1.</span> Allow the user of the TomCat webserver to run <a href="http://manpages.ubuntu.com/manpages/intrepid/man1/Rscript.1.html">Rscript</a>. <br />
        <span class="text_header_black">2.</span> Provide the location of the <a href="download/CleanData.R">CleanData.R</a> and <a href="download/CTD_Combat.R">CTD_Combat.R</a> scripts in the properties file.</p>
<p class="header2">Secure FTP channel.</p>
        <p><span class="text_header_black">1.</span><span class="text_normal"> The Ubuntu distribution of Linux has a standard sftp connection on port 22 for each user account. Create a new user:</span><br />
        </p>
        <p><span class="text_normal">adduser cleandata</span>  <br />
        <span class="text_normal">cd /home/cleandata<br />
        su cleandata<br />
        mkdir data
        <br />
        mkdir clean_data_R
        (Store the Rscripts here.)<br /></span>
          <span class="text_normal">chmod 777 data </span><br />
          <span class="text_normal">chmod 777 clean_data_R</span>    </p>
        <p><span class="text_header_black">2.</span><span class="text_normal"> Ensure that the ftp-host, port number, username, password and remote data-folder are included in the  properties.settings file located at the client. These settings are confidential between the client and the server.</span><br/>
        </p></td>
  </tr>
</table>