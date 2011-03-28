<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Upload3</title>
        <link href="./uploadify/uploadify.css" type="text/css" rel="stylesheet" />
        <script type="text/javascript" src="./uploadify/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="./uploadscripts.js"></script>
        <script type="text/javascript" src="./uploadify/swfobject.js"></script>
        <script type="text/javascript" src="./uploadify/jquery.uploadify.v2.1.4.min.js"></script>
        <script type="text/javascript" src="./uploadify/redips-drag-min.js"></script>
        <script type="text/javascript">
          $(document).ready(function() {
            $('#file_upload').uploadify({
              'uploader'     : './uploadify/uploadify.swf',
              'script'       : './uploadHandler.jsp',
              'cancelImg'    : './uploadify/cancel.png',
              'displayData'  : 'percentage',
              'onSelectOnce' : function() {init_step2()},
              'onAllComplete': function() {upload_ready = true; init_step4()},
              'fileExt'      : '*.zip;*.cel;*.7z',
              'fileDesc'     : '.cel or .zip files',
              'buttonText'   : 'Click here',
              'width'        : 400,
              'auto'         : true,
              'queueSizeLimit':1
            });
          });
        </script>
    </head>
    <body onLoad="init_step1();REDIPS.drag.init()">
        <h1>Upload Data</h1>

        <div id="step1" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>1. Upload a .zip containing .cel files:</h2><span id="spanstep1"><input type='file' id='file_upload' name='file_upload' /></span></div>
        <div id="step2" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>2. Select a study</h2><span id="spanstep2"><select id='selectStudy' name='selectStudy' onChange='study_selected();'></select></span></div>
        <div id="step3" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>3. Select an assay:</h2><span id="spanstep2"><select id='selectAssay' name='selectAssay' onChange='assay_selected();'></select></span></div>
        <div id="step4" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF">
            <h2>4. Link files to samples</h2>
            <div id="drag">
                <table border="1">
                    <tr><td class="forbid" style="width:200px">Filename1</td><td style="width:200px"><div class="drag">sample1</div></td></tr>
                    <tr><td class="forbid" style="width:200px">Filename2</td><td style="width:200px"><div class="drag">sample2</div></td></tr>
                    <tr><td class="forbid" style="width:200px">Filename3</td><td style="width:200px"><div class="drag">sample3</div></td></tr>
                    <tr><td colspan="2" style="height:100px"><div class="drag">sample4</div><div class="drag">sample5</div></td></tr>
                </table>
                <a href="#" onClick="init_step5();">Ok</a>
            </div>
        </div>
        <div id="step5" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>5. Save data</h2><input type="submit" value="Save data"/></div>
    </body>
</html> 
