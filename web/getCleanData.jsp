<%-- 
    Document   : getCleanData.jsp
    Created on : 8-feb-2010, 16:45:50
    Author     : kerkh010
--%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="clean_data" scope="page" class="ctd.services.cleanData"/>
<jsp:setProperty name="clean_data" property="*"/>

        <%
        String message = clean_data.cleanData();
        out.println(message);
        %>

