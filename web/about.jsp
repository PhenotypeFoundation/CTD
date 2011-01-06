<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*"  errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
<link href="style.css" rel="stylesheet" type="text/css">


 <style type="text/css">
        <!--
        body {
            background-color: #FFF;
        }
        .text_normal1 {	font-family: Verdana, Geneva, sans-serif;
            font-size: 14px;
            font-style: normal;
            line-height: normal;
            font-weight: bold;
            color: #0000;
        }
        .text_normal1 {	font-weight: normal;
        }
        -->
</style>
</head>

<body>
<p><span class="text_normal1">The NuGO Clean transcriptome API provides programmers a quick method for processing <br />
  RAW data derived from Affymetrix GeneChip systems for transcriptome analysis.<br />
  Analyzed data is captured in so called CEL-files (*.cel) which can be submitted by the <br />
  API for normalization, database storage and subsequent querying.<br />
  The method relies on the <a target="_blank" href="http://skaringa.sourceforge.net/index.html">Skaringa API</a> for Java, JSON and XML language binding. This <br/>
  technology
  enables the communication between the client API and the server with just <br />
  simple java objects (POJOs). Sending the raw data files is done by secure ftp with the <br />
Java secure channel library (JSch) from the <a target="_blank" href="http://www.jcraft.com/">JCraft</a> project.</span></p>
<p><span class="text_normal1">Instead of using the client library one can also use the JSON http requests directly. (See the example page)<br />
  </span><br />
  <span class="text_normal">The main advantage of the Clean Transcriptome Database is the equal normalization of CEL files by using RMA and GRSN, global rank-invariant set normalization (<a target="_blank" href="http://www.ncbi.nlm.nih.gov/pmc/articles/PMC2644708/?tool=pubmed">PMID:19055840</a>). GRSN ensures samples within a dataset are fitted to an average intensitiy distribution allowing for a more robust comparison. However, one should always be aware of the probability of overfitting datasets.<br />
  Another advantage is the fast retrieval of expression values from a specific probeset. This allows the fast visualization of hundreds experiments from the viewpoint of a single gene. </span>
  </p>
</p>
<p><img src="images/strategy.jpg" alt="" width="595" height="356" /></p>
</body>
</html>