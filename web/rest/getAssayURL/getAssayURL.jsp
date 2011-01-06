<%--
    Document   : getTicket
    Created on : 25-nov-2010, 10:36:15
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="getassayurl" scope="page" class="ctd.services.getAssayURL"/>
<jsp:setProperty name="getassayurl" property="*"/>


        <%
        String message = getassayurl.getAssayURL();
        out.println(message);
        %>
