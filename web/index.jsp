<%-- 
    Document   : index
    Created on : 4-feb-2010, 10:19:27
    Author     : kerkh010
    Edited by  : Tjeerd van Dijk and Taco Steemers
--%>

<%@page import="ctd.services.exceptions.Exception307TemporaryRedirect"%>
<%@page import="java.util.ResourceBundle"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>Clean Transcriptome Database</title>
        <link rel="icon" href="./images/favicon.ico">
        <link href="./style.css" rel="stylesheet" type="text/css">
        <link href="./scripts/uploadify.css" type="text/css" rel="stylesheet" />
        <script type="text/javascript" src="./scripts/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="./scripts/jquery.scrollTo-min.js"></script>
        <script type="text/javascript" src="./scripts/indexscripts.js"></script>
        <script type="text/javascript" src="./scripts/uploadscripts.js"></script>
        <script type="text/javascript" src="./scripts/overviewscripts.js"></script>
        <script type="text/javascript" src="./scripts/swfobject.js"></script>
        <script type="text/javascript" src="./scripts/jquery.uploadify.v2.1.4.min.js"></script>
        <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
        <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
    </head>
    <jsp:useBean id="login" scope="session" class="ctd.services.loginGSCF"/>
    <%
    String strPage = "about";
    if(request.getParameter("p")!=null) {
        strPage = request.getParameter("p");
    }
    login.setSessionToken(request.getSession().getId());
    String strUserResponse = login.getUser();
    %>
    <body onLoad="loadPage('<%=strPage %>.jsp','content');">
        <table style="width: 100%; margin-bottom: 5px;"><tr><td class="gscf_bar"><%=strUserResponse %></td></tr></table>
    <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td width="50" bgcolor="#FFFFFF">&nbsp;</td>
        <td colspan="5" bgcolor="#FFFFFF"><img src="images/nugo_logo1.jpg" alt="Logo Nugo" width="63" height="55" align="right" /><img src="images/logo.jpg" alt="Logo CTD" width="567" height="55" align="left" /></td>
        <td width="50" bgcolor="#FFFFFF">&nbsp;</td>
      </tr>
      <tr class="black_button">
        <td>&nbsp;</td>
        <td><a href="#" onClick="loadPage('about.jsp','content');">Home</a></td>
        <td><a href="#" onClick="loadPage('overview.jsp','content');">Overview</a></td>
        <%
        ResourceBundle res = ResourceBundle.getBundle("settings");
        out.print("<td><a href='"+res.getString("gscf.baseURL")+"/'>GSCF</a></td>");
        %>
        <td><a href="#" onClick="loadPage('upload3.jsp','content');">Upload data</a></td>
        <% //<td><a href="#" onClick="loadPage('query.jsp','content');">Query</a></td> %>
        <% //<td><a href="#" onClick="loadPage('example.jsp','content');">Code Examples</a></td> %>
        <% //<td><a href="#" onClick="loadPage('installation.jsp','content');">Installation</a></td> %>
        <td><a href="#" onClick="loadPage('contact.jsp','content');">Contact</a></td>
        <% //<td><a href="#" onClick="loadPage('download.jsp','content');">Download</a></td> %>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td bgcolor="#FFFFFF">&nbsp;</td>
        <td width="700" colspan="5" bgcolor="#FFFFFF"><div id="content" style="width:700px; min-height:500px; margin-top:20px;margin-bottom:10px;"><img src="./images/wait.gif" alt="loading page content..." /></div></td>
        <td bgcolor="#FFFFFF">&nbsp;</td>
      </tr>
    </table>
       
    </body>
</html>
