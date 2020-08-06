# HomeWork02
In this project, I developed a Music Search Application using iTunes API. You can search any music related Key Word (track title, artist name, etc.) and display the search results. Your application should be able to view the details of the selected music track. You will be using JSON parsing to retrieve the music tracks, dynamic layout/ ListView to display the list, and a different activity to show the details of the selected music track. 

The assignment consists of two screens:
1. Main screen.
2. Display Details screen.

JSON API: Used iTunes search API to develop the app. Please read the
following for the API details: 
API URL https://itunes.apple.com/search?term=<Search Keyword/s>&limit=<# of Results> term Keyword/s you put in the search bar. If you put two or more keywords in the search bar like in our example, you should modify the URL to https://itunes.apple.com/search?term=jack+johnson&limit=25 limit It should be taken from the SeekBar. The minimum value should be 10, and maximum value should be 30.
Track name: use trackName from JSON
Genre: use primaryGenreName from JSON
Artist: use artistName from JSON
Album: use collectionName from JSON
Track Price: use trackPrice from JSON
Album Price: use collectionPrice from JSON
