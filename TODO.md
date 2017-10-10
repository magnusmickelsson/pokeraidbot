Done:

See [Changelog](CHANGELOG.md).

Needs testing:

* In the !raid list response, add a sentence about using !raid status (gym) to see details or create a group and that 
!raid man signup can be used to help with signups
* Remove team buttons from raid group signup
* Change from embedmessages when links are concerned, doesn't work properly on iPhone whereas normal messages do

Being developed:

-

Discussion:
* Handling of groups, moving signups, removing signups etc.

Experiment with:

* Can we listen for +(number) (time) (gym) to signup using that?
* Ability to create channels on the fly to put certain raids in

Fix issue:

* Better error message if bot doesn't have correct rights on server, and give info to admin on what rights
to set

Fix, misc:
* Uniqueconstraints to prevent in database that the same user can signup more than once for a certain raid.

Do, features:

* !raid me - Answer in PM with my current signups over all raids in the region and my current trackings 
* Admin commands where bot owner can purge configuration, get statistics, help a user (send man help via DM?)
* !raid install needs to be "protected" so admins can only affect the configuration of their own server
(this may mean the install command has to be run in the context of a server chat so we can fetch server name)
* "What's new"-command so people can see what new features.
* Timezone handling via config, used in all commands related to time
* Credit Iconninja for icons
* Check permissions during config - need to have MANAGE_MESSAGE and access to send messages, among other things
* Enable channel admins to choose their default locale 
(and that the system doesn't have a default locale even for exceptions etc - also remove use of !raid help if possible)
* Embeddedlänkar vid sökning som möjliggör att köra kommandot igen för bara det resultatet via klick

Maybe, features:

* Nytt förslag; ifall man skriver "!raid info --dm" så ska infon skickas i PM istället för att visas i kanalen
* Se om det är möjligt att automatiskt adda raider inom ett område man satt upp från gymhuntr.com (deras bot)
* Config should have a note if a server is TEST or PROD and a env property which type of server,
so we can't get test config on a prod server running by mistake
* Add counters/counter moves to all pokemon, based on "good dps pokemons" for each type?
* Make locale configurable both on server level, but also for each user to be able to choose locale
* Persistent tracking (needs non-free Heroku version)
* Enable bot owner to trigger import of a regional dataset "on the fly" and save gym region data in database 
(needs non-free Heroku version)
* Checking error codes?
...
