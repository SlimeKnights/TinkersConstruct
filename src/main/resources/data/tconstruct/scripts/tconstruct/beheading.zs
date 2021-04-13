/*
 * Adds a Beheading recipe that drops a Diamond when a Sheep (Entity) is killed.
 */

// <recipetype:tconstruct:beheading>.addRecipe(name as string, ingredient as MCEntityType, output as IItemStack)

<recipetype:tconstruct:beheading>.addRecipe("beaheading_test", <entitytype:minecraft:sheep>, <item:minecraft:diamond>);

/*
 * Removes the all Beheading recipes that drop from a Creeper (Entity).
 */

// <recipetype:tconstruct:beheading>.removeRecipe(input as MCEntityType)

<recipetype:tconstruct:beheading>.removeRecipe(<entitytype:minecraft:creeper>);
