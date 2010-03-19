<%-- 
    Document   : getDeleteData
    Created on : 8-mrt-2010, 10:56:25
    Author     : kerkh010
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="delete_data" scope="page" class="ctd.services.deleteData"/>
<jsp:setProperty name="delete_data" property="*"/>

        <%
        String message = delete_data.deleteData();
        out.println(message);
        %>

