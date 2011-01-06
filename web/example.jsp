<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
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

<body link="#FFFFFF" vlink="#FFFFFF">
<table width="700" border="0" cellpadding="0" cellspacing="20">
  <tr>
    <td><table cellpadding="0" cellspacing="0" width="640" border="0">
        <tr>
          <td bgcolor="#0099CC" class="text_header_white"><table cellpadding="0" cellspacing="0" width="640" border="0">
            <tr>
              <td bgcolor="#0099CC" class="text_header_white">JSON implementation for the Nutritional Phenotype Database (<a target="_blank" href="http://dbnp.org/detailed-descriptions-of-restful-services">link</a>)</td>
            </tr>
          </table></td>
        </tr>
      </table>
      <br />
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p class="small-text"><span class="boldnumber">1.</span> Return the experiments in the CTD. Use the  webservice-password for this nbx (ask the administrator for this).<br/>
            http://nbx13.nugo.org/ctd/rest/getExperiments/query?wsPassword=&lt;ws_password&gt;<br/>
            <br />
            <span class="boldnumber">2.</span> Return the samples (assays) for this experiment. Use the experiment-password derived from the previous method. <br/>
            http://nbx13.nugo.org/ctd/rest/getExperimentAssays/query?password=&lt;password&gt;<br/>
            <br />
            <span class="boldnumber">3.</span> Return a list of  genes measured in this sample (assay).  Use the experiment-password and an assayToken obtained from the previous methods. <br/>
            http://nbx13.nugo.org/ctd/rest/getMeasurements/query?password=&lt;password&gt;&amp;assayToken=&lt;assayToken&gt;<br/>
            </p>
            <p class="small-text"><span class="boldnumber">4.</span> Return measurement metadata. Use the experiment password, an assayToken and a measurementToken derived from previous methods.<br />
              http://nbx13.nugo.org/ctd/rest/getMeasurementMetaData/query?password=&lt;password&gt;&amp;assayToken=&lt;assayToken&gt;&amp;measurementToken=&lt;measurementToken&gt;<br />
            </p>
            <p class="small-text"><span class="boldnumber">5.</span> Return list of measurement points for a sample. Use the experiment password and the assay name.<br />
            http://nbx13.nugo.org/ctd/rest/getMeasurementData/query?password=&lt;password&gt;&amp;assayToken=&lt;assayToken&gt;</p>
            <p class="small-text"><span class="boldnumber">6.</span> Return list of measurement points for a gene. Use the webservice-password and the gene name (measurementToken)<br />
              http://nbx13.nugo.org/ctd/rest/getMeasurementData/query?wsPassword=&lt;ws_password&gt;&amp;measurementToken=&lt;measurementToken&gt;              <br/>
              <br/>
              <span class="boldnumber">7. </span>Return experiment url location for a specified assay. Use the experiment password.<br/>
            http://nbx13.nugo.org/ctd/rest/getAssayURL/query?password=&lt;password&gt;</p></td>
        </tr>
      </table>
      <br />
      <span class="header2">Other JSON implementations:</span><br />
