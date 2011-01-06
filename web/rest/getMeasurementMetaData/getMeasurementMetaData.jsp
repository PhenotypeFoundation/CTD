<%--
    Document   : getTicket
    Created on : 25-nov-2010, 10:36:15
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="getmeasurementmetadata" scope="page" class="ctd.services.getMeasurementMetadata"/>
<jsp:setProperty name="getmeasurementmetadata" property="*"/>


        <%
        String message = getmeasurementmetadata.getMeasurementMetadata();
        out.println(message);
        %>
