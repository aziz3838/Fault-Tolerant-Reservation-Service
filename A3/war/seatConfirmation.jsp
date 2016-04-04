<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.UBC513.A3.Helpers.HandleRequests;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Seat Reservation Status</title>

<script src="http://code.jquery.com/jquery-1.10.2.js" type="text/javascript"></script> 
</script>
<script>
	$(document).ready(function() {
	$('#submit').click(function(event) {
			var data = $('form').serialize();
			$.get('status?' + data,function(responseText) {
				$('#welcometext').text(responseText);
			});
		});
	});
</script>

<link href="Site.css" rel="Stylesheet" type="text/css"/>
</head>
<body>

<div class="wrapper">
	<div id="Content">
		<div id="Header"><H1>Reserve Seat(s)</H1></div>
		<hr/>
		<div id="MainContent">

			<h3>Thank you <%=request.getParameter("FirstName") %> <%=request.getParameter("LastName") %></h3> 		
			
			We have successfully reserved your order!<br/><br/> 

			<form id="form1">
			
			<input type="hidden" name="FirstName" value="<%=request.getParameter("FirstName") %>"/>
			<input type="hidden" name="LastName" value="<%=request.getParameter("LastName") %>"/>
			<input type="hidden" name="waitlist" value="<%=request.getParameter("waitlist") %>"/>
			<% for(int flightIdx=0; flightIdx<4; flightIdx++) { %>
				<input type="hidden" name="Flight<%=flightIdx%>" value="<%=request.getParameter("Flight" + flightIdx) %>"/>
				<input type="hidden" name="SeatID<%=flightIdx%>" value="<%=request.getParameter("SeatID" + flightIdx) %>"/>
			<% } %>
			
								
			<input type="button" id="submit" value="Get Real-time Status of Your Order"/>
			<div id="welcometext">
			<br>
			</div>
			</form>
			
			
			<a href="/">Reserve another seat.</a>

		</div><!-- end MainContent -->
	</div><!-- end Content -->
</div><!-- end wrapper -->


			
</body>
</html>