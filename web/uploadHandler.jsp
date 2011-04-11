<%--
------------------------------------------------------------
	uploadHandler.jsp
------------------------------------------------------------
--%>

<%@page import="java.util.logging.Level"%>
<%@page import="ctd.services.setData"%>
<%@page import="java.util.logging.Logger"%>
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
	String dirName = "error";
	int maxPostSizeBytes = 10 * 1024 * 1024;
%><%

        ///Get UUID that will be used to create a folder for temporary storage
        int uuid = 0;
        int i = 0;
        while (i < 100) {
            i++;
            uuid++;
            dirName = res.getString("ws.temp_folder")+uuid;
            File file=new File(dirName);
            boolean exists = file.exists();
            if (!exists){
                break;
            }
        }
        File objMap = new File(dirName);

        boolean success = objMap.mkdir();
        if (success) {
            boolean bln1 = objMap.setExecutable(true);
            boolean bln2 = objMap.setWritable(true);
            boolean bln3 = objMap.setReadable(true);
            boolean bln4 = objMap.canWrite();
            Logger.getLogger(setData.class.getName()).log(Level.SEVERE, "setData directory: " + dirName +" created"+bln1+bln2+bln3+" en ook "+bln4);
        }
        
        File objMap2 = new File(dirName);
        Logger.getLogger(setData.class.getName()).log(Level.SEVERE, "setData directory: START "+ dirName);
        objMap2.list();
        Logger.getLogger(setData.class.getName()).log(Level.SEVERE, "setData directory: " + dirName +" EINDE");

        //
	//	initialize the multipart request which will handle file retrieval
	String contentType = request.getContentType();
	if( contentType == null || !contentType.startsWith( "multipart/form-data" ) ) {
		throw new RuntimeException( "content type must be 'multipart/form-data'" );
	}
	MultipartRequest mr = new MultipartRequest( request, dirName, maxPostSizeBytes );

        out.write(uuid+"");
%>