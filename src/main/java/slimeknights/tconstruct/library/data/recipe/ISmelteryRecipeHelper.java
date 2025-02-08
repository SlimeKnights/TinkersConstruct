package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static slimeknights.mantle.Mantle.COMMON;
import static slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

/**
 * Recipe helper for methods related to melting and casting
 */
public interface ISmelteryRecipeHelper extends ICastCreationHelper {
  /* Builders for casting and melting from tags */

  /** Creates a smeltery builder for a standard fluid */
  default SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, String name, FluidObject<?> fluid) {
    return SmelteryRecipeBuilder.fluid(consumer, location(name), fluid);
  }

  /** Creates a smeltery builder for a molten fluid */
  default SmelteryRecipeBuilder molten(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid) {
    return fluid(consumer, fluid.getId().getPath().substring("molten_".length()), fluid);
  }


  /* Melting */

  /**
   * Creates a melting recipe with a tag input
   * @param consumer    Recipe consumer
   * @param fluid       Fluid to melt into
   * @param temperature Minimum melting temperature
   * @param tagName     Input tag
   * @param factor      Melting factor
   * @param recipePath  Recipe output name
   * @param isOptional  If true, recipe is optional
   */
  default void tagMelting(Consumer<FinishedRecipe> consumer, FluidOutput fluid, int temperature, String tagName, float factor, String recipePath, boolean isOptional) {
    Consumer<FinishedRecipe> wrapped = isOptional ? withCondition(consumer, tagCondition(tagName)) : consumer;
    MeltingRecipeBuilder.melting(Ingredient.of(getItemTag(COMMON, tagName)), fluid, temperature, factor)
                        .save(wrapped, location(recipePath));
  }

  /**
   * Common usage of {@link #tagMelting(Consumer, FluidOutput, int, String, float, String, boolean)}
   * @param consumer    Recipe consumer
   * @param fluid       Fluid to melt into
   * @param amount      Fluid output amount
   * @param tagName     Input tag
   * @param factor      Melting factor
   * @param recipePath  Recipe output name
   * @param isOptional  If true, recipe is optional
   */
  default void tagMelting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, String tagName, float factor, String recipePath, boolean isOptional) {
    tagMelting(consumer, fluid.result(amount), getTemperature(fluid), tagName, factor, recipePath, isOptional);
  }

  /** Shared logic for metal melting */
  @Deprecated(forRemoval = true)
  private static void metalMelting(SmelteryRecipeBuilder builder, boolean hasOre, boolean hasDust) {
    // not using the helper as it will add casting
    builder.oreRate(OreRateType.METAL).baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET);
    builder.melting(9, "block", "storage_blocks", 3.0f, false, false);
    builder.melting(1, "ingot", 1, false);
    builder.melting(1/9f, "nugget", 1/3f, false);
    if (hasOre) {
      builder.rawOre().sparseOre(1).singularOre(2).denseOre(6);
      // removed geoes as no addon will have that, use the builder if you really need them
    }
    if (hasDust) {
      builder.dust();
    }
    // not using the helpers as those will add casting
    builder.melting(1,    "plates", 1f,   true);
    builder.melting(4,    "gears",  2f,   true);
    builder.melting(3,    "coins",  2/3f, true);
    builder.melting(1/2f, "rods",   1/5f, true);
    // removed wires/sheetmetals as no addon will have them, IE has a limited list
  }

  /** @deprecated use {@link SmelteryRecipeBuilder} vua {@link #molten(Consumer, FluidObject)} */
  @Deprecated(forRemoval = true)
  default void metalMelting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, String name, boolean hasOre, boolean hasDust, String folder, boolean isOptional, IByproduct... byproducts) {
    SmelteryRecipeBuilder builder = SmelteryRecipeBuilder.fluid(consumer, location(name), fluid).meltingFolder(folder).optional(isOptional);
    if (hasOre) {
      builder.ore(byproducts);
    }
    metalMelting(builder, hasOre, hasDust);
  }

  /** @deprecated use {@link SmelteryRecipeBuilder} via {@link SmelteryRecipeBuilder#fluid(Consumer, ResourceLocation, Fluid)} */
  @Deprecated(forRemoval = true)
  default void metalMelting(Consumer<FinishedRecipe> consumer, Fluid fluid, String name, boolean hasOre, boolean hasDust, String folder, boolean isOptional, IByproduct... byproducts) {
    SmelteryRecipeBuilder builder = SmelteryRecipeBuilder.fluid(consumer, location(name), fluid).meltingFolder(folder).optional(isOptional);
    if (hasOre) {
      builder.ore(byproducts);
    }
    metalMelting(builder, hasOre, hasDust);
  }

  /** Shared logic for gem melting */
  @Deprecated(forRemoval = true)
  private static void gemMelting(SmelteryRecipeBuilder builder, boolean hasOre, int blockSize) {
    // not using the gem helper as it will add casting
    builder.oreRate(OreRateType.GEM).baseUnit(FluidValues.GEM).damageUnit(FluidValues.GEM_SHARD);
    builder.melting(blockSize, "block", "storage_blocks", 3.0f, false, false);
    builder.melting(1, "gem", 1, false);
    if (hasOre) {
      builder.sparseOre(0.5f).singularOre(1).denseOre(3);
      // removed geoes as no addon will have that, use the builder if you really need them
    }
  }

  /** @deprecated use {@link SmelteryRecipeBuilder} vua {@link #molten(Consumer, FluidObject)} */
  @Deprecated(forRemoval = true)
  default void gemMelting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, String name, boolean hasOre, int blockSize, String folder, boolean isOptional, IByproduct... byproducts) {
    SmelteryRecipeBuilder builder = SmelteryRecipeBuilder.fluid(consumer, location(name), fluid).meltingFolder(folder).optional(isOptional);
    if (hasOre) {
      builder.ore(byproducts);
    }
    gemMelting(builder, hasOre, blockSize);
  }

  /** @deprecated use {@link SmelteryRecipeBuilder} via {@link SmelteryRecipeBuilder#fluid(Consumer, ResourceLocation, Fluid)} */
  @Deprecated(forRemoval = true)
  default void gemMelting(Consumer<FinishedRecipe> consumer, Fluid fluid, String name, boolean hasOre, int blockSize, String folder, boolean isOptional, IByproduct... byproducts) {
    SmelteryRecipeBuilder builder = SmelteryRecipeBuilder.fluid(consumer, location(name), fluid).meltingFolder(folder).optional(isOptional);
    if (hasOre) {
      builder.ore(byproducts);
    }
    gemMelting(builder, hasOre, blockSize);
  }


  /* Casting */

  /**
   * Adds a recipe for casting using a cast
   * @param consumer  Recipe consumer
   * @param fluid     Recipe fluid
   * @param amount    Fluid amount
   * @param cast      Cast used
   * @param output    Recipe output
   * @param location  Recipe base
   */
  default void castingWithCast(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, CastItemObject cast, ItemOutput output, String location) {
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluidAndTime(fluid, amount)
                            .setCast(cast.getMultiUseTag(), false)
                            .save(consumer, location(location + "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluidAndTime(fluid, amount)
                            .setCast(cast.getSingleUseTag(), true)
                            .save(consumer, location(location + "_sand_cast"));
  }

  /**
   * Adds a recipe for casting using a cast
   * @param consumer  Recipe consumer
   * @param fluid     Recipe fluid
   * @param amount    Fluid amount
   * @param cast      Cast used
   * @param output    Recipe output
   * @param location  Recipe base
   */
  default void castingWithCast(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, CastItemObject cast, ItemLike output, String location) {
    castingWithCast(consumer, fluid, amount, cast, ItemOutput.fromItem(output), location);
  }

  /**
   * Adds a recipe for casting an item from a tag
   * @param consumer     Recipe consumer
   * @param fluid        Input fluid
   * @param amount       Recipe amount
   * @param cast         Cast for recipe
   * @param tagName      Tag for output
   * @param recipeName   Name of the recipe for output
   * @param optional     If true, conditions the recipe on the tag
   */
  default void tagCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, CastItemObject cast, String tagName, String recipeName, boolean optional) {
    if (optional) {
      consumer = withCondition(consumer, tagCondition(tagName));
    }
    castingWithCast(consumer, fluid, amount, cast, ItemOutput.fromTag(getItemTag(COMMON, tagName)), recipeName);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param amount    Recipe amount
   * @param ingot     Ingot output
   * @param location  Recipe base
   */
  default void ingotCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, ItemLike ingot, String location) {
    castingWithCast(consumer, fluid, amount, TinkerSmeltery.ingotCast, ingot, location);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param ingot     Ingot output
   * @param location  Recipe base
   */
  default void ingotCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, ItemLike ingot, String location) {
    ingotCasting(consumer, fluid, FluidValues.INGOT, ingot, location);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param gem       Gem output
   * @param location  Recipe base
   */
  default void gemCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, ItemLike gem, String location) {
    castingWithCast(consumer, fluid, FluidValues.GEM, TinkerSmeltery.gemCast, gem, location);
  }

  /**
   * Adds a casting recipe using a nugget cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param nugget    Nugget output
   * @param location  Recipe base
   */
  default void nuggetCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, ItemLike nugget, String location) {
    castingWithCast(consumer, fluid, FluidValues.NUGGET, TinkerSmeltery.nuggetCast, nugget, location);
  }

  /** @deprecated use {@link SmelteryRecipeBuilder} with {@link #molten(Consumer, FluidObject)} */
  @Deprecated(forRemoval = true)
  default void metalCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, @Nullable ItemLike block, @Nullable ItemLike ingot, @Nullable ItemLike nugget, String folder, String metal) {
    String metalFolder = folder + metal + "/";
    if (block != null) {
      ItemCastingRecipeBuilder.basinRecipe(block)
                              .setFluidAndTime(fluid, FluidValues.METAL_BLOCK)
                              .save(consumer, location(metalFolder + "block"));
    }
    if (ingot != null) {
      ingotCasting(consumer, fluid, ingot, metalFolder + "ingot");
    }
    if (nugget != null) {
      nuggetCasting(consumer, fluid, nugget, metalFolder + "nugget");
    }
    // plates are always optional, we don't ship them
    tagCasting(consumer, fluid, FluidValues.INGOT, TinkerSmeltery.plateCast, "plates/" + metal, folder + metal + "/plate", true);
    tagCasting(consumer, fluid, FluidValues.INGOT * 4, TinkerSmeltery.gearCast, "gears/" + metal, folder + metal + "/gear", true);
    tagCasting(consumer, fluid, FluidValues.NUGGET * 3, TinkerSmeltery.coinCast, "coins/" + metal, folder + metal + "/coin", true);
    tagCasting(consumer, fluid, FluidValues.INGOT / 2, TinkerSmeltery.rodCast, "rods/" + metal, folder + metal + "/rod", true);
    tagCasting(consumer, fluid, FluidValues.INGOT / 2, TinkerSmeltery.wireCast, "wires/" + metal, folder + metal + "/wire", true);
  }

  /** @deprecated use {@link SmelteryRecipeBuilder} with {@link #molten(Consumer, FluidObject)}. */
  @Deprecated(forRemoval = true)
  default void metalCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, MetalItemObject metal, String folder, String name) {
    metalCasting(consumer, fluid, metal.get(), metal.getIngot(), metal.getNugget(), folder, name);
  }

  /** @deprecated use {@link SmelteryRecipeBuilder} with {@link #molten(Consumer, FluidObject)} */
  @Deprecated(forRemoval = true)
  default void metalTagCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, String name, String folder, boolean forceStandard) {
    // nugget and ingot
    tagCasting(consumer, fluid, FluidValues.NUGGET, TinkerSmeltery.nuggetCast, "nuggets/" + name, folder + name + "/nugget", !forceStandard);
    tagCasting(consumer, fluid, FluidValues.INGOT, TinkerSmeltery.ingotCast, "ingots/" + name, folder + name + "/ingot", !forceStandard);
    tagCasting(consumer, fluid, FluidValues.INGOT, TinkerSmeltery.plateCast, "plates/" + name, folder + name + "/plate", true);
    tagCasting(consumer, fluid, FluidValues.INGOT * 4, TinkerSmeltery.gearCast, "gears/" + name, folder + name + "/gear", true);
    tagCasting(consumer, fluid, FluidValues.NUGGET * 3, TinkerSmeltery.coinCast, "coins/" + name, folder + name + "/coin", true);
    tagCasting(consumer, fluid, FluidValues.INGOT / 2, TinkerSmeltery.rodCast, "rods/" + name, folder + name + "/rod", true);
    tagCasting(consumer, fluid, FluidValues.INGOT / 2, TinkerSmeltery.wireCast, "wires/" + name, folder + name + "/wire", true);
    // block
    Consumer<FinishedRecipe> wrapped = forceStandard ? consumer : withCondition(consumer, tagCondition("storage_blocks/" + name));
    ItemCastingRecipeBuilder.basinRecipe(getItemTag(COMMON, "storage_blocks/" + name))
                            .setFluidAndTime(fluid, FluidValues.METAL_BLOCK)
                            .save(wrapped, location(folder + name + "/block"));
  }
}
