<%-- 
    Document   : set_groups
    Created on : 11-jan-2011, 11:05:07
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:useBean id="qq" scope="session" class="ctd.services.internal.getGroups"/>
<jsp:setProperty name="qq" property="*"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Set Sample Groups</title>
        <link href="style.css" rel="stylesheet" type="text/css">
        <script>

            function read_selection(ele) {
                var selected = ele;
                document.forms["form1"].selected_id.value = selected;
            }
			
			function submit_change(){
					
					var srcElement = document.getElementById("to_do");
                    srcElement.value='show_samples';
					
					document.forms["form1"].submit(); 
				}

           
            
        </script>
        
        
    </head>
    <body>
    <table cellpadding="0" cellspacing="0" width="640" border="0">
  <tr>
            <td bgcolor="#0099CC" class="text_header_white"><span class="text_header_black"><span class="text_header_white">Set sample groups</span></span></td>
      </tr>
        </table>
        <br>
        <form name="form1" target="_self" method="post" action="set_groups.jsp">
          <table class="text_normal" width="800" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="33">&nbsp;</td>
              <td >Experiment list </td>
              <td width="39">&nbsp;</td>
            </tr>
            <tr>
              <td><input type="hidden" name="selected_id" id="selected_id" value="" /></td>
              <td><select name="experiment_id" id="experiment_id" onchange="read_selection(this.value)">
                <jsp:getProperty name="qq" property="experiments"/>
              </select>
              <label>
                <input type="submit" name="button" id="button" value="Retrieve">
              </label></td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <td><input type="hidden" name="to_do" id="to_do"></td>
              <td><jsp:getProperty name="qq" property="tableSamples"/></td>
              <td>&nbsp;</td>
            </tr>
            <tr>
            <td></td>
            <td></td>
            <td>&nbsp;</td>
            </tr>
          </table>
        </form>
<p>&nbsp;</p>
    </body>
</html>
