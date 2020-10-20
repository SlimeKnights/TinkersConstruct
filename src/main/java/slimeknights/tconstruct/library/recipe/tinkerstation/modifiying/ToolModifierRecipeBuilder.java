package slimeknights.tconstruct.library.recipe.tinkerstation.modifiying;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Builder for a recipe that will modify the tools
 */
@RequiredArgsConstructor(staticName = "modifierRecipe")
public class ToolModifierRecipeBuilder extends AbstractRecipeBuilder<ToolModifierRecipeBuilder> {

  private final ModifierId modifier;
  private Ingredient ingredient = Ingredient.EMPTY;
  @Setter @Accessors(chain = true)
  private int cost = 1;

  /**
   * Sets the input ingredient for this material recipe
   * @param tag  Tag input
   * @return Builder instance
   */
  public ToolModifierRecipeBuilder setIngredient(Tag<Item> tag) {
    return this.setIngredient(Ingredient.fromTag(tag));
  }

  /**
   * Sets the input ingredient for this material recipe
   * @param item  Item input
   * @return Builder instance
   */
  public ToolModifierRecipeBuilder setIngredient(IItemProvider item) {
    return this.setIngredient(Ingredient.fromItems(item));
  }

  /**
   * Sets the input ingredient for this material recipe
   * @param ingredient  Ingredient input
   * @return Builder instance
   */
  public ToolModifierRecipeBuilder setIngredient(Ingredient ingredient) {
    this.ingredient = ingredient;
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn) {
    this.build(consumerIn, modifier);
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    if (this.modifier == null) {
      throw new IllegalStateException("recipe " + id + " has no modifier associated with it");
    }
    if (this.ingredient == Ingredient.EMPTY) {
      throw new IllegalStateException("recipe " + id + " must have ingredient set");
    }
    if (this.cost <= 0) {
      throw new IllegalStateException("recipe " + id + " has no modifier cost associated with it");
    }

    ResourceLocation advancementId = this.buildAdvancement(id, "modifiers");
    consumerIn.accept(new Result(id, this.group, this.ingredient, this.modifier, this.cost, this.advancementBuilder, advancementId));
  }

  @AllArgsConstructor
  private static class Result implements IFinishedRecipe {

    @Getter
    private final ResourceLocation ID;
    private final String group;
    private final Ingredient ingredient;
    private final ModifierId modifier;
    private final int cost;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }
      json.add("ingredient", this.ingredient.serialize());
      json.addProperty("cost", this.cost);
      json.addProperty("modifier", this.modifier.toString());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerTables.toolModifierRecipeSerializer.get();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }
  }
}
