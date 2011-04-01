<%@page import="ctd.services.exceptions.Exception307TemporaryRedirect"%>
<%@page language = "java" import = "java.util.*" session = "true" contentType="image/svg+xml" %>
<jsp:useBean id="query" scope="session" class="ctd.services.internal.Query"/>
<jsp:setProperty name="query" property="*"/>

<% if (query.getGraphType().equals("ungrouped")){
    query.setSessionToken(request.getSession().getId());
    try {
        String message = query.getSvg();
        out.println(message);
    } catch (Exception307TemporaryRedirect e) {
        //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "REDIRECT! "+e.getError());
        response.sendRedirect(e.getError());
    }
}%>

<% if (query.getGraphType().equals("grouped")){
    try {
      query.setSessionToken(request.getSession().getId());
        String message = query.getSvgGroups();
        out.println(message);
    } catch (Exception307TemporaryRedirect e) {
        //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "REDIRECT! "+e.getError());
        response.sendRedirect(e.getError());
    }
}%>

