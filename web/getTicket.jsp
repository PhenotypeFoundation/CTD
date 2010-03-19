<%-- 
    Document   : getTicket
    Created on : 4-feb-2010, 10:36:15
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="ticket" scope="page" class="ctd.services.getTicket"/>
<jsp:setProperty name="ticket" property="*"/>


        <%
        String message = ticket.getTicket();
        out.println(message);
        %>
    