# Reporting issues

Before reporting an issue, search to see if anyone has the same issue. Make sure to check closed issues as well as there is a chance one of them has the solution.

Provide clear steps to reproduce the issue, especially in the case of crashes. "_It crashed_", is not useful, "_crash when placing an item in a tool station_" is useful, and "crash when placing a pickaxe in a tool station" is even better. If the bug happens on a server, make sure to test in single player, and to test with a normal Forge server if using Sponge Forge.

## Versions

Always test with the latest versions of all relevant mods; chances are the bug you are reporting has been fixed in a later version of Tinkers Construct, Mantle, or even Forge. We do not support versions Minecraft versions before the latest stable Forge release, which is currently 1.12.2. No more work is being done on older versions so issues from those versions will be closed.

## Crashes

For crashes, always provide a crash report. Crash reports should be added using an external site such as https://pastebin.com or https://gist.github.com and linked in the issue to avoid clutter.

## Mod list

Try to minimize the list of mods needed to reproduce the bug. Performance enhancing mods and core mods can be expecially problematic due to their changes to the base Minecraft code, so especially try to remove them to see if it is the cause and provide that information in the report.

OptiFine is especially problematic due to it being closed source and the fact that it makes many unknown changes to Forge internals, so we do not support issues caused by OptiFine.

# Suggestions

We do not take suggestions on the tracker. Ideas may be considered in the overall context, but are generally closed to keep the tracker clean. Tinkers' Constructs mechanics are designed to work as is. New tools or weapons would either be added if they fulfill a missing demand and nothing more important is to be done.

Please also read the [Frequently Asked Questions](https://github.com/SlimeKnights/TinkersConstruct/wiki/FAQ) on the wiki, as it covers many common suggestions.

If you want a better place to discuss ideas, consider joining [the SlimeKnights Discord](https://discord.gg/njGrvuh). We typically do not implement suggestions, but someone may like the idea enough to implement it in an addon or its own mod.

# Pull requests

Always talk to the developers first before working on pull requests, such as on [the SlimeKnights Discord](https://discord.gg/njGrvuh). Pull requests will only be accepted if they contribute something meaningful and do not hinder maintainability. Furthermore pull requests must be tested and ensure to not break anything.

An exception to this rule is translation pull requests, which we generally allow without previous discussion. Please do not translate using an automatic translator such as Google Translate as those translations tend to be filled with errors or use the wrong context.
