package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.tconstruct.common.registration.MetalItemObject;

import java.util.Objects;
import java.util.function.Consumer;

public interface ICommonRecipeHelper extends IRecipeHelper {
  /* Metals */

  /**
   * Registers a recipe packing a small item into a large one
   * @param consumer   Recipe consumer
   * @param large      Large item
   * @param small      Small item
   * @param largeName  Large name
   * @param smallName  Small name
   * @param folder     Recipe folder
   */
  default void packingRecipe(Consumer<IFinishedRecipe> consumer, String largeName, IItemProvider large, String smallName, IItemProvider small, String folder) {
    // ingot to block
    ShapedRecipeBuilder.shaped(large)
                       .define('#', small)
                       .pattern("###")
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_item", RecipeProvider.has(small))
                       .group(Objects.requireNonNull(large.asItem().getRegistryName()).toString())
                       .save(consumer, wrap(large.asItem(), folder, String.format("_from_%ss", smallName)));
    // block to ingot
    ShapelessRecipeBuilder.shapeless(small, 9)
                          .requires(large)
                          .unlockedBy("has_item", RecipeProvider.has(large))
                          .group(Objects.requireNonNull(small.asItem().getRegistryName()).toString())
                          .save(consumer, wrap(small.asItem(), folder, String.format("_from_%s", largeName)));
  }

  /**
   * Registers a recipe packing a small item into a large one
   * @param consumer   Recipe consumer
   * @param largeItem  Large item
   * @param smallItem  Small item
   * @param smallTag   Tag for small item
   * @param largeName  Large name
   * @param smallName  Small name
   * @param folder     Recipe folder
   */
  default void packingRecipe(Consumer<IFinishedRecipe> consumer, String largeName, IItemProvider largeItem, String smallName, IItemProvider smallItem, ITag<Item> smallTag, String folder) {
    // ingot to block
    // note our item is in the center, any mod allowed around the edges
    ShapedRecipeBuilder.shaped(largeItem)
                       .define('#', smallTag)
                       .define('*', smallItem)
                       .pattern("###")
                       .pattern("#*#")
                       .pattern("###")
                       .unlockedBy("has_item", RecipeProvider.has(smallItem))
                       .group(Objects.requireNonNull(largeItem.asItem().getRegistryName()).toString())
                       .save(consumer, wrap(largeItem.asItem(), folder, String.format("_from_%ss", smallName)));
    // block to ingot
    ShapelessRecipeBuilder.shapeless(smallItem, 9)
                          .requires(largeItem)
                          .unlockedBy("has_item", RecipeProvider.has(largeItem))
                          .group(Objects.requireNonNull(smallItem.asItem().getRegistryName()).toString())
                          .save(consumer, wrap(smallItem.asItem(), folder, String.format("_from_%s", largeName)));
  }

  /**
   * Adds recipes to convert a block to ingot, ingot to block, and for nuggets
   * @param consumer  Recipe consumer
   * @param metal     Metal object
   * @param folder    Folder for recipes
   */
  default void metalCrafting(Consumer<IFinishedRecipe> consumer, MetalItemObject metal, String folder) {
    IItemProvider ingot = metal.getIngot();
    packingRecipe(consumer, "block", metal.get(), "ingot", ingot, metal.getIngotTag(), folder);
    packingRecipe(consumer, "ingot", ingot, "nugget", metal.getNugget(), metal.getNuggetTag(), folder);
  }


  /* Building blocks */

  /**
   * Registers generic saveing block recipes for slabs and stairs
   * @param consumer  Recipe consumer
   * @param saveing  Building object instance
   */
  default void slabStairsCrafting(Consumer<IFinishedRecipe> consumer, BuildingBlockObject saveing, String folder, boolean addStonecutter) {
    Item item = saveing.asItem();
    ICriterionInstance hasBlock = RecipeProvider.has(item);
    // slab
    IItemProvider slab = saveing.getSlab();
    ShapedRecipeBuilder.shaped(slab, 6)
                       .define('B', item)
                       .pattern("BBB")
                       .unlockedBy("has_item", hasBlock)
                       .group(Objects.requireNonNull(slab.asItem().getRegistryName()).toString())
                       .save(consumer, wrap(item, folder, "_slab"));
    // stairs
    IItemProvider stairs = saveing.getStairs();
    ShapedRecipeBuilder.shaped(stairs, 4)
                       .define('B', item)
                       .pattern("B  ")
                       .pattern("BB ")
                       .pattern("BBB")
                       .unlockedBy("has_item", hasBlock)
                       .group(Objects.requireNonNull(stairs.asItem().getRegistryName()).toString())
                       .save(consumer, wrap(item, folder, "_stairs"));

    // only add stonecutter if relevant
    if (addStonecutter) {
      Ingredient ingredient = Ingredient.of(item);
      SingleItemRecipeBuilder.stonecutting(ingredient, slab, 2)
                             .unlocks("has_item", hasBlock)
                             .save(consumer, wrap(item, folder, "_slab_stonecutter"));
      SingleItemRecipeBuilder.stonecutting(ingredient, stairs)
                             .unlocks("has_item", hasBlock)
                             .save(consumer, wrap(item, folder, "_stairs_stonecutter"));
    }
  }

