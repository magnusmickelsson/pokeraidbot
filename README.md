# pokeraidbot
A Discord Pokemon go raiding bot, used to announce raids, sign up for them and get an overview.
Also contains commands for getting information about pokemons, pokestops and gyms.

## How?

* To get started, you need to create a Discord application via their 
[developer site](https://discordapp.com/developers/docs/intro), and register a Bot account for it. 
When doing this, you get an owner id (client id) and a token for your bot account.
* Clone this Git repository to your local machine: https://github.com/magnusmickelsson/pokeraidbot.git
* Add a property file to the classpath called pokeraidbot.properties (put it under src/main/resources/ and it's ok), 
containing these properties:
    * ownerId=(Bot application's ownerId)
    * token=(Bot user token)
* Build your application via [Maven](https://maven.apache.org) or a Java-IDE, for example 
[IntelliJ](https://www.jetbrains.com/idea/).
* Start the bot via the executable class main.BotServermain (or java -jar pokeraidbot.jar)
* Try browse 127.0.0.1:5000 - if it works you'll get a response from the bot
* Use this link to allow the application to access your Discord server:
https://discordapp.com/oauth2/authorize?&client_id=356483458316632074&scope=bot&permissions=0
(Replace client_id={something} with the client id you get when registering an application)
* Go to your Discord server, verify that the bot has logged in and is present.
* In the chat, try running the command "!raid usage".

## Branches

The simple bot, with command for map and showing pokemon information, is right now on master branch.

The full feature bot, with raid management, is right now on branch spring-boot. 
This branch has not been released yet and may contain bugs.

## Who?

Bot created by Magnus Mickelsson (right now, < 35h work has been put into it so cut me some slack).

Thanks for the support from Johan Millert and the people from the Pokemon Go Uppsala Discord server, primarily s1lence and Xandria.

## Notes

It's very likely you need to know a bit of Java and Spring Boot for this application to be useful to you,
in its current, source-code state.

## Data

Raid boss counter data copied from:

https://pokemongo.gamepress.gg/raid-boss-counters

Some data from https://raw.githubusercontent.com/BrunnerLivio/PokemonDataGraber/master/output.json

Gyms from Swepocks:
https://fusiontables.google.com/DataSource?docid=1hdTwBGdlonfgdZfU_zqeTTZYY5UgzVwT0Sh3iboA#rows:id=1
