package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.SetTag;
import net.minecraft.util.Identifier;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.tables.TinkerTables;

import org.jetbrains.annotations.Nullable;
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
  public MaterialRecipeBuilder setIngredient(SetTag<Item> tag) {
    return this.setIngredient(Ingredient.fromTag(tag));
  }

  /**
   * Sets the input ingredient for this material recipe
   * @param item  Item input
   * @return  Builder instance
   */
  public MaterialRecipeBuilder setIngredient(ItemConvertible item) {
    return this.setIngredient(Ingredient.ofItems(item));
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
  public void build(Consumer<RecipeJsonProvider> consumerIn) {
    this.build(consumerIn, material);
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumerIn, Identifier id) {
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
    Identifier advancementId = this.buildOptionalAdvancement(id, "materials");
    consumerIn.accept(new Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(Identifier ID, @Nullable Identifier advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      json.add("ingredient", ingredient.toJson());
      json.addProperty("value", value);
      json.addProperty("needed", needed);
      json.addProperty("material", material.toString());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerTables.materialRecipeSerializer.get();
    }
  }
}
