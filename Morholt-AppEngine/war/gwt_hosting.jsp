<!-- gwt-hosting.jsp from http://www.gwtproject.org/articles/dynamic_host_page.html-->
<%@page import="com.google.gwt.core.client.GWT"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.gwt.user.client.Window"%>
<%@page import="com.google.gwt.user.client.Window.Location"%>


<!doctype html>
<html>
 <head>
   <style>
   
body {
	background:#d6e9f8;
}
   
h1 {
    font-family:verdana;
    font-size:300%;
}
p  {
    font-family:verdana;
    font-size:160%;
}

a {
    font-family:verdana;
    font-size:120%;

}
</style>

<meta name="gwt:property" content="locale=<%=request.getLocale()%>">

<%
   UserService userService = UserServiceFactory.getUserService();
   if (userService.isUserLoggedIn()) {
%>
    <script type="text/javascript" src="arqueologia_appengine/arqueologia_appengine.nocache.js"></script>
    


	<script src="arqueologia_appengine/markerclusterer_compiled.js"></script>
	<script src="arqueologia_appengine/oms.js"></script>
  

  
    <link type="text/css" rel="stylesheet" href="Arqueologia_AppEngine.css">
    
    <script type="text/javascript">
      var info = { "email" : "<%= userService.getCurrentUser().getEmail() %>", "logoutUrl" : "<%= userService.createLogoutURL(request.getRequestURI()) %>", 
    		       "authDomain" : "<%= userService.getCurrentUser().getAuthDomain() %>", "userId" : "<%= userService.getCurrentUser().getUserId() %>",
    		       "federatedId" : "<%= userService.getCurrentUser().getFederatedIdentity() %>",
    		       "locale" : "<%=request.getLocale()%>"};
      
    		       
    </script>
    
    
    


  </head>
  <body>
  
<%
   } else {
%>
  
  </head>
  <body>
      <p>Bem vindo ao Sítio do Físico,</p>

  
    <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Log in</a>
    
    <br/><br/><br/>
    
    <a href='<%= request.getContextPath() +"anonymous.jsp" %>'>Entrar anonimamente</a>
    
<%
   }
%>
 </body>
</html>