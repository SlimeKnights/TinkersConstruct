package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.tables.TinkerTables;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for a recipe that builds a tool
 */
@RequiredArgsConstructor(staticName = "toolBuildingRecipe")
public class ToolBuildingRecipeBuilder extends AbstractRecipeBuilder<ToolBuildingRecipeBuilder> {
  private final ToolCore output;

  @Override
  public void build(Consumer<RecipeJsonProvider> consumerIn) {
    this.build(consumerIn, Objects.requireNonNull(this.output.asItem().getRegistryName()));
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumerIn, Identifier id) {
    Identifier advancementId = this.buildOptionalAdvancement(id, "parts");
    consumerIn.accept(new Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(Identifier ID, @Nullable Identifier advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      json.addProperty("result", Objects.requireNonNull(output.getRegistryName()).toString());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerTables.toolBuildingRecipeSerializer.get();
    }
  }
}
