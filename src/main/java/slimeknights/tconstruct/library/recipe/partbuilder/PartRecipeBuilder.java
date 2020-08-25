package slimeknights.tconstruct.library.recipe.partbuilder;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
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
    ResourceLocation advancementId = this.buildAdvancement(id, "parts");
    consumerIn.accept(new Result(id, this.group, this.output, this.outputAmount, this.cost, this.pattern, this.advancementBuilder, advancementId));
  }

  @AllArgsConstructor
  private static class Result implements IFinishedRecipe {
    @Getter
    private final ResourceLocation ID;
    private final String group;
    private final IMaterialItem output;
    private final int outputAmount;
    private final int cost;
    private final ResourceLocation pattern;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }
      json.addProperty("pattern", this.pattern.toString());
      json.addProperty("cost", this.cost);

      JsonObject output = new JsonObject();
      output.addProperty("item", Objects.requireNonNull(this.output.asItem().getRegistryName()).toString());
      if (outputAmount > 1) {
        output.addProperty("count", this.outputAmount);
      }
      json.add("output", output);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerTables.partRecipeSerializer.get();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }
  }
}
