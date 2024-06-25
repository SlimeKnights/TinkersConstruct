package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Builder for a recipe that builds a tool
 */
@RequiredArgsConstructor(staticName = "toolBuildingRecipe")
@Accessors(fluent = true)
public class ToolBuildingRecipeBuilder extends AbstractRecipeBuilder<ToolBuildingRecipeBuilder> {
  private final IModifiable output;
  @Setter
  private int outputSize = 1;
  @Nullable @Setter
  private ResourceLocation layoutSlot = null;
  private final List<Ingredient> extraRequirements = new ArrayList<>();

  /** Adds an extra ingredient requirement */
  public ToolBuildingRecipeBuilder addExtraRequirement(Ingredient ingredient) {
    extraRequirements.add(ingredient);
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn) {
    this.save(consumerIn, Registry.ITEM.getKey(this.output.asItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "parts");
    consumerIn.accept(new LoadableFinishedRecipe<>(new ToolBuildingRecipe(id, group, output, outputSize, layoutSlot, extraRequirements), ToolBuildingRecipe.LOADER, advancementId));
  }
}
