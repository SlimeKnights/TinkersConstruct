package slimeknights.tconstruct.common.data;

import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Shared logic for each module's recipe provider
 */
public abstract class BaseRecipeProvider extends RecipesProvider implements IConditionBuilder {
  public BaseRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  protected abstract void generate(Consumer<RecipeJsonProvider> consumer);

  @Override
  public abstract String getName();


  /* Location helpers */

  /**
   * Gets a resource location for Tinkers
   * @param id  Location path
   * @return  Location for Tinkers
   */
  protected static Identifier location(String id) {
    return new Identifier(TConstruct.modID, id);
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
  protected static Identifier wrap(ItemConvertible item, String prefix, String suffix) {
    Identifier loc = Objects.requireNonNull(item.asItem().getRegistryName());
    return location(prefix + loc.getPath() + suffix);
  }

  /**
   * Prefixes the resource location path with the given value
   * @param entry    Item registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  protected static Identifier wrapR(Modifier entry, String prefix, String suffix) {
    Identifier loc = Objects.requireNonNull(entry.getRegistryName());
    return location(prefix + loc.getPath() + suffix);
  }

  /**
   * Prefixes the resource location path with the given value
   * @param item    Item registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  protected static Identifier prefix(ItemConvertible item, String prefix) {
    Identifier loc = Objects.requireNonNull(item.asItem().getRegistryName());
    return location(prefix + loc.getPath());
  }

  /**
   * Prefixes the resource location path with the given value
   * @param entry   Entry registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  protected static Identifier prefixR(Supplier<? extends IForgeRegistryEntry<?>> entry, String prefix) {
    Identifier loc = Objects.requireNonNull(entry.get().getRegistryName());
    return location(prefix + loc.getPath());
  }

  /**
   * Prefixes the resource location path with the given value
   * @param entry   Entry registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  protected static Identifier prefixR(Modifier entry, String prefix) {
    Identifier loc = Objects.requireNonNull(entry.getRegistryName());
    return location(prefix + loc.getPath());
  }

  /**
   * Gets a tag by name
   * @param modId  Mod ID for tag
   * @param name   Tag name
   * @return  Tag instance
   */
  protected static Tag<Item> getTag(String modId, String name) {
    return ItemTags.register(modId + ":" + name);
  }


  /* Recipe helpers */

  /**
   * Registers generic building block recipes for slabs and stairs
   * @param consumer  Recipe consumer
   * @param building  Building object instance
   */
  protected void registerSlabStair(Consumer<RecipeJsonProvider> consumer, BuildingBlockObject building, String folder, boolean addStonecutter) {
    Item item = building.asItem();
    CriterionConditions hasBlock = conditionsFromItem(item);
    // slab
    ItemConvertible slab = building.getSlab();
    ShapedRecipeJsonFactory.create(slab, 6)
                       .input('B', item)
                       .pattern("BBB")
                       .criterion("has_item", hasBlock)
                       .group(Objects.requireNonNull(slab.asItem().getRegistryName()).toString())
                       .offerTo(consumer, wrap(item, folder, "_slab"));
    // stairs
    ItemConvertible stairs = building.getStairs();
    ShapedRecipeJsonFactory.create(stairs, 4)
                       .input('B', item)
                       .pattern("B  ")
                       .pattern("BB ")
                       .pattern("BBB")
                       .criterion("has_item", hasBlock)
                       .group(Objects.requireNonNull(stairs.asItem().getRegistryName()).toString())
                       .offerTo(consumer, wrap(item, folder, "_stairs"));

    // only add stonecutter if relevant
    if (addStonecutter) {
      Ingredient ingredient = Ingredient.ofItems(item);
      SingleItemRecipeJsonFactory.create(ingredient, slab, 2)
                             .create("has_item", hasBlock)
                             .offerTo(consumer, wrap(item, folder, "_slab_stonecutter"));
      SingleItemRecipeJsonFactory.create(ingredient, stairs)
                             .create("has_item", hasBlock)
                             .offerTo(consumer, wrap(item, folder, "_stairs_stonecutter"));
    }
  }

  /**
   * Registers generic building block recipes for slabs, stairs, and walls
   * @param consumer  Recipe consumer
   * @param building  Building object instance
   */
  protected void registerSlabStairWall(Consumer<RecipeJsonProvider> consumer, WallBuildingBlockObject building, String folder, boolean addStonecutter) {
    registerSlabStair(consumer, building, folder, addStonecutter);
    // wall
    Item item = building.asItem();
    CriterionConditions hasBlock = conditionsFromItem(item);
    ItemConvertible wall = building.getWall();
    ShapedRecipeJsonFactory.create(wall, 4)
                       .input('B', item)
                       .pattern("BBB")
                       .pattern("BBB")
                       .criterion("has_item", hasBlock)
                       .group(Objects.requireNonNull(wall.asItem().getRegistryName()).toString())
                       .offerTo(consumer, wrap(item, folder, "_wall"));
    // only add stonecutter if relevant
    if (addStonecutter) {
      Ingredient ingredient = Ingredient.ofItems(item);
      SingleItemRecipeJsonFactory.create(ingredient, wall)
                             .create("has_item", hasBlock)
                             .offerTo(consumer, wrap(item, folder, "_wall_stonecutter"));
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
  protected void registerPackingRecipe(Consumer<RecipeJsonProvider> consumer, String largeName, ItemConvertible large, String smallName, ItemConvertible small, String folder) {
    // ingot to block
    ShapedRecipeJsonFactory.create(large)
                       .input('#', small)
                       .pattern("###")
                       .pattern("###")
                       .pattern("###")
                       .criterion("has_item", conditionsFromItem(small))
                       .group(Objects.requireNonNull(large.asItem().getRegistryName()).toString())
                       .offerTo(consumer, wrap(large, folder, String.format("_from_%ss", smallName)));
    // block to ingot
    ShapelessRecipeJsonFactory.create(small, 9)
                          .input(large)
                          .criterion("has_item", conditionsFromItem(large))
                          .group(Objects.requireNonNull(small.asItem().getRegistryName()).toString())
                          .offerTo(consumer, wrap(small, folder, String.format("_from_%s", largeName)));
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
  protected void registerPackingRecipe(Consumer<RecipeJsonProvider> consumer, String largeName, ItemConvertible largeItem, String smallName, ItemConvertible smallItem, Tag<Item> smallTag, String folder) {
    // ingot to block
    // note our item is in the center, any mod allowed around the edges
    ShapedRecipeJsonFactory.create(largeItem)
                       .input('#', smallTag)
                       .input('*', smallItem)
                       .pattern("###")
                       .pattern("#*#")
                       .pattern("###")
                       .criterion("has_item", conditionsFromItem(smallItem))
                       .group(Objects.requireNonNull(largeItem.asItem().getRegistryName()).toString())
                       .offerTo(consumer, wrap(largeItem, folder, String.format("_from_%ss", smallName)));
    // block to ingot
    ShapelessRecipeJsonFactory.create(smallItem, 9)
                          .input(largeItem)
                          .criterion("has_item", conditionsFromItem(largeItem))
                          .group(Objects.requireNonNull(smallItem.asItem().getRegistryName()).toString())
                          .offerTo(consumer, wrap(smallItem, folder, String.format("_from_%s", largeName)));
  }


  /* conditions */

  /**
   * Creates a consumer instance with the added conditions
   * @param consumer    Base consumer
   * @param conditions  Extra conditions
   * @return  Wrapped consumer
   */
  protected static Consumer<RecipeJsonProvider> withCondition(Consumer<RecipeJsonProvider> consumer, ICondition... conditions) {
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
    protected static class CompoundIngredient extends Ingredient {
    public CompoundIngredient(List<Ingredient> children) {
      super((Stream<? extends Ingredient.Entry>) children);
    }

    public CompoundIngredient(Ingredient... children) {
      this(Arrays.asList(children));
    }
  }
}