  /**
   * Registers generic saveing block recipes for slabs, stairs, and walls
   * @param consumer  Recipe consumer
   * @param saveing  Building object instance
   */
  default void stairSlabWallCrafting(Consumer<IFinishedRecipe> consumer, WallBuildingBlockObject saveing, String folder, boolean addStonecutter) {
    slabStairsCrafting(consumer, saveing, folder, addStonecutter);
    // wall
    Item item = saveing.asItem();
    ICriterionInstance hasBlock = RecipeProvider.has(item);
    IItemProvider wall = saveing.getWall();
    ShapedRecipeBuilder.shaped(wall, 6)
                       .define('B', item)
                       .pattern("BBB")
                       .pattern("BBB")
                       .unlockedBy("has_item", hasBlock)
                       .group(Objects.requireNonNull(wall.asItem().getRegistryName()).toString())
                       .save(consumer, wrap(item, folder, "_wall"));
    // only add stonecutter if relevant
    if (addStonecutter) {
      Ingredient ingredient = Ingredient.of(item);
      SingleItemRecipeBuilder.stonecutting(ingredient, wall)
                             .unlocks("has_item", hasBlock)
                             .save(consumer, wrap(item, folder, "_wall_stonecutter"));
    }
  }

  /**
   * Registers recipes relevant to wood
   * @param consumer  Recipe consumer
   * @param wood      Wood types
   * @param folder    Wood folder
   */
  default void woodCrafting(Consumer<IFinishedRecipe> consumer, WoodBlockObject wood, String folder) {
    ICriterionInstance hasPlanks = RecipeProvider.has(wood);

    // planks
    ShapelessRecipeBuilder.shapeless(wood, 4).requires(wood.getLogItemTag())
                          .group("planks")
                          .unlockedBy("has_log", RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(wood.getLogItemTag()).build()))
                          .save(consumer, modResource(folder + "planks"));
    // slab
    IItemProvider slab = wood.getSlab();
    ShapedRecipeBuilder.shaped(slab, 6)
                       .define('#', wood)
                       .pattern("###")
                       .unlockedBy("has_planks", hasPlanks)
                       .group("wooden_slab")
                       .save(consumer, modResource(folder + "slab"));
    // stairs
    IItemProvider stairs = wood.getStairs();
    ShapedRecipeBuilder.shaped(stairs, 4)
                       .define('#', wood)
                       .pattern("#  ")
                       .pattern("## ")
                       .pattern("###")
                       .unlockedBy("has_planks", hasPlanks)
                       .group("wooden_stairs")
                       .save(consumer, modResource(folder + "stairs"));

    // log to stripped
    ShapedRecipeBuilder.shaped(wood.getWood(), 3)
                       .define('#', wood.getLog())
                       .pattern("##").pattern("##")
                       .group("bark")
                       .unlockedBy("has_log", RecipeProvider.has(wood.getLog()))
                       .save(consumer, modResource(folder + "log_to_wood"));
    ShapedRecipeBuilder.shaped(wood.getStrippedWood(), 3)
                       .define('#', wood.getStrippedLog())
                       .pattern("##").pattern("##")
                       .group("bark")
                       .unlockedBy("has_log", RecipeProvider.has(wood.getStrippedLog()))
                       .save(consumer, modResource(folder + "stripped_log_to_wood"));
    // doors
    ShapedRecipeBuilder.shaped(wood.getFence(), 3)
                       .define('#', Tags.Items.RODS_WOODEN).define('W', wood)
                       .pattern("W#W").pattern("W#W")
                       .group("wooden_fence")
                       .unlockedBy("has_planks", hasPlanks)
                       .save(consumer, modResource(folder + "fence"));
    ShapedRecipeBuilder.shaped(wood.getFenceGate())
                       .define('#', Items.STICK).define('W', wood)
                       .pattern("#W#").pattern("#W#")
                       .group("wooden_fence_gate")
                       .unlockedBy("has_planks", hasPlanks)
                       .save(consumer, modResource(folder + "fence_gate"));
    ShapedRecipeBuilder.shaped(wood.getDoor(), 3)
                       .define('#', wood)
                       .pattern("##").pattern("##").pattern("##")
                       .group("wooden_door")
                       .unlockedBy("has_planks", hasPlanks)
                       .save(consumer, modResource(folder + "door"));
    ShapedRecipeBuilder.shaped(wood.getTrapdoor(), 2)
                       .define('#', wood)
                       .pattern("###").pattern("###")
                       .group("wooden_trapdoor")
                       .unlockedBy("has_planks", hasPlanks)
                       .save(consumer, modResource(folder + "trapdoor"));
    // buttons
    ShapelessRecipeBuilder.shapeless(wood.getButton())
                          .requires(wood)
                          .group("wooden_button")
                          .unlockedBy("has_planks", hasPlanks)
                          .save(consumer, modResource(folder + "button"));
    ShapedRecipeBuilder.shaped(wood.getPressurePlate())
                       .define('#', wood)
                       .pattern("##")
                       .group("wooden_pressure_plate")
                       .unlockedBy("has_planks", hasPlanks)
                       .save(consumer, modResource(folder + "pressure_plate"));
    // signs
    ShapedRecipeBuilder.shaped(wood.getSign(), 3)
                       .group("sign")
                       .define('#', wood).define('X', Tags.Items.RODS_WOODEN)
                       .pattern("###").pattern("###").pattern(" X ")
                       .unlockedBy("has_planks", RecipeProvider.has(wood))
                       .save(consumer, modResource(folder + "sign"));

  }
}
