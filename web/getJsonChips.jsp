<%--
    Document   : addProtocol
    Created on : 12-mei-2010, 12:02:16
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="get_chip" scope="page" class="ctd.services.getJsonChips"/>
<jsp:setProperty name="get_chip" property="*"/>


        <%
         String message = get_chip.getChips();
         out.println(message);
        %>