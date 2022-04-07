package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipe.Serializer;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "WeakerAccess"})
@RequiredArgsConstructor(staticName = "castingRecipe")
public class MaterialCastingRecipeBuilder extends AbstractRecipeBuilder<MaterialCastingRecipeBuilder> {
  private final IMaterialItem result;
  private final Serializer<?> recipeSerializer;
  private Ingredient cast = Ingredient.EMPTY;
  @Setter @Accessors(chain = true)
  private int itemCost = 0;
  private boolean consumed = false;
  private boolean switchSlots = false;

  /**
   * Creates a new material casting recipe for an basin recipe
   * @param result            Material item result
   * @return  Builder instance
   */
  public static MaterialCastingRecipeBuilder basinRecipe(IMaterialItem result) {
    return castingRecipe(result, TinkerSmeltery.basinMaterialSerializer.get());
  }

  /**
   * Creates a new material casting recipe for an table recipe
   * @param result            Material item result
   * @return  Builder instance
   */
  public static MaterialCastingRecipeBuilder tableRecipe(IMaterialItem result) {
    return castingRecipe(result, TinkerSmeltery.tableMaterialSerializer.get());
  }

  /**
   * Sets the cast to the given tag
   * @param tag       Cast tag
   * @param consumed  If true, cast is consumed
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(TagKey<Item> tag, boolean consumed) {
    return this.setCast(Ingredient.of(tag), consumed);
  }

  /**
   * Sets the cast to the given item
   * @param item      Cast item
   * @param consumed  If true, cast is consumed
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(ItemLike item, boolean consumed) {
    return this.setCast(Ingredient.of(item), consumed);
  }

  /**
   * Set the cast to the given ingredient
   * @param cast      Ingredient
   * @param consumed  If true, cast is consumed
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(Ingredient cast, boolean consumed) {
    this.cast = cast;
    this.consumed = consumed;
    return this;
  }

  /**
   * Set output of recipe to be put into the input slot.
   * Mostly used for cast creation
   */
  public MaterialCastingRecipeBuilder setSwitchSlots() {
    this.switchSlots = true;
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    this.save(consumer, Objects.requireNonNull(this.result.asItem().getRegistryName()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (this.itemCost <= 0) {
      throw new IllegalStateException("Material casting recipes require a positive amount of fluid");
    }
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "casting");
    consumer.accept(new Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public RecipeSerializer<?> getType() {
      return recipeSerializer;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      if (cast != Ingredient.EMPTY) {
        json.add("cast", cast.toJson());
        if (consumed) {
          json.addProperty("cast_consumed", true);
        }
      }
      if (switchSlots) {
        json.addProperty("switch_slots", true);
      }
      json.addProperty("item_cost", itemCost);
      json.addProperty("result", Objects.requireNonNull(result.asItem().getRegistryName()).toString());
    }
  }
}
