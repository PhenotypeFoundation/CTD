<%--
    Document   : getTicket
    Created on : 25-nov-2010, 10:36:15
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="getmeasurementdata" scope="page" class="ctd.services.getMeasurementDataProbeset"/>
<jsp:setProperty name="getmeasurementdata" property="*"/>


        <%
        String message = getmeasurementdata.getMeasurementDataProbeset();
        out.println(message);
        %>
