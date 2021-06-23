/*
 * Adds four Melting recipes that do the following:
 * 
 * 1) Adds a normal Melting Recipe that produces 1mb of Water (Fluid) when Black Dye is melted at 0°C.
 * 2) Adds a Damageable Melting Recipe that produces 1000mb of Water (Fluid) when an Elytra is melted at 0°C.
 * 2.1) Damageable Melting Recipes are recipes who's output amount (the amount of Fluid that is produced) changes depending on how damaged the input item is.
 * 3) Adds an Ore Melting recipe that produces 100mb of Water (Fluid) when a Stick is melted at 0°C.
 * 3.1) Ore Melting Recipes are recipes who's output amount (the amount of Fluid that is produced) changes depending on which Controller (Smeltery or Melter) the recipe is made in.
 * 3.2) This recipe produces a byproduct of 50mb of Lava (Fluid).
 * 4) Adds a Material Melting Recipe that allows melting stone tool parts into 10mb of lava per stone block
 * 4.1) Material Melting Recipes are recipes who's can melt a specific material into a fluid, using any part that exists in a casting recipe.
 * 4.2) The recipe output is based on the part cost in the relevant casting recipe. For example, a pickaxe head costs 2 units to produce, so with this recipe a stone pickaxe head will melt into 20mb of lava
 * 4.3) The item needs to be an Item that works with Materials, you can find a list of valid items by running `/ct dump ticMaterialItems`.
 */

// <recipetype:tconstruct:melting>.addMeltingRecipe(name as string, input as IIngredient, output as IFluidStack, temperature as int, time as int, @Optional byproduct as IFluidStack[])
// <recipetype:tconstruct:melting>.addDamageableMeltingRecipe(name as string, input as IIngredient, output as IFluidStack, temperature as int, time as int, @Optional byproduct as IFluidStack[])
// <recipetype:tconstruct:melting>.addOreMeltingRecipe(name as string, input as IIngredient, output as IFluidStack, temperature as int, time as int, @Optional byproduct as IFluidStack[])
// <recipetype:tconstruct:melting>.addMaterialMeltingRecipe(name as string, inputId as String, output as IFluidStack, temperature as int)

<recipetype:tconstruct:melting>.addMeltingRecipe("melting_test", <item:minecraft:black_dye>, <fluid:minecraft:water>, 0, 50);
<recipetype:tconstruct:melting>.addDamageableMeltingRecipe("damageable_melting_test", <item:minecraft:elytra>, <fluid:minecraft:water> * 1000, 0, 50);
<recipetype:tconstruct:melting>.addOreMeltingRecipe("ore_melting_test", <item:minecraft:stick>, <fluid:minecraft:water> * 100, 0, 50, [<fluid:minecraft:lava> * 50]);
<recipetype:tconstruct:melting>.addMaterialMeltingRecipe("material_melting_test", "tconstruct:stone", <fluid:minecraft:lava> * 10, 1000);

/*
 * Removes the Anvil to Molten Iron (Fluid) recipe.
 */

// <recipetype:tconstruct:melting>.removeByName(name as string)

<recipetype:tconstruct:melting>.removeByName("tconstruct:smeltery/melting/metal/iron/anvil");
