<%-- 
    Document   : set_groups
    Created on : 11-jan-2011, 11:05:07
    Author     : kerkh010
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<jsp:useBean id="qqqqq" scope="session" class="ctd.services.internal.setCombat"/>
<jsp:setProperty name="qqqqq" property="*"/>

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

            function set_action(variable) {
                var action = variable;
                document.forms["form1"].transactionType.value = action;
            }

            function submit_change(){

                var srcElement = document.getElementById("to_do");
                srcElement.value='show_samples';
            }
			
            function addNewGroup(){

                var new_group = document.forms["form1"].newGroup.value;
                
                var element_new = document.createElement('option');
                element_new.text = new_group;
                element_new.value = new_group;
                document.getElementById('selectedGroup').add(element_new);
                document.getElementById('selectedGroup').selectedIndex=document.getElementById('selectedGroup').length-1;
				
				document.forms["form1"].currentGroup.value = new_group;
				
				//empty grouped samples
				for (var i=document.forms["form1"].selectedSamplesGroup.options.length-1; i>=0; i--){
					document.forms["form1"].selectedSamplesGroup.remove(i);
				}
            }

			function setCurrentGroup(){
				var cs = document.forms["form1"].selectedGroup.value;
				document.forms["form1"].currentGroup.value = cs;				
				}

        </script>

    </head>
    <body>
        <table cellpadding="0" cellspacing="0" width="640" border="0">
            <tr>
                <td bgcolor="#0099CC" class="text_header_white"><span class="text_header_black"><span class="text_header_white">Select and normalize multiple experiments</span></span></td>
            </tr>
        </table>
        <br>
        <form name="form1" target="_self" method="post" action="set_combat.jsp">
            <table class="text_normal" width="800" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td>&nbsp;</td>
                  <td >Password</td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                  <td><input type="hidden" name="transactionType" id="transactionType"></td>
                  <td ><label>
                    <input name="wsPassword" type="password" id="wsPassword" value="">
                  </label></td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                    <td width="5"><input type="hidden" name="transactionResult" id="transactionResult" value="<jsp:getProperty name="qqqqq" property="transactionResult"/>"></td>
                    <td >Select multiple Experiments </td>
                    <td width="10">&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td><select   name="experiment_ids" size="1" multiple id="experiment_ids">
                            <jsp:getProperty name="qqqqq" property="experiments"/>
                        </select>
                      <label>
                        <input type="submit" name="button" id="button" value="Normalize" onClick="set_action('combat')";>
                      </label></td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
            </table>
</form>
        <p>&nbsp;</p>
    </body>
</html>
