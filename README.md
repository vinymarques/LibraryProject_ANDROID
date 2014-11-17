LibraryProject_ANDROID
======================

minimum SDK: API 8 - Android 2.2

SDK target: API 19 - Android 4.4 ( KITKAT )

API: https://github.com/vinymarques/LibraryProject_API


INSTRUCTIONS

*Download and unzip the LibraryProject_API
-Start the web service (command): cd <directory API>  ->  rails server

*Download and unzip the LibraryProject_ANDROID

*Change APIHost in the Base.java:  Library /src /br /com /rubythree /library /models /Base.java
 line 5:  public static String APIHost  = "192.168.0.10:3000" ;
 - change to: public static String APIHost  = "<address IP>:3000" ;
 
*Run the project Library using your adt eclipse
