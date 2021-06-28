/*
 * NOTE: You should not have multiple incremental modifier recipes for the same modifier that take different items as it
 * may lead to unexpected results!
 *
 * Adds thirteen Tinker Station recipes that do the following:
 * 1) Adds an Overslime Modifier Recipe that uses Dirt to repair 800 durability.
 * 2) Adds a Modifier Recipe that uses 3 Sticks to add the Experienced Modifier to a Pickaxe.
 * 2.1) The Modifier can be applied 3 times, for a total of Experienced 3.
 * 3) Adds a Modifier Recipe that uses 3 Apples to add the Sturdy Modifier to any item in the tconstruct:modifiable Item Tag.
 * 3.1) The Modifier can be applied 5 times, for a total of Sturdy 3.
 * 3.2) The Recipe requires the Tool that it is being applied on to have the Haste Modifier at level 1 or more.
 * 4) Adds an Upgrade Modifier Recipe that uses Red Sand to add the Tasty Modifier to any item in the tconstruct:modifiable Item Tag.
 * 4.1) The Modifier can be applied 3 times, for a total of Tasty 3.
 * 4.2) The Modifier will use 1 Upgrade slot on the Tool.
 * 5) Adds an Upgrade Modifier Recipe that uses White Wool to add the Reach Modifier to any item in the tconstruct:modifiable Item Tag.
 * 5.1) The Modifier can be applied 3 times, for a total of Reach 3.
 * 5.2) The Modifier will use 2 Upgrade slots on the Tool.
 * 5.3) The Recipe requires the Tool that it is being applied on to have the Haste Modifier at level 1 or more.
 * 6) Adds an Ability Modifier Recipe that uses Gravel to add the Smite Modifier to any item in the tconstruct:modifiable Item Tag.
 * 6.1) The Modifier can be applied 3 times, for a total of Smite 3.
 * 6.2) The Modifier will use 1 Ability slot on the Tool.
 * 7) Adds an Ability Modifier Recipe that uses Orange Wool to add the Sharpness Modifier to any item in the tconstruct:modifiable Item Tag.
 * 7.1) The Modifier can be applied 3 times, for a total of Sharpness 3.
 * 7.2) The Modifier will use 1 Ability slot on the Tool.
 * 8) Adds an Incremental Modifier Recipe that uses Black Wool to add the Reinforced Modifier to any item in the tconstruct:modifiable Item Tag.
 * 8.1) The Modifier can be applied 3 times, for a total of Haste 3.
 * 8.2) The Recipe has 8 increments and each Black Wool adds 2 increments, meaning a total of 4 Black Wool will be required per level.
 * 8.3) If there are any increments left over (if there was somehow 7 increments and another Black Wool is used, for a total of 9 increments), the recipe will return a Black Dye.
 * 9) Adds an Incremental Modifier Recipe that uses Yellow Wool to add the Sharpness Modifier to any item in the tconstruct:modifiable Item Tag.
 * 9.1) The Modifier can be applied 3 times, for a total of Sharpness 3.
 * 9.2) The Recipe has 8 increments and each Yellow Wool adds 3 increments, meaning a total of 3 Yellow Wool will be required per level.
 * 9.3) If there are any increments left over (if there was 6 increments and another Yellow Wool is used, for a total of 9 increments), the recipe will return a Yellow Dye.
 * 9.4) The Recipe requires the Tool that it is being applied on to have the Emerald Modifier at level 1 or more.
 * 10) Adds an Incremental Upgrade Modifier Recipe that uses Blue Wool to add the Bane of Sssss Modifier to any item in the tconstruct:modifiable Item Tag.
 * 10.1) The Modifier can be applied 3 times, for a total of Bane of Sssss 3.
 * 10.2) The Recipe has 20 increments and each Blue Wool adds 4 increments, meaning a total of 5 Blue Wool will be required per level.
 * 10.3) If there are any increments left over (if there was 18 increments and another Blue Wool is used, for a total of 22 increments), the recipe will return a Blue Dye.
 * 10.4) The Modifier will use 1 Upgrade slot on the Tool.
 * 11) Adds an Incremental Upgrade Modifier Recipe that uses Red Wool to add the Fiery Modifier to any item in the tconstruct:modifiable Item Tag.
 * 11.1) The Modifier can be applied 3 times, for a total of Fiery 3.
 * 11.2) The Recipe has 20 increments and each Red Wool adds 4 increments, meaning a total of 5 Red Wool will be required per level.
 * 11.3) If there are any increments left over (if there was 18 increments and another Red Wool is used, for a total of 22 increments), the recipe will return a Red Dye.
 * 11.4) The Recipe requires the Tool that it is being applied on to have the Diamond Modifier at level 1 or more.
 * 11.5) The Modifier will use 1 Upgrade slot on the Tool.
 * 12) Adds an Incremental Ability Modifier Recipe that uses Brown Wool to add the Sweeping Edge Modifier to any item in the tconstruct:modifiable Item Tag.
 * 12.1) The Modifier can be applied 4 times, for a total of Sweeping Edge 4.
 * 12.2) The Recipe has 2 increments and each Brown Wool adds 1 increments, meaning a total of 2 Brown Wool will be required per level.
 * 12.3) If there are any increments left over (this should never happen as each Brown Wool adds 1 increment), the recipe will return a Brown Dye.
 * 12.4) The Modifier will use 1 Ability slot on the Tool.
 * 13) Adds an Incremental Ability Modifier Recipe that uses Magenta Wool to add the Antiaquatic Modifier to any item in the tconstruct:modifiable Item Tag.
 * 13.1) The Modifier can be applied 3 times, for a total of Antiaquatic 4.
 * 13.2) The Recipe has 2 increments and each Magenta Wool adds 1 increments, meaning a total of 2 Magenta Wool will be required per level.
 * 13.3) If there are any increments left over (this should never happen as each Magenta Wool adds 1 increment), the recipe will return a Magenta Dye.
 * 13.4) The Recipe requires the Tool that it is being applied on to have the Diamond Modifier at level 1 or more and the Haste Modifier at level 2 or more.
 * 13.5) The Modifier will use 1 Ability slot on the Tool.
 */

