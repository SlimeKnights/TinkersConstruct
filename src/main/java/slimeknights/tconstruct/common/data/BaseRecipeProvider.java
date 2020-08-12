package slimeknights.tconstruct.common.data;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Shared logic for each module's recipe provider
 */
public abstract class BaseRecipeProvider extends RecipeProvider implements IConditionBuilder {
  public BaseRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected abstract void registerRecipes(Consumer<IFinishedRecipe> consumer);


  /* Location helpers */

  /**
   * Gets a resource location for Tinkers
   * @param id  Location path
   * @return  Location for Tinkers
   */
  protected static ResourceLocation location(String id) {
    return new ResourceLocation(TConstruct.modID, id);
  }

  /**
   * Gets a resource location string for Tinkers
   * @param id  Location path
   * @return  Location for Tinkers
   */
  protected static String locationString(String id) {
    return TConstruct.modID + ":" + id;
  }

  /**
   * Prefixes the resource location path with the given value
   * @param item    Item registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  protected static ResourceLocation wrap(IItemProvider item, String prefix, String suffix) {
    ResourceLocation loc = Objects.requireNonNull(item.asItem().getRegistryName());
    return location(prefix + loc.getPath() + suffix);
  }

  /**
   * Prefixes the resource location path with the given value
   * @param item    Item registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  protected static ResourceLocation prefix(IItemProvider item, String prefix) {
    ResourceLocation loc = Objects.requireNonNull(item.asItem().getRegistryName());
    return location(prefix + loc.getPath());
  }

  /**
   * Gets a tag by name
   * @param modId  Mod ID for tag
   * @param name   Tag name
   * @return  Tag instance
   */
  protected static ITag<Item> getTag(String modId, String name) {
    return ItemTags.makeWrapperTag(modId + ":" + name);
  }


  /* Recipe helpers */

  /**
   * Adds a recipe to create a cast
   * @param consumer  Recipe consumer
   * @param input     Item consumed to create cast
   * @param cast      Produced cast
   * @param folder    Output folder
   */
  protected void addCastCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider cast, String folder) {
    ItemCastingRecipeBuilder.tableRecipe(cast)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot))
                            .setCast(input, true)
                            .setSwitchSlots()
                            .addCriterion("has_item", hasItem(input))
                            .build(consumer, location(folder + "casts/" + Objects.requireNonNull(input.asItem().getRegistryName()).getPath()));
  }

  /**
   * Registers generic building block recipes
   * @param consumer  Recipe consumer
   * @param building  Building object instance
   */
  protected void registerSlabStair(Consumer<IFinishedRecipe> consumer, BuildingBlockObject building, String folder, boolean addStonecutter) {
    Item item = building.asItem();
    ICriterionInstance hasBlock = hasItem(item);
    Ingredient ingredient = Ingredient.fromItems(item);
    // slab
    IItemProvider slab = building.getSlab();
    ShapedRecipeBuilder.shapedRecipe(slab, 6)
                       .key('B', item)
                       .patternLine("BBB")
                       .addCriterion("has_item", hasBlock)
                       .setGroup(Objects.requireNonNull(slab.asItem().getRegistryName()).toString())
                       .build(consumer, wrap(item, folder, "_slab"));
    // stairs
    IItemProvider stairs = building.getStairs();
    ShapedRecipeBuilder.shapedRecipe(stairs, 4)
                       .key('B', item)
                       .patternLine("B  ")
                       .patternLine("BB ")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasBlock)
                       .setGroup(Objects.requireNonNull(stairs.asItem().getRegistryName()).toString())
                       .build(consumer, wrap(item, folder, "_stairs"));

    // only add stonecutter if relevant
    if (addStonecutter) {
      SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, slab, 2)
                             .addCriterion("has_item", hasBlock)
                             .build(consumer, wrap(item, folder, "_slab_stonecutter"));
      SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, stairs)
                             .addCriterion("has_item", hasBlock)
                             .build(consumer, wrap(item, folder, "_stairs_stonecutter"));
    }
  }

  // Forge constructor is private, not sure if there is a public place for this
  protected static class CompoundIngredient extends net.minecraftforge.common.crafting.CompoundIngredient {
    public CompoundIngredient(List<Ingredient> children) {
      super(children);
    }
  }
}
