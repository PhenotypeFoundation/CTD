<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Untitled Document</title>
        <link href="style.css" rel="stylesheet" type="text/css">

        <script type="text/javascript">
            function createXMLHttpRequest(){

                if( typeof XMLHttpRequest == "undefined" ) XMLHttpRequest = function() {
                    try { return new ActiveXObject("Msxml2.XMLHTTP.6.0") } catch(e) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP.3.0") } catch(e) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP") } catch(e) {}
                    try { return new ActiveXObject("Microsoft.XMLHTTP") } catch(e) {}
                    throw new Error( "This browser does not support XMLHttpRequest." )
                };
                return new XMLHttpRequest();
            }

            var AJAX = createXMLHttpRequest();

            function handler() {
                if(AJAX.readyState == 4 && AJAX.status == 200) {
                    var json = eval('(' + AJAX.responseText +')');
                    alert('Ticket is received');

                    document.getElementById('upload').href = json.locationFTPFolder;
                    document.getElementById('password').value = json.password;					
					document.getElementById('ticket_password').innerHTML = "ticket password= "+json.password;

                    var srcElement = document.getElementById("UPLOAD_TABLE");
                    srcElement.style.display='block';
					
					var srcElement = document.getElementById("NORMALIZE_TABLE");
                    srcElement.style.display='block';
					
                }else if (AJAX.readyState == 4 && AJAX.status != 200) {
                    alert('Something went wrong...');
                }
            }

            function handlertitle() {
                if(AJAX.readyState == 4 && AJAX.status == 200) {
                    alert('Success');

                }else if (AJAX.readyState == 4 && AJAX.status != 200) {
                    alert('Something went wrong...');
                }
            }

            function handler_normalisation(){

				var srcElement = document.getElementById("TITLE_TABLE");
                srcElement.style.display='block';
				
				if (AJAX.readyState ==1){
					document.getElementById('processing').innerHTML = "processing data...";
					}
								
                if(AJAX.readyState == 4 && AJAX.status == 200) {
                    var json = eval('(' + AJAX.responseText +')');
					document.getElementById('processing').innerHTML = "finished";
                    alert('Success ='+json.message);

                }else if (AJAX.readyState == 4 && AJAX.status != 200) {
                    alert('Something went wrong...');
                }
            }

            function retrieveTicket(){

                var password = document.getElementById('wspassword').value;
                var url = "getJsonTicket.jsp?wsPassword="+password;

                AJAX.onreadystatechange = handler;
                AJAX.open("GET", url);
                AJAX.send("");
            }


            function setTitle(){

                var password = document.getElementById('password').value;
                var title = document.getElementById('title').value;
                var url = "addTitle.jsp?password="+password+"&title="+title;

                AJAX.onreadystatechange = handlertitle;
                AJAX.open("GET", url);
                AJAX.send("");
            }

            function startNorm(){
                var password = document.getElementById('password').value;
                var url = "getJsonCleanData.jsp?ticketPassword="+password;

                AJAX.onreadystatechange = handler_normalisation;
                AJAX.open("GET", url);
                AJAX.send("");

            }
        </script>
    </head>

    <body>
        <table width="660" border="0" cellspacing="0" cellpadding="0">
            <tr bgcolor="#000000" class="text_header_white">
                <td  colspan="4">Upload CEL-files (Zipped in one file)</td>
            </tr>
            <tr>
                <td width="79">&nbsp;</td>
                <td width="171">&nbsp;</td>
                <td width="297">&nbsp;</td>
                <td width="113">&nbsp;</td>
            </tr>
            <tr class="text_header_black">
                <td>Step 1</td>
                <td>Retrieve Ticket</td>
                <td>Password</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td><label>
                        <input type="text" id="wspassword" name="wspassword" />
                </label></td>
                <td>&nbsp;</td>
            </tr>
            <tr class="text_header_black">
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td><input type="button" name="getTicket" id="getTicket" value="Retrieve" onclick="retrieveTicket();" /></td>
              <td>&nbsp;</td>
            </tr>
            <tr class="text_header_black">
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td colspan="2"><p><b id='ticket_password'></b></p></td>
            </tr>
            
            
            
        </table>
        
        <div ID="UPLOAD_TABLE" style="display:none">
                <table width="659" border="0" cellspacing="0" cellpadding="0">
                <tr>
                <td width="73">&nbsp;</td>
                <td width="156">&nbsp;</td>
                <td width="316">&nbsp;</td>
                <td width="114">&nbsp;</td>
            </tr>
  
                <tr class="text_header_black">
                    <td>Step 2</td>
                    <td>Upload Data</td>
                    <td>

                        <a id="upload" name="upload" href="">upload link</a>

                    </td>


                    <td><a target="_blank" href="http://winscp.net/eng/docs/lang:nl">use winscp</a></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
</table>

            </div>
        <div ID="NORMALIZE_TABLE" style="display:none">
        <table width="600" border="0" cellspacing="0" cellpadding="0">
        <tr>
                <td width="79">&nbsp;</td>
                <td width="171">&nbsp;</td>
                <td width="297">&nbsp;</td>
                <td width="113">&nbsp;</td>
            </tr>
  <tr class="text_header_black">
                <td>Step 3</td>
                <td>Normalize Data</td>
                <td><label>
                        <input type="submit" name="Start" id="Start" value="Start" onclick="startNorm();" />
                </label></td>
                <td><input type="hidden" name="password" id="password" value="" /></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td colspan="2"><p><b id='processing'></b></p></td>
            </tr>
</table>

</div>
        
        <div ID="TITLE_TABLE" style="display:none">
        <table width="600" border="0" cellspacing="0" cellpadding="0">
<tr class="text_header_black">
                <td>Step 4</td>
                <td colspan="2">Provide Title Experiment</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td width="79">&nbsp;</td>
                <td width="171">&nbsp;</td>
                <td width="297">&nbsp;</td>
                <td width="113">&nbsp;</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td colspan="3"><input type="text" width="570" maxlength="500" name="title" id="title"/></td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td><input  type="button" name="setTitle" id="setTitle" value="Provide" onclick="setTitle();" /></td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
</table>

        </div>
        
    </body>
</html>