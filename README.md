# JFokus Demo App

Running on RedBridge Apps
----------------------------

Create an account at https://cloud.redbridge.se

Create a wildfly8 application

    rhc app create wildfly8 jboss-wildfly-8 

Add this upstream flask repo

    cd wildfly8
    git remote add upstream -m master https://github.com/redbridge/jfokus.git
    git pull -s recursive -X theirs upstream master
    
Then push the repo upstream

    git push

That's it, you can now checkout your application at:

    http://wildfly8-$yournamespace.apps.redbridge.se
