<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
<link href="style.css" rel="stylesheet" type="text/css">
<style type="text/css">
<!--
.header1 {
	font-weight: bold;
	font-size: 18px;
	font-family: Verdana, Geneva, sans-serif;
}
.text_normal {
	font-family: Verdana, Geneva, sans-serif;
	font-size: 14px;
	font-style: normal;
	line-height: normal;
	font-weight: bold;
	color: #000;
}
.text_normal {
	font-weight: normal;
}
.header2 {
	font-weight: bold;
	font-family: Verdana, Geneva, sans-serif;
	font-size: 14px;
}
.prog_style1 {
	color: #090;
}
.prog_style2 {
	color: #F93;
}
-->
</style>
</head>

<body>
<table width="700" border="0" cellpadding="0" cellspacing="20">
  <tr>
    <td><p class="header1">For programmers</p>
      <p class="text_normal">There are two ways to get started with the clean transcriptome database, the easy way is to use the client library to upload CEL-files and download the normalized expression data. Just install the jar as a library in your java project and you are ready to implement the code provided on the example page. Parameters for the server connection are found in the settings.properties file. Contact the local administrator from the server you want to connect with to fill in these details.<br />
        For installing the server one requiers a TomCat server, a MySQL database, a R installation for statistical computing and a sftp channel for sending files over the internet. <br />
      </p>
      <table cellpadding="0" cellspacing="0" width="640" border="0">
        <tr>
          <td bgcolor="#0099CC" class="text_header_white">Client installation</td>
        </tr>
      </table>
      <p class="text_normal">1. Download the client jar-library from the <a target="content" href="download.jsp">download page</a>.</p>
      <p class="text_normal">2. Edit the settings.properties file in the jar manually. There you find the webservice location, password and sftp parameters. Look at them carefully and manually edit it for the server you want to use. The default server is nbx13.nugo.org/ctd. (<a target="_blank" href="settings.properties">example</a>) </p>
      <table cellpadding="0" cellspacing="0" width="640" border="0">
        <tr>
          <td bgcolor="#0099CC" class="text_header_white">Server installation</td>
        </tr>
      </table>
      <p class="header2">TomCat webserver</p>
    <p class="text_normal">Use an existing TomCat server on your system, download the <a target="content" href="download.jsp">war-file</a> and deploy it in the webapps folder of the installation directory. Otherwise download it and follow the installation instructions on the <a target="_blank" href="http://tomcat.apache.org/">project webpage</a>. Another installation protocol has been tested succesfully on an Ubuntu platform (<a target="_blank" href="http://www.howtogeek.com/howto/linux/installing-tomcat-6-on-ubuntu/">link</a>).<br />
      The war file is automatically deployed into the &quot;nugoctdwebapp&quot; folder. A virtual link should be made to the ctd folder where the client has its default webservice location.<br />
      Create virtual link:</p>
    <p class="text_normal">cd /usr/local/tomcat/webapps<br />
ln -s nugoctdwebapp ctd<br />
    </p>
    <p><span class="header2">MySQL server</span>    </p>
    <p><span class="text_normal">1. Download and install the MySQL server if needed. (<a target="_blank" href="http://www.mysql.com/">link</a>)</span><br/>
      <span class="text_normal">2. Create the database, named &quot;ctd&quot;, and run the <a target="content" href="download.jsp">sql-script</a> to restore the database tables.</span></p>
    <p><span class="header2">R</span></p>
    <p class="text_normal">1. Allow the webserver to run <a href="http://manpages.ubuntu.com/manpages/intrepid/man1/Rscript.1.html">Rscript</a>. <br />
      2. 
      Provide the location of the <a href="download/CleanData.R">CleanData.R</a> script in the properties.settings file. It is located in the webapps subdirectory of your TomCat installation.</p>
    <p class="header2">Secure FTP channel.</p>
    <p><span class="text_normal">1.The Ubuntu distribution of Linux allready has a standard sftp connection on port 22 for each user account. Create a new user:</span><br />
      </p>
    <p><span class="text_normal">adduser cleandata</span>  <br />
      <span class="text_normal">cd /home/cleandata<br />
      su cleandata<br />
      mkdir data
      </span><br />
      <span class="text_normal">chmod 777 data </span></p>
    <p><span class="text_normal">2. Ensure that the ftp-host, port number, username, password and remote data-folder are included in the  properties.settings file located at the client. These settings are confidential.</span><br/>
    </p></td>
  </tr>
</table>
</body>
</html>