// <recipetype:tconstruct:tinker_station>.addOverslimeModifierRecipe(name as string, ingredient as IIngredient, restoreAmount as int)
// <recipetype:tconstruct:tinker_station>.addModifierRecipe(name as string, inputs as IIngredientWithAmount[], toolRequired as IIngredient, modifierResult as string, modifierResultLevel as int, maxLevel as int, @Optional modifierRequirements as IData, @Optional minMatch as int, @Optional requirementsError as string)
// <recipetype:tconstruct:tinker_station>.addUpgradeModifierRecipe(name as string, inputs as IIngredientWithAmount[], toolRequired as IIngredient, modifierResult as string, modifierResultLevel as int, maxLevel as int, upgradeSlots as int, @Optional modifierRequirements as IData, @Optional minMatch as int, @Optional requirementsError as string)
// <recipetype:tconstruct:tinker_station>.addAbilityModifierRecipe(name as string, inputs as IIngredientWithAmount[], toolRequired as IIngredient, modifierResult as string, modifierResultLevel as int, maxLevel as int, abilitySlots as int, @Optional modifierRequirements as IData, @Optional minMatch as int, @Optional requirementsError as string)
// <recipetype:tconstruct:tinker_station>.addIncrementalModifierRecipe(name as string, input as IIngredient, amountPerInput as int, neededPerLevel as int, toolRequirement as IIngredient, modifierResult as string, modifierResultLevel as int, maxLevel as int, leftover as IItemStack, @Optional modifierRequirements as IData, @Optional minMatch as int, @Optional requirementsError as string)
// <recipetype:tconstruct:tinker_station>.addIncrementalUpgradeModifierRecipe (name as string, input as IIngredient, amountPerInput as int, neededPerLevel as int, toolRequirement as IIngredient, modifierResult as String, modifierResultLevel as int, maxLevel as int, upgradeSlots as int, leftover as IItemStack, @Optional modifierRequirements as IData, @Optional minMatch as int, @Optional requirementsError as string)
// <recipetype:tconstruct:tinker_station>.addIncrementalAbilityModifierRecipe(name as string, input as IIngredient, amountPerInput as int, neededPerLevel as int, toolRequirement as IIngredient, modifierResult as String, modifierResultLevel as int, maxLevel as int, abilitySlots as int, leftover as IItemStack, @Optional modifierRequirements as IData, @Optional minMatch as int, @Optional requirementsError as string)

