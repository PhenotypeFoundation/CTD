<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="setData" scope="page" class="ctd.services.setData"/>

<%
String message = setData.setData();
out.println(message);
%>