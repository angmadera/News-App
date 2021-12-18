# News-App

This app displays current news articles from a wide variety of news sources covering a range of news categories.
![news1.png](https://github.com/angmadera/images/blob/main/news1.png?raw=true) ![news10.png](https://github.com/angmadera/images/blob/main/news10.png?raw=true)

## App Highlights

1) NewsAPI.org is used to acquire the news sources and news articles.
2) Selecting a news source (i.e., CNN, Time, etc.) displays up to 10 top stories from that news source.
3) Selecting a topic limits the news source choices to only those offering that topic of news.
4) Selecting a country limits news source choices to those from the selected country.
5) Selecting a language limits the news source choices to only those offering news in the selected language.
6) News articles are viewed by swiping right to read the next article, and left to go back to the previous article.
7) No separate landscape layout was developed.
8) The user can go to the complete extended article on the news source’s website by clicking on the articletitle, text, or image content.
9) An added professional looking launcher icon to the app.
10) The app saves and restores the state when transitioning between portrait and landscape layouts to ensure a smooth user experience. Without this, the app reverts to the start state upon rotation.
11) Display the news categories in the options menu different colors, and then color the news sources in the drawer according to their category.


## Menu Behavior Examples
Filter News Sources by Topic (Business) and Language (German) – then view the news from source “Handelsblatt”:

![news2.png](https://github.com/angmadera/images/blob/main/news2.png?raw=true) ![news6.png](https://github.com/angmadera/images/blob/main/news6.png?raw=true) ![news7.png](https://github.com/angmadera/images/blob/main/news7.png?raw=true) ![news8.png](https://github.com/angmadera/images/blob/main/news8.png?raw=true) ![news9.png](https://github.com/angmadera/images/blob/main/news9.png?raw=true) 

Filter News Sources by Language (French) – then view the news from source “Le Monde”:

![news2.png](https://github.com/angmadera/images/blob/main/news2.png?raw=true) ![news3.png](https://github.com/angmadera/images/blob/main/news3.png?raw=true) ![news4.png](https://github.com/angmadera/images/blob/main/news4.png?raw=true) ![news5.png](https://github.com/angmadera/images/blob/main/news5.png?raw=true)

Filter News Sources by Country (United Kingdom) and Topic (Sports) – then view the news from source “BBC Sport”:

![news2.png](https://github.com/angmadera/images/blob/main/news2.png?raw=true) ![news11.png](https://github.com/angmadera/images/blob/main/news11.png?raw=true) ![news6.png](https://github.com/angmadera/images/blob/main/news6.png?raw=true) ![news12.png](https://github.com/angmadera/images/blob/main/news12.png?raw=true) ![news13.png](https://github.com/angmadera/images/blob/main/news13.png?raw=true)

## News Data
Acquiring news source and news article data will be done via the NewsAPI.org news aggregation service. This service allows the apownload news sources and news articles (by news source).

The NewsAPI.org service offers 2 API calls – one to get news sources (organizations offering news – CNN, Time, etc.) and one to get top news articles from a selected news source.

## Application Behavior Diagrams
### Startup:

Topics, Countries, and Languages can be selected using the options menu. Note these top-level menus should always be present. The content displayed under each of these menus is dynamic – based upon the data retrieved from the news sources.

A News Source (i.e., “cnn”) can be selected by opening the “drawer” and selecting a source.

A news-related background is shown before any articles are loaded (you find a news related background image for
yourself)

### Selecting Topics, Countries, and Languages:

Note that the content found in the Topics, Countries, and Languages options-menu “submenus” are not hard-coded. Their content comes from the data retrieved from the news sources.

For example, the unique list of all news topics from all sources in this example is business, entertainment, general, health, science, sports, and technology. Those topics are used to populate the “Topics” submenu (with the default  “all” also added to the submenu). Note that the unique set of topics changes over time and should NOT be hard-coded.

The Countries and Languages submenus are populated in the same fashion.

Selecting a Topic, Language or Country will reduce the content of the News Sources drawer-list by eliminating those that do not match the specified Topic, Language or Country criteria.

If the specified criteria result in no News Sources at all, an AlertDialog should be displayed indicating that no sources exist that match the specified Topic, Language and/or Country

### Selecting a News Source:

Swipe Right (or Left) to scroll through articles from the selected new source:

Click on article title, image, or text to go to extended article on the news source web site:

News Source Data Representation:

The full news source list should always be stored as-is from the API, unchanged (nothing removed). Another “current” list of sources should be used to hold the “current” sources to be displayed in the drawer. This “current” list of sources will change based upon the Topic, Language or Country selections. The list of current sources should contain all sources at startup (as no Topic, Language or Country has yet been selected). When a criteria selection is made (i.e., the “business” topic), the “current” source list should be updated to contain only the sources that have “business” as the topic. If some additional criteria is then selected (i.e., “United Kingdom” selected for the country) the current source list should be updated again to contain only the sources that have “business” as the topic and “United Kingdom” as the country.
