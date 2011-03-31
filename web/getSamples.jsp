<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="samples" scope="page" class="ctd.services.getSamples"/>
<jsp:setProperty name="samples" property="*"/>

<%
String message = samples.getSamples(request.getSession().getId(), request.getParameter("assayToken"), request.getParameter("filename"));
out.println(message);
%>