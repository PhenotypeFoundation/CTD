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

            function set_action(variable) {
                var action = variable;
                document.forms["form1"].transactionType.value = action;
            }

            function submit_change(){

                var srcElement = document.getElementById("to_do");
                srcElement.value='show_samples';

                document.forms["form1"].submit();
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
                <td bgcolor="#0099CC" class="text_header_white"><span class="text_header_black"><span class="text_header_white">Set sample groups</span></span></td>
            </tr>
        </table>
        <br>
        <form name="form1" target="_self" method="post" action="set_groups.jsp">
            <table class="text_normal" width="800" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td width="5"><input type="hidden" name="transactionResult" id="transactionResult" value="<jsp:getProperty name="qq" property="transactionResult"/>"></td>
                    <td colspan="3" >Experiment list </td>
                    <td width="39"><input type="hidden" name="transactionType" id="transactionType"></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td colspan="3"><select  name="experiment_id" id="experiment_id" onchange="read_selection(this.value);">
                            <jsp:getProperty name="qq" property="experiments"/>
                        </select>
                        <label>
                            <input type="submit" name="button" id="button" value="Retrieve">
                    </label></td>
                    <td><input type="hidden" name="selected_id" id="selected_id" value="" /></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>Groups</td>
                    <td>&nbsp;</td>
                    <td>New Group Name</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td></td>
                    <td><select  name="selectedGroup" id="selectedGroup" onchange="setCurrentGroup();this.form.submit();">
                            <jsp:getProperty name="qq" property="groupList"/>
                    </select></td>
                    <td><input type="hidden" name="currentGroup" id="currentGroup" value="<jsp:getProperty name="qq" property="currentGroup"/>"></td>
                    <td ><label>
                            <input type="text" name="newGroup" id="newGroup">
                            <input align="right" type="button"  name="AddGroup" id="AddGroup" value="Add" onClick="addNewGroup();">
                    </label></td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td></td>
                    <td colspan="2">Ungrouped Samples</td>
                    <td >Grouped Samples</td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td></td>
                    <td colspan="2"><select size="15" tabindex="3" name="selectedSamples" id="selectedSamples" multiple>
                            <jsp:getProperty name="qq" property="samplesNoGroupList"/>
                    </select></td>
                    <td><select size="15" tabindex="3"  name="selectedSamplesGroup" id="selectedSamplesGroup" multiple>
                            <jsp:getProperty name="qq" property="samplesGroupList"/>
                    </select></td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td></td>
                    <td colspan="2"><label>
                            <input type="submit" name="Group" id="Group" value="Group >>" onClick="set_action('group');">
                    </label></td>
                    <td><label>
                            <input type="submit" name="Ungroup" id="Ungroup" value="<< Ungroup" onClick="set_action('ungroup');">
                    </label></td>
                    <td>&nbsp;</td>
                </tr>
            </table>
    </form>
        <p>&nbsp;</p>
    </body>
</html>
