<%--
    Document   : getExpressionByProbeSetId
    Created on : 2-mrt-2010, 12:02:45
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="experiments" scope="page" class="ctd.services.getExperiments"/>
<jsp:setProperty name="experiments" property="*"/>


        <%
         String message = experiments.getExperiments();
         out.println(message);
        %>