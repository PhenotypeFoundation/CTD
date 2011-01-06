<%-- 
    Document   : getExpressionByProbeSetId
    Created on : 2-mrt-2010, 12:02:45
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="get_expression_probesetid" scope="page" class="ctd.services.getJsonExpressionByProbeSetId"/>
<jsp:setProperty name="get_expression_probesetid" property="*"/>


        <%
         String message = get_expression_probesetid.getExpressionByProbeSetId();
         out.println(message);
        %>