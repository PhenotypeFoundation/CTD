<%-- 
    Document   : getExpressionByProbeSetId
    Created on : 2-mrt-2010, 12:02:45
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="exp_info" scope="page" class="ctd.services.getExperimentInfo"/>
<jsp:setProperty name="exp_info" property="*"/>


        <%
         String message = exp_info.getExperimentInfo();
         out.println(message);
        %>