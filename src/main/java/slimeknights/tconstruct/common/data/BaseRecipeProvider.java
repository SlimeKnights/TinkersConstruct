package slimeknights.tconstruct.common.data;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.registries.IForgeRegistryEntry;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.tconstruct.TConstruct;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
   * @param entry    Item registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  protected static ResourceLocation wrapR(Supplier<? extends IForgeRegistryEntry<?>> entry, String prefix, String suffix) {
    ResourceLocation loc = Objects.requireNonNull(entry.get().getRegistryName());
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
   * Prefixes the resource location path with the given value
   * @param entry   Entry registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  protected static ResourceLocation prefixR(Supplier<? extends IForgeRegistryEntry<?>> entry, String prefix) {
    ResourceLocation loc = Objects.requireNonNull(entry.get().getRegistryName());
    return location(prefix + loc.getPath());
  }

  /**
   * Prefixes the resource location path with the given value
   * @param entry   Entry registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  protected static ResourceLocation prefixR(IForgeRegistryEntry<?> entry, String prefix) {
    ResourceLocation loc = Objects.requireNonNull(entry.getRegistryName());
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
   * Registers generic building block recipes for slabs and stairs
   * @param consumer  Recipe consumer
   * @param building  Building object instance
   */
  protected void registerSlabStair(Consumer<IFinishedRecipe> consumer, BuildingBlockObject building, String folder, boolean addStonecutter) {
    Item item = building.asItem();
    ICriterionInstance hasBlock = hasItem(item);
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
      Ingredient ingredient = Ingredient.fromItems(item);
      SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, slab, 2)
                             .addCriterion("has_item", hasBlock)
                             .build(consumer, wrap(item, folder, "_slab_stonecutter"));
      SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, stairs)
                             .addCriterion("has_item", hasBlock)
                             .build(consumer, wrap(item, folder, "_stairs_stonecutter"));
    }
  }

  /**
   * Registers generic building block recipes for slabs, stairs, and walls
   * @param consumer  Recipe consumer
   * @param building  Building object instance
   */
  protected void registerSlabStairWall(Consumer<IFinishedRecipe> consumer, WallBuildingBlockObject building, String folder, boolean addStonecutter) {
    registerSlabStair(consumer, building, folder, addStonecutter);
    // wall
    Item item = building.asItem();
    ICriterionInstance hasBlock = hasItem(item);
    IItemProvider wall = building.getWall();
    ShapedRecipeBuilder.shapedRecipe(wall, 4)
                       .key('B', item)
                       .patternLine("BBB")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasBlock)
                       .setGroup(Objects.requireNonNull(wall.asItem().getRegistryName()).toString())
                       .build(consumer, wrap(item, folder, "_wall"));
    // only add stonecutter if relevant
    if (addStonecutter) {
      Ingredient ingredient = Ingredient.fromItems(item);
      SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, wall)
                             .addCriterion("has_item", hasBlock)
                             .build(consumer, wrap(item, folder, "_wall_stonecutter"));
    }
  }

  /**
   * Registers a recipe packing a small item into a large one
   * @param consumer   Recipe consumer
   * @param large      Large item
   * @param small      Small item
   * @param largeName  Large name
   * @param smallName  Small name
   * @param folder     Recipe folder
   */
  protected void registerPackingRecipe(Consumer<IFinishedRecipe> consumer, String largeName, IItemProvider large, String smallName, IItemProvider small, String folder) {
    // ingot to block
    ShapedRecipeBuilder.shapedRecipe(large)
                       .key('#', small)
                       .patternLine("###")
                       .patternLine("###")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(small))
                       .setGroup(Objects.requireNonNull(large.asItem().getRegistryName()).toString())
                       .build(consumer, wrap(large, folder, String.format("_from_%ss", smallName)));
    // block to ingot
    ShapelessRecipeBuilder.shapelessRecipe(small, 9)
                          .addIngredient(large)
                          .addCriterion("has_item", hasItem(large))
                          .setGroup(Objects.requireNonNull(small.asItem().getRegistryName()).toString())
                          .build(consumer, wrap(small, folder, String.format("_from_%s", largeName)));
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
  protected void registerPackingRecipe(Consumer<IFinishedRecipe> consumer, String largeName, IItemProvider largeItem, String smallName, IItemProvider smallItem, ITag<Item> smallTag, String folder) {
    // ingot to block
    // note our item is in the center, any mod allowed around the edges
    ShapedRecipeBuilder.shapedRecipe(largeItem)
                       .key('#', smallTag)
                       .key('*', smallItem)
                       .patternLine("###")
                       .patternLine("#*#")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(smallItem))
                       .setGroup(Objects.requireNonNull(largeItem.asItem().getRegistryName()).toString())
                       .build(consumer, wrap(largeItem, folder, String.format("_from_%ss", smallName)));
    // block to ingot
    ShapelessRecipeBuilder.shapelessRecipe(smallItem, 9)
                          .addIngredient(largeItem)
                          .addCriterion("has_item", hasItem(largeItem))
                          .setGroup(Objects.requireNonNull(smallItem.asItem().getRegistryName()).toString())
                          .build(consumer, wrap(smallItem, folder, String.format("_from_%s", largeName)));
  }


  /* conditions */

  /**
   * Creates a consumer instance with the added conditions
   * @param consumer    Base consumer
   * @param conditions  Extra conditions
   * @return  Wrapped consumer
   */
  protected static Consumer<IFinishedRecipe> withCondition(Consumer<IFinishedRecipe> consumer, ICondition... conditions) {
    ConsumerWrapperBuilder builder = ConsumerWrapperBuilder.wrap();
    for (ICondition condition : conditions) {
      builder.addCondition(condition);
    }
    return builder.build(consumer);
  }

  /**
   * Creates a condition for a tag existing
   * @param name  Forge tag name
   * @return  Condition for tag existing
   */
  protected static ICondition tagCondition(String name) {
    return new NotCondition(new TagEmptyCondition("forge", name));
  }

  // Forge constructor is private, not sure if there is a public place for this
  protected static class CompoundIngredient extends net.minecraftforge.common.crafting.CompoundIngredient {
    public CompoundIngredient(List<Ingredient> children) {
      super(children);
    }

    public CompoundIngredient(Ingredient... children) {
      this(Arrays.asList(children));
    }
  }
}
