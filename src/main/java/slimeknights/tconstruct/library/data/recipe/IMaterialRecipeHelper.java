package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Interface for adding recipes for tool materials
 */
public interface IMaterialRecipeHelper extends IRecipeHelper {
  /**
   * Registers a material recipe
   * @param consumer  Recipe consumer
   * @param material  Material ID
   * @param input     Recipe input
   * @param value     Material value
   * @param needed    Number of items needed
   * @param saveName  Material save name
   */
  default void materialRecipe(Consumer<FinishedRecipe> consumer, MaterialVariantId material, Ingredient input, int value, int needed, String saveName) {
    materialRecipe(consumer, material, input, value, needed, null, saveName);
  }

  /**
   * Registers a material recipe
   * @param consumer  Recipe consumer
   * @param material  Material ID
   * @param input     Recipe input
   * @param value     Material value
   * @param needed    Number of items needed
   * @param saveName  Material save name
   */
  default void materialRecipe(Consumer<FinishedRecipe> consumer, MaterialVariantId material, Ingredient input, int value, int needed, @Nullable ItemOutput leftover, String saveName) {
    MaterialRecipeBuilder builder = MaterialRecipeBuilder.materialRecipe(material)
                                                         .setIngredient(input)
                                                         .setValue(value)
                                                         .setNeeded(needed);
    if (leftover != null) {
      builder.setLeftover(leftover);
    }
    builder.save(consumer, location(saveName));
  }

  /**
   * Register ingots, nuggets, and blocks for a metal material
   * @param consumer  Consumer instance
   * @param material  Material
   * @param name      Material name
   */
  default void metalMaterialRecipe(Consumer<FinishedRecipe> consumer, MaterialVariantId material, String folder, String name, boolean optional) {
    Consumer<FinishedRecipe> wrapped = optional ? withCondition(consumer, tagCondition("ingots/" + name)) : consumer;
    String matName = material.getLocation('/').getPath();
    // ingot
    TagKey<Item> ingotTag = getItemTag("forge", "ingots/" + name);
    materialRecipe(wrapped, material, Ingredient.of(ingotTag), 1, 1, folder + matName + "/ingot");
    // nugget
    wrapped = optional ? withCondition(consumer, tagCondition("nuggets/" + name)) : consumer;
    materialRecipe(wrapped, material, Ingredient.of(getItemTag("forge", "nuggets/" + name)), 1, 9, folder + matName + "/nugget");
    // block
    wrapped = optional ? withCondition(consumer, tagCondition("storage_blocks/" + name)) : consumer;
    materialRecipe(wrapped, material, Ingredient.of(getItemTag("forge", "storage_blocks/" + name)), 9, 1, ItemOutput.fromTag(ingotTag), folder + matName + "/block");
  }

  /** Adds recipes to melt a material */
  default void materialMelting(Consumer<FinishedRecipe> consumer, MaterialVariantId material, Fluid fluid, int fluidAmount, String folder) {
    MaterialMeltingRecipeBuilder.material(material, new FluidStack(fluid, fluidAmount))
                                .save(consumer, location(folder + "melting/" + material.getLocation('_').getPath()));
  }

  /** Adds recipes to melt and cast a material */
  default void materialMeltingCasting(Consumer<FinishedRecipe> consumer, MaterialVariantId material, FluidObject<?> fluid, boolean forgeTag, int fluidAmount, String folder) {
    MaterialFluidRecipeBuilder.material(material)
                              .setFluid(fluid.ingredient(fluidAmount, forgeTag))
                              .setTemperature(fluid.getType().getTemperature() - 300)
                              .save(consumer, location(folder + "casting/" + material.getLocation('_').getPath()));
    materialMelting(consumer, material, fluid.get(), fluidAmount, folder);
  }

  /** Adds recipes to melt and cast a compat material of ingot size */
  default void materialMeltingCasting(Consumer<FinishedRecipe> consumer, MaterialVariantId material, FluidObject<?> fluid, boolean forgeTag, String folder) {
    materialMeltingCasting(consumer, material, fluid, forgeTag, FluidValues.INGOT, folder);
  }

  /** Adds recipes to melt and cast a compat material of ingot size with a second tag allowed to make the material exist */
  default void compatMeltingCasting(Consumer<FinishedRecipe> consumer, MaterialId material, FluidObject<?> fluid, String altTag, String folder) {
    materialMeltingCasting(withCondition(consumer, new OrCondition(tagCondition("ingots/" + material.getPath()), tagCondition("ingots/" + altTag))), material, fluid, true, folder);
  }

  /** Adds recipes to melt and cast a material of ingot size */
  default void compatMeltingCasting(Consumer<FinishedRecipe> consumer, MaterialId material, FluidObject<?> fluid, String folder) {
    materialMeltingCasting(withCondition(consumer, tagCondition("ingots/" + material.getPath())), material, fluid, true, folder);
  }

  /** Adds recipes to melt and cast a material */
  default void materialMeltingCasting(Consumer<FinishedRecipe> consumer, MaterialVariantId material, FluidObject<?> fluid, int fluidAmount, String folder) {
    materialMeltingCasting(consumer, material, fluid, false, fluidAmount, folder);
  }

  /** Adds recipes to melt and cast a material of ingot size */
  default void materialMeltingCasting(Consumer<FinishedRecipe> consumer, MaterialVariantId material, FluidObject<?> fluid, String folder) {
    materialMeltingCasting(consumer, material, fluid, FluidValues.INGOT, folder);
  }

  /** Adds recipes to melt and cast a material of ingot size */
  default void materialMeltingComposite(Consumer<FinishedRecipe> consumer, MaterialVariantId input, MaterialVariantId output, FluidObject<?> fluid, boolean forgeTag, int amount, String folder) {
    materialMelting(consumer, output, fluid.get(), amount, folder);
    materialComposite(consumer, input, output, fluid, forgeTag, amount, folder);
  }

  /** Adds recipes to melt and cast a material of ingot size */
  default void materialComposite(Consumer<FinishedRecipe> consumer, MaterialVariantId input, MaterialVariantId output, FluidObject<?> fluid, boolean forgeTag, int amount, String folder, String name) {
    MaterialFluidRecipeBuilder.material(output)
                              .setInputId(input)
                              .setFluid(fluid.ingredient(amount, forgeTag))
                              .setTemperature(fluid.getType().getTemperature() - 300)
                              .save(consumer, location(folder + "composite/" + name));
  }

  /** Adds recipes to melt and cast a material of ingot size */
  default void materialComposite(Consumer<FinishedRecipe> consumer, MaterialVariantId input, MaterialVariantId output, FluidObject<?> fluid, boolean forgeTag, int amount, String folder) {
    materialComposite(consumer, input, output, fluid, forgeTag, amount, folder, output.getLocation('_').getPath());
  }
}
