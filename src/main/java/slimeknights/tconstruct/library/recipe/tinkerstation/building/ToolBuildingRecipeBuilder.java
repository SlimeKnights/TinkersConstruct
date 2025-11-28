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
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tools.recipe.TippedToolTransformRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
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
  /** Modifier ID for the tipped arrow transformation recipe. Not typically needed by addons, just didn't feel like making another builder. */
  @Nullable @Setter
  private ModifierId tippedModifier = null;

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

  /** Adds a material to set after the end of the parts list */
  public ToolBuildingRecipeBuilder addExtraMaterial(MaterialVariantId... materials) {
    Collections.addAll(extraMaterials, materials);
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn) {
    this.save(consumerIn, BuiltInRegistries.ITEM.getKey(this.output.asItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "parts");
    if (tippedModifier != null) {
      if (extraMaterials.isEmpty()) {
        throw new IllegalArgumentException("Must have at least 1 material for modifier transform");
      }
      if (extraRequirements.size() != 1) {
        throw new IllegalArgumentException("Must have exactly one ingredient for modifier transform");
      }
      consumerIn.accept(new LoadableFinishedRecipe<>(new TippedToolTransformRecipe(id, group, output, layoutSlot, extraRequirements.get(0), extraMaterials, tippedModifier), TippedToolTransformRecipe.LOADER, advancementId));
    } else {
      consumerIn.accept(new LoadableFinishedRecipe<>(new ToolBuildingRecipe(id, group, output, outputSize, layoutSlot, extraRequirements, partsOverride, extraMaterials), ToolBuildingRecipe.LOADER, advancementId));
    }
  }
}
