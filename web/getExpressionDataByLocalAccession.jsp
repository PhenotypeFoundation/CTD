<%--
    Document   : addReference
    Created on : 24-feb-2010, 16:08:12
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="get_expression" scope="page" class="ctd.services.getExpressionDataByLocalAccession"/>
<jsp:setProperty name="get_expression" property="*"/>


        <%
         String message = get_expression.getExpressionDataByLocalAccession();
         out.println(message);
        %>