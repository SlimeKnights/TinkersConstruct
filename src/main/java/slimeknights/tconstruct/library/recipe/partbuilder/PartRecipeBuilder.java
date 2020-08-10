package slimeknights.tconstruct.library.recipe.partbuilder;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class PartRecipeBuilder {

  private String group;
  private ItemStack output;
  private int cost = 1;
  private ResourceLocation pattern = null;
  private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

  private PartRecipeBuilder(ItemStack output) {
    this.output = output;
  }

  public static PartRecipeBuilder partRecipe(ItemStack resultIn) {
    return new PartRecipeBuilder(resultIn);
  }

  public PartRecipeBuilder addCriterion(String name, ICriterionInstance criterionInstance) {
    this.advancementBuilder.withCriterion(name, criterionInstance);
    return this;
  }

  public PartRecipeBuilder setGroup(String groupIn) {
    this.group = groupIn;
    return this;
  }

  public PartRecipeBuilder setCost(int cost) {
    this.cost = cost;
    return this;
  }

  public PartRecipeBuilder setPattern(ResourceLocation pattern) {
    this.pattern = pattern;
    return this;
  }

  /*
   * Makes sure that this is obtainable
   */
  private void validate(ResourceLocation id) {
    if (this.output.isEmpty()) {
      throw new IllegalStateException("recipe " + id + " has no output associated with it");
    }

    if (this.cost == 0) {
      throw new IllegalStateException("recipe " + id + " has no value associated with it");
    }

    if (this.pattern == null) {
      throw new IllegalStateException("recipe " + id + " has no pattern associated with it");
    }

    if (this.advancementBuilder.getCriteria().isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + id);
    }
  }

  public void build(Consumer<IFinishedRecipe> consumerIn) {
    this.build(consumerIn, Objects.requireNonNull(this.output.getItem().getRegistryName()));
  }

  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    this.build(consumerIn, id, "parts");
  }

  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id, String group) {
    this.validate(id);
    this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
    consumerIn.accept(new Result(id, this.group == null ? "" : this.group, this.output, this.cost, this.pattern, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + group + "/" + id.getPath())));
  }

  public static class Result implements IFinishedRecipe {

    private final ResourceLocation id;
    private final String group;
    private final ItemStack output;
    private final int cost;
    private final ResourceLocation pattern;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementId;

    public Result(ResourceLocation id, String group, ItemStack output, int cost, ResourceLocation pattern, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
      this.id = id;
      this.group = group;
      this.output = output;
      this.cost = cost;
      this.pattern = pattern;
      this.advancementBuilder = advancementBuilder;
      this.advancementId = advancementId;
    }

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }

      json.addProperty("pattern", this.pattern.toString());

      json.addProperty("cost", this.cost);

      JsonObject output = new JsonObject();

      output.addProperty("item", Objects.requireNonNull(this.output.getItem().getRegistryName()).toString());
      if (this.output.getCount() > 1) {
        output.addProperty("count", this.output.getCount());
      }
      if (this.output.hasTag()) {
        output.addProperty("nbt", this.output.getTag().toString());
      }

      json.add("output", output);
    }

    @Override
    public ResourceLocation getID() {
      return this.id;
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

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return this.advancementId;
    }
  }
}
