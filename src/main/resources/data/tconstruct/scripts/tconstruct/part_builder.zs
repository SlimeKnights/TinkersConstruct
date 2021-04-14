/*
 * Adds a Part Builder recipe for a Tool Rod that costs 2 of whatever material is used to make it, and will output 3 Tool Rods of the given material.
 *
 * The pattern is set as the Axe Head pattern, so in the Part Builder, the recipe will show under the Axe Head, but it will still make Tool Rods.
 * Valid patterns are defined by the texture in the `tconstruct:gui/tinker_pattern/` folder in the resources.
 *
 * The output Item needs to be an Item that works with Materials, you can find a list of valid items by running `/ct dump ticMaterialItems`.
 */

// <recipetype:tconstruct:part_builder>.addMaterialRecipe(name as string, pattern as String, cost as int, output as Item, outputCount as int)

<recipetype:tconstruct:part_builder>.addMaterialRecipe("part_builder_test", "tconstruct:axe_head", 2, <item:tconstruct:pickaxe_head>, 3);

/*
 * Removes the Part Builder recipe for the Tool Binding part.
 */

// <recipetype:tconstruct:part_builder>.removeRecipe(name as string)

<recipetype:tconstruct:part_builder>.removeByName("tconstruct:tools/parts/builder/tool_binding");
