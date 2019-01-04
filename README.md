# CardsApp
2019.01.03<br/>
-Code cleanup using the CardManager and Card classes to convert the J… …SON card data.<br/>
-Implemented support for AUTO_DISMISS and UPDATE_FORCED cards.<br/>
-Added Server folder containing the cards.php and sample json files.<br/>

2018.12.01<br/>
Created a POC app called "CardsApp" with the following:<br/>
-Support for 3 types of Cards "Home", "Streaming" & "Custom". The "Custom" card has configurable Title, Message and Action Message.<br/>
-The order of the "Home", "Streaming" and x number of "Custom" cards is configurable by a JSONArray.<br/>
Note: Currently the cards can only be swiped horizontally, the JSONArray is currently hard-coded, and the Custom cards don't support any tap action yet.<br/>

2018.12.04<br/>
-Wrote cards.php script to retrieve JSONArray of Cards. cards.php uses one parameter called 'version' to determine which json file to return, i.e. cards_<version>.json, e.g. cards_1.0.json. Sample request: http://www.droidsdoit.com/augmedix/cards.php?version=1.0<br/>
-Updated CardsApp to load the cards on app startup using cards.php. If a failure occurs, e.g. due to no connectivity, then it defaults to showing the "Home" & "Streaming" cards.<br/>
  
2018.12.08<br/>
-Added support for caching JSON Cards data between app instances, which is now tracked by new JSON version number.<br/>
-Added support for temporary cards.<br/>
-Added to the app version number to main UI.<br/>
-Implemented support for upgrading the app using an JSON ‘action’ value of ‘UPDATE_OPTIONAL’.<br/>
-Implemented support for automatically restarting the app after an upgrade.<br/>
