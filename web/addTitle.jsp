<%--
    Document   : addProtocol
    Created on : 12-mei-2010, 12:02:16
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="add_title" scope="page" class="ctd.services.addTitle"/>
<jsp:setProperty name="add_title" property="*"/>


        <%
         String message = add_title.addTitle();
         out.println(message);
        %>
