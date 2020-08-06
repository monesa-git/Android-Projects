# Homework03
This is a weather app. This is just like the weather app in the android phones. The data are got from the accuweather.
User can add upto any number of cities. The user can set hte default city so that when the user opens again the default city will be shown in the top. The user can set a city as favorite so that it displays in the top of the list. When the user selects any city from the list The next page will display a forecast for that city for the next 5 days.

Required API Calls:
1. Location API : http://dataservice.accuweather.com/locations/v1/cities/{Country}/search?
apikey={YOUR_API_KEY}&q={CITY_NAME}
2. Current Conditions API: http://dataservice.accuweather.com/currentconditions/v1/
{CITY_UNIQUE_KEY}?apikey={YOUR_API_KEY}
 CITY_UNIQUE_KEY can be retrieved from JSON returned by the Location API
3. Forecast API, 5 Day Forecast for search cities: http://dataservice.accuweather.com/forecasts/v1/
daily/5day/{CITY_UNIQUE_KEY}?apikey={YOUR_API_KEY}
4. Weather ICON : http://developer.accuweather.com/sites/default/files/{Image_ID}-s.png
