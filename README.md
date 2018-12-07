# CardsApp

2018.12.01
Created a POC app called "CardsApp" with the following:
Support for 3 types of Cards "Home", "Streaming" & "Custom". The "Custom" card has configurable Title, Message and Action Message.
The order of the "Home", "Streaming" and x number of "Custom" cards is configurable by a JSONArray.
Note: Currently the cards can only be swiped horizontally, the JSONArray is currently hard-coded, and the Custom cards don't support any tap action yet.

2018.12.04
Wrote cards.php script to retrieve JSONArray of Cards. cards.php uses one parameter called 'version' to determine which json file to return, i.e. cards_<version>.json, e.g. cards_1.0.json. Sample request: http://www.droidsdoit.com/augmedix/cards.php?version=1.0
Updated CardsApp to load the cards on app startup using cards.php. If a failure occurs, e.g. due to no connectivity, then it defaults to showing the "Home" & "Streaming" cards.
