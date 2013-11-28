///////////////////////////////////////////////
//
//   Language for index.html
//
///////////////////////////////////////////////

var langIndex = {
    'lang1SvName': 'WebPortal',
    ///////////////////
    //	Painel
    ///////////////////
    'lang1Name': 'Name',
    'lang1Money': 'Money $',
    'lang1Admin': 'Admin',
    'lang1Mail': 'Mail',
    
    ///////////////////
    //	Menu
    ///////////////////
    'lang1Title': 'Shop',
    'lang1Home': 'Home',
    'lang1Shop': 'Shop',
    'lang1My_itens': 'Items',
    'lang1Settings': 'Settings',
    'lang1Options': 'Options',
    'lang1Info': 'Info',
    'lang1Sell': 'Sell',
    'lang1Buy': 'Buy',
    'lang1Signs': 'Signs',
    'lang1ItemInfo': 'Item Info',
    'lang1Logout': 'Log out',

    ///////////////////
    //	Menu Index
    ///////////////////
    'langItem_info': 'Item Info',
    'langSeller': 'Seller',
    'langExpires': 'Expires',
    'langID': 'ID',
    'langQuantity': 'Quantity',
    'langPrice_each': 'Price (Each)',
    'langPrice_total': 'Price (Total)',
    'langMarket_price': '% of Market Price',
    'langbuy': 'Buy',
    'langcancel': 'Cancel',
    'langNever': 'Never',
    'langNoauction': 'No auctions to display',
    'langSearchfor': 'Item Id or Player Name:',
    'langEnchant': 'Enchant',
    'langDurUse': 'Durability ( Uses/Total )',

    ///////////////////
    //	My Itens
    ///////////////////
    'langMyItensTitle': 'My Items',
    'langMailMe': 'Mail it',
    'langMymarket_price_each': 'Market Price (Each)',
    'langMymarket_price_total': 'Market Price (Total)',
    'langCreateSale': 'Create Sale',
    'langSendItemMail': 'Send Item Mail',
    'langPrice': 'Price',
    
    ///////////////////
    //	Sell
    ///////////////////
    'langSell': 'Sell',
    'langYourSales': 'Your Sales',
    'langAuctionItem': 'Item',
    'langMyauction_expires': 'Expires',
    'langCancel': 'Cancel',

    ///////////////////
    //	Buy
    ///////////////////
    'langWant':'What Item do you want ?',
    'langBuy': 'Buy list',
    'langItemId': 'Item Id ( x or x:x )',
    'langHelp': 'Help',
    
     ///////////////////
     //	Shop
    ///////////////////
     'langAll':'All',
     'langBlocks': 'Blocks',
     'langCombat': 'Combat',
     'langTools': 'Tools',
     'langFood': 'Food',
     'langDecoration': 'Decoration',
     'langRedstone': 'Redstone',
     'langTransportation': 'Transportation',
     'langMicellaneous': 'Miscellaneous',
     'langMaterials': 'Materials',
     'langBrewing':'Brewing',
     'langOthers': 'Others',

    ///////////////////
    //	Admin
    ///////////////////
     'langAdmListItem': 'List Items ( Admin Shop )',
     'langAdmAddItem': 'Add Item ( Admin Shop )',
     'langPlayerInfo': 'Player Website Info',
     'langInfo': 'Info',
     'langItem': 'Item',
     'langTransaction': 'Transaction',
     'langQtdInfo': 'Quantity 9999 for Infinity',
     'langCare' : 'Use with care',
    
	///////////////////
	//	Mail
	///////////////////
	 'langMyMail': 'My Mail',
	 
    ///////////////////
    //	Table
    ///////////////////
     'langPrevious': 'Previous',
     'langNext': 'Next',
     'langShowing':'Showing',
     'langOf':'of'
};

///////////////////////////////////////////////
//
//   Language for login.html
//
///////////////////////////////////////////////

var langLogin = {
    'lang1SvName': 'WebPortal',
    'langWarning': 'Create an account by typing /wa password [Your Password] on the server!',
    'langUsername': 'Username',
    'langPassword': 'Password',
    'langSubmit': 'Sign in',
    'langLast': 'Last 10 auctions.'
};

// NEED TO CHANGE THIS TO YOUR IP
// DO NOT remove http://
// Ex : http://123.123.123.123:25900
var qualifyURL = function (url) {
    return "http://localhost:25900" + url;
};