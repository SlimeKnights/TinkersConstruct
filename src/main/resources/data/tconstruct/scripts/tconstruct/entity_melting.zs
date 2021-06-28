/*
 * Adds an Entity Melting Recipe that produces Molten Rose Gold (Fluid) when a Sheep (Entity) is melted in a Smeltery dealing 1 damage to the Sheep.
 */

// <recipetype:tconstruct:entity_melting>.addRecipe(name as string, input as CTEntityIngredient, output as IFluidStack, damage as int)

<recipetype:tconstruct:entity_melting>.addRecipe("entity_melting_test", <entitytype:minecraft:sheep>, <fluid:tconstruct:molten_rose_gold>, 1);

/*
 * Removes two Entity Melting Recipes.
 *
 * 1) Removes all recipes that output Molten Iron (Fluid).
 * 2) Removes all recipes that use a Villager (Entity) as the input.
 * 2.1) Note this will put the villager back to the default blood recipe unless you use the tag to blacklist villager melting
 */

// <recipetype:tconstruct:entity_melting>.removeRecipe(output as IFluidStack)
// <recipetype:tconstruct:entity_melting>.removeRecipe(entity as MCEntityType)

<recipetype:tconstruct:entity_melting>.removeRecipe(<fluid:tconstruct:molten_iron>);
<recipetype:tconstruct:entity_melting>.removeRecipe(<entitytype:minecraft:villager>);
