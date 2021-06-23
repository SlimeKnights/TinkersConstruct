/*
 * Adds a Molding Table recipe that turns Glass into a Stick when it is Molded with Dirt. The Dirt is not consumed.
 */

// <recipetype:tconstruct:molding_table>.addRecipe(name as string, material as IIngredient, mold as IIngredient, moldConsumed as boolean, output as IItemStack)

<recipetype:tconstruct:molding_table>.addRecipe("molding_table_test", <item:minecraft:glass>, <item:minecraft:dirt>, false, <item:minecraft:stick>);

/*
 * Removes the Molding Table recipe that turns Part Red Sand Casts (Gem Red Sand Cast, Pickaxe Head Red Sand Cast, etc) back into a Blank Red Sand Cast.
 */

// <recipetype:tconstruct:molding_table>.removeRecipe(output as IItemStack)

<recipetype:tconstruct:molding_table>.removeRecipe(<item:tconstruct:blank_red_sand_cast>);
