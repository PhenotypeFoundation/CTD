<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="assays" scope="page" class="ctd.services.getAssays"/>
<jsp:setProperty name="assays" property="*"/>

<%
String message = assays.getAssays(request.getSession().getId(), request.getParameter("studyToken"));
out.println(message);
%>