<recipetype:tconstruct:tinker_station>.addOverslimeModifierRecipe("overslime_test", <item:minecraft:dirt>, 800);
<recipetype:tconstruct:tinker_station>.addModifierRecipe("modifier_example", [<item:minecraft:stick> * 3], <item:tconstruct:pickaxe>, "tconstruct:experienced", 1, 3);
<recipetype:tconstruct:tinker_station>.addModifierRecipe("modifier_requirements_example", [<item:minecraft:apple> * 3], <tag:items:tconstruct:modifiable>, "tconstruct:sturdy", 1, 3, {"tconstruct:haste": 1}, 1, "recipe.tconstruct.modifier.requirements_error");
<recipetype:tconstruct:tinker_station>.addUpgradeModifierRecipe("upgrade_modifier_example", [<item:minecraft:red_sand>], <tag:items:tconstruct:modifiable>, "tconstruct:tasty", 1, 3, 1);
<recipetype:tconstruct:tinker_station>.addUpgradeModifierRecipe("upgrade_modifier_requirements_example", [<item:minecraft:white_wool>], <tag:items:tconstruct:modifiable>, "tconstruct:reach", 1, 3, 2, {"tconstruct:haste": 1}, 1, "recipe.tconstruct.modifier.requirements_error");
<recipetype:tconstruct:tinker_station>.addAbilityModifierRecipe("ability_modifier_example", [<item:minecraft:gravel>], <tag:items:tconstruct:modifiable>, "tconstruct:smite", 1, 3, 1);
<recipetype:tconstruct:tinker_station>.addAbilityModifierRecipe("ability_modifier_requirements_example", [<item:minecraft:orange_wool>], <tag:items:tconstruct:modifiable>, "tconstruct:sharpness", 1, 3, 1, {"tconstruct:emerald": 1}, 1, "recipe.tconstruct.modifier.requirements_error");
<recipetype:tconstruct:tinker_station>.addIncrementalModifierRecipe("incremental_modifier_example", <item:minecraft:black_wool>, 2, 8, <tag:items:tconstruct:modifiable>, "tconstruct:reinforced", 1, 8, <item:minecraft:black_dye>);
<recipetype:tconstruct:tinker_station>.addIncrementalModifierRecipe("incremental_modifier_requirements_example", <item:minecraft:yellow_wool>, 3, 8, <tag:items:tconstruct:modifiable>, "tconstruct:sharpness", 1, 8, <item:minecraft:yellow_dye>, {"tconstruct:emerald": 1}, 1, "recipe.tconstruct.modifier.requirements_error");
<recipetype:tconstruct:tinker_station>.addIncrementalUpgradeModifierRecipe("incremental_upgrade_modifier_example", <item:minecraft:blue_wool>, 4, 20, <tag:items:tconstruct:modifiable>, "tconstruct:bane_of_sssss", 1, 3, 1, <item:minecraft:blue_dye>);
<recipetype:tconstruct:tinker_station>.addIncrementalUpgradeModifierRecipe("incremental_upgrade_modifier_requirements_example", <item:minecraft:red_wool>, 4, 20, <tag:items:tconstruct:modifiable>, "tconstruct:fiery", 1, 3, 1, <item:minecraft:red_dye>, {"tconstruct:diamond": 1}, 1, "recipe.tconstruct.modifier.requirements_error");
<recipetype:tconstruct:tinker_station>.addIncrementalAbilityModifierRecipe("incremental_ability_modifier_example", <item:minecraft:brown_wool>, 1, 2, <tag:items:tconstruct:modifiable>, "tconstruct:sweeping_edge", 1, 4, 1, <item:minecraft:brown_dye>);
<recipetype:tconstruct:tinker_station>.addIncrementalAbilityModifierRecipe("incremental_ability_modifier_requirements_example", <item:minecraft:magenta_wool>, 1, 2, <tag:items:tconstruct:modifiable>, "tconstruct:antiaquatic", 1, 4, 1, <item:minecraft:magenta_wool>, {"tconstruct:diamond": 1, "tconstruct:haste": 2}, 2, "recipe.tconstruct.modifier.requirements_error");

/*
 * Removes two recipes from the Tinker Station.
 *
 * 1) Removes the Expanded upgrade from the Tinker Station.
 * 2) Removes all recipes that produce the "haste" modifier.
 */

// <recipetype:tconstruct:tinker_station>.removeByName(name as string)
// <recipetype:tconstruct:tinker_station>.removeRecipe(modifierId as string)

<recipetype:tconstruct:tinker_station>.removeByName("tconstruct:tools/modifiers/ability/expanded");
<recipetype:tconstruct:tinker_station>.removeRecipe("tconstruct:haste");
