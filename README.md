# DemoApp
Android app through which user can SignIn/SignUp through Google Account and show/update users profile information and save into firebase database.

** Version Name 1.0  **
** Version code 1 **

## Licence & Copyright ##

 Gaurav Sharma , CityLink Portal Private Limited.(Bangalore)

 **
   Once app has been open   **

 **  checking if users is logged in **

 ** if loggedIn : then redirect users to profile details page and showing profile of users **

 ## if not leggedIn : redirect to logIn page



## Login Page ##

 *users has to register himself by going to register page (on click of SignUp button).
 * once user successfully registered redirect back users to login page .
 * once email and password got entered .it will check firebase data base if users is available in database then loggedIn and return true and redirect users to profile details page
   and logging signIn ,signUp,logout events in firebase analytics.

 * once users starts  interacting  with profile screen mean while fetching users location in every 5 sec using googleFusedApiProvider and updating firebase realtime database for location history.

 * once users moves away from profile screen stop updating firebase realtime database.




## users can signIn by google Account and show users googleAccount info .##




