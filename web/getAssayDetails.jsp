<%-- 
    Document   : getAssayDetails
    Created on : 28-jun-2011, 11:15:47
    Author     : Tjeerd van Dijk
--%>

<%@page import="ctd.services.exceptions.Exception307TemporaryRedirect"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="samples" scope="page" class="ctd.services.getSamples"/>
<html>
    <head>
        <link href="../../style.css" rel="stylesheet" type="text/css">
    </head>
    <body>
        <%
        try {
            samples.setSessionToken(request.getSession().getId());
            samples.setAssayToken(request.getParameter("assayToken"));
            String message = samples.getSamplesOverview(false);
            out.println(message);
        } catch (Exception307TemporaryRedirect e) {
            response.sendRedirect(e.getError());
            return;
        }
        %>
        <p class="text_normal">In order to be able to delete data, browse to the transcriptomics database.</p>
    </body>
</html>
