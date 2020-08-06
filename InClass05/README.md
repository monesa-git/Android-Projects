# InClass05
In this assignment I made a Photo Gallery application, which will use a URL that retrieves a text file containing a dictionary of keywords and image URLs related to the associated keyword. The application consists of a single activity that enables the
user to download and view online photos.
APIs:
• (Get Keywords API) To get the list of possible keywords:
• URL : http://dev.theappsdr.com/apis/photos/keywords.php
• Method : GET
• Output : returns the possible keywords separated by “;”. For example:
android;aurora;uncc;winter;wonders
• (Get Urls API) To get photos for a given keyword:
• URL : http://dev.theappsdr.com/apis/photos/index.php
• Method : GET
• Parameter:
• keyword : is the keyword selected by the user.
• Output: returns the photo URLs for images related to this keyword with each URL
on a separate line. For example, for a keyword parameter set to ‘aurora,’ the
following output is returned:
https://farm4.staticflickr.com/3340/3258641532_739f86c18d_z.jpg
https://farm8.staticflickr.com/7631/16859293891_cccaacfe9c_z.jpg
