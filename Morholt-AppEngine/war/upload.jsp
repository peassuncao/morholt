<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 
<%@page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Upload your CSV file here</title>
</head>
<body>
    <% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); %>
    <form action="<%= blobstoreService.createUploadUrl("/uploadcsv") %>" method="post" enctype="multipart/form-data">
        <input type="file" name="data">
        <input type="submit" value="Submit">
    </form>
</body>
</html>