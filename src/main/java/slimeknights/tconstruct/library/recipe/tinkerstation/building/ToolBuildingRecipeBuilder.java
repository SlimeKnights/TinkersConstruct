package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tools.ToolCore;
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
    ResourceLocation advancementId = this.buildAdvancement(id, "parts");
    consumerIn.accept(new ToolBuildingRecipeBuilder.Result(id, this.getGroup(), this.output, this.advancementBuilder, advancementId));
  }

  @AllArgsConstructor
  private static class Result implements IFinishedRecipe {

    @Getter
    private final ResourceLocation ID;
    private final String group;
    private final ToolCore output;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }

      json.addProperty("output", Objects.requireNonNull(this.output.getRegistryName()).toString());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerTables.toolBuildingRecipeSerializer.get();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }
  }
}
