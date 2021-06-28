/*
 * Adds two recipes:
 *
 * The first will allow crafting diamonds in the part builder using a custom pattern and the iron material (that is, iron blocks, ingots, or nuggets).
 *
 * The second is a Part Builder recipe for a Tool Rod that costs 2 of whatever material is used to make it, and will output 3 Tool Rods of the given material.
 *
 * The pattern is set as "custom_pattern", which is not a valid pattern in default Tinkers Construct, you will need a
 * mod that can load textures to load a texture with the given name.
 * Valid patterns are defined by the texture in the `tconstruct:gui/tinker_pattern/` folder in the resources.
 *
 * The output Item needs to be an Item that works with Materials, you can find a list of valid items by running `/ct dump ticMaterialItems`.
 */

// <recipetype:tconstruct:part_builder>.addMaterialRecipe(name as string, pattern as String, cost as int, output as Item, outputCount as int)

<recipetype:tconstruct:part_builder>.addItemRecipe("item_builder_test", "tconstruct:iron", "tconstruct:custom_pattern", 2, <item:minecraft:diamond>);
<recipetype:tconstruct:part_builder>.addMaterialRecipe("part_builder_test", "tconstruct:custom_pattern", 2, <item:tconstruct:pickaxe_head>, 3);

/*
 * Removes the Part Builder recipe for the Tool Binding part.
 */

// <recipetype:tconstruct:part_builder>.removeByName(name as string)

<recipetype:tconstruct:part_builder>.removeByName("tconstruct:tools/parts/builder/tool_binding");
