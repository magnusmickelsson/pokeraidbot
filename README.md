# pokeraidbot
A Discord Pokemon go raiding bot, used to announce raids, sign up for them and get an overview.

#How?

* To get started, you need to create a Discord application via their developer site, and
register a Bot account for it. When doing this, you get an owner id (client id) and a token for your bot account.
* Add a property file to the classpath called pokeraidbot.properties, containing these properties:
    * ownerId=(Bot application's ownerId)
    * token=(Bot user token)
* Use this link to allow the application to access a Discord server:
https://discordapp.com/oauth2/authorize?&client_id=354980212112621570&scope=bot&permissions=0
(Replace client_id={something} with the client id you get when registering an application)
* Compile/build, then start the executable class BotServerMain.
* Go to your Discord server, and try running the command "!raid usage".
