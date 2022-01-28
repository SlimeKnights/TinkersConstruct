package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.data.CompoundIngredient;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.IngredientDifference;
import slimeknights.mantle.recipe.ingredient.IngredientIntersection;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.json.TagDifferencePresentCondition;
import slimeknights.tconstruct.library.json.TagIntersectionPresentCondition;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Recipe helper for methods related to melting and casting
 */
public interface ISmelteryRecipeHelper extends ICastCreationHelper {
  /* Melting */

  /**
   * Base logic for {@link  #metalMelting(Consumer, Fluid, String, boolean, String, boolean, IByproduct...)}
   * @param consumer    Recipe consumer
   * @param fluid       Fluid to melt into
   * @param amount      Amount to melt into
   * @param tagName     Input tag
   * @param factor      Melting factor
   * @param recipePath  Recipe output name
   * @param isOptional  If true, recipe is optional
   */
  default void tagMelting(Consumer<FinishedRecipe> consumer, Fluid fluid, int amount, String tagName, float factor, String recipePath, boolean isOptional) {
    Consumer<FinishedRecipe> wrapped = isOptional ? withCondition(consumer, tagCondition(tagName)) : consumer;
    MeltingRecipeBuilder.melting(Ingredient.of(getTag("forge", tagName)), fluid, amount, factor)
                        .save(wrapped, modResource(recipePath));
  }

