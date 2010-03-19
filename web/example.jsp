<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
<link href="style.css" rel="stylesheet" type="text/css">
<style type="text/css">
<!--
.header1 {
	font-weight: bold;
	font-size: 18px;
	font-family: Verdana, Geneva, sans-serif;
}
.text_normal {
	font-family: Verdana, Geneva, sans-serif;
	font-size: 14px;
	font-style: normal;
	line-height: normal;
	font-weight: bold;
	color: #000;
}
.text_normal {
	font-weight: normal;
}
.header2 {
	font-weight: bold;
	font-family: Verdana, Geneva, sans-serif;
	font-size: 14px;
}
.prog_style1 {
	color: #090;
}
.prog_style2 {
	color: #F93;
}
.text_normal1 {	font-family: Verdana, Geneva, sans-serif;
	font-size: 14px;
	font-style: normal;
	line-height: normal;
	font-weight: bold;
	color: #000;
}
.text_normal1 {	font-weight: normal;
}
-->
</style>
</head>

<body>
<table width="700" border="0" cellpadding="0" cellspacing="20">
  <tr>
    <td><p class="header1"><br/>
      Introduction</p>
      <p class="text_normal">The CleanTranscriptomeAPI can be used as a library in any java application for handling the pre-processing of CEL-files derived from the Affymetrix mRNA-array platforms. Post- processing of the normalized data is partially provided by the retrieval method for z-scores.<br />
        Be certain to use the right variables in the settings.properties. This file defines the connections with the server.<br />
      </p>
      <table cellpadding="0" cellspacing="0" width="640" border="0">
        <tr>
          <td bgcolor="#0099CC" class="text_header_white">Quick Start</td>
        </tr>
      </table>
      <p class="text_normal"><span class="header2">Retrieve a ticket, send the CEL-files [zipped] and perform normalization.</span></p>
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>CTD  session = <span class="prog_style1">new</span> CTD();<br />
            Ticket ticket = session.retrieveTicket();<br />
            String file = <span class="prog_style2">&quot;c://ftp//cel.zip&quot;</span>;<br />
            session.uploadZippedCELFiles(file);<br />
            CleanDataResult result = session.cleanData();</p>
            <p>############# Store ticket-password in a local database.<br />
              String password = ticket.getPassword();<br />
              ...store it...
              <br />
          </p></td>
        </tr>
      </table>
      <br />
      <p class="text_normal1"><span class="header2">Store a local GCT file (file-format containing processed CEL-files.)</span></p>
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>String gct_file = <span class="prog_style2">&quot;c://ftp//result.gct&quot;</span>;<br />
            String chip_file = <span class="prog_style2">&quot;c://ftp//mouse4302mmentrezg.chip&quot;</span>;<br />
            TicketClient ticket = session.uploadGCTFileToDatabase(gct_file,chip_file);</p>
            <p>############# Store ticket-password in a local database.<br />
              String password = ticket.getPassword();<br />
              ...store it... <br />
          </p></td>
        </tr>
      </table>
<br />
      <span class="header2">Specify local accession codes for the stored CEL-files (database linkage).</span> <br />
      <br />
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
      <tr>
        <td bordercolor="#FFFFFF" bgcolor="#FFFFFF">String localReference = <span class="prog_style2">&quot;dbNP:001&quot;</span>;<br />
