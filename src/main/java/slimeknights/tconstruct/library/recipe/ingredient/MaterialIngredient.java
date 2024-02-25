package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Extension of the vanilla ingredient to display materials on items and support matching by materials
 * TODO: abstract ingredient
 */
public class MaterialIngredient extends Ingredient {
  /** Material ID meaning any material matches */
  private static final MaterialId WILDCARD = IMaterial.UNKNOWN.getIdentifier();

  private final MaterialVariantId material;
  @Nullable
  private ItemStack[] materialStacks;
  protected MaterialIngredient(Stream<? extends Ingredient.Value> itemLists, MaterialVariantId material) {
    super(itemLists);
    this.material = material;
  }

  /**
   * Creates a new instance from a set of items
   * @param item      Material item
   * @param material  Material ID
   * @return  Material ingredient instance
   */
  public static MaterialIngredient fromItem(IMaterialItem item, MaterialId material) {
    return new MaterialIngredient(Stream.of(new ItemValue(new ItemStack(item))), material);
  }

  /**
   * Creates a new ingredient matching any material from items
   * @param item  Material item
   * @return  Material ingredient instance
   */
  public static MaterialIngredient fromItem(IMaterialItem item) {
    return fromItem(item, WILDCARD);
  }

  /**
   * Creates a new ingredient from a tag
   * @param tag       Tag instance
   * @param material  Material value
   * @return  Material with tag
   */
  public static MaterialIngredient fromTag(TagKey<Item> tag, MaterialId material) {
    return new MaterialIngredient(Stream.of(new TagValue(tag)), material);
  }

  /**
   * Creates a new ingredient matching any material from a tag
   * @param tag       Tag instance
   * @return  Material with tag
   */
  public static MaterialIngredient fromTag(TagKey<Item> tag) {
    return fromTag(tag, WILDCARD);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    }
    // if material is not wildcard, must match materials
    if (!WILDCARD.equals(material) && !material.matchesVariant(stack)) {
      return false;
    }
    // otherwise fallback to base logic
    return super.test(stack);
  }

  @Override
  public ItemStack[] getItems() {
    if (materialStacks == null) {
      if (!MaterialRegistry.isFullyLoaded()) {
        return getPlainMatchingStacks();
      }
      // no material? apply all materials for variants
      Stream<ItemStack> items = Arrays.stream(getPlainMatchingStacks());
      if (material.equals(WILDCARD)) {
        items = items.flatMap(stack -> MaterialRegistry.getMaterials().stream()
                                                       .map(mat -> IMaterialItem.withMaterial(stack, mat.getIdentifier()))
                                                       .filter(ItemStack::hasTag));
      } else {
        // specific material? apply to all stacks
        items = items.map(stack -> IMaterialItem.withMaterial(stack, this.material)).filter(ItemStack::hasTag);
      }
      materialStacks = items.distinct().toArray(ItemStack[]::new);
    }
    return materialStacks;
  }

  /**
   * Gets the matching stacks without materials, used for syncing mainly
   * @return  Matching stacks with no materials
   */
  private ItemStack[] getPlainMatchingStacks() {
    return super.getItems();
  }

  @Override
  public JsonElement toJson() {
    JsonElement parent = super.toJson();
    if (!parent.isJsonObject()) {
      throw new JsonIOException("Cannot serialize an array of material ingredients, use CompoundIngredient instead");
    }
    JsonObject object = parent.getAsJsonObject();
    object.addProperty("type", Serializer.ID.toString());
    if (material != WILDCARD) {
      object.addProperty("material", material.toString());
    }
    return object;
  }

  @Override
  protected void invalidate() {
    super.invalidate();
    this.materialStacks = null;
  }

  @Override
  public boolean isSimple() {
    return material == WILDCARD;
  }

  @Override
  public IIngredientSerializer<? extends Ingredient> getSerializer() {
    return Serializer.INSTANCE;
  }

  /**
   * Serializer instance
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Serializer implements IIngredientSerializer<MaterialIngredient> {
    public static final ResourceLocation ID = TConstruct.getResource("material");
    public static final Serializer INSTANCE = new Serializer();

    @Override
    public MaterialIngredient parse(JsonObject json) {
      MaterialId material;
      if (json.has("material")) {
        material = new MaterialId(GsonHelper.getAsString(json, "material"));
      } else {
        material = WILDCARD;
      }
      return new MaterialIngredient(Stream.of(Ingredient.valueFromJson(json)), material);
    }

    @Override
    public MaterialIngredient parse(FriendlyByteBuf buffer) {
      MaterialVariantId material = Objects.requireNonNull(MaterialVariantId.tryParse(buffer.readUtf()));
      return new MaterialIngredient(Stream.generate(() -> new ItemValue(buffer.readItem())).limit(buffer.readVarInt()), material);
    }

    @Override
    public void write(FriendlyByteBuf buffer, MaterialIngredient ingredient) {
      // write first as the order of the stream is uncertain
      buffer.writeUtf(ingredient.material.toString());
      // write stacks
      ItemStack[] items = ingredient.getPlainMatchingStacks();
      buffer.writeVarInt(items.length);
      for (ItemStack stack : items) {
        buffer.writeItem(stack);
      }
    }
  }
}
