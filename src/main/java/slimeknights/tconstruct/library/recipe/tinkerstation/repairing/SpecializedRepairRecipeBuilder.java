package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builds a recipe to repair a tool in the tinker station */
@RequiredArgsConstructor(staticName = "repair")
public class SpecializedRepairRecipeBuilder extends AbstractRecipeBuilder<SpecializedRepairRecipeBuilder> {
  private final Ingredient tool;
  private final MaterialId repairMaterial;

  /** Creates a builder from the given item and material */
  public static SpecializedRepairRecipeBuilder repair(IItemProvider item, MaterialId repairMaterial) {
    return repair(Ingredient.fromItems(item), repairMaterial);
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, repairMaterial);
  }

  /** Builds the recipe for the crafting table using a repair kit */
  public SpecializedRepairRecipeBuilder buildRepairKit(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new FinishedRecipe(id, advancementId, TinkerTables.specializedRepairKitSerializer.get()));
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new FinishedRecipe(id, advancementId, TinkerTables.specializedRepairSerializer.get()));
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
      json.add("tool", tool.serialize());
      json.addProperty("repair_material", repairMaterial.toString());
    }
  }
}
