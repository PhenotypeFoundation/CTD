<%-- 
    Document   : addReference
    Created on : 24-feb-2010, 16:08:12
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="add_reference" scope="page" class="ctd.services.addReference"/>
<jsp:setProperty name="add_reference" property="*"/>


        <%
         String message = add_reference.addReference();
         out.println(message);
        %>


