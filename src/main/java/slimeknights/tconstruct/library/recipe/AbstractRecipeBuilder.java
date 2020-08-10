package slimeknights.tconstruct.library.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

/**
 * Common logic to create a recipe builder class
 * @param <T>
 */
public abstract class AbstractRecipeBuilder<T extends AbstractRecipeBuilder<T>> {

  /** Advancement builder for this class */
  protected final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

  /**
   * Adds a criteria to the recipe
   * @param name      Criteria name
   * @param criteria  Criteria instance
   * @return  Builder
   */
  @SuppressWarnings("unchecked")
  public T addCriterion(String name, ICriterionInstance criteria) {
    this.advancementBuilder.withCriterion(name, criteria);
    return (T)this;
  }

  /**
   * Builds the recipe
   * @param consumerIn  Recipe consumer
   * @param id          Recipe ID
   */
  public abstract void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id);

  /**
   * Builds and validates the advancement, intended to be called in {@link #build(Consumer, ResourceLocation)}
   * @param id  Recipe ID
   * @return Advancement ID
   */
  protected ResourceLocation buildAdvancement(ResourceLocation id, String group) {
    if (this.advancementBuilder.getCriteria().isEmpty()) {
      throw new IllegalStateException("No way of obtaining recipe " + id);
    }
    this.advancementBuilder
      .withParentId(new ResourceLocation("recipes/root"))
      .withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id))
      .withRewards(AdvancementRewards.Builder.recipe(id))
      .withRequirementsStrategy(IRequirementsStrategy.OR);
    return new ResourceLocation(id.getNamespace(), "recipes/" + group + "/" + id.getPath());
  }
}