<br />
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p class="small-text">append to http://nbx13.nugo.org/ctd/.....<br />
            getJsonTicket.jsp?wsPassword=&lt;secret&gt;<br />
            getJsonCleanData.jsp?ticketPassword=&lt;ticketpassword&gt; <br />
            getJsonExperiments.jsp?wsPassword=&lt;secret&gt;<br />
            getJsonExperimentInfo.jsp?ticketPassword=&lt;ticketpassword&gt; <br />
            getJsonExpressionDataByLocalAccession.jsp?ticketPassword=&lt;ticketpassword&gt;&amp;reference=&lt;sample1&gt;<br />
            getJsonExpressionByProbeSetId.jsp?wsPassword=&lt;secret&gt;&amp;probeSetId=&lt;100012_at&gt;<br />
            getJsonProtocols.jsp?wsPassword=&lt;secret&gt; <br />
            getJsonChips.jsp?wsPassword=&lt;secret&gt; <br />
            getJsonChipAnnotation.jsp?chipName=&lt;name&gt;&amp;wsPassword=&lt;secret&gt; <br />
            getJsonCombatNormalization.jsp?wsPassword=&lt;secret&gt;&amp;password_ticket1=&lt;password1&gt;&amp;<br />
          password_ticket2=&lt;password2&gt; (up to 5 tickets) </p></td>
        </tr>
      </table>
      <p class="header1">Introduction</p>
      <p class="text_normal1">The CleanTranscriptomeAPI can be used as a library in any java application for handling the pre-processing of CEL-files derived from the Affymetrix mRNA-array platforms. <br />
        Define the connections with the server by using the right variables in the settings.properties file.</p>
      <table cellpadding="0" cellspacing="0" width="640" border="0">
        <tr>
        <td bgcolor="#0099CC" class="text_header_white"><table cellpadding="0" cellspacing="0" width="640" border="0">
          <tr>
            <td bgcolor="#0099CC" class="text_header_white">Quick Start API</td>
          </tr>
        </table></td>
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
String password = <span class="prog_style2">&quot;example..af3e211e-f773-450f-9662-e90994968c57&quot;</span>;  <br/>
String localAccession = <span class="prog_style2">&quot;dbNP:001&quot;</span>;<br />
String nameCELfile = <span class="prog_style2">&quot;A75_B06_KO_WY&quot;</span>;<br />
session.addReference(password,localAccession, nameCELfile);</td>
      </tr>
      </table>
      <p><span class="header2">Specify a title for an experiment.</span><br />
      </p>
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>String title = <span class="prog_style2">&quot;Effect of PUFAs on liver cell proliferation&quot;</span>;<br />
            String ticket_password = 
            <span class="prog_style2">&quot;example..af3e211e-f773-450f-9662-e90994968c57&quot;</span>;<br />
              CTD session = <span class="prog_style1">new</span> CTD();<br />
          String message = session.addTitle(ticket_password,title);</p></td>
        </tr>
      </table>
      <p><span class="header2">Retrieve a map with protocols for sample preparation.</span><br />
      </p>
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>CTD session = <span class="prog_style1">new</span> CTD();<br />
            HashMap&lt;String,String&gt; protocols = session.getProtocols();</p></td>
        </tr>
      </table>
<p><span class="header2">Retrieve experiments in the CTD.</span><br />
</p>
<table  width="640" border="1" cellpadding="0" cellspacing="5">
  <tr>
    <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>CTD session = <span class="prog_style1">new</span> CTD();<br />
      ArrayList&lt;TicketClient&gt; tickets = session.getExperiments();<br />
      while (it1.hasNext()){<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;TicketClient tc = (TicketClient) it1.next();<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(tc.getTitle());<br />
}</p></td>
  </tr>
</table>
<p><span class="header2">Retrieve expression data by local accession code.</span></p>
      <table  width="640" border="1" cellpadding="0" cellspacing="5">
        <tr>
          <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>ArrayList&lt;ProbeSetExpression&gt; psa = <span class="prog_style1">new</span> ArrayList&lt;ProbeSetExpression&gt;();<br />
            #############
            Retrieve ticket-password from your local database.<br />
            String password = <span class="prog_style2">&quot;example..af3e211e-f773-450f-9662-e90994968c57&quot;</span>;            <br />
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
            String password = <span class="prog_style2">&quot;example..af3e211e-f773-450f-9662-e90994968c57&quot;</span>; <br />
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
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String password_ticket = ps.getTicketPassword();<br />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(password_ticket + &quot; &quot; + ref_id + &quot; &quot; + chip_name + &quot; &quot; + value);<br />
            }<br />
          </p></td>
        </tr>
      </table>
<p><span class="header2">Normalize 2 to 5 experiments with the ComBAT method.</span></p>
<table  width="640" border="1" cellpadding="0" cellspacing="5">
  <tr>
    <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>############# Initiate experiments normalisation, use the ticket passwords. (No group assignments...)<br />
      CombatInfo info = session.getCombatNormalization(&quot;28d35708-4e3e-4c37-8aaa-ce3ba052f7cc&quot;, &quot;8bf2c340-73d6-4066-9a67-af7e39e65495&quot;,&quot;fa6f8dc8-f3b2-44a5-a3cc-0805c1e14866&quot;);<br />
String ftp_location = info.getLocationFtpFile();<br />
Integer amount_assays = info.getAmountAssays();<br />
Integer amount_probesets = info.getAmountProbes();</p></td>
  </tr>
</table>
<p><span class="header2">Delete dataset with the ticket password.</span></p>
<table  width="640" border="1" cellpadding="0" cellspacing="5">
  <tr>
    <td bordercolor="#FFFFFF" bgcolor="#FFFFFF"><p>############# Retrieve ticket-password from your local database.<br />
      String password = <span class="prog_style2">&quot;example..af3e211e-f773-450f-9662-e90994968c57&quot;</span>; <br />
      DeleteDataResult result = session.deleteData(password);<br />
    </p></td>
  </tr>
</table>
<br />
<p></p></td>
  </tr>
</table>
<p class="header1"><br/>
</p>
</body>
</html>