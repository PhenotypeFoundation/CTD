<%-- 
    Document   : addProtocol
    Created on : 12-mei-2010, 12:02:16
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="add_protocol" scope="page" class="ctd.services.addProtocol"/>
<jsp:setProperty name="add_protocol" property="*"/>


        <%
         String message = add_protocol.addProtocol();
         out.println(message);
        %>
