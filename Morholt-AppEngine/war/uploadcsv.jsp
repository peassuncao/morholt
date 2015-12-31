<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 
<%@page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@page import="com.google.gwt.core.client.GWT"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.gwt.user.client.Window"%>
<%@page import="com.google.gwt.user.client.Window.Location"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Upload your CSV file here</title>
</head>
<body>

<%
   UserService userService = UserServiceFactory.getUserService();
   if (userService.isUserLoggedIn()) {
%>


    <% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); %>
    <form action="<%= blobstoreService.createUploadUrl("/uploadcsv") %>" method="post" enctype="multipart/form-data">
    	
		Tabela: <input type="text" name="kind" value="coleta3">
		</br></br>
		
		Separador: <input type="text" name="separator" value="¬">
		</br></br>
		
		Colunas(com separador): <input type="text" name="columns" style="width:70%">
        </br></br>
        (sem header)
        <input type="file" name="csvdata">
        <input type="hidden" name="email" value="<%= userService.getCurrentUser().getEmail() %>">
        </br></br>
        <input type="submit" value="Submit">
    </form>



<%
   } else {
%>
  
    <p>Login</p>
    <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Log in</a>
    
<%
   }
%>
</body>
</html>