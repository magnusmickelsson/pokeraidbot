FUTURE (?)
=====

1.6.0 (?)
=====
* !raid track notification handling refactored
* Gen3 pokemons added to repository
* EX raids will no longer be created via bot integration

1.5.1 (2017-11-30)
=====
* Bugfix: any user should be able to hatch eggs

1.5.0 (2017-11-28)
=====
* Report and create raids and groups for eggs, and be able to hatch them when we know what they are
* Admins and mods should be able to remove raids even if they have signups
* Bot integration can now report eggs, and automatically report what hatched as soon as the bot reports it

1.4.4 (2017-11-28)
=====
* Ho-oh added as raid boss (not EX)
* Better error handling for some situations

1.4.3 (2017-11-27)
=====
* Command to explicitly reset a server's overview
* Remove raid group entity when group is cleaned up
* Admin command for Zhorhn only - push message to the default channel of all servers (and some other stuff)
* Raid tracking for a user that left the server, handle that situation

1.4.2 (2017-11-23)
=====
* Better logging
* Changed headline text and title for group messages from Umeå feedback, better notice on mobile device

1.4.1 (2017-11-21)
=====
* Bugfix: !raid overview fixes that hopefully sort out the problem where it stops working and ends up in a state that
needs manual cleanup and lots of exceptions in logs
* !raid overview is no longer a reply but its own message, so the original command can be removed without 
removing overview
* Remove all but map feedback strategy now cleans up even harder

1.4.0 (2017-11-18)
=====
* Bugfix: !raid change group can no longer set time before current time, even if it is within raid duration
* Gymhuntrbot and PokeAlarm integration (reported raids get created in pokeraidbot if > 10 minutes remaining)
* Regexp that removes unnecessary spaces for most commands
* Raid group is now one message instead of two, reduces risk that a message comes in between info and emotes
* Feedback strategies, configurable for each server
* Only use a single Emoticon listener for signups to reduce memory and complexity
* Settings to automatically create raidgroup for raids, for tier 5 at the time of hatching 
* Increase default time that feedback messages remain, 30 seconds

1.3.1 (2017-11-08)
=====
* Pokemon tracking now also catches commands run via !raid start

1.3.0 (2017-11-07)
=====
* Members of server mods group can now change group time
* Moved raid time from group message title to description to avoid no linebreaks on Android
* !raid overview and !raid list - only the first date is shown, to save space.
* Fixed so not lots of stacktrace in the log regarding !raid overview if someone has removed the message

1.2.0 (2017-11-06)
=====
* Bugfix: no duplicate raids
* Persistent raid groups, survives restart of bot
* Bugfix: !raid man of an unknown topic gave weird message in DM
* Counter data and max CP for new raid bosses
* Command to report raid based on start time and not end - *!raid start*
* Possible to configure a mod role per server, so mods can do what the server admin can
* *!raid vs* no longer lists explicit moves to reduce amount of text
* *!raid overview* only available to server admin or mods
* Bugfix: "Second" instead of "sekund" for some messages due to locale issue
* !raid start and !raid end to create raids
* Bugfix: Raid group sometimes gives a bad number for individual user signing up. - hasn't happened since
we changed message layout
* Bugfix: !raid man with bad topic, gives bad message 

1.1.0 (2017-11-05)
=====
* EX raid support fully implemented and released
* Adapt to Niantic making huge changes without warning (new bosses, 45 min raid duration - NOTE: counter data not yet
updated!)

1.0.1 (2017-11-03)
=====
* Bugfix for removing signups for a raid, could lead to ConcurrentModificationException
* Parsing time should be able to handle 9.00, 9:00 etc without a starting 0 (Swepocks)
* Some refactoring
* Minor other fixes
* Changed raid entity and signup entity to not use @ElementCollection from JPA, but signup entity is now 
its own JPA @Entity

1.0.0 (2017-10-31)
=====
* Fixed readme for both english and swedish including images
* Fixed getting started guide for english locale
* Input parameter to application with default locale (en or sv)
* raid group removed 5 mins after finished group, not at exact time
* !raid overview - automatically updated "!raid list" every 60 seconds, 
should be put in a read-only channel
* Link to getting started guides from !raid usage
* Persistent !raid track - is now stored in database. Up to 3 pokemons to track per user.
* Raid creator can remove their created raid, if there are no signups
* Feedback after !raid new removed after 15 seconds
* !raid mapinchat - raid map forced in server chat, despite server settings (meant for admins in servers with such config)
* Minor text changes here and there
* Possible to change time for a raid group: !raid change group (time) (gym)
* !raid list should show next ETA
* Clean up signups for expired group
* Persistent tracking of certain pokemon raids
* User can set their own locale
* Move time for raid group: !raid change group (time) (gym)
* Moved attaching to overview to its own event listener
* Fuzzy search for pokemon names

0.9.1 (2017-10-14)
======
* Minor bugfixes
* No emoticon if a \+ command signup goes wrong. No feedback whatsoever, to save chat space and not scare users.

0.9.0 (2017-10-13)
======
* \+ command for signups, as users are used to it (+{number} {time} {gym} is now a signup)
* More automatic cleanup of messages/feedback
* "What's new"-command so people can see what new features.

0.8.0 (2017-10-12)
=====
* Group signup, existing sign ups blanked out if a user changes the time within the same raid.
* Cut down on text for a lot of messages.
* Commands that go wrong will be deleted after 15 seconds, along with the bot's feedback message, to keep chat clean.

0.7.1 (2017-10-11)
=====
* Fixed issue where many raids could cause the embedded message's description to reach its limit.
* Minor text adjustments.
* Fixed link handling for Google Maps so they work on iPhones as well.

0.7.0 (2017-10-10)
=====
* Team buttons removed from raid group signup (feedback from Uppsala)
* Improved texts
* Minor fixes

0.6.0 (2017-10-08)
=====
* Defaultconfig for channel can be set/changed in runtime by an admin
* Config should be put in the database
* "Change raid"-command if you make a mistake creating the raid (!raid change x)
* Delete raid command, for channel admin/owner only
* "man" command to replace !raid usage, which is sooo big. Make !raid usage small, and !raid man {topic} have details
* !raid add x should be able to take existing signup and add to it, not get an error,
unless you exceed limit
* Fixed: Clear all raids at the end of raiding for the day via scheduled job (22:00) - 
bot send message about it. - Handled it instead via checking expire both on date and time.
* Config command should enable servers to be configured in runtime on the fly (but only by server owner)
* Better raid status overview (emote buttons to register people in a group arriving at a certain time)

Earlier versions
================
* Raid list, sort by pokemon then by time (The FInal Shadow)
* Command to list server config
* Donate-command
* !raid list {pokemon} filters raids based on pokemon
* Fixed: raid track sends message whether the command was ok or not. The message should say why they
are getting the message (tracking), and also instructions on how to remove tracking.
* Fixed (always shows start/end): If endtime is more than 1 hour from current time, 
also include start/hatch time in message for !raid list and !raid status
* !raid untrack {pokemon} - !raid untrack without params clears all tracking

