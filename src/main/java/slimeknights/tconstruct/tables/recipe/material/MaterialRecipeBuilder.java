package slimeknights.tconstruct.tables.recipe.material;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class MaterialRecipeBuilder {

  private Ingredient ingredient = Ingredient.EMPTY;
  private String group;
  private MaterialId material;
  private int value = 1;
  private int needed = 1;
  private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

  public MaterialRecipeBuilder(MaterialId material) {
    this.material = material;
  }

  public static MaterialRecipeBuilder materialRecipe(MaterialId material) {
    return new MaterialRecipeBuilder(material);
  }

  public MaterialRecipeBuilder addCriterion(String name, ICriterionInstance criterionInstance) {
    this.advancementBuilder.withCriterion(name, criterionInstance);
    return this;
  }

  public MaterialRecipeBuilder setGroup(String groupIn) {
    this.group = groupIn;
    return this;
  }

  public MaterialRecipeBuilder setIngredient(Tag<Item> tagIn) {
    return this.setIngredient(Ingredient.fromTag(tagIn));
  }

  public MaterialRecipeBuilder setIngredient(IItemProvider itemIn) {
    return this.setIngredient(Ingredient.fromItems(itemIn));
  }

  public MaterialRecipeBuilder setIngredient(Ingredient ingredient) {
    this.ingredient = ingredient;
    return this;
  }

  public MaterialRecipeBuilder setValue(int value) {
    this.value = value;
    return this;
  }

  public MaterialRecipeBuilder setNeeded(int needed) {
    this.needed = needed;
    return this;
  }

  /*
   * Makes sure that this is obtainable
   */
  private void validate(ResourceLocation id) {
    if (this.material == null) {
      throw new IllegalStateException("recipe " + id + " has no material associated with it");
    }

    if (this.value == 0) {
      throw new IllegalStateException("recipe " + id + " has no value associated with it");
    }

    if (this.needed == 0) {
      throw new IllegalStateException("recipe " + id + " has no needed associated with it");
    }

    if (this.advancementBuilder.getCriteria().isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + id);
    }
  }

  public void build(Consumer<IFinishedRecipe> consumerIn) {
    this.build(consumerIn, material);
  }

  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    this.build(consumerIn, id, "materials");
  }

  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id, String group) {
    this.validate(id);
    this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
    consumerIn.accept(new Result(id, this.group == null ? "" : this.group, this.ingredient, this.material, this.value, this.needed, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + group + "/" + id.getPath())));
  }

  public static class Result implements IFinishedRecipe {

    private final ResourceLocation id;
    private final String group;
    private final Ingredient ingredient;
    private final MaterialId material;
    private final int value;
    private final int needed;
    private final Advancement.Builder advancementBuilder;
    private final ResourceLocation advancementId;

    public Result(ResourceLocation id, String group, Ingredient ingredient, MaterialId material, int value, int needed, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
      this.id = id;
      this.group = group;
      this.ingredient = ingredient;
      this.material = material;
      this.value = value;
      this.needed = needed;
      this.advancementBuilder = advancementBuilder;
      this.advancementId = advancementId;
    }

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }

      if (ingredient != Ingredient.EMPTY) {
        json.add("ingredient", this.ingredient.serialize());
      }

      json.addProperty("value", this.value);

      json.addProperty("needed", this.needed);

      if (this.material != null) {
        json.addProperty("material", this.material.toString());
      }
    }

    @Override
    public ResourceLocation getID() {
      return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerTables.materialRecipeSerializer.get();
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
