<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="loginGSCF" scope="page" class="ctd.services.loginGSCF"/>

<%
String message = loginGSCF.loginGSCF(request.getSession().getId());
out.println(message);
%>