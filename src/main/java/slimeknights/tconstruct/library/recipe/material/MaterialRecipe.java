package slimeknights.tconstruct.library.recipe.material;

import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.container.ISingleStackContainer;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe to get the material from an ingredient
 */
public class MaterialRecipe implements ICustomOutputRecipe<ISingleStackContainer>, IMaterialValue {
  /** Empty material instance for the cache */
  public static final MaterialRecipe EMPTY = new MaterialRecipe(new ResourceLocation("missingno"), "", Ingredient.EMPTY, 0, 0, IMaterial.UNKNOWN_ID, ItemOutput.EMPTY);
  public static final RecordLoadable<MaterialRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP,
    IngredientLoadable.DISALLOW_EMPTY.requiredField("ingredient", MaterialRecipe::getIngredient),
    IMaterialValue.VALUE_FIELD,
    IMaterialValue.NEEDED_FIELD,
    MaterialVariantId.LOADABLE.requiredField("material", r -> r.getMaterial().getVariant()),
    ItemOutput.Loadable.OPTIONAL_STACK.emptyField("leftover", r -> r.leftover),
    MaterialRecipe::new);

  /** Vanilla requires 4 ingots for full repair, we drop it down to 3 to mesh better with nuggets and blocks and to fit small head costs better */
  public static final float INGOTS_PER_REPAIR = 3f;

  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final Ingredient ingredient;
  /** Amount of material this recipe returns */
  @Getter
  protected final int value;
  /** Amount of input items needed to craft this material */
  @Getter
  protected final int needed;
  /** Material ID for the recipe return */
  @Getter
  protected final MaterialVariant material;
  /** Leftover stack of value 1, used if the value is more than 1 */
  protected final ItemOutput leftover;

  /**
   * Creates a new material recipe
   */
  @SuppressWarnings("WeakerAccess")
  public MaterialRecipe(ResourceLocation id, String group, Ingredient ingredient, int value, int needed, MaterialVariantId materialId, ItemOutput leftover) {
    this.id = id;
    this.group = group;
    this.ingredient = ingredient;
    this.value = value;
    this.needed = needed;
    this.material = MaterialVariant.of(materialId);
    // ignore leftover if the value is 1, its useless to us
    this.leftover = value > 1 ? leftover : ItemOutput.EMPTY;

    // save recipe into the cache
    MaterialRecipeCache.registerRecipe(this);
  }

  /* Basic */

  @Override
  public RecipeType<?> getType() {
    return TinkerRecipeTypes.MATERIAL.get();
  }

  @Override
  public ItemStack getToastSymbol() {
    return new ItemStack(TinkerTables.partBuilder);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.materialRecipeSerializer.get();
  }

  @Override
  public boolean hasLeftover() {
    return !this.leftover.isEmpty();
  }

  @Override
  public ItemStack getLeftover() {
    return this.leftover.get().copy();
  }

  /* Material methods */

  @Override
  public boolean matches(ISingleStackContainer inv, Level worldIn) {
    return !material.isUnknown() && this.ingredient.test(inv.getStack());
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.of(Ingredient.EMPTY, ingredient);
  }

  /** Cache of the display items list */
  private List<ItemStack> displayItems = null;

  /** Gets a list of stacks for display in the recipe */
  public List<ItemStack> getDisplayItems() {
    if (displayItems == null) {
      if (needed > 1) {
        displayItems = Arrays.stream(ingredient.getItems())
                             .map(stack -> ItemHandlerHelper.copyStackWithSize(stack, needed))
                             .collect(Collectors.toList());
      } else {
        displayItems = Arrays.asList(ingredient.getItems());
      }
    }
    return displayItems;
  }

  /**
   * Gets the amount to repair per item for tool repair
   * @param amount  Base material amount, typically the head durability stat
   * @return  Float amount per item to repair
   */
  public float scaleRepair(float amount) {
    // not cached as it may vary per stat type
    return this.getValue() * amount / INGOTS_PER_REPAIR / this.getNeeded();
  }
}
