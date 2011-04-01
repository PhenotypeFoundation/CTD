<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="setData" scope="page" class="ctd.services.setData"/>

<%
setData.setCtdRef(request.getParameter("assayToken"));
setData.setFolder(request.getParameter("filename"));
setData.setStudytoken(request.getParameter("studyToken"));
setData.setSampletokens(request.getParameter("matches"));
setData.setPassword(request.getSession().getId());
String message = setData.setData();
out.println(message);
%>