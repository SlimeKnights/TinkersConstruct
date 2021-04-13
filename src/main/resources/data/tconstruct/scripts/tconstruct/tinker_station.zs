/*
 * Adds five Tinker Station recipes that do the following:
 * 1) Adds an Overslime Modifier Recipe that uses Dirt to repair 800 durability.
 * 2) Adds a Modifier Recipe that uses 3 Sticks to add the Diamond Modifier to only a Pickaxe (no other tools). The Diamond Modifier will have a level of 1, with a max level of 5, will use 1 upgrade slot and 1 ability slot.
 * 3) Adds a Modifier Recipe that uses 3 Apples to add the Emerald Modifier to any Item in the Modifiable Tag. The Emerald Modifier will have a level of 1, with a max level of 5, will use 1 upgrade slot and 1 ability slot. The Recipe has a requirement that the Item being modified has the Haste Modifier at level 1 or above. minMatch means it needs to match at-least one of the requirements and the requirementsError is used to tell the user that they are missing a requirement.
 * 3.1) You can use `/ct dump ticModifiers` to get a list of Modifiers.
 * 3.2) For the Modifier Requirements, if there are multiple requirements (such as requiring Haste and Luck to be applied), you can use the minMatch to say how many of the requirements need to match, so setting it to 1, will mean either Luck Or Haste need to be present, both are not required.
 * 3.3) The requirementsError is localized, so you can pass in an unlocalized string.
 * 4) Adds an Incremental Modifier Recipe that uses an Arrow to add the Emerald Modifier to any Item in the Modifiable Tag. The Recipe required 80 per level, but Arrows are set to give 2 per Arrow, so only 40 Arrows are required. The Emerald Modifier will have a level of 1, with a max level of 5, will use 0 upgrade slots and 1 ability slot. If only part of the Arrow is required to complete a level, a piece of Glass will be returned.
 * 4.1) You can use `/ct dump ticModifiers` to get a list of Modifiers.
 * 4.2) The left over is in the following instance: You have a tool that has 79/80 put into the Recipe, but an Arrow gives 2, so using an Arrow would give you 81/80, so in this case it adds 1 to get 80/80 and gives you the left over so no material is lost. The normal Haste Recipe using Redstone dust and Redstone Blocks operates the same way.
 * 5) Adds an Incremental Modifier Recipe that uses Glowstone to add the Haste Modifier to any Item in the Modifiable Tag. The Recipe required 4 per level, and Glowstone are set to give 1 per Glowstone, so 4 Glowstone are required. The Haste Modifier will have a level of 1, with a max level of 2, will use 1 upgrade slot and 0 ability slots. If only part of the Glowstone is required to complete a level, a Diamond will be returned. The Recipe has a requirement that the Item being modified has the Emerald Modifier at level 1 or above. minMatch means it needs to match at-least one of the requirements and the requirementsError is used to tell the user that they are missing a requirement.
 * 5.1) You can use `/ct dump ticModifiers` to get a list of Modifiers.
 * 5.2) The left over is in the following instance: You have a tool that has 79/80 put into the Recipe, but an Arrow gives 2, so using an Arrow would give you 81/80, so in this case it adds 1 to get 80/80 and gives you the left over so no material is lost. The normal Haste Recipe using Redstone dust and Redstone Blocks operates the same way.
 * 5.3) For the Modifier Requirements, if there are multiple requirements (such as requiring Haste and Luck to be applied), you can use the minMatch to say how many of the requirements need to match, so setting it to 1, will mean either Luck Or Haste need to be present, both are not required.
 * 5.4) The requirementsError is localized, so you can pass in an unlocalized string.
 */

// <recipetype:tconstruct:tinker_station>.addOverslimeModifierRecipe(name as string, ingredient as IIngredient, restoreAmount as int)
// <recipetype:tconstruct:tinker_station>.addModifierRecipe(name as string, inputs as IIngredientWithAmount[], toolRequired as IIngredient, modifierResult as string, modifierResultLevel as int, maxLevel as int, upgradeSlots as int, abilitySlots as int)
// <recipetype:tconstruct:tinker_station>.addModifierRecipe(name as string, inputs as IIngredientWithAmount[], toolRequired as IIngredient, modifierResult as string, modifierResultLevel as int, maxLevel as int, upgradeSlots as int, abilitySlots as int, modifierRequirements as IData, minMatch as int, requirementsError as string)
// <recipetype:tconstruct:tinker_station>.addIncrementalModifierRecipe(String name, IIngredient input, int amountPerInput, int neededPerLevel, IIngredient toolRequirement, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, int abilitySlots, IItemStack leftover)
// <recipetype:tconstruct:tinker_station>.addIncrementalModifierRecipe(String name, IIngredient input, int amountPerInput, int neededPerLevel, IIngredient toolRequirement, String modifierResult, int modifierResultLevel, int maxLevel, int upgradeSlots, int abilitySlots, IItemStack leftover, IData modifierRequirements, int minMatch, String requirementsError)

<recipetype:tconstruct:tinker_station>.addOverslimeModifierRecipe("overslime_test", <item:minecraft:dirt>, 800);
<recipetype:tconstruct:tinker_station>.addModifierRecipe("modifier_test", [<item:minecraft:stick> * 3], <item:tconstruct:pickaxe>, "tconstruct:diamond", 2, 5, 1, 1);
<recipetype:tconstruct:tinker_station>.addModifierRecipe("modifier_test_requirements", [<item:minecraft:apple> * 3], <tag:items:tconstruct:modifiable>, "tconstruct:emerald", 2, 5, 1, 1, {"tconstruct:haste": 1}, 1, "Haste needs to be applied first!");
<recipetype:tconstruct:tinker_station>.addIncrementalModifierRecipe("inc_modifier_test", <item:minecraft:arrow>, 2, 80, <tag:items:tconstruct:modifiable>, "tconstruct:emerald", 1, 5, 0, 1, <item:minecraft:glass>);
<recipetype:tconstruct:tinker_station>.addIncrementalModifierRecipe("inc_modifier_test_requirements", <item:minecraft:glowstone>, 1, 4, <tag:items:tconstruct:modifiable>, "tconstruct:haste", 1, 2, 1, 0, <item:minecraft:diamond>, {"tconstruct:emerald": 1}, 1, "An Emerald needs to be applied first!");

/*
 * Removes the Expanded upgrade from the Tinker Station.
 */

// <recipetype:tconstruct:tinker_station>.removeByName(name as string)

<recipetype:tconstruct:tinker_station>.removeByName("tconstruct:tools/modifiers/upgrade/expanded");
