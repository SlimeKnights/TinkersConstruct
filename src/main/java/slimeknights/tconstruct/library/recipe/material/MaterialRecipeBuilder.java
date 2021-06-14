package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Builder for a recipe to determine the material from an input
 */
@RequiredArgsConstructor(staticName = "materialRecipe")
@Accessors(chain = true)
public class MaterialRecipeBuilder extends AbstractRecipeBuilder<MaterialRecipeBuilder> {
  private final MaterialId material;
  private Ingredient ingredient = Ingredient.EMPTY;
  @Setter
  private int value = 1;
  @Setter
  private int needed = 1;
  @Setter
  private ItemOutput leftover = null;

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
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "materials");
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
      json.add("ingredient", ingredient.serialize());
      json.addProperty("value", value);
      json.addProperty("needed", needed);
      json.addProperty("material", material.toString());
      if (value > 1 && leftover != null) {
        json.add("leftover", leftover.serialize());
      }
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerTables.materialRecipeSerializer.get();
    }
  }
}
