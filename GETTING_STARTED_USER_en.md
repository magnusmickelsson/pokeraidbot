Are you interested in Pokemon Go raiding? Are you a new member of a Discord-server that uses pokeraidbot? 
Maybe you'd like to know how to get a hang of it, without spending a lot of time reading documentation and manuals?

This getting started guide might be of assistance.

For starters, your server should have a channel with a *raid overview*.

It's a read-only channel that only contains a single command and a message with
all current raids and signups, to give you a quick overview of what raids are available, and which ones
would most likely happen since there are enough signups.

Here is an example:

![Overview](img/overview_en.png)

"Next ETA" means the next time from now where there will be raiders arriving to the raid, who signed up.
It means, that time might be a good time to arrive at the raid so you can join up with those who signed up.

If there is no "next ETA" but there are people signed up, it's likely that they are either in the process of
doing the raid, or they've just done it.

So, how can you contribute to what the bot shows in the overview?

You can:
* Report raids you see in-game, or let a bot like Gymhuntr or PokeAlarm report raids automatically (pokeraidbot can integrate with them)
* Sign up for raids via the bot, either using groups or directly via +2-syntax (see below)
* Help each other out :)

Here are some instructions on how to do this in practice.

So you have discovered a raid nearby that you want to share with your friends?
The raid is for a Suicune at the gym Café Lalo, and it **expires** at 09:45.

Type the following in your server's text channel where pokeraidbot is active:

*!raid new Suicune 09:45 cafe lalo*

![Create](img/en/started1.png)

(If you think it's too much of a pain to figure out the end time of a raid, you can report the raid via start 
time/hatch time - use *!raid start Suicune 09:00 cafe lalo* instead)

Then, you want to create a group that raids at 09:30.

Type the following:
*!raid group 09:30 cafe lalo*

![Group](img/en/started2.png)

To sign up yourself to the raid, click the emote with number "1". If you have two friends who are joining,
click the emote with the number "3" to sign up yourself and your friends.

If you click the emote you chose again, the signup for the person(s) you added to the group are removed.

So why is there a 2 next to the emote that you pressed?

Well, in order for you to have emotes to press, the bot needs to react to the message first.
It adds all the emotes you can see on the picture above, and listens for if a user presses them.

![Signup](img/en/started3.png)

The group's signups will automatically be updated every 15 seconds, and when it's time for your group to 
start the raid, the group message will be automatically removed (5 minutes after raid start, so you have time to
do a ready-check with the people you know should be there).

Do you want to see the current active raids, which have been reported? 

The best way is of course to use the overview mentioned above, but you can also type the following:

*!raid list*

![List](img/en/raidlist.png)

If you want to see what raids are only for a certain pokemon, for example Suicune, type the following:

*!raid list Suicune*

.. and you'll see only those raids.

Do you want help with deciding what pokemons to use against your raid boss?

Type the following:

*!raid vs suicune*

![Vs](img/en/vs.png)

Do you need help finding the gym the raid is at? Type:

*!raid map cafe lalo*

You'll get a static mini map of the gym, but you can also click the Gym name for a link to Google Maps,
featuring directions, calculating how long it will take you to get there, and being able to save the location. 

![Map](img/en/map.png)

For more detailed help, access the help manual via typing:

*!raid man*

It will respond in direct message and give you assistance with choosing the right help manual topics.
To read help for a certain topic, type for example:

*!raid man raid*

.. to get help on raid functions, such as reporting a raid. The available help manual topics are:

* raid - Raid functions, like reporting a raid, seeing a list of available raids, and checking details for a certain raid
* change - Functions to change a raid, such as changing the raid boss, the expiry time etc. (if the reporter made a mistake for example)
* signup - Functions to sign up to a raid, and removing your signup
* group - How to create a raid group for a certain time, like the example above
* tracking - Tell the bot to notify the user in direct message if a raid is reported for a raid boss you're interested in
* install - How a server administrator gets the bot working

If you type the following, you get help in direct messages:

*!raid man raid dm*

If you type like this, you can get help either in DM or in server chat, depending on what settings you have for your server:

*!raid man raid*

I hope this gives you an idea of what the bot can do, and that it shouldn't be **too** hard to get started.

Report any problems or suggestions via Github's issue handling.

Good luck and have fun!