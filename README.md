# pokeraidbot
A Discord Pokemon go raiding bot, used to announce raids, sign up for them and get an overview.
Also contains commands for getting information about pokemons, pokestops and gyms.

## What is this?

Best to show some screenshots I guess. These are in Swedish, but there is also English locale.

Command to get map of a gym:

![Map command](mapcmd.png)
![Map response](mapcmdresponse.png)

Command to get raid boss info:

![Raidboss info command](pokecmd.png)
![Raidboss response](pokecmdresponse.png)

Raid commands:

![Raid commands](raidcmd.png)

## Support development

<a href='https://pledgie.com/campaigns/34823'><img alt='Click here to lend your support to: pokeraidbot and make a donation at pledgie.com !' src='https://pledgie.com/campaigns/34823.png?skin_name=chrome' border='0'></a>

## Prerequisities

* You need to have Java 8 installed
* You need to have Maven 3+ installed
* You need to be administrator on your local machine
* It's probably a good idea if you know a bit about Java development, and Spring Boot
* You need to be administrator of a Discord server with permission to add a bot

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
* Start the bot via the executable class **main.BotServerMain** (or java -jar pokeraidbot.jar)
* Try browse 127.0.0.1:5000 - if it works you'll get a response from the bot
* Use this link to allow the application to access your Discord server:
https://discordapp.com/oauth2/authorize?&client_id=356483458316632074&scope=bot&permissions=0
(Replace client_id={something} with the client id you get when registering an application)
* Go to your Discord server, verify that the bot has logged in and is present.
* In the chat, try running the command "!raid usage".

## Going into production

### How do I get the gym data for MY region in there?
Since you probably don't want the gyms in your bot to be those in Uppsala, Sweden, like it is
in the repo (due to this bot being made for the Uppsala discord), here are instructions on how to fix that. 

First check the file gyms_uppsala.csv to see an example of the data such a file should have. 
It's a good idea to keep the uppsala file around though so you don't have to change the JUnit test suite.

The recommended way to create a dataset for your region is to use the **GymDataImportTool** class. Run this mainclass:

        dataimport.GymDataImportTool

.. with these parameters: {number of kilometres which will be a side of a "box" we get gyms from} {search entry for region centre (will be centre of the "box")}

Example:

        dataimport.GymDataImportTool 20 falun
        
.. will create a dataset file called falun.csv under {project_dir}/target/ folder, which contains
all gyms/raids within 10 km of the centre of the city Falun in Sweden (centre as defined by Google maps).

Copy this file into src/main/resources/ - name it gyms_falun.csv and add an entry to the configuration in the 
BotServerMain class that maps your server name to use the "falun" region, and your server will now have access to data from Falun.

Example of the configuration you need to edit:

    @Bean
    public ConfigRepository getConfigRepository() {
        final HashMap<String, Config> configurationMap = new HashMap<>();
        configurationMap.put("zhorhn tests stuff", new Config("uppsala"));
        configurationMap.put("pokeraidbot_beta", new Config("stockholm"));
        configurationMap.put("pokeraidbot_testing", new Config("luleå"));
        return new ConfigRepository(configurationMap);
    }

### Hosting
I'd recommend you deploy your adapted bot to a cloud service, or use a local server you know works and will be up.

Personally, I used Heroku's free service, available here:

http://herokuapp.com

To get started, create an account there and then follow these instructions (after you've got it working locally, obviously):

https://devcenter.heroku.com/articles/getting-started-with-java#introduction

Things to note about Heroku:
* Choose the correct region when you create your Heroku application; eu or us! Example for eu:

        heroku create {app_name} --region=eu

* Choose the correct timezone for your application, for example: 
    
        heroku config:add TZ="Europe/Stockholm"
        
The bot is currently deployed on a Heroku node in EU backed by a Postgresql database, at Stockholm timezone,
here: https://pokeraidbot2.herokuapp.com

## Branches

The simple bot, with command for map and showing pokemon information, is right now on master branch.

The full feature bot, with raid management, is right now on branch raid-features. 
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