  /**
   * Base logic for {@link  #metalMelting(Consumer, Fluid, String, boolean, String, boolean, IByproduct...)}
   * @param consumer    Recipe consumer
   * @param fluid       Fluid to melt into
   * @param amount      Amount to melt into
   * @param tagName     Input tag
   * @param factor      Melting factor
   * @param recipePath  Recipe output name
   * @param oreRate     Ore rate for boosting
   * @param isOptional  If true, recipe is optional
   * @param byproducts  List of byproduct options for this metal, first one that is present will be used
   */
  default void oreMelting(Consumer<FinishedRecipe> consumer, Fluid fluid, int amount, String tagName, @Nullable Tag.Named<Item> size, float factor, String recipePath, boolean isOptional, OreRateType oreRate, float byproductScale, IByproduct... byproducts) {
    Consumer<FinishedRecipe> wrapped;
    Ingredient baseIngredient = Ingredient.of(getTag("forge", tagName));
    Ingredient ingredient;
    // not everyone sets size, so treat singular as the fallback, means we want anything in the tag that is not sparse or dense
    if (size == Tags.Items.ORE_RATES_SINGULAR) {
      ingredient = IngredientDifference.difference(baseIngredient, CompoundIngredient.from(Ingredient.of(Tags.Items.ORE_RATES_SPARSE), Ingredient.of(Tags.Items.ORE_RATES_DENSE)));
      wrapped = withCondition(consumer, new TagDifferencePresentCondition(new ResourceLocation("forge", tagName), Tags.Items.ORE_RATES_SPARSE.getName(), Tags.Items.ORE_RATES_DENSE.getName()));
      // size tag means we want an intersection between the tag and that size
    } else if (size != null) {
      ingredient = IngredientIntersection.intersection(baseIngredient, Ingredient.of(size));
      wrapped = withCondition(consumer, new TagIntersectionPresentCondition(new ResourceLocation("forge", tagName), size.getName()));
      // default only need it to be in the tag
    } else {
      ingredient = baseIngredient;
      wrapped = isOptional ? withCondition(consumer, tagCondition(tagName)) : consumer;
    }
    Supplier<MeltingRecipeBuilder> supplier = () -> MeltingRecipeBuilder.melting(ingredient, fluid, amount, factor).setOre(oreRate);
    ResourceLocation location = modResource(recipePath);

    // if no byproducts, just build directly
    if (byproducts.length == 0) {
      supplier.get().save(wrapped, location);
      // if first option is always present, only need that one
    } else if (byproducts[0].isAlwaysPresent()) {
      supplier.get()
              .addByproduct(new FluidStack(byproducts[0].getFluid(), (int)(byproducts[0].getAmount() * byproductScale)))
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
        builder.addRecipe(supplier.get().addByproduct(new FluidStack(byproduct.getFluid(), (int)(byproduct.getAmount() * byproductScale)))::save);

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

  /**
   * Mod compat for Geores, adds melting for geore shards and blocks
   * @param consumer  Recipe consumer
   * @param fluid     Fluid
   * @param name      Material name
   * @param folder    Output folder
   */
  default void georeMelting(Consumer<FinishedRecipe> consumer, Fluid fluid, int unit, String name, String folder) {
    // base
    tagMelting(consumer, fluid, unit,     "geore_shards/" + name, 1.0f, folder + "geore/shard", true);
    tagMelting(consumer, fluid, unit * 4, "geore_blocks/" + name, 2.0f, folder + "geore/block", true);
    // clusters
    tagMelting(consumer, fluid, unit * 4, "geore_clusters/" + name,    2.5f, folder + "geore/cluster", true);
    tagMelting(consumer, fluid, unit,     "geore_small_buds/" + name,  1.0f, folder + "geore/bud_small", true);
    tagMelting(consumer, fluid, unit * 2, "geore_medium_buds/" + name, 1.5f, folder + "geore/bud_medium", true);
    tagMelting(consumer, fluid, unit * 3, "geore_large_buds/" + name,  2.0f, folder + "geore/bud_large", true);
  }

  /**
   * Adds a basic ingot, nugget, block, ore melting recipe set
   * @param consumer    Recipe consumer
   * @param fluid       Fluid result
   * @param name        Resource name for tags
   * @param hasOre      If true, adds recipe for melting the ore
   * @param hasDust     If false, the dust form of this item does not correspond to the ingot form
   * @param folder      Recipe folder
   * @param isOptional  If true, this recipe is entirely optional
   * @param byproducts  List of byproduct options for this metal, first one that is present will be used
   */
  default void metalMelting(Consumer<FinishedRecipe> consumer, Fluid fluid, String name, boolean hasOre, boolean hasDust, String folder, boolean isOptional, IByproduct... byproducts) {
    String prefix = folder + "/" + name + "/";
    tagMelting(consumer, fluid, FluidValues.METAL_BLOCK, "storage_blocks/" + name, 3.0f, prefix + "block", isOptional);
    tagMelting(consumer, fluid, FluidValues.INGOT, "ingots/" + name, 1.0f, prefix + "ingot", isOptional);
    tagMelting(consumer, fluid, FluidValues.NUGGET, "nuggets/" + name, 1 / 3f, prefix + "nugget", isOptional);
    if (hasOre) {
      oreMelting(consumer, fluid, FluidValues.INGOT,     "raw_materials/" + name,      null, 1.5f, prefix + "raw",       isOptional, OreRateType.METAL, 1.0f, byproducts);
      oreMelting(consumer, fluid, FluidValues.INGOT * 9, "storage_blocks/raw_" + name, null, 6.0f, prefix + "raw_block", isOptional, OreRateType.METAL, 9.0f, byproducts);
      oreMelting(consumer, fluid, FluidValues.INGOT,     "ores/" + name, Tags.Items.ORE_RATES_SPARSE,   1.5f, prefix + "ore_sparse",   isOptional, OreRateType.METAL, 1.0f, byproducts);
      oreMelting(consumer, fluid, FluidValues.INGOT * 2, "ores/" + name, Tags.Items.ORE_RATES_SINGULAR, 2.5f, prefix + "ore_singular", isOptional, OreRateType.METAL, 2.0f, byproducts);
      oreMelting(consumer, fluid, FluidValues.INGOT * 6, "ores/" + name, Tags.Items.ORE_RATES_DENSE,    4.5f, prefix + "ore_dense",    isOptional, OreRateType.METAL, 6.0f, byproducts);
      georeMelting(consumer, fluid, FluidValues.INGOT, name, prefix);
    }
    // remaining forms are always optional as we don't ship them
    // allow disabling dust as some mods treat dust as distinct from ingots
    if (hasDust) {
      tagMelting(consumer, fluid, FluidValues.INGOT, "dusts/" + name, 0.75f, prefix + "dust", true);
    }
    tagMelting(consumer, fluid, FluidValues.INGOT, "plates/" + name, 1.0f, prefix + "plates", true);
    tagMelting(consumer, fluid, FluidValues.INGOT * 4, "gears/" + name, 2.0f, prefix + "gear", true);
    tagMelting(consumer, fluid, FluidValues.NUGGET * 3, "coins/" + name, 2 / 3f, prefix + "coin", true);
    tagMelting(consumer, fluid, FluidValues.INGOT / 2, "rods/" + name, 1 / 5f, prefix + "rod", true);
    tagMelting(consumer, fluid, FluidValues.INGOT / 2, "wires/" + name, 1 / 5f, prefix + "wire", true);
    tagMelting(consumer, fluid, FluidValues.INGOT, "sheetmetals/" + name, 1.0f, prefix + "sheetmetal", true);
  }

  /**
   * Adds a basic ingot, nugget, block, ore melting recipe set
   * @param consumer    Recipe consumer
   * @param fluid       Fluid result
   * @param name        Resource name for tags
   * @param hasOre      If true, adds recipe for melting the ore
   * @param folder      Recipe folder
   * @param isOptional  If true, this recipe is entirely optional
   * @param byproducts  List of byproduct options for this metal, first one that is present will be used
   */
  default void metalMelting(Consumer<FinishedRecipe> consumer, Fluid fluid, String name, boolean hasOre, String folder, boolean isOptional, IByproduct... byproducts) {
    metalMelting(consumer, fluid, name, hasOre, true, folder, isOptional, byproducts);
  }

  /**
   * Adds a basic gem, block, ore melting recipe set
   * @param consumer    Recipe consumer
   * @param fluid       Fluid result
   * @param name        Resource name for tags
   * @param blockSize   Number of gems to make one block
   * @param folder      Recipe folder
   * @param isOptional  If true, this recipe is entirely optional
   * @param byproducts  List of byproduct options for this metal, first one that is present will be used
   */
  default void gemMelting(Consumer<FinishedRecipe> consumer, Fluid fluid, String name, boolean hasOre, int blockSize, String folder, boolean isOptional, IByproduct... byproducts) {
    String prefix = folder + "/" + name + "/";
    // basic
    tagMelting(consumer, fluid, FluidValues.GEM * blockSize, "storage_blocks/" + name, (float)Math.sqrt(blockSize), prefix + "block", isOptional);
    tagMelting(consumer, fluid, FluidValues.GEM, "gems/" + name, 1.0f, prefix + "gem", isOptional);
    // ores
    if (hasOre) {
      oreMelting(consumer, fluid, FluidValues.GEM / 2, "ores/" + name, Tags.Items.ORE_RATES_SPARSE,   1.0f, prefix + "ore_sparse",   isOptional, OreRateType.GEM, 0.5f, byproducts);
      oreMelting(consumer, fluid, FluidValues.GEM,     "ores/" + name, Tags.Items.ORE_RATES_SINGULAR, 1.5f, prefix + "ore_singular", isOptional, OreRateType.GEM, 1.0f, byproducts);
      oreMelting(consumer, fluid, FluidValues.GEM * 3, "ores/" + name, Tags.Items.ORE_RATES_DENSE,    4.5f, prefix + "ore_dense",    isOptional, OreRateType.GEM, 3.0f, byproducts);
      georeMelting(consumer, fluid, FluidValues.GEM, name, prefix);
    }
  }


  /* Casting */

  /**
   * Adds a recipe for casting using a cast
   * @param consumer  Recipe consumer
   * @param fluid     Recipe fluid
   * @param forgeTag  If true, uses the forge tag from the fluid instead of the local tag
   * @param amount    Fluid amount
   * @param cast      Cast used
   * @param output    Recipe output
   * @param location  Recipe base
   */
  default void castingWithCast(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, boolean forgeTag, int amount, CastItemObject cast, ItemOutput output, String location) {
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluidAndTime(fluid, forgeTag, amount)
                            .setCast(cast.getMultiUseTag(), false)
                            .save(consumer, modResource(location + "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluidAndTime(fluid, forgeTag, amount)
                            .setCast(cast.getSingleUseTag(), true)
                            .save(consumer, modResource(location + "_sand_cast"));
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
  default void castingWithCast(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, CastItemObject cast, ItemOutput output, String location) {
    castingWithCast(consumer, fluid, false, amount, cast, output, location);
  }

  /**
   * Adds a recipe for casting using a cast
   * @param consumer  Recipe consumer
   * @param fluid     Recipe fluid
   * @param forgeTag  If true, uses the forge tag from the fluid instead of the local tag
   * @param amount    Fluid amount
   * @param cast      Cast used
   * @param output    Recipe output
   * @param location  Recipe base
   */
  default void castingWithCast(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, boolean forgeTag, int amount, CastItemObject cast, ItemLike output, String location) {
    castingWithCast(consumer, fluid, forgeTag, amount, cast, ItemOutput.fromItem(output), location);
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
   * @param forgeTag  If true, uses the forge tag from the fluid instead of the local tag
   * @param amount       Recipe amount
   * @param cast         Cast for recipe
   * @param tagName      Tag for output
   * @param recipeName   Name of the recipe for output
   * @param optional     If true, conditions the recipe on the tag
   */
  default void tagCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, boolean forgeTag, int amount, CastItemObject cast, String tagName, String recipeName, boolean optional) {
    if (optional) {
      consumer = withCondition(consumer, tagCondition(tagName));
    }
    castingWithCast(consumer, fluid, forgeTag, amount, cast, ItemOutput.fromTag(getTag("forge", tagName), 1), recipeName);
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
    tagCasting(consumer, fluid, false, amount, cast, tagName, recipeName, optional);
  }


  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param forgeTag  If true, uses the forge tag from the fluid instead of the local tag
   * @param amount    Recipe amount
   * @param ingot     Ingot output
   * @param location  Recipe base
   */
  default void ingotCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, boolean forgeTag, int amount, ItemLike ingot, String location) {
    castingWithCast(consumer, fluid, forgeTag, amount, TinkerSmeltery.ingotCast, ingot, location);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param forgeTag  If true, uses the forge tag from the fluid instead of the local tag
   * @param ingot     Ingot output
   * @param location  Recipe base
   */
  default void ingotCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, boolean forgeTag, ItemLike ingot, String location) {
    ingotCasting(consumer, fluid, forgeTag, FluidValues.INGOT, ingot, location);
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
    ingotCasting(consumer, fluid, false, amount, ingot, location);
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
   * @param forgeTag  If true, uses the forge tag from the fluid instead of the local tag
   * @param nugget    Nugget output
   * @param location  Recipe base
   */
  default void nuggetCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, boolean forgeTag, ItemLike nugget, String location) {
    castingWithCast(consumer, fluid, forgeTag, FluidValues.NUGGET, TinkerSmeltery.nuggetCast, nugget, location);
  }

  /**
   * Adds a casting recipe using a nugget cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param nugget    Nugget output
   * @param location  Recipe base
   */
  default void nuggetCastingRecipe(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, ItemLike nugget, String location) {
    nuggetCasting(consumer, fluid, false, nugget, location);
  }

  /**
   * Add recipes for a standard mineral, uses local tag
   * @param consumer  Recipe consumer
   * @param fluid     Fluid input
   * @param forgeTag  If true, uses the forge tag from the fluid instead of the local tag
   * @param block     Block result
   * @param ingot     Ingot result
   * @param nugget    Nugget result
   * @param folder    Output folder
   */
  default void metalCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, boolean forgeTag, @Nullable ItemLike block, @Nullable ItemLike ingot, @Nullable ItemLike nugget, String folder, String metal) {
    String metalFolder = folder + metal + "/";
    if (block != null) {
      ItemCastingRecipeBuilder.basinRecipe(block)
                              .setFluidAndTime(fluid, forgeTag, FluidValues.METAL_BLOCK)
                              .save(consumer, modResource(metalFolder + "block"));
    }
    if (ingot != null) {
      ingotCasting(consumer, fluid, forgeTag, ingot, metalFolder + "ingot");
    }
    if (nugget != null) {
      nuggetCasting(consumer, fluid, forgeTag, nugget, metalFolder + "nugget");
    }
    // plates are always optional, we don't ship them
    tagCasting(consumer, fluid, forgeTag, FluidValues.INGOT, TinkerSmeltery.plateCast, "plates/" + metal, folder + metal + "/plate", true);
    tagCasting(consumer, fluid, forgeTag, FluidValues.INGOT * 4, TinkerSmeltery.gearCast, "gears/" + metal, folder + metal + "/gear", true);
    tagCasting(consumer, fluid, forgeTag, FluidValues.NUGGET * 3, TinkerSmeltery.coinCast, "coins/" + metal, folder + metal + "/coin", true);
    tagCasting(consumer, fluid, forgeTag, FluidValues.INGOT / 2, TinkerSmeltery.rodCast, "rods/" + metal, folder + metal + "/rod", true);
    tagCasting(consumer, fluid, forgeTag, FluidValues.INGOT / 2, TinkerSmeltery.wireCast, "wires/" + metal, folder + metal + "/wire", true);
  }

  /**
   * Add recipes for a standard mineral, uses local tag
   * @param consumer  Recipe consumer
   * @param fluid     Fluid input
   * @param block     Block result
   * @param ingot     Ingot result
   * @param nugget    Nugget result
   * @param folder    Output folder
   */
  default void metalCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, @Nullable ItemLike block, @Nullable ItemLike ingot, @Nullable ItemLike nugget, String folder, String metal) {
    metalCasting(consumer, fluid, false, block, ingot, nugget, folder, metal);
  }

  /**
   * Add recipes for a standard mineral, uses local tag
   * @param consumer  Recipe consumer
   * @param fluid     Fluid input
   * @param metal     Metal object
   * @param folder    Output folder
   */
  default void metalCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, MetalItemObject metal, String folder, String name) {
    metalCasting(consumer, fluid, metal.get(), metal.getIngot(), metal.getNugget(), folder, name);
  }

  /**
   * Add recipes for a standard mineral, uses forge tag
   * @param consumer       Recipe consumer
   * @param fluid          Fluid input
   * @param name           Name of ore
   * @param folder         Output folder
   * @param forceStandard  If true, all default materials will always get a recipe, used for common materials provided by the mod (e.g. copper)
   */
  default void metalTagCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, String name, String folder, boolean forceStandard) {
    // nugget and ingot
    tagCasting(consumer, fluid, true, FluidValues.NUGGET, TinkerSmeltery.nuggetCast, "nuggets/" + name, folder + name + "/nugget", !forceStandard);
    tagCasting(consumer, fluid, true, FluidValues.INGOT, TinkerSmeltery.ingotCast, "ingots/" + name, folder + name + "/ingot", !forceStandard);
    tagCasting(consumer, fluid, true, FluidValues.INGOT, TinkerSmeltery.plateCast, "plates/" + name, folder + name + "/plate", true);
    tagCasting(consumer, fluid, true, FluidValues.INGOT * 4, TinkerSmeltery.gearCast, "gears/" + name, folder + name + "/gear", true);
    tagCasting(consumer, fluid, true, FluidValues.NUGGET * 3, TinkerSmeltery.coinCast, "coins/" + name, folder + name + "/coin", true);
    tagCasting(consumer, fluid, true, FluidValues.INGOT / 2, TinkerSmeltery.rodCast, "rods/" + name, folder + name + "/rod", true);
    tagCasting(consumer, fluid, true, FluidValues.INGOT / 2, TinkerSmeltery.wireCast, "wires/" + name, folder + name + "/wire", true);
    // block
    Tag<Item> block = getTag("forge", "storage_blocks/" + name);
    Consumer<FinishedRecipe> wrapped = forceStandard ? consumer : withCondition(consumer, tagCondition("storage_blocks/" + name));
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(fluid, true, FluidValues.METAL_BLOCK)
                            .save(wrapped, modResource(folder + name + "/block"));
  }
}
