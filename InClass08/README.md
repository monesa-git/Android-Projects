# InClass08

In this project I used HTTP connections, authentication, and implemented an app to send e-mails. The API details are provided in  For authentication we needed to pass the token returned from login api as part of the header. OkHttp and Shared Preferences to implement the application. 

API Description 
/api/login Login POST No
/api/signup Signup POST No
/api/inbox Get all the emails GET Yes
/api/users Get registered users POST Yes
/api/inbox/add Send an email POST Yes
/api/inbox/delete/{id} Delete email from inbox GET Yes

The user can sign in and sign up and send emails to the users. User can delete the emails as well.