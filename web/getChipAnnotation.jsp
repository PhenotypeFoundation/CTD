<%--
    Document   : addProtocol
    Created on : 12-mei-2010, 12:02:16
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="get_chipannotation" scope="page" class="ctd.services.getChipAnnotation"/>
<jsp:setProperty name="get_chipannotation" property="*"/>


        <%
         String message = get_chipannotation.getChipAnnotation();
         out.println(message);
        %>