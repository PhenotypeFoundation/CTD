<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="studies" scope="page" class="ctd.services.getStudies"/>

<%
String message = studies.getStudies(request.getSession().getId());
out.println(message);
%>