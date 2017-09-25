Done:

* Raid list, sort by pokemon then by time (The FInal Shadow)
* Command to list server config
* Donate-command
* !raid list {pokemon} filters raids based on pokemon
* Fixed: raid track sends message whether the command was ok or not. The message should say why they
are getting the message (tracking), and also instructions on how to remove tracking.
* Fixed (always shows start/end): If endtime is more than 1 hour from current time, 
also include start/hatch time in message for !raid list and !raid status
* !raid untrack {pokemon} - !raid untrack without params clears all tracking

Fix:

* Clear all raids at the end of raiding for the day via scheduled job (22:00) - bot send message about it.

Maybe:

* Defaultconfig för kanal kan sättas och ändras i runtime av admin.
* Bättre raid status overview (inklusive lag och knappar för att registrera fler signups)
* Registrera lag vid signup, Registrera lag automatiskt vid signup baserat på roller
* Enable channel admins to configure a server, to choose region/dataset and default locale
* Config command should enable servers to be configured in runtime on the fly (but only by bot owner)
* Embeddedlänkar vid sökning som möjliggör att köra kommandot igen för bara det resultatet
* Persistent tracking for users of pokemons
* Delete raid command, for channel admin/owner only
* Nytt förslag; ifall man skriver "!raid info pm" så ska infon skickas i PM istället för att visas i kanalen
* Se om det är möjligt att automatiskt adda raider inom ett område man satt upp från gymhuntr.com (deras bot)
* Config should be put in the database, but synch with config during startup
* Config should have a note if a server is TEST or PROD and a env property which type of server,
so we can't get test config on a prod server running by mistake
* Add counters/counter moves to all pokemon, based on "good dps pokemons" for each type?
* Make locale configurable both on server level, but also for each user to be able to choose locale
* Enable bot owner to trigger import of a regional dataset "on the fly" and save gym region data in database 
(needs non-free Heroku version)
* Checking error codes?
...
