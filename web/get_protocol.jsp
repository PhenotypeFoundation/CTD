<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>

<jsp:useBean id="qqq" scope="session" class="ctd.services.internal.getProtocol"/>
<jsp:setProperty name="qqq" property="*"/>



<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Untitled Document</title>
        <link href="style.css" rel="stylesheet" type="text/css">

        <script>

            function read_selection(ele) {
                var selected = ele;
                document.forms["form1"].selected_id.value = selected;
            }
			
			function submit_change(){
					
					var srcElement = document.getElementById("to_do");
                    srcElement.value='update';
					
					document.forms["form1"].submit(); 
				}

            function edit_textarea(){

                if (document.forms["form1"].edit.checked == true){
                    document.getElementById('description').disabled=false;
                
                    var srcElement = document.getElementById("EDIT_FIELD");
                    srcElement.style.display='block';

                }
                if (document.forms["form1"].edit.checked == false){
                    document.getElementById('description').disabled= true;

                    var srcElement = document.getElementById("EDIT_FIELD");
                    srcElement.style.display='none';


                }
            }

        </script>


    </head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JSP Page</title>
    </head>
    <body>
        <form id="form1"  target="_self" action="get_protocol.jsp">
            <table  class="text_normal" width="640" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td>Password</td>
                    <td width="12">&nbsp;</td>
                    <td width="14">&nbsp;</td>
                </tr>
                <tr>
                    <td><label>
                            <input name="wsPassword" type="password" id="wsPassword" value="<jsp:getProperty name="qqq" property="wsPassword"/>" />
                  </label></td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>Protocol Name </td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td><label>
                            <select name="names" id="names" onchange="read_selection(this.value)">
                                <jsp:getProperty name="qqq" property="names"/>
                            </select>
                            <input type="hidden" name="selected_id" id="selected_id" value="" />
                            <input type="submit" name="submit" id="submit" value="Get" />
                    </label></td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>Protocol Description </td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td><textarea name="description" id="description" cols="70" rows="15" disabled="true"><jsp:getProperty name="qqq" property="description"/></textarea></td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                  <td><input name="edit" type="checkbox" id="edit" onclick="edit_textarea()"  />
                    <label>Edit</label>
                    <div id="EDIT_FIELD" style="display:none">
                      <input type="submit" name="submit" id="submit" value="Change" onclick="submit_change()" />
                  </div></td>
                  <td><input type="hidden" name="to_do" id="to_do" value=""/></td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
            </table>


</form>
        <h1>&nbsp;</h1>
    </body>
</html>
