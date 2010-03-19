<%-- 
    Document   : getStoreGCTFile
    Created on : 12-mrt-2010, 10:42:06
    Author     : kerkh010
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="store_gct" scope="page" class="ctd.services.storeGCTFile"/>
<jsp:setProperty name="store_gct" property="*"/>


        <%
        String message = store_gct.storeGCTFile();
        out.println(message);
        %>