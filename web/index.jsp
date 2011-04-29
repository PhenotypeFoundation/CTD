<%-- 
    Document   : index
    Created on : 4-feb-2010, 10:19:27
    Author     : kerkh010
    Edited by  : Tjeerd van Dijk and Taco Steemers
--%>

<%@page import="java.util.ResourceBundle"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>Clean Transcriptome Database</title>
        <link href="style.css" rel="stylesheet" type="text/css">
        <script type="text/javascript" src="./uploadify/jquery-1.4.2.min.js"></script>
        <script type="text/javascript">
            function loadPage(page, div) {
                $.ajax({
                    url: "./"+page,
                    success: function(data, textStatus, jqXHR){
                        if(jqXHR.getResponseHeader("RedirGSCF") != null) {
                            window.location.replace(jqXHR.getResponseHeader("RedirGSCF"));
                        } else {
                            $("#"+div).html(data);
                        }
                    }
                });
            }
        </script>
        <link href="./uploadify/uploadify.css" type="text/css" rel="stylesheet" />
        <script type="text/javascript" src="./uploadscripts.js"></script>
        <script type="text/javascript" src="./uploadify/swfobject.js"></script>
        <script type="text/javascript" src="./uploadify/jquery.uploadify.v2.1.4.min.js"></script>
        <script type="text/javascript" src="./uploadify/redips-drag.js"></script>
    </head>
    <%
    String strPage = "about";
    if(request.getParameter("p")!=null) {
        strPage = request.getParameter("p");
    }
    %>
    <body onLoad="loadPage('<%=strPage %>.jsp','content');">
    <table width="800" border="0" align="center" cellpadding="0" cellspacing="0"s>
      <tr>
        <td width="50" bgcolor="#FFFFFF">&nbsp;</td>
        <td colspan="8" bgcolor="#FFFFFF"><img src="images/nugo_logo1.jpg" width="63" height="55" align="right" /><img src="images/logo.jpg" width="567" height="55" align="left" /></td>
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
        <td><a href="#" onClick="loadPage('query.jsp','content');">Query</a></td>
        <td><a href="#" onClick="loadPage('example.jsp','content');">Code Examples</a></td>
        <td><a href="#" onClick="loadPage('installation.jsp','content');">Installation</a></td>
        <td><a href="#" onClick="loadPage('contact.jsp','content');">Contact</a></td>
        <td><a href="#" onClick="loadPage('download.jsp','content');">Download</a></td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td bgcolor="#FFFFFF">&nbsp;</td>
        <td width="700" colspan="8" bgcolor="#FFFFFF"><div id="content" style="width:700px; min-height:500px; margin-top:20px;"><img src="./images/wait.gif" alt="loading page content..." /></div></td>
        <td bgcolor="#FFFFFF">&nbsp;</td>
      </tr>
    </table>
       
    </body>
</html>
