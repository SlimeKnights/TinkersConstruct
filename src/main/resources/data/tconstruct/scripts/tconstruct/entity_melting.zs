/*
 * Adds an Entity Melting Recipe that produces Molten Rose Gold (Fluid) when a Sheep (Entity) is melted in a Smeltery dealing 1 damage to the Sheep.
 */

// <recipetype:tconstruct:entity_melting>.addRecipe(name as string, input as MCEntityType, output as IFluidStack, damage as int)

<recipetype:tconstruct:entity_melting>.addRecipe("entity_melting_test", <entitytype:minecraft:sheep>, <fluid:tconstruct:molten_rose_gold>, 1);

/*
 * Removes the all Entity Melting Recipes that produce Molten Iron (Fluid).
 */

// <recipetype:tconstruct:entity_melting>.removeRecipe(input as IFluidStack)

<recipetype:tconstruct:entity_melting>.removeRecipe(<fluid:tconstruct:molten_iron>);
