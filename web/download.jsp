<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<jsp:useBean id="query" scope="session" class="ctd.services.internal.Query"/>
<jsp:setProperty name="query" property="*"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
<link href="style.css" rel="stylesheet" type="text/css">
</head>
<body>
<table width="700" border="0" cellpadding="0" cellspacing="20">
  <tr>
    <td><table cellpadding="0" cellspacing="0" width="640" border="0">
        <tr>
          <td bgcolor="#0099CC" class="text_header_white">Download </td>
        </tr>
      </table>
      <p class="text_header_black">Release: Version 1.0</p>
      <table width="600" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="100" class="text_normal">Download</td>
          <td width="250">&nbsp;</td>
          <td width="250" class="text_normal"> &nbsp;&nbsp;&nbsp;&nbsp;Notes</td>
        </tr>
      </table>
      <table width="600" border="1" bordercolor="#CCCCCC" cellspacing="0" cellpadding="0">
        <tr>
          <td width="100" class="text_normal">JAR File:</td>
          <td width="250"><a target="_blank" href="download/nugoctdclient.jar">nugoctdclient</a></td>
          <td width="250">The Client library </td>
        </tr>
        <tr>
          <td class="text_normal">WAR File:</td>
          <td><a target="_blank" href="download/nugoctdwebapp.war">nugoctdwebapp</a></td>
          <td>The Server war file</td>
        </tr>
        <tr>
          <td class="text_normal">R script:</td>
          <td><a href="download/CleanData.R">CleanData.R</a></td>
          <td>The R-script for RMA and GRSN</td>
        </tr>
        <tr>
          <td class="text_normal">&nbsp;</td>
          <td><a href="download/CTD_Combat.R">CTD_Combat.R</a></td>
          <td>Script for combining datasets </td>
        </tr>
        <tr>
          <td class="text_normal">SQL File:</td>
          <td><a href="download/ctd_v1.sql">ctd_v1.sql</a></td>
          <td>sql backup file</td>
        </tr>
      </table>
      <p class="text_header_black"><span class="text_normal">To check out the latest code, use this checkout command:</span><br />
        <span class="text_normal">svn checkout https://gforge.nbic.nl/svn/nugoctdclient</span><br/>
      <span class="text_normal">svn checkout https://gforge.nbic.nl/svn/nugoctdwebapp</span></p>
      <table cellpadding="0" cellspacing="0" width="640" border="0">
        <tr>
          <td bgcolor="#0099CC" class="text_header_white">Data Links </td>
        </tr>
      </table>
      <br />
      <span class="text_header_white">(<a target="_blank" href="http://winscp.net">WinSCP is needed for downloading</a>)<br />
      </span><br />
      <table width="600" border="1" bordercolor="#CCCCCC" cellspacing="0" cellpadding="0">
        <tr>
          <td width="70" class="text_normal"><span class="text_header_black">Id</span></td>
          <td width="480" class="text_header_black">Title</td>
          <td width="50" class="text_header_black">Link</td>
        </tr>
        <%
         String message = query.getDownloadData();
         out.println(message);
        %>
      </table>
      <p class="text_header_black"><br/>
        <br/>
    </p></td>
  </tr>
</table>
</body>
</html>