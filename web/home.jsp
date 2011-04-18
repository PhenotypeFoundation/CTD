<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<jsp:useBean id="query" scope="session" class="ctd.services.internal.Query"/>
<jsp:setProperty name="query" property="*"/>
<table width="100%" border="0" cellspacing="2" cellpadding="0">
  <tr>
    <td class="blue_button"><a href="#" onClick="loadPage('about.jsp','content');">About the CTD</a></td>
    <td class="blue_button"><a href="#" onClick="loadPage('upload3.jsp','content');">Upload data</a></td>
    <td class="blue_button"><a href="#" onClick="loadPage('add_protocol.jsp','content');">Add protocol</a></td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td class="blue_button"><a href="#" onClick="loadPage('set_groups.jsp','content');">Set groups</a></td>
    <td class="blue_button"><a href="#" onClick="loadPage('get_protocol.jsp','content');">Get protocol</a></td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td class="blue_button"><a href="#" onClick="loadPage('set_delete.jsp','content');">Delete data</a></td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td class="blue_button"><a href="#" onClick="loadPage('set_combat.jsp','content');">GRSN</a></td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
</table>