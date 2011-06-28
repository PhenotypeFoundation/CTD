<%--
    Document   : getMeasurements
    Created on : 25-nov-2010, 10:36:15
    Author     : kerkh010
    Edited on  : 07 March 11 - 12:57:32
    Author     : Tjeerd van Dijk and Taco Steemers
--%>

<%@page contentType="text/plain" pageEncoding="UTF-8"%>
<jsp:useBean id="ctdservice" scope="page" class="ctd.services.internal.CtdService"/>

<%
    String[] message = ctdservice.ProcessRestCall("getMeasurements",request.getParameterMap());
    // ProcessRestCall gives 2 Strings back, message[0] is the response code and message[1] is the body of the message
    if(Integer.valueOf(message[0])!=307) {
        // A 307 is a redirect code and needs to be treated different than the other returns
        response.setStatus(Integer.valueOf(message[0]));
        response.getWriter().println(message[1]);
    } else {
        response.sendRedirect(message[1]);
    }
%>