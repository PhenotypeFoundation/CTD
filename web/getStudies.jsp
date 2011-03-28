<%--
    Document   : getTicket
    Created on : 4-feb-2010, 10:36:15
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="studies" scope="page" class="ctd.services.getStudies"/>
<jsp:setProperty name="ticket" property="*"/>


        <%
        String message = studies.getStudies();
        out.println(message);
        %>
