package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;

import java.util.function.Consumer;

/** Builds a recipe to repair a tool using a modifier */
@RequiredArgsConstructor(staticName = "repair")
public class ModifierRepairRecipeBuilder extends AbstractRecipeBuilder<ModifierRepairRecipeBuilder> {
  private final ModifierId modifier;
  private final Ingredient ingredient;
  private final int repairAmount;

  public static ModifierRepairRecipeBuilder repair(LazyModifier modifier, Ingredient ingredient, int repairAmount) {
    return repair(modifier.getId(), ingredient, repairAmount);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, modifier);
  }

  /** Builds the recipe for the crafting table using a repair kit */
  public ModifierRepairRecipeBuilder buildCraftingTable(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new LoadableFinishedRecipe<>(new ModifierRepairCraftingRecipe(id, modifier, ingredient, repairAmount), ModifierRepairCraftingRecipe.LOADER, advancementId));
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new LoadableFinishedRecipe<>(new ModifierRepairTinkerStationRecipe(id, modifier, ingredient, repairAmount), ModifierRepairTinkerStationRecipe.LOADER, advancementId));
  }
}
