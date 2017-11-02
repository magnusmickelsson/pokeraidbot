Done:

See [Changelog](CHANGELOG.md).

Needs testing:

* Parsing time should be able to handle 9.00, 9:00 etc without a starting 0 (Swepocks)
* ConcurrentModificationException when removing signups from raid when raid group expires -
  create test case for this scenario

Being developed:

-

Discussion:

- 

Experiment with:

* Present !raid overview as a table
* Read Pokemon go screenshot to create raid automatically (Swepocks)
* Ability to create channels on the fly to put certain raids in
* Can we create a map with a certain pokestop as centre and plot all raids in that area around it?

Fix issue:

* Fix release tag for 1.0.0
* Raid group sometimes gives a bad number for individual user signing up. Happens when the first person in the list
signsup and then unsigns. Resets sometimes when somebody adds themselves to the list.
* Can signup at raid group end time via raid group emote pressing. Seems bad.
* Raid ex raid group - need to accept date and time, right now gives error
* Better error message if bot doesn't have correct rights on server, and give info to admin on what rights
to set

Fix, misc:
* Regexp to remove all duplicate or more spaces in between arguments
* Uniqueconstraints to prevent in database that the same user can signup more than once for a certain raid.

Do, features:

* In !raid list, if the server has an overview, give a hint that there is an overview the user can use instead.
* -1 syntax to remove signups from a raid
* Can we listen for +(number) (time) (gym) and fix possible user weirdness like forgetting 
time (equalling no time to "now", if raid is active) to signup using that?
* Snooze button for raid group
* !raid change remove-group (gym) so admins can clean up user mess when for example setting
wrong time
* Gymhuntr integration via GymhuntrMessageListener (isBot())
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

Maybe, features:

* Config should have a note if a server is TEST or PROD and a env property which type of server,
so we can't get test config on a prod server running by mistake
* Add counters/counter moves to all pokemon, based on "good dps pokemons" for each type?
* Checking error codes?
...
