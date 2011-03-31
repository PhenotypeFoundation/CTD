<%@page language = "java" import = "java.util.*" session = "true" contentType="image/svg+xml" %>
<jsp:useBean id="query" scope="session" class="ctd.services.internal.Query"/>
<jsp:setProperty name="query" property="*"/>

<% if (query.getGraphType().equals("ungrouped")){
    String message = query.getSvg();
    out.println(message);
}%>

<% if (query.getGraphType().equals("grouped")){
    String message = query.getSvgGroups();
    out.println(message);
}%>

