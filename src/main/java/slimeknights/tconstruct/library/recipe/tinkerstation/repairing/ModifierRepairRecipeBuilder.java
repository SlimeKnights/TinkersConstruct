package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builds a recipe to repair a tool using a modifier */
@RequiredArgsConstructor(staticName = "repair")
public class ModifierRepairRecipeBuilder extends AbstractRecipeBuilder<ModifierRepairRecipeBuilder> {
  private final Modifier modifier;
  private final Ingredient ingredient;
  private final int repairAmount;

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, modifier.getId());
  }

  /** Builds the recipe for the crafting table using a repair kit */
  public ModifierRepairRecipeBuilder buildCraftingTable(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new FinishedRecipe(id, advancementId, TinkerModifiers.craftingModifierRepair.get()));
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new FinishedRecipe(id, advancementId, TinkerModifiers.modifierRepair.get()));
  }

  private class FinishedRecipe extends AbstractFinishedRecipe {
    @Getter
    private final IRecipeSerializer<?> serializer;

    public FinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID, IRecipeSerializer<?> serializer) {
      super(ID, advancementID);
      this.serializer = serializer;
    }

    @Override
    public void serialize(JsonObject json) {
      json.addProperty("modifier", modifier.getId().toString());
      json.add("ingredient", ingredient.serialize());
      json.addProperty("repair_amount", repairAmount);
    }
  }
}
