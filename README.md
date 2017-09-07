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

## Notes

## Data

Some data from https://raw.githubusercontent.com/BrunnerLivio/PokemonDataGraber/master/output.json

https://gist.github.com/anonymous/077d6dea82d58b8febde54ae9729b1bf

Gyms:
https://fusiontables.google.com/DataSource?docid=1hdTwBGdlonfgdZfU_zqeTTZYY5UgzVwT0Sh3iboA#rows:id=1

## See

https://github.com/gnufred/pgmicrotypedex

https://github.com/FoglyOgly/Meowth

http://pokeapi.co/docsv2/

http://pokeapi.co/api/v2/pokemon/200/
https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/200.png

Google static map API key: AIzaSyAZm7JLojr2KaUvkeHEpHh0Y-zPwP3dpCU

https://maps.googleapis.com/maps/api/staticmap?center=59.883035,17.661403&zoom=14&size=400x400&maptype=roadmap&markers=icon:http://millert.se/pogo/marker_xsmall.png%7C59.883035,17.661403&key=AIzaSyAZm7JLojr2KaUvkeHEpHh0Y-zPwP3dpCU

https://maps.googleapis.com/maps/api/staticmap?center=59.844542,17.63993&zoom=14&size=400x400&markers=icon:http://millert.se/pogo/marker_xsmall.png%7C59.844542,17.63993

## Reply to (when done)
https://www.reddit.com/r/pokemongodev/comments/6yk2ld/discord_bot_for_raid/