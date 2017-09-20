# Frequently Asked Questions

* _I suggested X, why did you close the issue?_

I don't take suggestions. I might consider things in the overall context, but generally suggestions will just be closed to keep the issue tracker clean. Tinkers' Constructs mechanics are designed to work as is. New tools or weapons would either be added if they fulfill a missing demand and nothing more important is to be done.

* _Why did my issue get closed?_

This can have several reasons: Unsupported version, missing information, outdated versions or it was a suggestion and not an issue.
In general issues will be closed after about 2 weeks without activity unless their issue has not been resolved and all needed information is present.
Any 1.7.10 related things will be closed. No more work will be done on the 1.7.10 branch in favor of the reworked version.

* _Why did my pull request get rejected?_

Always talk to me first before working on pull requests. Pull requests will only be accepted if they contribute something meaningful and do not hinder maintainability. Furthermore pull requests must be tested and ensure to not break anything.

* _When will you update X? Why was X removed in 1.8?_

1.8 and onward is a long term project which changes many things about Tinkers' while keeping the core the same. If something is not present in the new version it either was removed by design, or it simply didn't get reimplemented since other things were more important.

* _I'm getting a "dangerous alternative prefix 'tconstruct'"_

No reason to worry, this is intended. TCon only registers some of its things (fluid blocks for example) if the required things are present. This allows us to e.g. only have molten copper if there actually is copper in the game, saving a lot of texture space. The only place to do that is during the oredict event. Forge complains because we're adding TCon blocks while it's not TCons turn to add things. So normally it'd be an error, since it means you're registering blocks and the like wrong, but in this case it's intended.

* _Where did Copper, Aluminum,... go?_

Many many mods come with Copper, Tin,.. and they all have different usages. TiC only used them to make tools. Since pretty much every modpack will have at least one mod with Copper, Tin,.. there is no decisive point to include it in TiC itself. Tinkers', however, integrates all those common materials we know and love if they're present from other mods, so you will not lose out on the tools.

* _But how do I get Aluminum-Brass now?_

You can use gold (as always), brass (if present) and yes, even aluminum-brass to create casts. Aluminum-brass will be added by TiC if some other mods add both copper and aluminum. Furthermore you can now create and use clay casts which don't require any metal at all.

* _Tinkers' Armor and shields?_

No. Armor in itself is way too complex to fit it into TiC the same way weapons do, I'll leave that for other people. I also don't have any plans to add shields in that style. Battlesigns, however, double as shields.

* _Why can't I add more modifiers?_

The weight and meaning of modifiers got shifted. Modifiers have much more power than materials themselves, in old TiC it literally didn't matter what materials you chose, since durability was abundant and modifiers heavily outweighed material stats. Now modifiers are even stronger than before, but more limited in number. To keep the power similar, materials are more powerful now. So in short: Modifiers are stronger per modifier, but less modifiers overall. Use them in combination with the materials to achieve your goals.

* _Why take out moss and RF modifiers?_

Simply put: infinite durability for free = bad. Moss was not bad in itself, which is why I'm working on a mending-style variant for it. I think it'll be quite fitting for the job, since it also removes the RNG. RF just completely trivializes any durability concerns, hence it will not return.

* _Are we going to get the tinkers hearts back?_

I'd love to, but I don't have any means of getting them. Also considering pulling them out of the mod itself, since there are other modpacks that want to use them.

* _Will Traveler's Gear be returning?_

No plans from my side. There simply are more important things to work on.

* _When?_

No.
