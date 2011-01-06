<%--
    Document   : getTicket
    Created on : 25-nov-2010, 10:36:15
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="getmeasurementassays" scope="page" class="ctd.services.getExperimentAssays"/>
<jsp:setProperty name="getmeasurementassays" property="*"/>

        <%
        String message = getmeasurementassays.getExperimentAssays();
        out.println(message);
        %>
