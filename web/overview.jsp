<%-- 
    Document   : overview
    Created on : 14-mrt-2011, 12:23:55
    Author     : Tjeerd van Dijk and Taco Steemers
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="overview" scope="session" class="ctd.services.internal.Overview"/>
<jsp:setProperty name="overview" property="*"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link href="style.css" rel="stylesheet" type="text/css">
    </head>
    <body>
        <table cellpadding="0" cellspacing="0" width="640" border="0">
          <tr>
            <td class="text_header_black">Overview</td>
          </tr>
        </table>
        <br>
        <table cellpadding="0" cellspacing="0" width="640" border="1">
          <tr>
            <th bgcolor="#0099CC" class="text_header_white">Study name</th>
            <th bgcolor="#0099CC" class="text_header_white">Assay name</th>
            <th bgcolor="#0099CC" class="text_header_white">Number of samples (with data)</th>
          </tr>
            <jsp:getProperty name="overview" property="content"/>
        </table>
    </body>
</html>
