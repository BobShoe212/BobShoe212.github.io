# AntiGhost

This Bukkit plugin prevents a common cause of "ghost blocks" (blocks that a client thinks have been mined yet the server knows are still there). This most often happens when a player is mining blocks very quickly while occasionally falling.

When all of the following conditions are met:
* A player is breaking a block
* That player is not flying (creative flight)
* That player is not standing on solid ground

then this plugin will send an update to the client so that it knows the correct state of the block.

BobShoe212 has Added

Added Check for when a player places a block while they are in mid air.
Hopefully this will prevent players from pillaring up on Ghost Blocks.
