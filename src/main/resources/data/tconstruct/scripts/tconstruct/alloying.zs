/*
 * Adds an Alloying recipe that will produce Water (Fluid) from 1mb of Molten Rose Gold (Fluid) and 1mb of Lava (Fluid) when combined with a heat of at least 50°C
 */

// <recipetype:tconstruct:alloying>.addRecipe(name as string, ingredients as CTFluidIngredient[], output as IFluidStack, temperature as int)

<recipetype:tconstruct:alloying>.addRecipe("alloy_test", [<fluid:tconstruct:molten_rose_gold>, <fluid:minecraft:lava>], <fluid:minecraft:water>, 50);

/*
 * Removes all Alloying recipes that output Molten Pig Iron (Fluid).
 */

// <recipetype:tconstruct:alloying>.removeRecipe(output as Fluid)

<recipetype:tconstruct:alloying>.removeRecipe(<fluid:tconstruct:molten_pig_iron>);
