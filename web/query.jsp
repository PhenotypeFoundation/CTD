<%-- 
    Document   : query
    Created on : 24-mrt-2010, 15:59:27
    Author     : kerkh010
--%>
<%@page import="ctd.services.exceptions.Exception307TemporaryRedirect"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="query" scope="session" class="ctd.services.internal.Query"/>
<jsp:setProperty name="query" property="*"/>
        <jsp:useBean id="login" scope="session" class="ctd.services.loginGSCF"/>
        <%
        try {
            login.setSessionToken(request.getSession().getId());
            login.setReturnScript("index.jsp?p=query");
            login.loginGSCF();
        } catch (Exception307TemporaryRedirect e) {
            //Logger.getLogger(getTicket.class.getName()).log(Level.SEVERE, "REDIRECT! "+e.getError());
            response.setHeader("RedirGSCF", e.getError());
            return;
        }
        %>
   
  
    <table cellpadding="0" cellspacing="0" width="640" border="0">
      <tr>
        <td class="text_header_white"><span class="text_header_black">Query</span></td>
      </tr>
      <tr>
        <td class="text_normal"><br>
          Download the scalable vector graphics plugin. (<a target="_blank" href="http://www.adobe.com/svg/viewer/install">link Adobe</a>)<br></td>
      </tr>
    </table>
    <br>
    <table cellpadding="0" cellspacing="0" width="640" border="0">
      <tr>
        <td bgcolor="#0099CC" class="text_header_white">Show  values by ProbeSet name</td>
      </tr>
    </table>
    <br>
    <form action="" target="_self" method="get" enctype="multipart/form-data" name="form1">
      <table width="423" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="106" class="text_normal">Probeset Id  </td>
          <td width="165"><input type="text" name="probesetId" value="<jsp:getProperty name="query" property="probesetId"/>" id="probesetId"></td>
          <td width="152" class="text_normal">e.g. 19013_at</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td><label>
            <select name="zvalue" id="zvalue">
              <option value="normal">log2</option>
            </select>
            <br>
          </label></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td><label>
            <input name="graphType" type="radio" id="graphType" value="grouped" checked>
          <span class="text_normal">Group samples</span></label></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td><label>
            <input type="radio" name="graphType" id="graphType" value="ungrouped">
          <span class="text_normal"><span class="text_normal">ungroup samples</span></span></label></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td><input type="submit" name="submit" id="submit" value="Submit"></td>
          <td>&nbsp;</td>
        </tr>
      </table>
    </form>
   

    
    <IFRAME SRC="svg.jsp?probesetId=<jsp:getProperty name="query" property="probesetId"/>" scrolling="auto" width="700" height="<jsp:getProperty name="query" property="amountValues"/>" frameborder="0" >
    <p>&nbsp;</p>
    <p><br>
    </p>
    </IFRAME>
