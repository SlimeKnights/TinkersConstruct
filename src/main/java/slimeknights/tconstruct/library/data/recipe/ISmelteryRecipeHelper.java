package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
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
  default void metalMeltingBase(Consumer<IFinishedRecipe> consumer, Fluid fluid, int amount, String tagName, float factor, String recipePath, boolean isOptional) {
    Consumer<IFinishedRecipe> wrapped = isOptional ? withCondition(consumer, tagCondition(tagName)) : consumer;
    MeltingRecipeBuilder.melting(Ingredient.fromTag(getTag("forge", tagName)), fluid, amount, factor)
                        .build(wrapped, modResource(recipePath));
  }

  /**
   * Base logic for {@link  #metalMelting(Consumer, Fluid, String, boolean, String, boolean, IByproduct...)}
   * @param consumer    Recipe consumer
   * @param fluid       Fluid to melt into
   * @param amount      Amount to melt into
   * @param tagName     Input tag
   * @param factor      Melting factor
   * @param recipePath  Recipe output name
   * @param isOptional  If true, recipe is optional
   * @param byproducts  List of byproduct options for this metal, first one that is present will be used
   */
  default void oreMelting(Consumer<IFinishedRecipe> consumer, Fluid fluid, int amount, String tagName, float factor, String recipePath, boolean isOptional, IByproduct... byproducts) {
    Consumer<IFinishedRecipe> wrapped = isOptional ? withCondition(consumer, tagCondition(tagName)) : consumer;
    Supplier<MeltingRecipeBuilder> supplier = () -> MeltingRecipeBuilder.melting(Ingredient.fromTag(getTag("forge", tagName)), fluid, amount, factor).setOre();
    ResourceLocation location = modResource(recipePath);

    // if no byproducts, just build directly
    if (byproducts.length == 0) {
      supplier.get().build(wrapped, location);
      // if first option is always present, only need that one
    } else if (byproducts[0].isAlwaysPresent()) {
      supplier.get()
              .addByproduct(new FluidStack(byproducts[0].getFluid(), byproducts[0].getNuggets()))
              .build(wrapped, location);
    } else {
      // multiple options, will need a conditonal recipe
      ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
      boolean alwaysPresent = false;
      for (IByproduct byproduct : byproducts) {
        builder.addCondition(tagCondition("ingots/" + byproduct.getName()));
        builder.addRecipe(supplier.get().addByproduct(new FluidStack(byproduct.getFluid(), byproduct.getNuggets()))::build);
        // found an always present byproduct? we are done
        alwaysPresent = byproduct.isAlwaysPresent();
        if (alwaysPresent) {
          break;
        }
      }
      // not always present? add a recipe with no byproducts as a final fallback
      if (!alwaysPresent) {
        builder.addCondition(TrueCondition.INSTANCE);
        builder.addRecipe(supplier.get()::build);
      }
      builder.build(wrapped, location);
    }
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
  default void metalMelting(Consumer<IFinishedRecipe> consumer, Fluid fluid, String name, boolean hasOre, String folder, boolean isOptional, IByproduct... byproducts) {
    String prefix = folder + "/" + name + "/";
    metalMeltingBase(consumer, fluid, FluidValues.METAL_BLOCK, "storage_blocks/" + name, 3.0f, prefix + "block", isOptional);
    metalMeltingBase(consumer, fluid, FluidValues.INGOT, "ingots/" + name, 1.0f, prefix + "ingot", isOptional);
    metalMeltingBase(consumer, fluid, FluidValues.NUGGET, "nuggets/" + name, 1 / 3f, prefix + "nugget", isOptional);
    if (hasOre) {
      oreMelting(consumer, fluid, FluidValues.INGOT, "ores/" + name, 1.5f, prefix + "ore", isOptional, byproducts);
    }
    // dust is always optional, as we don't do dust
    metalMeltingBase(consumer, fluid, FluidValues.INGOT, "dusts/" + name, 0.75f, prefix + "dust", true);
    metalMeltingBase(consumer, fluid, FluidValues.INGOT, "plates/" + name, 1.0f, prefix + "plates", true);
    metalMeltingBase(consumer, fluid, FluidValues.INGOT * 4, "gears/" + name, 2.0f, prefix + "gear", true);
    metalMeltingBase(consumer, fluid, FluidValues.NUGGET * 3, "coins/" + name, 2 / 3f, prefix + "coin", true);
    metalMeltingBase(consumer, fluid, FluidValues.INGOT / 2, "rods/" + name, 1 / 5f, prefix + "rod", true);
    metalMeltingBase(consumer, fluid, FluidValues.INGOT, "sheetmetals/" + name, 1.0f, prefix + "sheetmetal", true);
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
  default void castingWithCast(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, int amount, CastItemObject cast, ItemOutput output, String location) {
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluidAndTime(fluid, amount)
                            .setCast(cast.getMultiUseTag(), false)
                            .build(consumer, modResource(location + "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluidAndTime(fluid, amount)
                            .setCast(cast.getSingleUseTag(), true)
                            .build(consumer, modResource(location + "_sand_cast"));
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
  default void castingWithCast(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, int amount, CastItemObject cast, IItemProvider output, String location) {
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
  default void tagCasting(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, int amount, CastItemObject cast, String tagName, String recipeName, boolean optional) {
    if (optional) {
      consumer = withCondition(consumer, tagCondition(tagName));
    }
    castingWithCast(consumer, fluid, amount, cast, ItemOutput.fromTag(getTag("forge", tagName), 1), recipeName);
  }


  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param amount    Recipe amount
   * @param ingot     Ingot output
   * @param location  Recipe base
   */
  default void ingotCasting(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, int amount, IItemProvider ingot, String location) {
    castingWithCast(consumer, fluid, amount, TinkerSmeltery.ingotCast, ingot, location);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param gem       Gem output
   * @param location  Recipe base
   */
  default void gemCasting(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, IItemProvider gem, String location) {
    castingWithCast(consumer, fluid, FluidValues.GEM, TinkerSmeltery.gemCast, gem, location);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param ingot     Ingot output
   * @param location  Recipe base
   */
  default void ingotCasting(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, IItemProvider ingot, String location) {
    ingotCasting(consumer, fluid, FluidValues.INGOT, ingot, location);
  }

  /**
   * Adds a casting recipe using a nugget cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param nugget    Nugget output
   * @param location  Recipe base
   */
  default void nuggetCastingRecipe(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, IItemProvider nugget, String location) {
    castingWithCast(consumer, fluid, FluidValues.NUGGET, TinkerSmeltery.nuggetCast, nugget, location);
  }

  /**
   * Add recipes for a standard mineral
   * @param consumer  Recipe consumer
   * @param fluid     Fluid input
   * @param block     Block result
   * @param ingot     Ingot result
   * @param nugget    Nugget result
   * @param folder    Output folder
   */
  default void metalCasting(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, @Nullable IItemProvider block, @Nullable IItemProvider ingot, @Nullable IItemProvider nugget, String folder, String metal) {
    String metalFolder = folder + metal + "/";
    if (block != null) {
      ItemCastingRecipeBuilder.basinRecipe(block)
                              .setFluidAndTime(fluid, FluidValues.METAL_BLOCK)
                              .build(consumer, modResource(metalFolder + "block"));
    }
    if (ingot != null) {
      ingotCasting(consumer, fluid, ingot, metalFolder + "ingot");
    }
    if (nugget != null) {
      nuggetCastingRecipe(consumer, fluid, nugget, metalFolder + "nugget");
    }
    // plates are always optional, we don't ship them
    tagCasting(consumer, fluid, FluidValues.INGOT, TinkerSmeltery.plateCast, "plates/" + metal, folder + metal + "/plate", true);
    tagCasting(consumer, fluid, FluidValues.INGOT * 4, TinkerSmeltery.gearCast, "gears/" + metal, folder + metal + "/gear", true);
    tagCasting(consumer, fluid, FluidValues.NUGGET * 3, TinkerSmeltery.coinCast, "coins/" + metal, folder + metal + "/coin", true);
    tagCasting(consumer, fluid, FluidValues.INGOT / 2, TinkerSmeltery.rodCast, "rods/" + metal, folder + metal + "/rod", true);
  }

  /**
   * Add recipes for a standard mineral
   * @param consumer  Recipe consumer
   * @param fluid     Fluid input
   * @param metal     Metal object
   * @param folder    Output folder
   */
  default void metalCasting(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, MetalItemObject metal, String folder, String name) {
    metalCasting(consumer, fluid, metal.get(), metal.getIngot(), metal.getNugget(), folder, name);
  }

  /**
   * Add recipes for a standard mineral
   * @param consumer       Recipe consumer
   * @param fluid          Fluid input
   * @param name           Name of ore
   * @param folder         Output folder
   * @param forceStandard  If true, all default materials will always get a recipe, used for common materials provided by the mod (e.g. copper)
   */
  default void metalTagCasting(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, String name, String folder, boolean forceStandard) {
    // nugget and ingot
    tagCasting(consumer, fluid, FluidValues.NUGGET, TinkerSmeltery.nuggetCast, "nuggets/" + name, folder + name + "/nugget", !forceStandard);
    tagCasting(consumer, fluid, FluidValues.INGOT, TinkerSmeltery.ingotCast, "ingots/" + name, folder + name + "/ingot", !forceStandard);
    tagCasting(consumer, fluid, FluidValues.INGOT, TinkerSmeltery.plateCast, "plates/" + name, folder + name + "/plate", true);
    tagCasting(consumer, fluid, FluidValues.INGOT * 4, TinkerSmeltery.gearCast, "gears/" + name, folder + name + "/gear", true);
    tagCasting(consumer, fluid, FluidValues.NUGGET * 3, TinkerSmeltery.coinCast, "coins/" + name, folder + name + "/coin", true);
    tagCasting(consumer, fluid, FluidValues.INGOT / 2, TinkerSmeltery.rodCast, "rods/" + name, folder + name + "/rod", true);
    // block
    ITag<Item> block = getTag("forge", "storage_blocks/" + name);
    Consumer<IFinishedRecipe> wrapped = forceStandard ? consumer : withCondition(consumer, tagCondition("storage_blocks/" + name));
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(new FluidStack(fluid.get(), FluidValues.METAL_BLOCK))
                            .build(wrapped, modResource(folder + name + "/block"));
  }
}
