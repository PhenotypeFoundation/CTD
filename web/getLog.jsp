<%--
    Document   : index
    Created on : 7-apr-2011, 15:46:58
    Author     : Tjeerd
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>LOGS</title>
    </head>
    <body>
        <table border="1"><tr><td><b>Timestamp</b></td><td><b>Errormsg</b>
        <jsp:useBean id="logfile" scope="page" class="ctd.services.getLog"/>
        <%
        String message = logfile.getLog(request.getParameter("log"), request.getParameter("noshow"));
        out.println(message);
        %>
        </td></tr></table>
        <a name="bottom"> </a>
    </body>
</html>
