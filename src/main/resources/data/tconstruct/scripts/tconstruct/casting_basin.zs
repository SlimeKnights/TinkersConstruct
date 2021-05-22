/*
 *
 * NOTE: Composite casting recipes only work for parts that already have an existing material casting recipe.
 * The recipe is only listed to show an example but it will not actually craft.
 *
 * Adds four Casting Basin recipes that do the following:
 * 
 * 1) Adds an Item Casting Recipe that, when a Honey Bottle is used as a Cast for 1000mb of Blood (Fluid), it gets cast into a Diamond of 200 ticks of cooling time. The Honey Bottle is not consumed and does not switch slots with the Diamond.
 * 2) Adds a Material Casting Recipe that will allow a Diamond to be used as a Cast to make any Axe Head (Part), regardless of material (The Diamond acts as an Axe Head Cast). The Diamond is consumed by the recipe and does not switch slots with the Axe Head.
 * 2.1) The result Item needs to be an Item that works with Materials, you can find a list of valid items by running `/ct dump ticMaterialItems`.
 * 3) Adds a Composite Casting Recipe that uses Iron Tool Parts (Iron Axe Head for example) and Water (Fluid) to create a Bone version of that part. The recipe has a cooling temperature of 0°C. 
 * 3.1) Composite Casting Recipes are Recipes where the Fluid is Cast onto a pre-existing tool part to create a new part.
 * 3.2) You can find valid materialIds by running `/ct dump ticMaterials` in-game.
 * 4) Adds a Container Filling Recipe that will fill a Copper Can with any Fluid when cast.
 * 4.1) This script is commented out as it conflicts with default Tinkers Recipes.
 * 4.2) The amount of fluid is the maximum amount of fluid that can be inserted into the container at a time.
 */

// <recipetype:tconstruct:casting_basin>.addItemCastingRecipe(name as string, cast as IIngredient, fluid as IFluidStack, result as IItemStack, coolingTime as int, consumeCast as boolean, switchSlots as boolean)
// <recipetype:tconstruct:casting_basin>.addMaterialCastingRecipe(name as string, cast as IIngredient, fluidAmount as int, result as Item, consumeCast as boolean, switchSlots as boolean)
// <recipetype:tconstruct:casting_basin>.addCompositeCastingRecipe(name as string, materialId as string, fluidstack as IFluidStack, outputMaterialId as string, coolingTemperature as int)
// <recipetype:tconstruct:casting_basin>.makeContainerFillingRecipe(name as string, fluidAmount as int, containerIn as Item)

<recipetype:tconstruct:casting_basin>.addItemCastingRecipe("item_casting_basin_test", <item:minecraft:honey_bottle>, <fluid:tconstruct:blood> * 1000, <item:minecraft:diamond>, 200, false, false);
<recipetype:tconstruct:casting_basin>.addMaterialCastingRecipe("material_casting_basin_test", <item:minecraft:diamond>, 250, <item:tconstruct:small_axe_head>, true, false);
<recipetype:tconstruct:casting_basin>.addCompositeCastingRecipe("composite_casting_basin_test", "tconstruct:iron", <fluid:minecraft:water>, "tconstruct:bone", 0);
// <recipetype:tconstruct:casting_basin>.makeContainerFillingRecipe("filling_casting_test", 100, <item:tconstruct:copper_can>);

/*
 * Removes two recipes from the Casting Basin. If you are having trouble removing by the output, you may need to remove the recipe by its name.
 *  
 * 1) Removes the all Casting Basin recipes for Blazewood.
 * 2) Removes the Diamond block Casting Basin recipe.
 */

// <recipetype:tconstruct:casting_basin>.removeRecipe(output as IItemStack)
// <recipetype:tconstruct:casting_basin>.removeByName(name as string)

<recipetype:tconstruct:casting_basin>.removeRecipe(<item:tconstruct:blazewood>);
<recipetype:tconstruct:casting_basin>.removeByName("tconstruct:smeltery/casting/diamond/block");
