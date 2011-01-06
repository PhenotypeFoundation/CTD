<%--
    Document   : getTicket
    Created on : 25-nov-2010, 10:36:15
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="getmeasurements" scope="page" class="ctd.services.getMeasurements"/>
<jsp:setProperty name="getmeasurements" property="*"/>


        <%
        String message = getmeasurements.getMeasurements();
        out.println(message);
        %>
