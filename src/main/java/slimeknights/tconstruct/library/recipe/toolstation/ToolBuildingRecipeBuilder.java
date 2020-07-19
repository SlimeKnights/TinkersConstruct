package slimeknights.tconstruct.library.recipe.toolstation;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ToolBuildingRecipeBuilder {

  private String group;
  private Item output;
  private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

  private ToolBuildingRecipeBuilder(Item output) {
    this.output = output;
  }

  public static ToolBuildingRecipeBuilder toolBuildingRecipe(Item resultIn) {
    return new ToolBuildingRecipeBuilder(resultIn);
  }

  public ToolBuildingRecipeBuilder addCriterion(String name, ICriterionInstance criterionInstance) {
    this.advancementBuilder.withCriterion(name, criterionInstance);
    return this;
  }

  public ToolBuildingRecipeBuilder setGroup(String groupIn) {
    this.group = groupIn;
    return this;
  }

  /*
   * Makes sure that this is obtainable
   */
  private void validate(ResourceLocation id) {
    if (this.output == null) {
      throw new IllegalStateException("recipe " + id + " has no output associated with it");
    }

    if (this.advancementBuilder.getCriteria().isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + id);
    }
  }

  public void build(Consumer<IFinishedRecipe> consumerIn) {
    this.build(consumerIn, this.output.getRegistryName());
  }

  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    this.build(consumerIn, id, "tools");
  }

  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id, String group) {
    this.validate(id);

    this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
    consumerIn.accept(new ToolBuildingRecipeBuilder.Result(id, this.group == null ? "" : this.group, this.output, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + group + "/" + id.getPath())));
  }

  public static class Result implements IFinishedRecipe {

    private final ResourceLocation id;
    private final String group;
    private final Item output;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementId;

    public Result(ResourceLocation id, String group, Item output, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
      this.id = id;
      this.group = group;
      this.output = output;
      this.advancementBuilder = advancementBuilder;
      this.advancementId = advancementId;
    }

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }

      json.addProperty("output", ForgeRegistries.ITEMS.getKey(this.output).toString());
    }

    @Override
    public ResourceLocation getID() {
      return this.id;
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

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return this.advancementId;
    }
  }
}
