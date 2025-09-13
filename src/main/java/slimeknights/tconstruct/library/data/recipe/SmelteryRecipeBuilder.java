package slimeknights.tconstruct.library.data.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.recipe.condition.TagCombinationCondition;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static slimeknights.mantle.Mantle.commonResource;
import static slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

/**
 * Helper for building melting and casting recipes for a fluid.
 *
 * Using this builder takes place in three steps:
 * <ol>
 *   <li>Start by calling any relevant builder methods to set relevant properties, such as {@link #optional()}, {@link #ore(IByproduct...)} and alike.</li>
 *   <li>Once recipes are prepared, call either {@link #smallGem()}, {@link #largeGem()}, or {@link #metal()} to generate common recipes.</li>
 *   <li>Finally, call any other recipe helpers such as {@link #dust()}, {@link #plate()} or the tool helpers through {@link #common(CommonRecipe...)} to generate non-common recipes.</li>
 * </ol>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@CanIgnoreReturnValue
public class SmelteryRecipeBuilder {
  /** Array to pass into undamagable recipes */
  private static final int[] UNDAMAGABLE = {0};

  /** Consumer for recipe results */
  private final Consumer<FinishedRecipe> consumer;
  /** Resource name, domain is location for results and name is tag root */
  private final ResourceLocation name;
  /** Fluid object to generate results and ingredients, takes top priority */
  @Nullable
  private final FluidObject<?> fluidObject;
  /** Fluid to generate results and ingredients. If set along tag, tag is priorized for ingredients. */
  @Nullable
  private final Fluid fluid;
  /** Fluid tag to generate results and ingredients. If set along fluid, fluid is prioritized for results */
  @Nullable
  private final TagKey<Fluid> fluidTag;
  /** Temperature for recipes */
  @Setter
  private int temperature;
  /** If true, all recipes are optional. If false only unsupported types are optional */
  @Setter
  private boolean optional = false;
  /** If true, ore recipes should be added */
  private boolean hasOre = false;
  /** List of byproducts for ore recipes */
  private IByproduct[] oreByproducts = new IByproduct[0];
  /** List of byproducts for non-ore recipes */
  private IByproduct[] unitByproducts = new IByproduct[0];
  /** Folder to save melting recipes */
  private String meltingFolder = "melting/";
  /** Folder to save casting recipes */
  private String castingFolder = "casting/";
  /** Keeps track of whether this builder is used for gems or metals */
  @Setter
  private OreRateType oreRate = null;
  /** Base unit value for builder */
  @Setter
  private int baseUnit = 0;
  /** Base unit value for builder */
  @Setter
  private int damageUnit = 1;

  /* Constructors */

  /** Creates a builder for the given fluid object */
  @CheckReturnValue
  public static SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, ResourceLocation name, FluidObject<?> fluid) {
    return new SmelteryRecipeBuilder(consumer, name, fluid, null, null).temperature(getTemperature(fluid));
  }

  /** Creates a builder for the given fluid and tags. Tag will be used for inputs and fluid for outputs */
  @CheckReturnValue
  public static SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, ResourceLocation name, @Nullable Fluid fluid, @Nullable TagKey<Fluid> fluidTag) {
    assert fluid != null || fluidTag != null;
    SmelteryRecipeBuilder builder = new SmelteryRecipeBuilder(consumer, name, null, fluid, fluidTag);
    if (fluid != null) {
      builder.temperature(getTemperature(fluid));
    }
    return builder;
  }

  /** Creates a builder for the given fluid, used as input and output */
  @CheckReturnValue
  public static SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, ResourceLocation name, Fluid fluid) {
    return fluid(consumer, name, fluid, null);
  }

  /** Creates a builder for the given fluid tags, used as input and output */
  @CheckReturnValue
  public static SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, ResourceLocation name, TagKey<Fluid> fluidTag) {
    return fluid(consumer, name, null, fluidTag);
  }


  /* Setters */

  /** Sets all recipes to optional, used for compat */
  public SmelteryRecipeBuilder optional() {
    return optional(true);
  }

  /** Sets the byproducts for following recipes */
  public SmelteryRecipeBuilder ore(IByproduct... byproducts) {
    this.oreByproducts = byproducts;
    this.hasOre = true;
    return this;
  }

  /** Sets the byproducts for unit melting */
  public SmelteryRecipeBuilder unitByproducts(IByproduct... byproducts) {
    this.unitByproducts = byproducts;
    return this;
  }

  /** Sets the output prefix for melting recipes */
  public SmelteryRecipeBuilder meltingFolder(String meltingFolder) {
    this.meltingFolder = meltingFolder + '/';
    return this;
  }

  /** Sets the output prefix for casting recipes */
  public SmelteryRecipeBuilder castingFolder(String castingFolder) {
    this.castingFolder = castingFolder + '/';
    return this;
  }


  /* Basic helpers */

  /** Creates a recipe result for melting */
  @CheckReturnValue
  private FluidOutput result(int amount) {
    if (fluidObject != null) {
      return fluidObject.result(amount);
    }
    if (fluid != null) {
      return FluidOutput.fromFluid(fluid, amount);
    }
    assert fluidTag != null;
    return FluidOutput.fromTag(fluidTag, amount);
  }

  /** Creates an ingredient input for casting */
  @CheckReturnValue
  private FluidIngredient ingredient(int amount) {
    if (fluidObject != null) {
      return fluidObject.ingredient(amount);
    }
    if (fluidTag != null) {
      return FluidIngredient.of(fluidTag, amount);
    }
    assert fluid != null;
    return FluidIngredient.of(fluid, amount);
  }

  /** Adds the given conditions to the given builder */
  @CheckReturnValue
  private Consumer<FinishedRecipe> withCondition(ICondition... conditions) {
    ConsumerWrapperBuilder builder = ConsumerWrapperBuilder.wrap();
    for (ICondition condition : conditions) {
      builder.addCondition(condition);
    }
    return builder.build(consumer);
  }

  /** Creates a condition for a tag being empty */
  @CheckReturnValue
  public static ICondition tagCondition(ResourceLocation tag) {
    return new TagFilledCondition<>(ItemTags.create(tag));
  }

  /** Creates a condition for a tag being empty */
  @CheckReturnValue
  public static ICondition tagCondition(String name) {
    return tagCondition(commonResource(name));
  }

  /** Creates a tag key for an item */
  @CheckReturnValue
  public static TagKey<Item> itemTag(String name) {
    return ItemTags.create(commonResource(name));
  }

  /** Creates a location under the given domain with the passed prefix  */
  @CheckReturnValue
  private ResourceLocation location(String folder, String variant) {
    return name.withPath(folder + name.getPath() + '/' + variant);
  }

  /** Gets the units for a damagable melting recipe */
  private int[] damageUnits() {
    int[] units = new int[1 + unitByproducts.length];
    units[0] = damageUnit;
    for (int i = 0; i < unitByproducts.length; i++) {
      units[i+1] = unitByproducts[i].getDamageUnit();
    }
    return units;
  }


  /* Melting helpers */

  /** Adds a recipe for melting a list of items. Never optional */
  private void minecraftArmorMelting(int cost, String prefix, String name) {
    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(prefix + '_' + name));
    if (item == Items.AIR) {
      throw new IllegalArgumentException("Unknown item name minecraft:" + name);
    }
    MeltingRecipeBuilder.melting(Ingredient.of(item), result(cost * baseUnit), temperature, (float)Math.sqrt(cost))
                        .setDamagable(damageUnit)
                        .save(consumer, location(meltingFolder, name));
  }

  /** Adds a recipe for melting an item by ID. Automatically optional */
  private void itemMelting(float scale, String output, float factor, ResourceLocation itemName, boolean damagable) {
    MeltingRecipeBuilder builder = MeltingRecipeBuilder.melting(ItemNameIngredient.from(itemName), result((int) (baseUnit * scale)), temperature, factor);
    if (damagable) {
      builder.setDamagable(damageUnits());
    }
    for (IByproduct byproduct : unitByproducts) {
      builder.addByproduct(byproduct.getFluid(scale));
    }
    builder.save(withCondition(new ItemExistsCondition(itemName)), location(meltingFolder, output));
  }

  /** Adds a recipe for melting an item from a tag */
  private void tagMelting(float scale, String output, float factor, String tagName, boolean forceOptional) {
    tagMelting(scale, output, factor, commonResource(tagName), false, forceOptional);
  }

  /** Adds a recipe for melting an item from a tag */
  private void tagMelting(float scale, String output, float factor, ResourceLocation tagName, boolean damagable, boolean forceOptional) {
    Consumer<FinishedRecipe> wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    MeltingRecipeBuilder builder = MeltingRecipeBuilder.melting(Ingredient.of(ItemTags.create(tagName)), result((int) (baseUnit * scale)), temperature, factor);
    if (damagable) {
      builder.setDamagable(damageUnits());
    }
    for (IByproduct byproduct : unitByproducts) {
      builder.addByproduct(byproduct.getFluid(scale));
    }
    builder.save(wrapped, location(meltingFolder, output));
  }

  /** Adds recipes to melt an ore item with byproducts */
  private void oreMelting(float scale, String tagPrefix, @Nullable TagKey<Item> size, float factor, String output, boolean forceOptional) {
    assert oreRate != null;
    assert baseUnit != 0;
    String tagName = tagPrefix + this.name.getPath();
    Consumer<FinishedRecipe> wrapped;
    Ingredient baseIngredient = Ingredient.of(itemTag(tagName));
    Ingredient ingredient;
    // not everyone sets size, so treat singular as the fallback, means we want anything in the tag that is not sparse or dense
    if (size == Tags.Items.ORE_RATES_SINGULAR) {
      ingredient = DifferenceIngredient.of(baseIngredient, Ingredient.of(TinkerTags.Items.NON_SINGULAR_ORE_RATES));
      wrapped = withCondition(TagCombinationCondition.difference(itemTag(tagName), TinkerTags.Items.NON_SINGULAR_ORE_RATES));
      // size tag means we want an intersection between the tag and that size
    } else if (size != null) {
      ingredient = IntersectionIngredient.of(baseIngredient, Ingredient.of(size));
      wrapped = withCondition(TagCombinationCondition.intersection(itemTag(tagName), size));
      // default only need it to be in the tag
    } else {
      ingredient = baseIngredient;
      wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    }
    Supplier<MeltingRecipeBuilder> supplier = () -> MeltingRecipeBuilder.melting(ingredient, result((int)(baseUnit * scale)), temperature, factor).setOre(oreRate);
    ResourceLocation location = location(meltingFolder, output);

    // if no byproducts, just build directly
    if (oreByproducts.length == 0) {
      supplier.get().save(wrapped, location);
      // if first option is always present, only need that one
    } else if (oreByproducts[0].isAlwaysPresent()) {
      supplier.get()
              .addByproduct(oreByproducts[0].getFluid(scale))
              .setOre(oreRate, oreByproducts[0].getOreRate())
              .save(wrapped, location);
    } else {
      // multiple options, will need a conditonal recipe
      ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
      boolean alwaysPresent = false;
      for (IByproduct byproduct : oreByproducts) {
        // found an always present byproduct? no need to tag and we are done
        alwaysPresent = byproduct.isAlwaysPresent();
        if (alwaysPresent) {
          builder.addCondition(TrueCondition.INSTANCE);
        } else {
          builder.addCondition(tagCondition("ingots/" + byproduct.getName()));
        }
        builder.addRecipe(supplier.get().addByproduct(byproduct.getFluid(scale)).setOre(oreRate, byproduct.getOreRate())::save);

        if (alwaysPresent) {
          break;
        }
      }
      // not always present? add a recipe with no byproducts as a final fallback
      if (!alwaysPresent) {
        builder.addCondition(TrueCondition.INSTANCE);
        builder.addRecipe(supplier.get()::save);
      }
      builder.build(wrapped, location);
    }
  }


  /* Casting helpers */

  /** Recipe to cast using a cast */
  private void tagCasting(float scale, String outputPrefix, CastItemObject cast, String tagName, boolean forceOptional) {
    if (unitByproducts.length > 0) {
      throw new IllegalArgumentException("Cannot cast using a cast for a fluid with byproducts");
    }
    Consumer<FinishedRecipe> wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    ItemOutput output = ItemOutput.fromTag(itemTag(tagName));
    int amount = (int) (baseUnit * scale);
    FluidIngredient fluid = ingredient(amount);
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluid(fluid)
                            .setCoolingTime(temperature, amount)
                            .setCast(cast.getMultiUseTag(), false)
                            .save(wrapped, location(castingFolder, outputPrefix + "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluid(fluid)
                            .setCoolingTime(temperature, amount)
                            .setCast(cast.getSingleUseTag(), true)
                            .save(wrapped, location(castingFolder, outputPrefix + "_sand_cast"));
  }

  /** Recipe to composite cast */
  private void tagCasting(float scale, String outputName, Ingredient cast, String tagName, boolean forceOptional) {
    Consumer<FinishedRecipe> wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    ItemOutput output = ItemOutput.fromTag(itemTag(tagName));
    int amount = (int) (baseUnit * scale);
    FluidIngredient fluid = ingredient(amount);
    ItemCastingRecipeBuilder.tableRecipe(output)
      .setFluid(fluid)
      .setCoolingTime(temperature, amount)
      .setCast(cast, true)
      .save(wrapped, location(castingFolder, outputName));
  }

  /** Recipe to cast the block */
  public SmelteryRecipeBuilder blockCasting(int factor, Ingredient cast, boolean forceOptional) {
    String tagName = "storage_blocks/" + this.name.getPath();
    Consumer<FinishedRecipe> wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    ItemCastingRecipeBuilder.basinRecipe(ItemOutput.fromTag(itemTag(tagName)))
      .setFluid(ingredient(baseUnit * factor))
      .setCoolingTime(temperature, baseUnit * factor)
      .setCast(cast, true)
      .save(wrapped, location(castingFolder, "block"));
    return this;
  }


  /* Joint helpers */

  /** Adds melting and casting recipes for the given object */
  public SmelteryRecipeBuilder meltingCasting(float scale, String tagPrefix, CastItemObject cast, float factor, boolean forceOptional) {
    assert baseUnit != 0;
    String tagName = tagPrefix + "s/" + name.getPath();
    tagMelting(scale, tagPrefix, factor, tagName, forceOptional);
    tagCasting(scale, tagPrefix, cast,   tagName, forceOptional);
    return this;
  }

  /** Adds melting and casting recipes for the given object */
  public SmelteryRecipeBuilder meltingCasting(float scale, CastItemObject cast, float factor, boolean forceOptional) {
    return meltingCasting(scale, cast.getName().getPath(), cast, factor, forceOptional);
  }


  /* Composite casting */

  /** Adds melting and casting recipes for the given object */
  public SmelteryRecipeBuilder meltingCasting(float scale, String tagPrefix, Ingredient cast, float factor, boolean forceOptional) {
    assert baseUnit != 0;
    String tagName = tagPrefix + "s/" + name.getPath();
    tagMelting(scale, tagPrefix, factor, tagName, forceOptional);
    tagCasting(scale, tagPrefix, cast,   tagName, forceOptional);
    return this;
  }

  /** Adds melting and casting recipes for the given object */
  public SmelteryRecipeBuilder meltingCasting(float scale, String tagPrefix, String castMaterial, float factor, boolean forceOptional) {
    return meltingCasting(scale, tagPrefix, Ingredient.of(itemTag(tagPrefix + "s/" + castMaterial)), factor, forceOptional);
  }


  /* Melting helpers */

  /** Adds a recipe for melting a tool from the given mod */
  public SmelteryRecipeBuilder itemMelting(float scale, String domain, String path, boolean damagable) {
    itemMelting(scale, domain + '_' + path, (float)Math.sqrt(scale), new ResourceLocation(domain, path), damagable);
    return this;
  }

  /** Adds a recipe for melting an metal item with the metal prefix in the name */
  public SmelteryRecipeBuilder metalMelting(float scale, String domain, String path, boolean damagable) {
    itemMelting(scale, domain + '_' + path, (float)Math.sqrt(scale), new ResourceLocation(domain, name.getPath() + '_' + path), damagable);
    return this;
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String output, ResourceLocation tagName, float factor, boolean damagable, boolean forceOptional) {
    assert baseUnit != 0;
    tagMelting(scale, output, factor, tagName, damagable, forceOptional);
    return this;
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String output, String tagPrefix, float factor, boolean damagable, boolean forceOptional) {
    return melting(scale, output, commonResource(tagPrefix + '/' + name.getPath()), factor, damagable, forceOptional);
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String output, String tagPrefix, boolean damagable, boolean forceOptional) {
    return melting(scale, output, tagPrefix, (float)Math.sqrt(scale), damagable, forceOptional);
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String tagPrefix, float factor, boolean forceOptional) {
    return melting(scale, tagPrefix, tagPrefix + "s", factor, false, forceOptional);
  }


  /* Tool melting */

  /** Adds a recipe for melting a tool from the given mod, automatically prefixing the metal into the name */
  public SmelteryRecipeBuilder toolItemMelting(int cost, String domain, String path) {
    return metalMelting(cost, domain, path, true);
  }

  /** Adds a recipe melting a tool with the given cost using the common tools tag. See {@link #melting(float, String, String, boolean, boolean)} for armor as names are less standard */
  public SmelteryRecipeBuilder toolTagMelting(int cost, String name) {
    return melting(cost, name, "tools/" + name + 's', true, true);
  }

  /** Adds a recipe melting a tool with the given cost using the local tools_costing tag */
  public SmelteryRecipeBuilder toolCostMelting(int cost, String name, boolean forceOptional) {
    return melting((float)cost, name, this.name.withPath("melting/" + this.name.getPath() + "/tools_costing_" + cost), (float)Math.sqrt((float)cost), true, forceOptional);
  }

  /** Adds a recipe melting a tool with the given cost using the local tools_costing tag */
  public SmelteryRecipeBuilder toolCostMelting(int cost, String name) {
    return toolCostMelting(cost, name, true);
  }


  /* Main recipes */

  /**
   * Adds the raw ore and raw ore block metal melting recipes.
   * This is automatically called by {@link #metal()} if {@link #hasOre}, which is set automatically by {@link #ore(IByproduct...)}.
   * Provided for non-standard ores (like gold).
   */
  public SmelteryRecipeBuilder rawOre() {
    assert oreRate != null;
    assert baseUnit != 0;
    oreMelting(1, "raw_materials/",      null, 1.5f, "raw",       false);
    oreMelting(9, "storage_blocks/raw_", null, 6.0f, "raw_block", false);
    return this;
  }

  /** Adds a raw ore recipe with the given byproducts */
  public SmelteryRecipeBuilder rawOre(IByproduct... byproducts) {
    this.oreByproducts = byproducts;
    return rawOre();
  }

  /** Adds the sparse ore recipe at the given scale. Automatcally called by {@link #metal()} and {@link #gem(int)}, so only needed if doing unusual things. */
  public SmelteryRecipeBuilder sparseOre(float scale) {
    oreMelting(scale, "ores/", Tags.Items.ORE_RATES_SPARSE, 1.5f, "ore_sparse", false);
    return this;
  }

  /** Adds the sparse ore recipe at the given scale. Automatcally called by {@link #metal()} and {@link #gem(int)}, so only needed if doing unusual things. */
  public SmelteryRecipeBuilder singularOre(float scale) {
    oreMelting(scale, "ores/", Tags.Items.ORE_RATES_SINGULAR, 2.5f, "ore_singular", false);
    return this;
  }

  /** Adds the sparse ore recipe at the given scale. Automatcally called by {@link #metal()} and {@link #gem(int)}, so only needed if doing unusual things. */
  public SmelteryRecipeBuilder denseOre(float scale) {
    oreMelting(scale, "ores/", Tags.Items.ORE_RATES_DENSE, 4.5f, "ore_dense", false);
    return this;
  }

  /** Adds standard metal recipes for melting ingots, nuggets, and blocks */
  public SmelteryRecipeBuilder metal() {
    oreRate = OreRateType.METAL;
    baseUnit = FluidValues.INGOT;
    damageUnit = FluidValues.NUGGET;
    melting(9, "block", "storage_blocks", 3.0f, false, false);
    blockCasting(9, Ingredient.EMPTY, false);
    meltingCasting(1,      TinkerSmeltery.ingotCast,  1.0f, false);
    meltingCasting(1 / 9f, TinkerSmeltery.nuggetCast, 1 / 3f, false);
    // if we set byproducts, we are an ore
    if (hasOre) {
      rawOre();
      sparseOre(1);
      singularOre(2);
      denseOre(6);
    }
    return this;
  }

  /** Adds basic recipes for gems */
  public SmelteryRecipeBuilder gem(int storageSize) {
    oreRate = OreRateType.GEM;
    baseUnit = FluidValues.GEM;
    damageUnit = FluidValues.GEM_SHARD;
    melting(storageSize, "block", "storage_blocks", (float)Math.sqrt(storageSize), false, false);
    blockCasting(storageSize, Ingredient.EMPTY, false);
    meltingCasting(1, TinkerSmeltery.gemCast, 1.0f, false);
    // if we set byproducts, we are an ore
    if (hasOre) {
      sparseOre(0.5f);
      singularOre(1);
      denseOre(3);
    }
    return this;
  }

  /** Adds basic recipes for a amethyst/quartz style gem */
  public SmelteryRecipeBuilder smallGem() {
    return gem(4);
  }

  /** Adds basic recipes for a diamond/emerald style gem */
  public SmelteryRecipeBuilder largeGem() {
    return gem(9);
  }

  /** Adds geore recipes. Will add either metal or gem based on the ore rate set. */
  public SmelteryRecipeBuilder geore() {
    assert oreRate != null;
    assert baseUnit != 0;
    // base - no byproducts
    melting(1, "geore/shard", "geore_shards", 1.0f, false, true);
    melting(4, "geore/block", "geore_blocks", 2.0f, false, true);
    // clusters - ores with byproducts
    oreMelting(4, "geore_clusters/",    null, 2.5f, "geore/cluster",    true);
    oreMelting(1, "geore_small_buds/",  null, 1.0f, "geore/bud_small",  true);
    oreMelting(2, "geore_medium_buds/", null, 1.5f, "geore/bud_medium", true);
    oreMelting(3, "geore_large_buds/",  null, 2.0f, "geore/bud_large",  true);
    return this;
  }

  /** Adds recipes to melt oreberries */
  public SmelteryRecipeBuilder oreberry() {
    assert baseUnit == FluidValues.INGOT;
    itemMelting(1/9f, "oreberry", 1 / 3f, new ResourceLocation("oreberriesreplanted", name.getPath() + "_oreberry"), false);
    return this;
  }

  /** Adds a recipe for melting dust */
  public SmelteryRecipeBuilder dust() {
    return melting(1, "dust", 0.75f, true);
  }

  /** Adds a recipe for melting plates */
  public SmelteryRecipeBuilder plate() {
    return meltingCasting(1, TinkerSmeltery.plateCast, 1, true);
  }

  /** Adds a recipe for melting gears */
  public SmelteryRecipeBuilder gear() {
    return meltingCasting(4, TinkerSmeltery.gearCast, 2, true);
  }

  /** Adds a recipe for melting rods from IE */
  public SmelteryRecipeBuilder rod() {
    return meltingCasting(0.5f, TinkerSmeltery.rodCast, 1 / 5f, true);
  }

  /** Adds a recipe for melting sheetmetal from IE */
  public SmelteryRecipeBuilder sheetmetal() {
    return melting(1, "sheetmetal", 1, true);
  }

  /** Adds a recipe for melting coins from thermal */
  public SmelteryRecipeBuilder coin() {
    return meltingCasting(1 / 3f, TinkerSmeltery.coinCast, 2/3f, true);
  }

  /** Adds a recipe for melting wires from IE */
  public SmelteryRecipeBuilder wire() {
    return meltingCasting(0.5f, TinkerSmeltery.wireCast, 1 / 5f, true);
  }

  /** Adds vanilla tools with the given prefix for item IDs */
  @Internal
  public SmelteryRecipeBuilder minecraftTools(String prefix, boolean minotaur) {
    // shovel needs the cost tag for tool's complement knife
    toolCostMelting(1, "shovel", false);
    // sword recipe also handles hoe
    toolCostMelting(2, "sword", false);
    // axe and pickaxe together
    toolCostMelting(3, "axes", false);
    // armor
    minecraftArmorMelting(5, prefix, "helmet");
    minecraftArmorMelting(8, prefix, "chestplate");
    // diamond and gold use the boots tag for minotaur axes. Any other can just directly add the boots recipe
    // yeah, its a special case, but this is a builder method just for us
    if (minotaur) {
      toolCostMelting(4, "boots", false);
    } else {
      minecraftArmorMelting(4, prefix, "boots");
    }
    // mekanism adds paxels for all vanilla tools, so use a tag to make supporting that easy
    toolCostMelting(7, "leggings", false);
    return this;
  }

  /** Adds vanilla tools with the default prefix for item IDs */
  @Internal
  public SmelteryRecipeBuilder minecraftTools() {
    return minecraftTools(name.getPath(), false);
  }


  /* Modded tools */

  /** Applies the list of common recipes */
  public SmelteryRecipeBuilder common(CommonRecipe... recipes) {
    for (CommonRecipe recipe : recipes) {
      recipe.accept(this);
    }
    return this;
  }

  /** Avoids generic array creation for a recipe consumer */
  public interface CommonRecipe extends Consumer<SmelteryRecipeBuilder> {}

  /** Consumer melting a tool via the common tag */
  public record ToolTagMelting(int cost, String name) implements CommonRecipe {
    @Override
    public void accept(SmelteryRecipeBuilder builder) {
      builder.toolTagMelting(cost, name);
    }
  }

  /** Consumer melting a tool via the common tag */
  public record ArmorTagMelting(int cost, String name, String tag) implements CommonRecipe {
    @Override
    public void accept(SmelteryRecipeBuilder builder) {
      builder.melting(cost, name, "armors/" + tag, true, true);
    }
  }

  /** Consumer for melting a set of tools using the local tag */
  public record ToolCostMelting(int cost, String name) implements CommonRecipe {
    @Override
    public void accept(SmelteryRecipeBuilder builder) {
      builder.toolCostMelting(cost, name, true);
    }
  }

  /** Consumer for melting a specific tool from a mod with the metal prefix */
  public record ToolItemMelting(int cost, String domain, String name) implements CommonRecipe {
    @Override
    public void accept(SmelteryRecipeBuilder builder) {
      builder.toolItemMelting(cost, domain, name);
    }
  }

  /** Consumer for melting a specific tool from a mod with the metal prefix */
  public record MetalMelting(float cost, String domain, String name) implements CommonRecipe {
    @Override
    public void accept(SmelteryRecipeBuilder builder) {
      builder.metalMelting(cost, domain, name, false);
    }
  }


  /* Common tool melting */

  // common
  public static final ToolTagMelting SHOVEL = new ToolTagMelting(1, "shovel");
  public static final ToolCostMelting SHOVEL_PLUS = new ToolCostMelting(1, "shovel");
  public static final ToolCostMelting SWORD = new ToolCostMelting(2, "sword");
  public static final ToolCostMelting AXES = new ToolCostMelting(3, "axes");
  public static final CommonRecipe[] TOOLS = { SHOVEL, SWORD, AXES };
  // armor
  public static final ArmorTagMelting HELMET = new ArmorTagMelting(5, "helmet", "helmets");
  public static final ArmorTagMelting CHESTPLATE = new ArmorTagMelting(8, "chestplate", "chestplates");
  public static final ArmorTagMelting LEGGINGS = new ArmorTagMelting(7, "leggings", "leggings");
  public static final ArmorTagMelting BOOTS = new ArmorTagMelting(4, "boots", "boots");
  public static final CommonRecipe[] ARMOR = { HELMET, CHESTPLATE, LEGGINGS, BOOTS };
  public static final ToolCostMelting HELMET_PLUS = new ToolCostMelting(5, "helmet");
  public static final ToolCostMelting CHESTPLATE_PLUS = new ToolCostMelting(8, "chestplate");
  public static final ToolCostMelting LEGGINGS_PLUS = new ToolCostMelting(7, "leggings");
  public static final ToolCostMelting BOOTS_PLUS = new ToolCostMelting(4, "boots");
  public static final CommonRecipe[] ARMOR_PLUS = { HELMET_PLUS, CHESTPLATE_PLUS, LEGGINGS_PLUS, BOOTS_PLUS };
}
