package slimeknights.tconstruct.library.recipe.material;

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
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Builder for a recipe to determine the material from an input
 */
@RequiredArgsConstructor(staticName = "materialRecipe")
public class MaterialRecipeBuilder extends AbstractRecipeBuilder<MaterialRecipeBuilder> {
  private final MaterialId material;
  private Ingredient ingredient = Ingredient.EMPTY;
  @Setter @Accessors(chain = true)
  private int value = 1;
  @Setter @Accessors(chain = true)
  private int needed = 1;

  /**
   * Sets the input ingredient for this material recipe
   * @param tag  Tag input
   * @return  Builder instance
   */
  public MaterialRecipeBuilder setIngredient(Tag<Item> tag) {
    return this.setIngredient(Ingredient.fromTag(tag));
  }

  /**
   * Sets the input ingredient for this material recipe
   * @param item  Item input
   * @return  Builder instance
   */
  public MaterialRecipeBuilder setIngredient(IItemProvider item) {
    return this.setIngredient(Ingredient.fromItems(item));
  }

  /**
   * Sets the input ingredient for this material recipe
   * @param ingredient  Ingredient input
   * @return  Builder instance
   */
  public MaterialRecipeBuilder setIngredient(Ingredient ingredient) {
    this.ingredient = ingredient;
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn) {
    this.build(consumerIn, material);
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    if (this.material == null) {
      throw new IllegalStateException("recipe " + id + " has no material associated with it");
    }
    if (this.ingredient == Ingredient.EMPTY) {
      throw new IllegalStateException("recipe " + id + " must have ingredient set");
    }
    if (this.value <= 0) {
      throw new IllegalStateException("recipe " + id + " has no value associated with it");
    }
    if (this.needed <= 0) {
      throw new IllegalStateException("recipe " + id + " has no needed associated with it");
    }
    ResourceLocation advancementId = this.buildAdvancement(id, "materials");
    consumerIn.accept(new Result(id, this.group, this.ingredient, this.material, this.value, this.needed, this.advancementBuilder, advancementId));
  }

  @AllArgsConstructor
  private static class Result implements IFinishedRecipe {
    @Getter
    private final ResourceLocation ID;
    private final String group;
    private final Ingredient ingredient;
    private final MaterialId material;
    private final int value;
    private final int needed;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }
      json.add("ingredient", this.ingredient.serialize());
      json.addProperty("value", this.value);
      json.addProperty("needed", this.needed);
      json.addProperty("material", this.material.toString());
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
  }
}
