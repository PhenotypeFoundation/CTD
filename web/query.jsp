<%-- 
    Document   : query
    Created on : 24-mrt-2010, 15:59:27
    Author     : kerkh010
--%>
<%@ page language="java" import="java.util.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="query" scope="session" class="ctd.services.internal.Query"/>
<jsp:setProperty name="query" property="*"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link href="style.css" rel="stylesheet" type="text/css">
        
    </head>
    <body>
   
    <p class="text_header_black">Query</p>
    <form action="" target="_self" method="get" enctype="multipart/form-data" name="form1">
      <table width="423" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="106" class="text_normal">Probeset Id </td>
          <td width="165"><input type="text" name="probesetId" value="<jsp:getProperty name="query" property="probesetId"/>" id="probesetId"></td>
          <td width="152" class="text_normal">e.g. 19013_at</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td><input type="submit" name="submit" id="submit" value="Submit"></td>
          <td>&nbsp;</td>
        </tr>
      </table>
    </form>
    <IFRAME SRC="svg.jsp?probesetId=<jsp:getProperty name="query" property="probesetId"/>" scrolling="auto" width="700" height="<jsp:getProperty name="query" property="amountValues" />" frameborder="0" >
    <p>&nbsp;</p>
    <p><br>
    </p>
    </IFRAME>
    
    </body>
</html>
