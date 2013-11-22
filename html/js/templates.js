(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['menu.html'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [2,'>= 1.0.0-rc.3'];
helpers = helpers || Handlebars.helpers; data = data || {};
  


  return "<div class=\"navbar navbar-inverse navbar-fixed-top\">\r\n	<div class=\"navbar-inner\">\r\n		<div class=\"container\">\r\n			<a class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">\r\n				<span class=\"icon-bar\"></span>\r\n				<span class=\"icon-bar\"></span>\r\n				<span class=\"icon-bar\"></span>\r\n			</a>\r\n			<a class=\"brand replace\" href=\"#\">lang1SvName</a>\r\n			<div class=\"nav-collapse collapse\">\r\n				<ul class=\"nav\">\r\n					<li class=\"active\"><a href=\"index.html\" class=\"replace\">lang1Home</a></li>\r\n					<li><a href=\"shop.html\" class=\"replace\">lang1Shop</a></li>\r\n					<li class=\"dropdown\">\r\n						<a href=\"#\" class=\"dropdown-toggle replace\" data-toggle=\"dropdown\">lang1Settings <b class=\"caret\"></b></a>\r\n						<ul class=\"dropdown-menu\">\r\n							<li class=\"nav-header replace\">lang1Options</li>\r\n							<li><a href=\"myitems.html\" class=\"replace\">lang1My_itens</a></li>\r\n							<li><a href=\"sell.html\" class=\"replace\">lang1Sell</a></li>\r\n							<li><a href=\"buy.html\" class=\"replace\">lang1Buy</a></li>\r\n							<li class=\"divider\"></li>\r\n							<li class=\"nav-header replace\">lang1Info</li>\r\n							<li><a href=\"mail.html\" class=\"replace\">lang1Mail</a></li>\r\n							<li><a href=\"signs.html\" class=\"replace\">lang1Signs</a></li>\r\n						</ul>\r\n					</li>\r\n					<li><a href=\"logout\" class=\"replace\">lang1Logout</a></li>\r\n				</ul>\r\n			</div>\r\n			<!--/.nav-collapse -->\r\n		</div>\r\n	</div>\r\n</div>";
  });
})();