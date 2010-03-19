<%-- 
    Document   : getZscoresByLocalAccession
    Created on : 16-mrt-2010, 11:30:25
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="get_zscore" scope="page" class="ctd.services.getZscoresByLocalAccession"/>
<jsp:setProperty name="get_zscore" property="*"/>


        <%
         String message = get_zscore.getZscoresByLocalAccession();
         out.println(message);
        %>