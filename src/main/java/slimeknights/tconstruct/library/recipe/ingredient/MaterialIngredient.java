package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Extension of the vanilla ingredient to display materials on items and support matching by materials
 */
public class MaterialIngredient extends NestedIngredient {
  /** Material ID meaning any material matches */
  private static final MaterialId WILDCARD = IMaterial.UNKNOWN.getIdentifier();

  private final MaterialVariantId material;
  @Nullable
  private final TagKey<IMaterial> tag;
  @Nullable
  private ItemStack[] materialStacks;
  protected MaterialIngredient(Ingredient nested, MaterialVariantId material, @Nullable TagKey<IMaterial> tag) {
    super(nested);
    this.material = material;
    this.tag = tag;
  }

  /** Creates an ingredient matching a single material */
  public static MaterialIngredient of(Ingredient ingredient, MaterialVariantId material) {
    return new MaterialIngredient(ingredient, material, null);
  }

  /** Creates an ingredient matching a material tag */
  public static MaterialIngredient of(Ingredient ingredient, TagKey<IMaterial> tag) {
    return new MaterialIngredient(ingredient, WILDCARD, tag);
  }

  /**
   * Creates a new instance from an item with a fixed material
   * @param item      Material item
   * @param material  Material ID
   * @return  Material ingredient instance
   */
  public static MaterialIngredient of(ItemLike item, MaterialVariantId material) {
    return of(Ingredient.of(item), material);
  }

  /**
   * Creates a new instance from an item with a tagged material
   * @param item      Material item
   * @param tag   Material tag
   * @return  Material ingredient instance
   */
  public static MaterialIngredient of(ItemLike item, TagKey<IMaterial> tag) {
    return of(Ingredient.of(item), tag);
  }

  /**
   * Creates a new ingredient matching any material from items
   * @param item  Material item
   * @return  Material ingredient instance
   */
  public static MaterialIngredient of(ItemLike item) {
    return of(item, WILDCARD);
  }

  /**
   * Creates a new ingredient from a tag
   * @param tag       Tag instance
   * @param material  Material value
   * @return  Material with tag
   */
  public static MaterialIngredient of(TagKey<Item> tag, MaterialVariantId material) {
    return of(Ingredient.of(tag), material);
  }

  /**
   * Creates a new ingredient matching any material from a tag
   * @param tag       Tag instance
   * @return  Material with tag
   */
  public static MaterialIngredient of(TagKey<Item> tag) {
    return of(tag, WILDCARD);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    // check super first, should be faster
    if (stack == null || stack.isEmpty() || !super.test(stack)) {
      return false;
    }
    // if material is not wildcard, must match materials
    boolean hasMaterial = !WILDCARD.equals(material);
    if (hasMaterial || tag != null) {
      // check fixed material match
      MaterialVariantId stackMaterial = IMaterialItem.getMaterialFromStack(stack);
      if (hasMaterial && !material.matchesVariant(stackMaterial)) {
        return false;
      }
      // check material tag match
      return tag == null || MaterialRegistry.getInstance().isInTag(stackMaterial.getId(), tag);
    }
    return true;
  }

  @Override
  public ItemStack[] getItems() {
    if (materialStacks == null) {
      if (!MaterialRegistry.isFullyLoaded()) {
        return nested.getItems();
      }
      // no material? apply all materials for variants
      Stream<ItemStack> items = Arrays.stream(nested.getItems());
      // if we have a specific material, use that
      if (!material.equals(WILDCARD)) {
        items = items.map(stack -> IMaterialItem.withMaterial(stack, this.material)).filter(ItemStack::hasTag);
      } else {
        // if we have a tag, filter values, else get all values
        Collection<IMaterial> materials = tag != null ? MaterialRegistry.getInstance().getTagValues(tag) : MaterialRegistry.getMaterials();
        items = items.flatMap(stack -> materials.stream()
                                                .map(mat -> IMaterialItem.withMaterial(stack, mat.getIdentifier()))
                                                .filter(ItemStack::hasTag));
      }
      materialStacks = items.distinct().toArray(ItemStack[]::new);
    }
    return materialStacks;
  }

  @Override
  public JsonElement toJson() {
    JsonElement parent = nested.toJson();
    JsonObject result;
    if (nested.isVanilla() && parent.isJsonObject()) {
      result = parent.getAsJsonObject();
    } else {
      result = new JsonObject();
      result.add("match", parent);
    }
    result.addProperty("type", Serializer.ID.toString());
    Serializer.MATERIAL_FIELD.serialize(this, result);
    Serializer.TAG_FIELD.serialize(this, result);
    return result;
  }

  @Override
  protected void invalidate() {
    super.invalidate();
    this.materialStacks = null;
  }

  @Override
  public boolean isSimple() {
    return material == WILDCARD && tag == null;
  }

  @Override
  public IIngredientSerializer<? extends Ingredient> getSerializer() {
    return Serializer.INSTANCE;
  }

  /** Serializer instance */
  public enum Serializer implements IIngredientSerializer<MaterialIngredient> {
    INSTANCE;
    public static final ResourceLocation ID = TConstruct.getResource("material");
    private static final LoadableField<MaterialVariantId,MaterialIngredient> MATERIAL_FIELD = MaterialVariantId.LOADABLE.defaultField("material", WILDCARD, i -> i.material);
    private static final LoadableField<TagKey<IMaterial>,MaterialIngredient> TAG_FIELD = TinkerLoadables.MATERIAL_TAGS.nullableField("material_tag", i -> i.tag);

    @Override
    public MaterialIngredient parse(JsonObject json) {
      // if we have match, parse as a nested object. Without match, just parse the object as vanilla
      Ingredient ingredient;
      if (json.has("match")) {
        ingredient = CraftingHelper.getIngredient(json.get("match"), false);
      } else {
        ingredient = VanillaIngredientSerializer.INSTANCE.parse(json);
      }
      return new MaterialIngredient(ingredient, MATERIAL_FIELD.get(json), TAG_FIELD.get(json));
    }

    @Override
    public MaterialIngredient parse(FriendlyByteBuf buffer) {
      return new MaterialIngredient(
        Ingredient.fromNetwork(buffer),
        MATERIAL_FIELD.decode(buffer),
        TAG_FIELD.decode(buffer)
      );
    }

    @Override
    public void write(FriendlyByteBuf buffer, MaterialIngredient ingredient) {
      ingredient.nested.toNetwork(buffer);
      MATERIAL_FIELD.encode(buffer, ingredient);
      TAG_FIELD.encode(buffer, ingredient);
    }
  }
}
