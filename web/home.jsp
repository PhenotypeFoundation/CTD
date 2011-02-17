<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<jsp:useBean id="query" scope="session" class="ctd.services.internal.Query"/>
<jsp:setProperty name="query" property="*"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Untitled Document</title>
    <link href="style.css" rel="stylesheet" type="text/css">

    <style type="text/css">
<!--
a {
	font-family: Verdana, Geneva, sans-serif;
	font-size: 14px;
	color: #FFF;
}
a:link {
	text-decoration: none;
}
a:visited {
	text-decoration: none;
	color: #FFF;
}
a:hover {
	text-decoration: underline;
	color: #0CF;
}
a:active {
	text-decoration: none;
	color: #0CF;
}
-->
</style></head>

<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    
    <td bgcolor="#0099CC" width="120"><a target="subcontent" href="about.jsp">&nbsp;About the CTD</a></td>
    <td width="10">&nbsp;</td>
    <td bgcolor="#0099CC" width="100"><a target="subcontent" href="upload.jsp">&nbsp;Upload data</a></td>
    <td  width="10">&nbsp;</td>
    <td bgcolor="#0099CC" width="120"><a target="subcontent" href="add_protocol.jsp">&nbsp;Add protocol</a></td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
</table>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    
    <td width="120"><a target="subcontent" href="about.jsp"></a></td>
    <td width="10">&nbsp;</td>
    <td bgcolor="#0099CC" width="100">&nbsp; <a target="subcontent" href="set_groups.jsp">Set groups</a></td>
    <td  width="10">&nbsp;</td>
    <td bgcolor="#0099CC" width="120"><a target="subcontent" href="get_protocol.jsp">&nbsp;Get protocol</a></td>
    <td >&nbsp;</td>
    <td >&nbsp;</td>
  </tr>
</table>
<p>
<iframe name="subcontent" src="about.jsp" width="100%" height="700" frameborder="0"  scrolling="auto">
</iframe>

</body>
</html>