/*
 * Adds four Casting Table recipes that do the following:
 * 
 * 1) Adds an Item Casting Recipe that, when a Honey Bottle is used as a Cast for 1000mb of Blood (Fluid), it gets cast into a Diamond after 200 ticks of cooling time. The Honey Bottle is not consumed and does not switch slots with the Diamond.
 * 2) Adds a Material Casting Recipe that will allow a Diamond to be used as a Cast to make any Axe Head (Part), regardless of material (The Diamond acts as an Axe Head Cast). The Diamond is consumed by the recipe and does not switch slots with the Axe Head.
 * 2.1) The result Item needs to be an Item that works with Materials, you can find a list of valid items by running `/ct dump ticMaterialItems`.
 * 3) Adds a Composite Casting Recipe that will allow creating any composite recipe for an axe head in a casting basin. Note that specific combinations are defined as part of the material recipes
 * 4) Adds a Container Filling Recipe that will fill a Seared Ingot Tank with 123mb of any Fluid when cast.
 * 4.1) The amount of fluid is the maximum amount of fluid that can be inserted into the container at a time.
 */

// <recipetype:tconstruct:casting_table>.addItemCastingRecipe(name as string, cast as IIngredient, fluid as IFluidStack, result as IItemStack, coolingTime as int, consumeCast as boolean, switchSlots as boolean)
// <recipetype:tconstruct:casting_table>.addMaterialCastingRecipe(name as string, cast as IIngredient, itemCost as int, result as Item, consumeCast as boolean, switchSlots as boolean)
// <recipetype:tconstruct:casting_table>.addCompositeCastingRecipe(name as string, result as Item, itemCost as int)
// <recipetype:tconstruct:casting_table>.addContainerFillingRecipe(name as string, fluidAmount as int, containerIn as Item)

<recipetype:tconstruct:casting_table>.addItemCastingRecipe("item_casting_table_test", <item:minecraft:honey_bottle>, <fluid:tconstruct:blood> * 1000, <item:minecraft:diamond>, 200, false, true);
<recipetype:tconstruct:casting_table>.addMaterialCastingRecipe("material_casting_table_test", <item:minecraft:diamond>, 250, <item:tconstruct:small_axe_head>, true, false);
<recipetype:tconstruct:casting_table>.addCompositeCastingRecipe("composite_casting_table_test", <item:tconstruct:small_axe_head>, 10);
<recipetype:tconstruct:casting_table>.addContainerFillingRecipe("filling_casting_table_test", 123, <item:tconstruct:seared_ingot_tank>);

/*
 * Removes two recipes from the Casting Table. If you are having trouble removing by the output, you may need to remove the recipe by its name.
 *  
 * 1) Removes the all Casting Table recipes for the Compass.
 * 2) Removes the Copper Can filling recipe.
 */

// <recipetype:tconstruct:casting_table>.removeRecipe(output as IItemStack)
// <recipetype:tconstruct:casting_table>.removeByName(name as string)

<recipetype:tconstruct:casting_table>.removeRecipe(<item:minecraft:compass>);
<recipetype:tconstruct:casting_table>.removeByName("tconstruct:smeltery/casting/filling/copper_can");
