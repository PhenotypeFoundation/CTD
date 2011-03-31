<%-- 
    Document   : overview
    Created on : 14-mrt-2011, 12:23:55
    Author     : Tjeerd van Dijk and Taco Steemers
--%>

<%@page import="ctd.services.getTicket"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Level"%>
<%@page import="ctd.services.exceptions.Exception307TemporaryRedirect"%>
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
        <table cellpadding="0" cellspacing="0" width="640" class="tableoverview">
          <tr>
            <th bgcolor="#0099CC" class="thoverview">Assay name</th>
            <th bgcolor="#0099CC" class="thoverview">Study name</th>
            <th bgcolor="#0099CC" class="thoverview">Number of samples (with data)</th>
          </tr>
            <%
            try {
                overview.setSessionToken(request.getSession().getId());
                String message = overview.getContent();
                out.println(message);
            } catch (Exception307TemporaryRedirect e) {
                //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "REDIRECT! "+e.getError());
                response.sendRedirect(e.getError());
            }
            %>
        </table>
    </body>
</html>
