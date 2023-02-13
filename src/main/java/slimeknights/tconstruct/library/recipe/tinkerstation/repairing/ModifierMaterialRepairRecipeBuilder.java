package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builds a recipe to repair a tool using a modifier via a material */
@RequiredArgsConstructor(staticName = "repair")
public class ModifierMaterialRepairRecipeBuilder extends AbstractRecipeBuilder<ModifierMaterialRepairRecipeBuilder> {
  private final ModifierId modifier;
  private final MaterialId material;

  public static ModifierMaterialRepairRecipeBuilder repair(LazyModifier modifier, MaterialId material) {
    return repair(modifier.getId(), material);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, modifier);
  }

  /** Builds the recipe for the crafting table using a repair kit */
  public ModifierMaterialRepairRecipeBuilder saveCraftingTable(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new Finished(id, advancementId, TinkerModifiers.craftingModifierMaterialRepair.get()));
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new Finished(id, advancementId, TinkerModifiers.modifierMaterialRepair.get()));
  }

  private class Finished extends AbstractFinishedRecipe {
    @Getter
    private final RecipeSerializer<?> type;

    public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID, RecipeSerializer<?> type) {
      super(ID, advancementID);
      this.type = type;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.addProperty("modifier", modifier.toString());
      json.addProperty("repair_material", material.toString());
    }
  }
}
