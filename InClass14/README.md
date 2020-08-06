# InClass14
Built a city explorer app. In this we can add new Trips, add new places in the trips, delete places and display a map of the trip. Used the Google Places API. The launcher screen will display the saved trips, which should be retrieved from Firebase. Each trip has a trip name and city information. In addition, each trip also maintain a list of places that will be included in the trip. 
Used https://maps.googleapis.com/maps/api/place/autocomplete/json url to get the list of cities.
The geo coordinates are got from https://maps.googleapis.com/maps/api/place/details/json url
And the places close to the selected city are got from this api
https://maps.googleapis.com/maps/api/place/nearbysearch/json