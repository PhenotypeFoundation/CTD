<%--
    Document   : addProtocol
    Created on : 12-mei-2010, 12:02:16
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="get_protocol" scope="page" class="ctd.services.getJsonProtocols"/>
<jsp:setProperty name="get_protocol" property="*"/>


        <%
         String message = get_protocol.getProtocols();
         out.println(message);
        %>
