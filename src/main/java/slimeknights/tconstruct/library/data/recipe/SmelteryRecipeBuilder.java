package slimeknights.tconstruct.library.data.recipe;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.json.condition.TagDifferencePresentCondition;
import slimeknights.tconstruct.library.json.condition.TagIntersectionPresentCondition;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static slimeknights.mantle.Mantle.COMMON;
import static slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

/** Helper for building melting and casting recipes for a fluid */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Setter
@Accessors(fluent = true)
@CanIgnoreReturnValue
public class SmelteryRecipeBuilder {
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
  private int temperature;
  /** If true, all recipes are optional. If false only unsupported types are optional */
  private boolean optional = false;
  /** If true, ore recipes should be added */
  private boolean hasOre = false;
  /** List of byproducts for these recipes */
  private IByproduct[] byproducts = new IByproduct[0];
  /** Folder to save melting recipes */
  private String meltingFolder = "melting/";
  /** Folder to save casting recipes */
  private String castingFolder = "casting/";
  /** Keeps track of whether this builder is used for gems or metals */
  private OreRateType oreRate = null;
  /** Base unit value for builder */
  private int baseUnit = 0;


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
  public SmelteryRecipeBuilder byproducts(IByproduct... byproducts) {
    this.byproducts = byproducts;
    this.hasOre = true;
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
  public Consumer<FinishedRecipe> withCondition(ICondition... conditions) {
    ConsumerWrapperBuilder builder = ConsumerWrapperBuilder.wrap();
    for (ICondition condition : conditions) {
      builder.addCondition(condition);
    }
    return builder.build(consumer);
  }

  /** Creates a condition for a tag being empty */
  @CheckReturnValue
  public static ICondition tagCondition(String name) {
    return new NotCondition(new TagEmptyCondition(COMMON, name));
  }

  /** Creates a tag key for an item */
  @CheckReturnValue
  public static TagKey<Item> itemTag(String name) {
    return ItemTags.create(new ResourceLocation(COMMON, name));
  }

  /** Creates a location under the given domain with the passed prefix  */
  @CheckReturnValue
  private ResourceLocation location(String folder, String variant) {
    return name.withPath(folder + name.getPath() + '/' + variant);
  }


  /* Melting helpers */

  /** Adds a recipe for melting an item from a tag*/
  private void tagMelting(int amount, String output, float factor, String tagName, int damageUnit, boolean forceOptional) {
    Consumer<FinishedRecipe> wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    MeltingRecipeBuilder builder = MeltingRecipeBuilder.melting(Ingredient.of(itemTag(tagName)), result(amount), temperature, factor);
    if (damageUnit > 0) {
      builder.setDamagable(damageUnit);
    }
    builder.save(wrapped, location(meltingFolder, output));
  }

  private void oreMelting(float scale, String tagName, @Nullable TagKey<Item> size, float factor, String output, boolean forceOptional) {
    assert oreRate != null;
    assert baseUnit != 0;
    Consumer<FinishedRecipe> wrapped;
    Ingredient baseIngredient = Ingredient.of(itemTag(tagName));
    Ingredient ingredient;
    // not everyone sets size, so treat singular as the fallback, means we want anything in the tag that is not sparse or dense
    if (size == Tags.Items.ORE_RATES_SINGULAR) {
      ingredient = DifferenceIngredient.of(baseIngredient, CompoundIngredient.of(Ingredient.of(Tags.Items.ORE_RATES_SPARSE), Ingredient.of(Tags.Items.ORE_RATES_DENSE)));
      wrapped = withCondition(TagDifferencePresentCondition.ofKeys(itemTag(tagName), Tags.Items.ORE_RATES_SPARSE, Tags.Items.ORE_RATES_DENSE));
      // size tag means we want an intersection between the tag and that size
    } else if (size != null) {
      ingredient = IntersectionIngredient.of(baseIngredient, Ingredient.of(size));
      wrapped = withCondition(TagIntersectionPresentCondition.ofKeys(itemTag(tagName), size));
      // default only need it to be in the tag
    } else {
      ingredient = baseIngredient;
      wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    }
    Supplier<MeltingRecipeBuilder> supplier = () -> MeltingRecipeBuilder.melting(ingredient, result((int)(baseUnit * scale)), temperature, factor).setOre(oreRate);
    ResourceLocation location = location(meltingFolder, output);

    // if no byproducts, just build directly
    if (byproducts.length == 0) {
      supplier.get().save(wrapped, location);
      // if first option is always present, only need that one
    } else if (byproducts[0].isAlwaysPresent()) {
      supplier.get()
              .addByproduct(byproducts[0].getFluid(scale))
              .save(wrapped, location);
    } else {
      // multiple options, will need a conditonal recipe
      ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
      boolean alwaysPresent = false;
      for (IByproduct byproduct : byproducts) {
        // found an always present byproduct? no need to tag and we are done
        alwaysPresent = byproduct.isAlwaysPresent();
        if (alwaysPresent) {
          builder.addCondition(TrueCondition.INSTANCE);
        } else {
          builder.addCondition(tagCondition("ingots/" + byproduct.getName()));
        }
        builder.addRecipe(supplier.get().addByproduct(byproduct.getFluid(scale))::save);

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
  private void tagCasting(int amount, String outputPrefix, CastItemObject cast, String tagName, boolean forceOptional) {
    Consumer<FinishedRecipe> wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    ItemOutput output = ItemOutput.fromTag(itemTag(tagName));
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

  /** Recipe to cast using a basin */
  private void basinCasting(int amount, String output, String tagName, boolean forceOptional) {
    Consumer<FinishedRecipe> wrapped = optional || forceOptional ? withCondition(tagCondition(tagName)) : consumer;
    ItemCastingRecipeBuilder.basinRecipe(ItemOutput.fromTag(itemTag(tagName)))
                            .setFluid(ingredient(amount))
                            .setCoolingTime(temperature, amount)
                            .save(wrapped, location(castingFolder, output));
  }


  /* Joint helpers */

  /** Adds melting and casting recipes for the given object */
  public SmelteryRecipeBuilder meltingCasting(float scale, String tagPrefix, CastItemObject cast, float factor, boolean forceOptional) {
    assert baseUnit != 0;
    int amount = (int)(baseUnit * scale);
    String tagName = tagPrefix + "s/" + name.getPath();
    tagMelting(amount, tagPrefix, factor, tagName, 0, forceOptional);
    tagCasting(amount, tagPrefix, cast,   tagName,    forceOptional);
    return this;
  }

  /** Adds melting and casting recipes for the given object */
  public SmelteryRecipeBuilder meltingCasting(float scale, CastItemObject cast, float factor, boolean forceOptional) {
    return meltingCasting(scale, cast.getName().getPath(), cast, factor, forceOptional);
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String output, String tagPrefix, float factor, int damageUnit, boolean forceOptional) {
    assert baseUnit != 0;
    tagMelting((int)(baseUnit * scale), output, factor, tagPrefix + "/" + name.getPath(), damageUnit, forceOptional);
    return this;
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String output, String tagPrefix, int damageUnit, boolean forceOptional) {
    assert baseUnit != 0;
    tagMelting((int)(baseUnit * scale), output, (float)Math.sqrt(scale), tagPrefix + "/" + name.getPath(), damageUnit, forceOptional);
    return this;
  }

  /** Adds a recipe melting a tag item */
  public SmelteryRecipeBuilder melting(float scale, String tagPrefix, float factor, boolean forceOptional) {
    return melting(scale, tagPrefix, tagPrefix + "s", factor, 0, forceOptional);
  }


  /* Main recipes */

  /** Adds standard metal recipes for melting ingots, nuggets, and blocks */
  public SmelteryRecipeBuilder metal() {
    oreRate = OreRateType.METAL;
    baseUnit = FluidValues.INGOT;
    String name = this.name.getPath();
    tagMelting(FluidValues.METAL_BLOCK, "block", 3.0f, "storage_blocks/" + name, 0, false);
    basinCasting(FluidValues.METAL_BLOCK, "block", "storage_blocks/" + name, false);
    meltingCasting(1,      TinkerSmeltery.ingotCast,  1.0f, false);
    meltingCasting(1 / 9f, TinkerSmeltery.nuggetCast, 1 / 3f, false);
    // if we set byproducts, we are an ore
    if (hasOre) {
      oreMelting(1, "raw_materials/" + name,      null, 1.5f, "raw",       false);
      oreMelting(9, "storage_blocks/raw_" + name, null, 6.0f, "raw_block", false);
      oreMelting(1, "ores/" + name, Tags.Items.ORE_RATES_SPARSE,   1.5f, "ore_sparse",   false);
      oreMelting(2, "ores/" + name, Tags.Items.ORE_RATES_SINGULAR, 2.5f, "ore_singular", false);
      oreMelting(6, "ores/" + name, Tags.Items.ORE_RATES_DENSE,    4.5f, "ore_dense",    false);
    }
    return this;
  }

  /** Adds basic recipes for gems */
  public SmelteryRecipeBuilder gem(int storageSize) {
    oreRate = OreRateType.GEM;
    baseUnit = FluidValues.GEM;
    String name = this.name.getPath();
    tagMelting(FluidValues.GEM * storageSize, "block", (float)Math.sqrt(storageSize), "storage_blocks/" + name, 0, false);
    basinCasting(FluidValues.GEM * storageSize, "block", "storage_blocks/" + name, false);
    meltingCasting(1, TinkerSmeltery.gemCast, 1.0f, false);
    // if we set byproducts, we are an ore
    if (hasOre) {
      oreMelting(0.5f, "ores/" + name, Tags.Items.ORE_RATES_SPARSE,   1.0f, "ore_sparse",   false);
      oreMelting(1.0f, "ores/" + name, Tags.Items.ORE_RATES_SINGULAR, 1.5f, "ore_singular", false);
      oreMelting(3.0f, "ores/" + name, Tags.Items.ORE_RATES_DENSE,    4.5f, "ore_dense",    false);
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
    String name = this.name.getPath();
    // base - no byproducts
    tagMelting(baseUnit, "geore/shard", 1.0f, "geore_shards/" + name, 0, true);
    tagMelting(baseUnit * 4, "geore/block", 2.0f, "geore_blocks/" + name, 0, true);
    // clusters - ores with byproducts
    oreMelting(4, "geore_clusters/" + name,    null, 2.5f, "geore/cluster",    true);
    oreMelting(1, "geore_small_buds/" + name,  null, 1.0f, "geore/bud_small",  true);
    oreMelting(2, "geore_medium_buds/" + name, null, 1.5f, "geore/bud_medium", true);
    oreMelting(3, "geore_large_buds/" + name,  null, 2.0f, "geore/bud_large",  true);
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

  /** Adds armor melting recipes */
  public SmelteryRecipeBuilder armor() {
    int damageUnit = oreRate == OreRateType.GEM ? FluidValues.GEM_SHARD : FluidValues.NUGGET;
    melting(5, "helmet",     "armors/boots",       damageUnit, true);
    melting(8, "chestplate", "armors/chestplates", damageUnit, true);
    melting(7, "leggings",   "armors/leggings",    damageUnit, true);
    melting(4, "boots",      "armors/boots",       damageUnit, true);
    return this;
  }

  public SmelteryRecipeBuilder paxel() {
    int damageUnit = oreRate == OreRateType.GEM ? FluidValues.GEM_SHARD : FluidValues.NUGGET;
    return melting(7, "paxel",   "tools/paxels", damageUnit, true); // paxels are a bad idea, but might as well allow melting them
  }

  /** Adds armor melting recipes */
  public SmelteryRecipeBuilder tools() {
    int damageUnit = oreRate == OreRateType.GEM ? FluidValues.GEM_SHARD : FluidValues.NUGGET;
    melting(3, "axe",     "tools/axes",     damageUnit, true);
    melting(2, "hoe",     "tools/hoes",     damageUnit, true);
    melting(3, "pickaxe", "tools/pickaxes", damageUnit, true);
    melting(1, "shovel",  "tools/shovels",  damageUnit, true);
    melting(1, "sword",   "tools/swords",   damageUnit, true);
    return this;
  }
}