String nameCELfile = <span class="prog_style2">&quot;A75_B06_KO_WY&quot;</span>; <br />
session.addReference(localReference, nameCELfile);<br />
<br />
############# Use  password (from ticket) to add local reference afterwards.<br />
CTD session = <span class="prog_style1">new</span> CTD();<br />
String password = <span class="prog_style2">&quot;af3e211e-f773-450f-9662-e90994968c57&quot;</span>;<br/>
String localAccession = <span class="prog_style2">&quot;dbNP:001&quot;</span>;<br />
String nameCELfile = <span class="prog_style2">&quot;A75_B06_KO_WY&quot;</span>;<br />
session.addReference(password,localAccession, nameCELfile);</td>
      </tr>
      </table>
      <p><span class="header2">Retrieve expression data by local accession code.</span></p>
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>ArrayList&lt;ProbeSetExpression&gt; psa = <span class="prog_style1">new</span> ArrayList&lt;ProbeSetExpression&gt;();<br />
            #############
            Retrieve ticket-password from your local database.<br />
            String password = <span class="prog_style2">&quot;af3e211e-f773-450f-9662-e90994968c57&quot;</span>;            <br />
              psa = session.getExpressionDataByLocalAccession(password, <span class="prog_style2">&quot;dbNP:001&quot;</span>);<br />
              Iterator it1 = psa.iterator();<br />
              while (it1.hasNext()) {<br />
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ps = (ProbeSetExpression) it1.next();<br />
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Double value = ps.getLog2Value();<br />
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String name = ps.getProbeSetName();<br />
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(name + &quot; &quot; + value);<br />
              }<br />
          </p></td>
        </tr>
      </table>
      <p><span class="header2">Retrieve expression data by ProbeSetId from whole database.</span></p>
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>ArrayList&lt;ProbeSetExpressionInfo&gt; psa = <span class="prog_style1">new</span> ArrayList&lt;ProbeSetExpressionInfo&gt;();<br />
            psa = session.getExpressionDataByProbeSetId(<span class="prog_style2">&quot;100012_at&quot;</span>);<br />
            Iterator it1 = psa.iterator();<br />
            while (it1.hasNext()) {<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ps = (ProbeSetExpressionInfo) it1.next();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Double value = ps.getLog2Value();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String ref_id = ps.getLocalAccession();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String chip_name = ps.getChipName();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String password = ps.getTicketPassword();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(password + &quot; &quot; + ref_id + &quot; &quot; + chip_name + &quot; &quot; + value);<br />
            }<br />
          </p></td>
        </tr>
      </table>
      <p><span class="header2">Retrieve z-scores by local accession code. (if local accession code is specified)</span></p>
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>### Retrieve ticket password from local database<br />
            String password = <span class="prog_style2">&quot;af3e211e-f773-450f-9662-e90994968c57&quot;</span>; <br />
              ZscoresDataSet zsds = session.getZscoresByLocalAccession(password,<span class="prog_style2"> &quot;dbNP:001&quot;</span>);<br />
              </p>
            <p>Iterator it1 = zsds.getProbeSetZscoreList().iterator();<br />
              while (it1.hasNext()) {<br />
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ProbeSetZscore ps = (ProbeSetZscore) it1.next();<br />
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Double value = ps.getZScore();<br />
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String name = ps.getProbeSetName();<br />
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(name + &quot; &quot; + value);<br />
            }</p></td>
        </tr>
      </table>
      <p><span class="header2">Retrieve expression data by ProbeSetId from whole database.</span></p>
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>ArrayList&lt;ProbeSetExpressionInfo&gt; psa = <span class="prog_style1">new</span> ArrayList&lt;ProbeSetExpressionInfo&gt;();<br />
            psa = session.getExpressionDataByProbeSetId(<span class="prog_style2">&quot;100012_at&quot;</span>);<br />
            Iterator it1 = psa.iterator();<br />
            while (it1.hasNext()) {<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ps = (ProbeSetExpressionInfo) it1.next();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Double value = ps.getLog2Value();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String ref_id = ps.getLocalAccession();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String chip_name = ps.getChipName();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String password = ps.getTicketPassword();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(password + &quot; &quot; + ref_id + &quot; &quot; + chip_name + &quot; &quot; + value);<br />
            }<br />
          </p></td>
        </tr>
      </table>
<p><span class="header2">Delete dataset with the ticket password.</span></p>
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>############# Retrieve ticket-password from your local database.<br />
            String password = <span class="prog_style2">&quot;af3e211e-f773-450f-9662-e90994968c57&quot;</span>; <br />
            DeleteDataResult result = session.deleteData(password);<br />
          </p></td>
        </tr>
      </table>
<p></p></td>
  </tr>
</table>
<p class="header1"><br/>
</p>
</body>
</html>