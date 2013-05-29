(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['menu'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [2,'>= 1.0.0-rc.3'];
helpers = helpers || Handlebars.helpers; data = data || {};
  


  return "<div class=\"navbar navbar-inverse navbar-fixed-top\">\r\n	<div class=\"navbar-inner\">\r\n		<div class=\"container\">\r\n			<a class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">\r\n				<span class=\"icon-bar\"></span>\r\n				<span class=\"icon-bar\"></span>\r\n				<span class=\"icon-bar\"></span>\r\n			</a>\r\n			<a class=\"brand replace\" href=\"#\">lang1SvName</a>\r\n			<div class=\"nav-collapse collapse\">\r\n				<ul class=\"nav\">\r\n					<li class=\"active\"><a href=\"index.html\" class=\"replace\">lang1Home</a></li>\r\n					<li><a href=\"auction.html\" class=\"replace\">lang1Auction</a></li>\r\n					<li class=\"dropdown\">\r\n						<a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">My Settings <b class=\"caret\"></b></a>\r\n						<ul class=\"dropdown-menu\">\r\n							<li class=\"nav-header\">Options</li>\r\n							<li><a href=\"myitems.html\" class=\"replace\">lang1My_itens</a></li>\r\n							<li><a href=\"myauctions.html\" class=\"replace\">lang1My_auction</a></li>\r\n							<li><a href=\"withlist.html\" class=\"replace\">With List</a></li>\r\n							<li class=\"divider\"></li>\r\n							<li class=\"nav-header\">Info</li>\r\n							<li><a href=\"mail.html\" class=\"replace\">Mail</a></li>\r\n							<li><a href=\"signs.html\" class=\"replace\">Signs</a></li>\r\n						</ul>\r\n					</li>\r\n					<li><a href=\"logout\" class=\"replace\">lang1Logout</a></li>\r\n				</ul>\r\n			</div>\r\n			<!--/.nav-collapse -->\r\n		</div>\r\n	</div>\r\n</div>";
  });
})();