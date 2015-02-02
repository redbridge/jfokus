# JFokus Demo App

Running on RedBridge Apps
----------------------------

Create an account at https://cloud.redbridge.se

Create a jboss-wildfly-8 application

    rhc app create jfokus jboss-wildfly-8 

Add this upstream jfokus repo

    cd jfokus
    git remote add upstream -m master https://github.com/redbridge/jfokus.git
    git pull -s recursive -X theirs upstream master
    
Then push the repo upstream

    git push

That's it, you can now checkout your application at:

    http://jfokus-$yournamespace.apps.redbridge.se
