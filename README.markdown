Samurai Java Example App
========================

Requirements
------------

To run the application you need the Play Framework, which can be found at http://www.playframework.org 
Direct download url: http://download.playframework.org/releases/play-1.2.2.zip

Configuration
-------------

Before running the application edit the configuration file:

    conf/application.conf 

and provide the following:

    samurai.merchant_key
    samurai.merchant_password
    samurai.processor_token
       
Running
-------

To run the application invoke the play command from the Play Framework with two arguments: 
* 'run'
* the path to the directory with the example application

```
$ play run samurai_java_example_app
~        _            _ 
~  _ __ | | __ _ _  _| |
~ | '_ \| |/ _' | || |_|
~ |  __/|_|\____|\__ (_)
~ |_|            |__/   
~
~ play! 1.2.2, http://www.playframework.org
~
~ Ctrl+C to stop
~ 
Listening for transport dt_socket at address: 8000
12:23:47,493 INFO  ~ Starting /Users/adko/Projects/samurai/samurai_java_example_app
12:23:47,498 WARN  ~ Declaring modules in application.conf is deprecated. Use dependencies.yml instead (module.crud)
12:23:47,499 INFO  ~ Module crud is available (/Users/adko/Projects/samurai/lib/play-1.2.2/play-1.2.2/modules/crud)
12:23:48,380 WARN  ~ You're running Play! in DEV mode
12:23:48,496 INFO  ~ Listening for HTTP on port 9000 (Waiting a first request to start) ...
```
   
Testing
-------

The application is available at:
    http://localhost:9000