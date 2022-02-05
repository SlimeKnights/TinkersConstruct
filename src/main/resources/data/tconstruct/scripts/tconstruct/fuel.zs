/*
 * Adds Water (Fluid) as a Fuel. The water will provide heat at a temperature of 10000°C for 500 ticks.
 */

// <recipetype:tconstruct:fuel>.addFuel(name as string, input as CTFluidIngredient, duration as int, temperature as int)

<recipetype:tconstruct:fuel>.addFuel("fuel_test", <fluid:minecraft:water>, 500, 10000);

/*
 * Removes Lava (Fluid) as a valid Fuel for the Smeltery.
 */

// <recipetype:tconstruct:fuel>.removeRecipe(input as IFluidStack)

<recipetype:tconstruct:fuel>.removeRecipe(<fluid:minecraft:lava>);
