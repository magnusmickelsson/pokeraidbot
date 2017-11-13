Done:

See [Changelog](CHANGELOG.md).

Needs testing:

* !raid change group can be before current time, fix.
* Remove raid group entity when group is cleaned up

Being developed:

* Gymhuntr integration via GymhuntrMessageListener (isBot())
* Umeå request: setting to remove ALL bot commands after X time, even the ones that go ok
* Regexp to remove all duplicate or more spaces in between arguments
* Only use a single Emoticon listener for signups to reduce memory and complexity

Discussion:

- 

Experiment with:

* Present !raid overview as a table
* Read Pokemon go screenshot to create raid automatically (Swepocks)
* Can we create a map with a certain pokestop as centre and plot all raids in that area around it?
* Ability to create channels on the fly to put certain raids in

Fix issue:

* !raid change remove should lead to any related group messages being removed
* Can signup at raid group end time via raid group emote pressing. Seems bad.
* Better error message if bot doesn't have correct rights on server, and give info to admin on what rights
to set

Fix, misc:
* Fix release tag for 1.0.0
* Uniqueconstraints to prevent in database that the same user can signup more than once for a certain raid.

Do, features:

* We should only ever have ONE emoticon eventlistener, that instead checks the groups in DB
if it's a message to react to
* Settings to automatically create raidgroup for raids, for tier 5 at the time of hatching 
(or with a delay, configurable - like +10 minutes)
* Umeå request: If people do similar map commands after one other, skip following commands
* Performance improvements. Reduce number of queries, optimize, add caching.
* !raid change group (time) (gym) - if more groups possible, reply with list of id:s and info to decide what group
* !raid change groupbyid (id) (time)
* !raid remove group (time) (gym) - if more groups possible, reply with list of id:s and info to decide what group
* !raid remove groupbyid (id)
* Use nickname instead of user name in raid list etc (s1lence)
* Handle changing raid group when user has many raid groups for a raid
* Handle changing raid group as mod when there are many raid groups for a raid
* -1 syntax to remove signups from a raid
* Max number of chars for a gym name in !raid list and !raid overview?
* Admin command for Zhorhn only - push message to the default channel of all servers
* Create an in-bot FAQ, f.ex. "Why does my group not update? What to do?"
* In !raid list, if the server has an overview, give a hint that there is an overview the user can use instead.
* Can we listen for +(number) (time) (gym) and fix possible user weirdness like forgetting 
time (equalling no time to "now", if raid is active) to signup using that?
* Snooze button for raid group
* !raid change remove-group (gym) so admins can clean up user mess when for example setting
wrong time
* REST API with passtoken and acqquiring tickets

Maybe, features:

* Emote to mark group as having finished (removes all signups) - only group creator?
* Config for permission setting of various groups of commands:
    * Raid functions like create, list can be set to be admin only
    * Raid group creation can be set to admin only
* Config for time of automatic message deletion
    * Number of seconds, or -1 = never delete
* Config for turning on and off some feature groups (map, info, raid)
* Enable bot owner to trigger import of a regional dataset "on the fly" and save gym region data in database 
(needs non-free Heroku version)
* !raid me - Answer in PM with my current signups over all raids in the region and my current trackings 
* Admin commands where bot owner can purge configuration, get statistics, help a user (send man help via DM?)
* !raid install needs to be "protected" so admins can only affect the configuration of their own server
(this may mean the install command has to be run in the context of a server chat so we can fetch server name)
* Timezone handling via config, used in all commands related to time
* Credit Iconninja for icons
* Check permissions during config - need to have MANAGE_MESSAGE and access to send messages, among other things
* Embeddedlänkar vid sökning som möjliggör att köra kommandot igen för bara det resultatet via klick
* Config should have a note if a server is TEST or PROD and a env property which type of server,
so we can't get test config on a prod server running by mistake
* Add counters/counter moves to all pokemon, based on "good dps pokemons" for each type?
* Checking error codes?
...
