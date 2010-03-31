<%@page language = "java" import = "java.util.*" session = "true" contentType="image/svg+xml" %>
<jsp:useBean id="query" scope="session" class="ctd.services.internal.Query"/>
<jsp:setProperty name="query" property="*"/>

<jsp:getProperty name="query" property="svg"/>
