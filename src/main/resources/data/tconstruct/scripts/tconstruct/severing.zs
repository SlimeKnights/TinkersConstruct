/*
 * Adds a Severing recipe that drops a Diamond when a Sheep (Entity) is killed.
 */

// <recipetype:tconstruct:severing>.addRecipe(name as string, ingredient as CTEntityIngredient, output as IItemStack)

<recipetype:tconstruct:severing>.addRecipe("beaheading_test", <entitytype:minecraft:sheep>, <item:minecraft:diamond>);

/*
 * Removes all Severing recipes that drop a Skeleton Skull.
 * Removes all Severing recipes that drop from a Creeper (Entity).
 */

// <recipetype:tconstruct:severing>.removeRecipe(output as IItemStack)
// <recipetype:tconstruct:severing>.removeRecipe(input as CTEntityIngredient)

<recipetype:tconstruct:severing>.removeRecipe(<item:minecraft:skeleton_skull>);
<recipetype:tconstruct:severing>.removeRecipe(<entitytype:minecraft:creeper>);
