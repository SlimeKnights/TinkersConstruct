package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Ingredient matching material items with the given value. Typically, matches ingots or blocks
 */
@Getter
@RequiredArgsConstructor
public class MaterialValueIngredient extends AbstractIngredient {
  private final MaterialStatsId statType;
  private final float minValue;
  private final float maxValue;
  private ItemStack[] items;

  /** Creates an ingredient matching a range of values */
  public static MaterialValueIngredient of(MaterialStatsId statType, float minValue, float maxValue) {
    return new MaterialValueIngredient(statType, minValue, maxValue);
  }

  /** Creates an ingredient matching an exact value */
  public static MaterialValueIngredient of(MaterialStatsId statType, float value) {
    return of(statType, value, value);
  }

  /** Checks the given material recipe against our filters */
  public boolean test(MaterialRecipe material) {
    float value = material.getValue() / (float) material.getNeeded();
    return minValue <= value && value <= maxValue && statType.canUseMaterial(material.getMaterial().getId());
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null) {
      return false;
    }
    MaterialRecipe recipe = MaterialRecipeCache.findRecipe(stack);
    return recipe != MaterialRecipe.EMPTY && test(recipe);
  }

  @Override
  public ItemStack[] getItems() {
    if (items == null) {
      items = MaterialRecipeCache.getAllRecipes().stream()
        .filter(this::test)
        .flatMap(material -> Arrays.stream(material.getIngredient().getItems()))
        .toArray(ItemStack[]::new);
    }
    return items;
  }

  @Override
  protected void invalidate() {
    super.invalidate();
    this.items = null;
  }

  @Override
  public boolean isSimple() {
    return true;
  }


  /* Helpers for ShapedMaterialRecipe */

  /** Checks if this ingredient fully contains the range of the other */
  private boolean contains(MaterialValueIngredient other) {
    return this.minValue <= other.minValue && other.maxValue <= this.maxValue;
  }

  /** Creates an ingredient that matches anything either of the two ingredients matches */
  @Nullable
  public MaterialValueIngredient merge(MaterialValueIngredient other) {
    if (!this.statType.equals(other.statType)) {
      return null;
    }
    if (this.contains(other)) {
      return this;
    }
    if (other.contains(this)) {
      return other;
    }
    return new MaterialValueIngredient(statType, Math.min(this.minValue, other.minValue), Math.max(this.maxValue, other.maxValue));
  }

  /** Gets the material matching this recipe */
  @Nullable
  public MaterialVariantId getMaterial(ItemStack stack) {
    MaterialRecipe recipe = MaterialRecipeCache.findRecipe(stack);
    return recipe != MaterialRecipe.EMPTY && test(recipe) ? recipe.getMaterial().getVariant() : null;
  }


  /* JSON */

  @Override
  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", Serializer.ID.toString());
    Serializer.STAT_TYPE_FIELD.serialize(this, json);
    if (minValue == maxValue) {
      json.addProperty("value", minValue);
    } else {
      JsonObject value = new JsonObject();
      if (minValue > 0) {
        value.addProperty("min", minValue);
      }
      if (Float.isFinite(maxValue)) {
        value.addProperty("max", maxValue);
      }
      json.add("value", value);
    }
    return json;
  }

  @Override
  public IIngredientSerializer<? extends Ingredient> getSerializer() {
    return Serializer.INSTANCE;
  }


  /** Serializer instance */
  public enum Serializer implements IIngredientSerializer<MaterialValueIngredient> {
    INSTANCE;
    public static final ResourceLocation ID = TConstruct.getResource("material_value");
    private static final LoadableField<MaterialStatsId, MaterialValueIngredient> STAT_TYPE_FIELD = MaterialStatsId.PARSER.requiredField("stat_type", i -> i.statType);

    @Override
    public MaterialValueIngredient parse(JsonObject json) {
      MaterialStatsId statType = STAT_TYPE_FIELD.get(json);
      float minValue, maxValue;
      JsonElement value = json.get("value");
      if (value.isJsonPrimitive()) {
        minValue = maxValue = value.getAsJsonPrimitive().getAsFloat();
      } else {
        JsonObject object = GsonHelper.convertToJsonObject(value, "value");
        minValue = GsonHelper.getAsFloat(object, "min", 0);
        maxValue = GsonHelper.getAsFloat(object, "max", Float.POSITIVE_INFINITY);
      }
      return new MaterialValueIngredient(statType, minValue, maxValue);
    }

    @Override
    public MaterialValueIngredient parse(FriendlyByteBuf buffer) {
      return new MaterialValueIngredient(
        MaterialStatsId.PARSER.decode(buffer),
        buffer.readFloat(),
        buffer.readFloat()
      );
    }

    @Override
    public void write(FriendlyByteBuf buffer, MaterialValueIngredient ingredient) {
      STAT_TYPE_FIELD.encode(buffer, ingredient);
      buffer.writeFloat(ingredient.minValue);
      buffer.writeFloat(ingredient.maxValue);
    }
  }
}
