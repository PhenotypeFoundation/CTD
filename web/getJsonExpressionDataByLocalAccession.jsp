<%--
    Document   : addReference
    Created on : 24-feb-2010, 16:08:12
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="get_expression" scope="page" class="ctd.services.getJsonExpressionDataByLocalAccession"/>
<jsp:setProperty name="get_expression" property="*"/>


        <%
         String message = get_expression.getJsonExpressionDataByLocalAccession();
         out.println(message);
        %>