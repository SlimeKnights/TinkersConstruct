package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for a recipe that builds a tool
 */
@RequiredArgsConstructor(staticName = "toolBuildingRecipe")
public class ToolBuildingRecipeBuilder extends AbstractRecipeBuilder<ToolBuildingRecipeBuilder> {
  private final ToolCore output;

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn) {
    this.build(consumerIn, Objects.requireNonNull(this.output.asItem().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "parts");
    consumerIn.accept(new ToolBuildingRecipeBuilder.Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
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
    public IRecipeSerializer<?> getSerializer() {
      return TinkerTables.toolBuildingRecipeSerializer.get();
    }
  }
}
