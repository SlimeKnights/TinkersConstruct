package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IToolPart;

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
  private List<IToolPart> partsOverride = null;
  private final List<MaterialVariantId> extraMaterials = new ArrayList<>();

  /** Adds an extra ingredient requirement */
  public ToolBuildingRecipeBuilder addExtraRequirement(Ingredient ingredient) {
    extraRequirements.add(ingredient);
    return this;
  }

  /** Overrides the parts list to empty */
  public ToolBuildingRecipeBuilder noParts() {
    this.partsOverride = new ArrayList<>();
    return this;
  }

  /** Overrides the parts list from the tool definition, using the passed list instead */
  public ToolBuildingRecipeBuilder partOverride(IToolPart part) {
    if (partsOverride == null) {
      partsOverride = new ArrayList<>();
    }
    partsOverride.add(part);
    return this;
  }

  /** Adds a material to set after the end of the parts list */
  public ToolBuildingRecipeBuilder addExtraMaterial(MaterialVariantId material) {
    extraMaterials.add(material);
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn) {
    this.save(consumerIn, BuiltInRegistries.ITEM.getKey(this.output.asItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "parts");
    consumerIn.accept(new LoadableFinishedRecipe<>(new ToolBuildingRecipe(id, group, output, outputSize, layoutSlot, extraRequirements, partsOverride, extraMaterials), ToolBuildingRecipe.LOADER, advancementId));
  }
}
