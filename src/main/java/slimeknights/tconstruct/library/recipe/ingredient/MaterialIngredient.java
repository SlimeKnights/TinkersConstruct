package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Extension of the vanilla ingredient to display materials on items and support matching by materials
 */
public class MaterialIngredient extends Ingredient {
  /** Material ID meaning any material matches */
  private static final MaterialId WILDCARD = IMaterial.UNKNOWN.getIdentifier();

  private final MaterialId materialID;
  @Nullable
  private ItemStack[] materialStacks;
  protected MaterialIngredient(Stream<? extends Ingredient.IItemList> itemLists, MaterialId material) {
    super(itemLists);
    this.materialID = material;
  }

  /**
   * Creates a new instance from a set of items
   * @param item      Material item
   * @param material  Material ID
   * @return  Material ingredient instance
   */
  public static MaterialIngredient fromItem(IMaterialItem item, MaterialId material) {
    return new MaterialIngredient(Stream.of(new SingleItemList(new ItemStack(item))), material);
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
  public static MaterialIngredient fromTag(ITag<Item> tag, MaterialId material) {
    return new MaterialIngredient(Stream.of(new TagList(tag)), material);
  }

  /**
   * Creates a new ingredient matching any material from a tag
   * @param tag       Tag instance
   * @return  Material with tag
   */
  public static MaterialIngredient fromTag(ITag<Item> tag) {
    return fromTag(tag, WILDCARD);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    }
    // if material is not wildcard, must match materials
    if (!WILDCARD.equals(materialID) && !materialID.equals(IMaterialItem.getMaterialFromStack(stack).getIdentifier())) {
      return false;
    }
    // otherwise fallback to base logic
    return super.test(stack);
  }

  @Override
  public ItemStack[] getMatchingStacks() {
    if (materialStacks == null) {
      if (!MaterialRegistry.isFullyLoaded()) {
        return getPlainMatchingStacks();
      }
      // no material? apply all materials for variants
      Stream<ItemStack> items = Arrays.stream(getPlainMatchingStacks());
      if (materialID.equals(WILDCARD)) {
        items = items.flatMap(stack -> MaterialRegistry.getMaterials().stream()
                                                       .map(mat -> IMaterialItem.withMaterial(stack, mat))
                                                       .filter(ItemStack::hasTag));
      } else {
        // specific material? apply to all stacks
        IMaterial material = MaterialRegistry.getMaterial(this.materialID);
        items = items.map(stack -> IMaterialItem.withMaterial(stack, material)).filter(ItemStack::hasTag);
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
    return super.getMatchingStacks();
  }

  @Override
  public JsonElement serialize() {
    JsonElement parent = super.serialize();
    if (!parent.isJsonObject()) {
      throw new JsonIOException("Cannot serialize an array of material ingredients, use CompoundIngredient instead");
    }
    JsonObject object = parent.getAsJsonObject();
    object.addProperty("type", Serializer.ID.toString());
    if (materialID != WILDCARD) {
      object.addProperty("material", materialID.toString());
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
    return materialID == WILDCARD && super.isSimple();
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
    public static final ResourceLocation ID = Util.getResource("material");
    public static final Serializer INSTANCE = new Serializer();

    @Override
    public MaterialIngredient parse(JsonObject json) {
      MaterialId material;
      if (json.has("material")) {
        material = new MaterialId(JSONUtils.getString(json, "material"));
      } else {
        material = WILDCARD;
      }
      return new MaterialIngredient(Stream.of(Ingredient.deserializeItemList(json)), material);
    }

    @Override
    public MaterialIngredient parse(PacketBuffer buffer) {
      MaterialId material = new MaterialId(buffer.readResourceLocation());
      return new MaterialIngredient(Stream.generate(() -> new SingleItemList(buffer.readItemStack())).limit(buffer.readVarInt()), material);
    }

    @Override
    public void write(PacketBuffer buffer, MaterialIngredient ingredient) {
      // write first as the order of the stream is uncertain
      buffer.writeResourceLocation(ingredient.materialID);
      // write stacks
      ItemStack[] items = ingredient.getPlainMatchingStacks();
      buffer.writeVarInt(items.length);
      for (ItemStack stack : items) {
        buffer.writeItemStack(stack);
      }
    }
  }
}
