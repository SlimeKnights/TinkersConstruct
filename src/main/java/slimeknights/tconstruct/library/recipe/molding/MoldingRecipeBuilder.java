package slimeknights.tconstruct.library.recipe.molding;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "molding")
public class MoldingRecipeBuilder extends AbstractRecipeBuilder<MoldingRecipeBuilder> {
  private final ItemStack output;
  private Ingredient material = Ingredient.EMPTY;
  private Ingredient mold = Ingredient.EMPTY;
  private boolean moldConsumed = false;

  /**
   * Creates a new builder of the given item
   * @param item  Item output
   * @return  Recipe
   */
  public static MoldingRecipeBuilder molding(IItemProvider item) {
    return molding(new ItemStack(item));
  }

  /* Inputs */

  /** Sets the material item, on the table */
  public MoldingRecipeBuilder setMaterial(Ingredient ingredient) {
    this.material = ingredient;
    return this;
  }

  /** Sets the material item, on the table */
  public MoldingRecipeBuilder setMaterial(IItemProvider item) {
    return setMaterial(Ingredient.fromItems(item));
  }

  /** Sets the material item, on the table */
  public MoldingRecipeBuilder setMaterial(ITag<Item> tag) {
    return setMaterial(Ingredient.fromTag(tag));
  }

  /** Sets the mold item, in the players hand */
  public MoldingRecipeBuilder setMold(Ingredient ingredient, boolean consumed) {
    this.mold = ingredient;
    this.moldConsumed = consumed;
    return this;
  }

  /** Sets the mold item, in the players hand */
  public MoldingRecipeBuilder setMold(IItemProvider item, boolean consumed) {
    return setMold(Ingredient.fromItems(item), consumed);
  }

  /** Sets the mold item, in the players hand */
  public MoldingRecipeBuilder setMold(ITag<Item> tag, boolean consumed) {
    return setMold(Ingredient.fromTag(tag), consumed);
  }


  /* Building */

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(output.getItem().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (material == Ingredient.EMPTY) {
      throw new IllegalStateException("Missing material for molding recipe");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "molding");
    consumer.accept(new FinishedRecipe(id, advancementId));
  }

  /**
   * Serializes the given result to JSON
   * @param result  Result
   * @return  JSON element
   */
  public static JsonElement serializeResult(ItemStack result) {
    // if the item has NBT, write both, else write just the name
    String itemName = Objects.requireNonNull(result.getItem().getRegistryName()).toString();
    if (result.hasTag()) {
      JsonObject jsonResult = new JsonObject();
      jsonResult.addProperty("item", itemName);
      jsonResult.addProperty("nbt", Objects.requireNonNull(result.getTag()).toString());
      return jsonResult;
    } else {
      return new JsonPrimitive(itemName);
    }
  }

  private class FinishedRecipe extends AbstractFinishedRecipe {
    public FinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      json.add("material", material.serialize());
      if (mold != Ingredient.EMPTY) {
        json.add("mold", mold.serialize());
        if (moldConsumed) {
          json.addProperty("mold_consumed", true);
        }
      }
      json.add("result", serializeResult(output));
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.moldingSerializer.get();
    }
  }
}
