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
.style1 {color: #000000}
        -->
 </style>
</head>

<body>
<p><span class="text_normal1"><strong>Usage</strong><br />
The NuGO <strong>C</strong>lean <strong>T</strong>ranscriptome <strong>D</strong>atabase is a module of the <a target="_blank" href="http://dbnp.org/dbnp/modules-1/cleantranscriptomicsdatabase">Nutritional Phenotype database</a> that allows users to submit their transcriptome data in the raw format (*.CEL files). Along the way the data is processed, normalized and stored. Users are asked to provide an experiment title and are allowed to arrange the individual hybridizations into groups of biological repeats for statistical needs. <br />
A global password is provided making the CTD safe for external access. In addition, laboratory protocols can be uploaded, edited and shared.</span> <span class="text_normal1">Programmable access is provided by JSON http requests directly (<a target="_blank" href="http://dbnp.org/dbnp/modules-1/detailed-descriptions-of-restful-services">protocol</a>). JSON examples with a password are found here on the example page.</span><br />
</p>
<p><span class="text_normal">The main advantage of the Clean Transcriptome Database is the equal normalization of CEL files using RMA and GRSN (global rank-invariant set normalization, <a target="_blank" href="http://www.ncbi.nlm.nih.gov/pmc/articles/PMC2644708/?tool=pubmed">PMID:19055840</a>). GRSN ensures samples within a dataset are fitted to an average intensitiy distribution allowing for a more robust comparison. However, one should always be aware of the probability of overfitting datasets.<br />
<span class="text_normal1">The probeset name can be used to query the database resulting in a graphical representation of all expression values for a specific gene. If samples have been grouped a standard deviation bar is shown.</span></span></p>
<p>  <div style="display:none"><span class="text_normal1">API provides programmers a quick method for processing <br />
  RAW data derived from Affymetrix GeneChip systems for transcriptome analysis.<br />
  Analyzed data is captured in so called CEL-files (*.cel) which can be submitted by the <br />
  API for normalization, database storage and subsequent querying.<br />
  The method relies on the <a target="_blank" href="http://skaringa.sourceforge.net/index.html">Skaringa API</a> for Java, JSON and XML language binding. This <br/>
  technology
  enables the communication between the client API and the server with just <br />
  simple java objects (POJOs). Sending the raw data files is done by secure ftp with the <br />
  Java secure channel library (JSch) from the <a target="_blank" href="http://www.jcraft.com/">JCraft</a> project.</span></p>
<p><span class="text_normal1"><br />
  </span><br />
</p>
</p>
<p>&nbsp;</p></div>
</body>
</html>