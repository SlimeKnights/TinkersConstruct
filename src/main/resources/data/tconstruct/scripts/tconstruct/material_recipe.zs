/*
 * Adds a Material Recipe that allows a Diamond to be used to create Bone (Material) parts in the Part Builder with a material value of 3 and a needed value of 5.
 * 
 * The `needed` value is how many of the input (in this case Diamonds) are needed to make `1` Material.
 * An example of this being Ingots vs Nuggets. Ingots have a `needed` value of `1` while Nuggets have a `needed` value of `9` (since it takes 9 Nuggets to make 1 Ingot).
 * 
 * You can find valid materialIds by running `/ct dump ticMaterials` in-game.
 */

// <recipetype:tconstruct:material>.addItem(name as string, ingredient as IIngredient, value as int, needed as int, materialId as string)

<recipetype:tconstruct:material>.addItem("test_material", <item:minecraft:cocoa_beans>, 3, 5, "tconstruct:bone");

/*
 * Removes the Flint to Flint (Material) parts from the Part builder.
 */

// <recipetype:tconstruct:material>.removeByName(name as string)

<recipetype:tconstruct:material>.removeByName("tconstruct:tools/materials/flint");
