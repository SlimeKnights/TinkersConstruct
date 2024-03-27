package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import java.util.function.Consumer;

/** Builds a recipe to repair a tool in the tinker station */
@RequiredArgsConstructor(staticName = "repair")
public class SpecializedRepairRecipeBuilder extends AbstractRecipeBuilder<SpecializedRepairRecipeBuilder> {
  private final Ingredient tool;
  private final MaterialId repairMaterial;

  /** Creates a builder from the given item and material */
  public static SpecializedRepairRecipeBuilder repair(ItemLike item, MaterialId repairMaterial) {
    return repair(Ingredient.of(item), repairMaterial);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, repairMaterial);
  }

  /** Builds the recipe for the crafting table using a repair kit */
  public SpecializedRepairRecipeBuilder buildRepairKit(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new LoadableFinishedRecipe<>(new SpecializedRepairKitRecipe(id, tool, repairMaterial), SpecializedRepairKitRecipe.LOADER, advancementId));
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new LoadableFinishedRecipe<>(new SpecializedRepairRecipe(id, tool, repairMaterial), SpecializedRepairRecipe.LOADER, advancementId));
  }
}
