package slimeknights.tconstruct.library.recipe.partbuilder;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for a material item part crafting recipe
 */
@RequiredArgsConstructor(staticName = "partRecipe")
public class PartRecipeBuilder extends AbstractRecipeBuilder<PartRecipeBuilder> {
  private final IMaterialItem output;
  private final int outputAmount;
  @Setter @Accessors(chain = true)
  private int cost = 1;
  @Setter @Accessors(chain = true)
  private ResourceLocation pattern = null;

  /**
   * Creates a new part recipe that outputs a single item
   * @param output  Output item
   * @return  Builder instance
   */
  public static PartRecipeBuilder partRecipe(IMaterialItem output) {
    return partRecipe(output, 1);
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn) {
    this.build(consumerIn, Objects.requireNonNull(this.output.asItem().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    if (this.outputAmount <= 0) {
      throw new IllegalStateException("recipe " + id + " must output at least 1");
    }
    if (this.cost <= 0) {
      throw new IllegalStateException("recipe " + id + " has no cost associated with it");
    }
    if (this.pattern == null) {
      throw new IllegalStateException("recipe " + id + " has no pattern associated with it");
    }
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "parts");
    consumerIn.accept(new Result(id, advancementId));
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
      json.addProperty("pattern", pattern.toString());
      json.addProperty("cost", cost);

      JsonObject jsonOutput = new JsonObject();
      jsonOutput.addProperty("item", Objects.requireNonNull(output.asItem().getRegistryName()).toString());
      if (outputAmount > 1) {
        jsonOutput.addProperty("count", outputAmount);
      }
      json.add("result", jsonOutput);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerTables.partRecipeSerializer.get();
    }
  }
}
