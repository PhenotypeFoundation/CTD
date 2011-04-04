<%--
------------------------------------------------------------
	uploadHandler.jsp
------------------------------------------------------------
--%>

<%@ 
page session="false" %><%@ 
page contentType="text/html; charset=UTF-8" %><%@ 
page import="java.io.*" %><%@ 
page import="java.net.*" %><%@ 
page import="java.util.*" %><%@
page import="com.oreilly.servlet.*" %><%!

	//
	//	constants
        ResourceBundle res = ResourceBundle.getBundle("settings");
	String dirName = res.getString("ws.upload_folder");
	int maxPostSizeBytes = 10 * 1024 * 1024 * 1024 * 1024;
%><%
	//
	//	initialize the multipart request which will handle file retrieval
	String contentType = request.getContentType();
	if( contentType == null || !contentType.startsWith( "multipart/form-data" ) ) {
		throw new RuntimeException( "content type must be 'multipart/form-data'" );
	}
	MultipartRequest mr = new MultipartRequest( request, dirName, maxPostSizeBytes );	
	//
	//	log request files/parameters
	log( "Dump for request " + mr );
	
	log( "Files:" );
	Enumeration	fileNames = mr.getFileNames();
	for( int i = 0; fileNames.hasMoreElements(); i++ ) {
		String fileName = ( String )fileNames.nextElement();
		File file = mr.getFile( fileName );
		log( "" + ( i + 1 ) + ". fileName=" + fileName + "\r\n" +
			"fileName=" + fileName + "\r\n" +
			"originalName=" + mr.getOriginalFileName( fileName ) + "\r\n" +
			"contentType=" + mr.getContentType( fileName ) + "\r\n" +
			"filesystemName=" + mr.getFilesystemName( fileName ) + "\r\n" +
			"localPath=" + file.getAbsolutePath() + "\r\n" +
			"length=" + file.length() + "\r\n" +
			"" );
	}
	
	log( "Parameters:" );
	Enumeration parameterNames = mr.getParameterNames();
	for( int i = 0; parameterNames.hasMoreElements(); i++ ) {
		String parameterName = ( String )parameterNames.nextElement();
		String parameterValue = mr.getParameter( parameterName );
		log( "" + ( i + 1 ) + ". " + parameterName + "=" + parameterValue );
	}
	
%><%
	//
	//	print info to html, if requested
	if( "true".equals( mr.getParameter( "printInfo" ) ) ) {
%>
	<html>
	<head>
		<title>File upload result</title>
	</head>
	<body>

		<h3>Files</h3>
		<p>
			<table border="1">
				<tr>
					<th>#</th>
					<th>File name</th>
					<th>Original file name</th>
					<th>Content type</th>
					<th>Filesystem name</th>
					<th>Local path</th>
					<th>Length</th>
				</tr>
				<%
				fileNames = mr.getFileNames();
				for( int i = 0; fileNames.hasMoreElements(); i++ ) {
					String fileName = ( String )fileNames.nextElement();
					File file = mr.getFile( fileName );
					out.write( "<tr>" );
					out.write( "<td>" + ( i + 1 ) + "</td>" );
					out.write( "<td>" + fileName + "</td>" );
					out.write( "<td>" + mr.getOriginalFileName( fileName ) + "</td>" );
					out.write( "<td>" + mr.getContentType( fileName ) + "</td>" );
					out.write( "<td>" + mr.getFilesystemName( fileName ) + "</td>" );
					out.write( "<td>" + file.getAbsolutePath() + "</td>" );
					out.write( "<td>" + file.length() + "</td>" );
					out.write( "</tr>" );
				}
				%>
			</table>
		<p/>
		
		<h3>Parameters</h3>
		<p>
			<%
			parameterNames = mr.getParameterNames();
			for( int i = 0; parameterNames.hasMoreElements(); i++ ) {
				String parameterName = ( String )parameterNames.nextElement();
				String parameterValue = mr.getParameter( parameterName );
				out.write( "" + ( i + 1 ) + ". " + parameterName + "=" + parameterValue + "<br>" );
			}
			%>
		</p>
		
		<a href="javascript:window.history.back()">&lt;&lt; back</a>
	</body>
	</html>
<%
	}
%>