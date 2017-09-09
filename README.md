# pokeraidbot
A Discord Pokemon go raiding bot, used to announce raids, sign up for them and get an overview.
Also contains commands for getting information about pokemons, pokestops and gyms.

## How?

* To get started, you need to create a Discord application via their developer site, and
register a Bot account for it. When doing this, you get an owner id (client id) and a token for your bot account.
* Add a property file to the classpath called pokeraidbot.properties, containing these properties:
    * ownerId=(Bot application's ownerId)
    * token=(Bot user token)
* Use this link to allow the application to access a Discord server:
https://discordapp.com/oauth2/authorize?&client_id=354980212112621570&scope=bot&permissions=0
(Replace client_id={something} with the client id you get when registering an application)
* Compile/build, then start the executable class BotServerMain.
* Go to your Discord server, verify that the bot has logged in and is present.
* In the chat, try running the command "!raid usage".

## Who?

Bot created by Magnus Mickelsson (right now, < 20h work has been put into it so cut me some slack).

Thanks for the support from Johan Millert and the people from the Pokemon Go Uppsala Discord server, primarily s1lence and Xandria.

## Notes

Right now, the application is not backed by a database. That means, that if the bot is restarted,
all active raids are gone.

Suffice to say, database support will be added asap.

## Data

Raid boss counter data copied from:

https://pokemongo.gamepress.gg/raid-boss-counters

Some data from https://raw.githubusercontent.com/BrunnerLivio/PokemonDataGraber/master/output.json

Gyms from Swepocks:
https://fusiontables.google.com/DataSource?docid=1hdTwBGdlonfgdZfU_zqeTTZYY5UgzVwT0Sh3iboA#rows:id=1
