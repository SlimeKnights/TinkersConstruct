package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
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
  default void materialRecipe(Consumer<IFinishedRecipe> consumer, MaterialId material, Ingredient input, int value, int needed, String saveName) {
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
  default void materialRecipe(Consumer<IFinishedRecipe> consumer, MaterialId material, Ingredient input, int value, int needed, @Nullable ItemOutput leftover, String saveName) {
    MaterialRecipeBuilder builder = MaterialRecipeBuilder.materialRecipe(material)
                                                         .setIngredient(input)
                                                         .setValue(value)
                                                         .setNeeded(needed);
    if (leftover != null) {
      builder.setLeftover(leftover);
    }
    builder.build(consumer, modResource(saveName));
  }

  /**
   * Register ingots, nuggets, and blocks for a metal material
   * @param consumer  Consumer instance
   * @param material  Material
   * @param name      Material name
   */
  default void metalMaterialRecipe(Consumer<IFinishedRecipe> consumer, MaterialId material, String folder, String name, boolean optional) {
    Consumer<IFinishedRecipe> wrapped = optional ? withCondition(consumer, tagCondition("ingots/" + name)) : consumer;
    String matName = material.getPath();
    // ingot
    ITag<Item> ingotTag = getTag("forge", "ingots/" + name);
    materialRecipe(wrapped, material, Ingredient.fromTag(ingotTag), 1, 1, folder + matName + "/ingot");
    // nugget
    wrapped = optional ? withCondition(consumer, tagCondition("nuggets/" + name)) : consumer;
    materialRecipe(wrapped, material, Ingredient.fromTag(getTag("forge", "nuggets/" + name)), 1, 9, folder + matName + "/nugget");
    // block
    wrapped = optional ? withCondition(consumer, tagCondition("storage_blocks/" + name)) : consumer;
    materialRecipe(wrapped, material, Ingredient.fromTag(getTag("forge", "storage_blocks/" + name)), 9, 1, ItemOutput.fromTag(ingotTag, 1), folder + matName + "/block");
  }


  /** Adds recipes to melt and cast a material */
  default void materialMeltingCasting(Consumer<IFinishedRecipe> consumer, MaterialId material, FluidObject<?> fluid, int fluidAmount, String folder) {
    MaterialFluidRecipeBuilder.material(material)
                              .setFluid(fluid.getLocalTag(), fluidAmount)
                              .setTemperature(fluid.get().getAttributes().getTemperature() - 300)
                              .build(consumer, modResource(folder + "casting/" + material.getPath()));
    MaterialMeltingRecipeBuilder.material(material, new FluidStack(fluid.get(), fluidAmount))
                                .build(consumer, modResource(folder + "melting/" + material.getPath()));
  }

  /** Adds recipes to melt and cast a material of ingot size */
  default void materialMeltingCasting(Consumer<IFinishedRecipe> consumer, MaterialId material, FluidObject<?> fluid, String folder) {
    materialMeltingCasting(consumer, material, fluid, FluidValues.INGOT, folder);
  }

  /** Adds recipes to melt and cast a material of ingot size */
  default void materialComposite(Consumer<IFinishedRecipe> consumer, MaterialId input, MaterialId output, FluidObject<?> fluid, int amount, boolean forgeTag, String folder) {
    MaterialFluidRecipeBuilder.material(output)
                              .setInputId(input)
                              .setFluid(forgeTag ? fluid.getForgeTag() : fluid.getLocalTag(), amount)
                              .setTemperature(fluid.get().getAttributes().getTemperature() - 300)
                              .build(consumer, modResource(folder + "composite/" + output.getPath()));
  }
}
