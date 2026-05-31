package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.compat.neoforged.neoforge.common.crafting.IIngredientSerializer;
import net.neoforged.neoforge.common.crafting.IngredientType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.Objects;

/** Ingredient matching an item with no container item, used to ensure NBT fluid items are empty */
public class NoContainerIngredient extends NestedIngredient {
  public static final ResourceLocation ID = TConstruct.getResource("no_container");

  protected NoContainerIngredient(Ingredient nested) {
    super(nested);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && super.test(stack) && !stack.hasCraftingRemainingItem();
  }

  @Override
  public boolean isSimple() {
    return false;
  }

  public JsonElement toJson() {
    JsonElement nestedElement = Ingredient.CODEC_NONEMPTY.encodeStart(JsonOps.INSTANCE, nested).getOrThrow(IllegalArgumentException::new);
    // if we are a vanilla ingredient, and not an array ingredient, serialize into the ingredient directly
    if (!nested.isCustom() && nestedElement.isJsonObject()) {
      JsonObject nestedObject = nestedElement.getAsJsonObject();
      nestedObject.addProperty("type", ID.toString());
      return nestedObject;
    }
    // if we have an array or a type, then serialize nested
    JsonObject json = JsonUtils.withType(ID);
    json.add("match", nestedElement);
    return json;
  }

  @Override
  public IngredientType<?> getType() {
    return TinkerCommons.noContainerIngredient.get();
  }

  @Override
  public boolean equals(Object object) {
    return this == object || object instanceof NoContainerIngredient that && nested.equals(that.nested);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nested);
  }

  public enum Serializer implements IIngredientSerializer<NoContainerIngredient> {
    INSTANCE;

    @Override
    public NoContainerIngredient parse(JsonObject json) {
      // if we have match, parse as a nested object. Without match, just parse the object as vanilla
      Ingredient ingredient;
      if (json.has("match")) {
        ingredient = Ingredient.CODEC_NONEMPTY.parse(JsonOps.INSTANCE, json.get("match")).getOrThrow(IllegalArgumentException::new);
      } else {
        JsonObject copy = json.deepCopy();
        copy.remove("type");
        ingredient = Ingredient.CODEC_NONEMPTY.parse(JsonOps.INSTANCE, copy).getOrThrow(IllegalArgumentException::new);
      }
      return new NoContainerIngredient(ingredient);
    }

    @Override
    public NoContainerIngredient parse(FriendlyByteBuf buffer) {
      return new NoContainerIngredient(Ingredient.CONTENTS_STREAM_CODEC.decode((RegistryFriendlyByteBuf)buffer));
    }

    @Override
    public void write(FriendlyByteBuf buffer, NoContainerIngredient ingredient) {
      Ingredient.CONTENTS_STREAM_CODEC.encode((RegistryFriendlyByteBuf)buffer, ingredient.nested);
    }
  }


  /* Static constructors */

  /** Creates an instance from the given nested ingredient */
  public static Ingredient of(Ingredient ingredient) {
    return new NoContainerIngredient(ingredient).toVanilla();
  }

  /** Creates an instance from the given items */
  public static Ingredient of(ItemLike... items) {
    return of(Ingredient.of(items));
  }

  /** Creates an instance from the given stacks */
  public static Ingredient of(ItemStack... stacks) {
    return of(Ingredient.of(stacks));
  }

  /** Creates an instance from the given tag */
  public static Ingredient of(TagKey<Item> tag) {
    return of(Ingredient.of(tag));
  }
}
