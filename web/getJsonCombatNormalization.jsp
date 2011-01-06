<%--
    Document   : getDeleteData
    Created on : 13-oct-2010, 10:56:25
    Author     : kerkh010
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="combat_normalization" scope="page" class="ctd.services.getJsonCombatNormalization"/>
<jsp:setProperty name="combat_normalization" property="*"/>

        <%
        String message = combat_normalization.getCombatNormalization();
        out.println(message);
        %>
