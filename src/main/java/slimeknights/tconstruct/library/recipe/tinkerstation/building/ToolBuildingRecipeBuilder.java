package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
  private final List<Ingredient> extraRequirements = new ArrayList<>();

  /** Adds an extra ingredient requirement */
  public ToolBuildingRecipeBuilder addExtraRequirement(Ingredient ingredient) {
    extraRequirements.add(ingredient);
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn) {
    this.save(consumerIn, Objects.requireNonNull(this.output.asItem().getRegistryName()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "parts");
    consumerIn.accept(new ToolBuildingRecipeBuilder.Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      json.addProperty("result", Objects.requireNonNull(output.asItem().getRegistryName()).toString());
      json.addProperty("result_count", outputSize);
      if (!extraRequirements.isEmpty()) {
        JsonArray array = new JsonArray();
        for (Ingredient ingredient : extraRequirements) {
          array.add(ingredient.toJson());
        }
        json.add("extra_requirements", array);
      }
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerTables.toolBuildingRecipeSerializer.get();
    }
  }
}
