package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Extension of the vanilla ingredient to display materials on items and support matching by materials
 */
public class MaterialIngredient {
  /** Material ID meaning any material matches */
  private static final MaterialId WILDCARD = IMaterial.UNKNOWN.getIdentifier();

  private final Ingredient ingredient;

  private final MaterialId materialID;
  @Nullable
  private ItemStack[] materialStacks;
  protected MaterialIngredient(Stream<? extends Ingredient.Entry> itemLists, MaterialId material) {
    ingredient = new Ingredient(itemLists);
    this.materialID = material;
  }

  public Ingredient getIngredient() {
    return ingredient;
  }


//  public static MaterialIngredient fromItem(IMaterialItem item, MaterialId material) {
//    return new MaterialIngredient(Stream.of(new StackEntry(new ItemStack(item))), material);
//  }


//  public static MaterialIngredient fromItem(IMaterialItem item) {
//    return fromItem(item, WILDCARD);
//  }

//  public static MaterialIngredient fromTag(Tag<Item> tag, MaterialId material) {
//    return new MaterialIngredient(Stream.of(new TagEntry(tag)), material);
//  }

//  public static MaterialIngredient fromTag(Tag<Item> tag) {
//    return fromTag(tag, WILDCARD);
//  }

  public boolean test(@Nullable ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    }
    // if material is not wildcard, must match materials
    if (!WILDCARD.equals(materialID) && !materialID.equals(IMaterialItem.getMaterialFromStack(stack).getIdentifier())) {
      return false;
    }
    // otherwise fallback to base logic
    return ingredient.test(stack);
  }

  public ItemStack[] getMatchingStacksClient() {
    if (materialStacks == null) {
      // no material? apply all materials for variants
      Stream<ItemStack> items = Arrays.stream(getPlainMatchingStacks());
      if (materialID == WILDCARD) {
        items = items.flatMap(stack -> MaterialRegistry.getMaterials().stream()
                                                       .map(mat -> IMaterialItem.withMaterial(stack, mat))
                                                       .filter(ItemStack::hasTag));
      } else {
        // specific material? apply to all stacks
        IMaterial material = MaterialRegistry.getMaterial(this.materialID);
        items = items.map(stack -> IMaterialItem.withMaterial(stack, material));
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
    return ingredient.getMatchingStacksClient();
  }

//  @Override
  public JsonElement toJson() {
    JsonElement parent = ingredient.toJson();
    if (!parent.isJsonObject()) {
      throw new JsonIOException("Cannot serialize an array of material ingredients, use CompoundIngredient instead");
    }
    JsonObject object = parent.getAsJsonObject();
    throw new RuntimeException("CRAB!");
    //FIXME: PORT
//    object.addProperty("type", Serializer.ID.toString());
//    if (materialID != WILDCARD) {
//      object.addProperty("material", materialID.toString());
//    }
//    return object;
  }

  /**
   * Serializer instance
   */
//  public static class Serializer implements IIngredientSerializer<MaterialIngredient> {
//    public static final Identifier ID = Util.getResource("material");
//    public static final Serializer INSTANCE = new Serializer();
//
//    private Serializer() {
//    }
//
//    @Override
//    public MaterialIngredient parse(JsonObject json) {
//      MaterialId material;
//      if (json.has("material")) {
//        material = new MaterialId(JsonHelper.getString(json, "material"));
//      } else {
//        material = WILDCARD;
//      }
//      return new MaterialIngredient(Stream.of(Ingredient.entryFromJson(json)), material);
//    }
//
//    @Override
//    public MaterialIngredient parse(PacketByteBuf buffer) {
//      MaterialId material = new MaterialId(buffer.readIdentifier());
//      return new MaterialIngredient(Stream.generate(() -> new StackEntry(buffer.readItemStack())).limit(buffer.readVarInt()), material);
//    }
//
//    @Override
//    public void write(PacketByteBuf buffer, MaterialIngredient ingredient) {
//      // write first as the order of the stream is uncertain
//      buffer.writeIdentifier(ingredient.materialID);
//      // write stacks
//      ItemStack[] items = ingredient.getPlainMatchingStacks();
//      buffer.writeVarInt(items.length);
//      for (ItemStack stack : items) {
//        buffer.writeItemStack(stack);
//      }
//    }
//  }
}
