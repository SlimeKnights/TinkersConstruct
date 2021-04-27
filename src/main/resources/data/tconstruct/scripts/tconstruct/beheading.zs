/*
 * Adds a Beheading recipe that drops a Diamond when a Sheep (Entity) is killed.
 */

// <recipetype:tconstruct:beheading>.addRecipe(name as string, ingredient as CTEntityIngredient, output as IItemStack)

<recipetype:tconstruct:beheading>.addRecipe("beaheading_test", <entitytype:minecraft:sheep>, <item:minecraft:diamond>);

/*
 * Removes all Beheading recipes that drop a Skeleton Skull.
 * Removes all Beheading recipes that drop from a Creeper (Entity).
 */

// <recipetype:tconstruct:beheading>.removeRecipe(output as IItemStack)
// <recipetype:tconstruct:beheading>.removeRecipe(input as CTEntityIngredient)

<recipetype:tconstruct:beheading>.removeRecipe(<item:minecraft:skeleton_skull>);
<recipetype:tconstruct:beheading>.removeRecipe(<entitytype:minecraft:creeper>);
