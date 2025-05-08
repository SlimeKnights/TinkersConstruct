package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;

import java.util.function.Consumer;

/** @deprecated use {@link slimeknights.tconstruct.library.modifiers.modules.behavior.MaterialRepairModule} */
@Deprecated(forRemoval = true)
@RequiredArgsConstructor(staticName = "repair")
public class ModifierMaterialRepairRecipeBuilder extends AbstractRecipeBuilder<ModifierMaterialRepairRecipeBuilder> {
  private final ModifierId modifier;
  private final MaterialId material;
  private final MaterialStatsId statType;

  public static ModifierMaterialRepairRecipeBuilder repair(LazyModifier modifier, MaterialId material, MaterialStatsId statType) {
    return repair(modifier.getId(), material, statType);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, modifier);
  }

  /** Builds the recipe for the crafting table using a repair kit */
  @SuppressWarnings("removal")
  public ModifierMaterialRepairRecipeBuilder saveCraftingTable(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new LoadableFinishedRecipe<>(new ModifierMaterialRepairKitRecipe(id, modifier, material, statType), ModifierMaterialRepairKitRecipe.LOADER, advancementId));
    return this;
  }

  @SuppressWarnings("removal")
  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new LoadableFinishedRecipe<>(new ModifierMaterialRepairRecipe(id, modifier, material, statType), ModifierMaterialRepairRecipe.LOADER, advancementId));
  }
}
