# DemoApp
Android app through which user can SignIn/SignUp through Google Account and show/update users profile information and save into firebase database.

** Version Name 1.0  **
** Version code 1 **

## Licence & Copyright ##

 Gaurav Sharma , CityLink Portal Private Limited.(Bangalore)

 **
   Once app will open    **

 **  checking if users is logged in **

 ** if loggedIn : then redirect users to profile details page and showing profile of users **

 ## if not leggedIn : redirect to logIn page



## SignIn Page ##

 * click on signUp button and goto register page .
 *users has to register himself by going to register page (on click of SignUp button).


                                                       ## FIREBASE LOGIN/REGISTER ##

 ## SignUp Page ##

 * fill all  details with valid name ,email and user image and click on SignUp button.
 * once user successfully registered  updating users database with extra info like full name ,profile photo ...and redirect  users back to login page .

  * once signUp  successfully logging  signUp event into firebase analytics.


 ## SignIn Page (Once user gets registered successfully) ##

  * enter valid  email and password . and calling to firebase authentication method ,it will check firebase data base if users is available in database if yes then return true and  loggedIn Successfully and redirect users to profile details page

  * if not it will show a valid reason received by firebase .
  * once successfully signIn users will be redirect to profile screen.
   * once signIn successfully logging signIn event into firebase analytics.

 ## Profile Page

  * once users starts  interacting  with profile screen mean it will get updated user data and updating the ui.
  * accept runtime permission for location once permission granted a foreground service will be start for fetching location data at the interval of every 5 sec.

  * fetching users location in every 5 sec using googleFusedApiProvider and updating firebase realtime database for location history.

  * once users moves away from profile screen  stop updating location and firebase realtime database.

  ## SignOut ##

  * in navigation logout option is there .once clicked on logout it will logout user from firebase db and redirect him to login page .
   * once logout successfully logging signOut  event into firebase analytics.


                                             ## GOOGLE Account SignIn LOGIN/Sign_Up ##


## users can signIn by google Account and show users googleAccount info in profile screen .

#SignInPage ##

 * click on button signIn with google btn ,it will fetch existing signIn google account and also provide a option for add new google account .

 * after click on certain account it will sigIn by that account and navigates to ProfileInfo with user details .

  * once signIn successfully logging signIn event into firebase analytics.

 ## Profile Page ##

 * if user details is available then it will update the ui and check for permission .
 * once runtime permission granted it will start location updates service to log the location data in firebase database  in every 5 sec.
 * once user came back it will stop updating location service .


 ## Sign Out  ##

 ** in navigation logout option is there .once clicked on logout it will check if user is signIn  with  google account then call function and
 logout user from firebase db and redirect users  to login page .

 * once logout successfully logging signOut  event into firebase analytics.



                                                 ##
                                                 firebase authentication /log event /location history/ firebase realtime database / ##


  ** 1.search url  https://console.firebase.google.com/       and signIn with google firebase developer console .

        ##// ***   Credentials
                    email id = gaurvaksharma.gks@gmail.com
                    password = shiva@mahadev

   **     Once signIn successfully with firebase go to console and search for project (SignInDemo) and go inside it .

   ** once SignInDemo firebase project open track all details here like .....

      **--firebase authentication --**
      **--click on RealTime Database and check users database and location history here --**
      **--click on events and check siginIn,signUp,and signOut events triggered by android app --**
      **--click on realtime for getting realtime app data--** and check dashboard--##
      **--click debugView for tracking DebugView of android demo app--**











