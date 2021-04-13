/*
 * Adds a Molding Basin recipe that turns Dirt into a Diamond when it is Molded with Glass. The Glass is not consumed.
 */

// <recipetype:tconstruct:molding_basin>.addRecipe(name as string, material as IIngredient, mold as IIngredient, moldConsumed as boolean, output as IItemStack)

<recipetype:tconstruct:molding_basin>.addRecipe("molding_basin_test", <item:minecraft:dirt>, <item:minecraft:glass>, false, <item:minecraft:diamond>);

/*
 * Removes the Molding Basin recipe that turns Part Sand Casts (Gem Sand Cast, Pickaxe Head Sand Cast, etc) back into a Blank Sand Cast.
 *
 * NOTE: This script is commented out as there are no default Tinkers recipes that are made in the Molding Basin to remove.
 */

// <recipetype:tconstruct:molding_basin>.removeRecipe(output as IItemStack)

// <recipetype:tconstruct:molding_basin>.removeRecipe(<item:tconstruct:blank_sand_cast>);
