Done:

See [Changelog](CHANGELOG.md).

Needs testing:

-

Being developed:

* Complete English locale handling and do a English language deploy of the bot
* !raid overview - admin only command that creates a message updated every minute between 07-22 and does a raid list (with params) -
will enable admins to have overviews in separate channels which are read-only

Discussion:

- 

Experiment with:

* Can we listen for +(number) (time) (gym) and fix possible user weirdness like forgetting time (equalling no time to "now", if raid is active) to signup using that?
* Ability to create channels on the fly to put certain raids in
* Read Pokemon go screenshot to create raid automatically (Swepocks)

Fix issue:

* Raid ex raid group - need to accept date and time, right now gives error
* Better error message if bot doesn't have correct rights on server, and give info to admin on what rights
to set
* Under some circumstances regarding unsign of a raid group it seems that there is an exception that ends up in the logs like this:
2017-10-12 22:41:15.237  WARN 6116 --- [inWS-ReadThread] p.d.r.s.EmoticonSignUpMessageListener    : We have a situation where user or exception message is null! Event: net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent@3d36a3c1
- doesn't seem to break anything but needs checking.

Fix, misc:
* Message in !raid add that you can do +1 instead
* raid group removed 5 mins after finished group, not at exact time
* Regexp to remove all duplicate or more spaces in between arguments
* Uniqueconstraints to prevent in database that the same user can signup more than once for a certain raid.

Do, features:

* Input parameter to application with default locale (en or sv)
* Gymhuntr integration via GymhuntrMessageListener (isBot())
* Emote to mark group as having finished (removes all signups) - only group creator?
* Emote to move signup
* Emote to remove signup from group
* Config for permission setting of various groups of commands:
    * Raid functions like create, list can be set to be admin only
    * Raid group creation can be set to admin only
* Config for time of automatic message deletion
    * Number of seconds, or -1 = never delete
* Config for turning on and off some feature groups (map, info, raid)
* The big feedback windows could have emotes for removing the message, and for updating it with latest info (raid status)
* Enable bot owner to trigger import of a regional dataset "on the fly" and save gym region data in database 
(needs non-free Heroku version)
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
* Checking error codes?
...
