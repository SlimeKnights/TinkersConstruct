package slimeknights.tconstruct.library.recipe.material;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import org.jetbrains.annotations.Nullable;
import java.util.Optional;

/**
 * Recipe to get the material from an ingredient
 */
public class MaterialRecipe implements ICustomOutputRecipe<ISingleItemInventory> {
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  protected final Ingredient ingredient;
  /** Amount of material this recipe returns */
  @Getter
  protected final int value;
  /** Amount of input items needed to craft this material */
  @Getter
  protected final int needed;
  /** Material ID for the recipe return */
  protected final MaterialId materialId;
  /** Material returned by this recipe, lazy loaded */
  private final LazyValue<IMaterial> material;
  /** Durability restored per item input, lazy loaded */
  @Nullable
  private Float repairPerItem;

  /**
   * Creates a new material recipe
   */
  @SuppressWarnings("WeakerAccess")
  public MaterialRecipe(ResourceLocation id, String group, Ingredient ingredient, int value, int needed, MaterialId materialId) {
    this.id = id;
    this.group = group;
    this.ingredient = ingredient;
    this.value = value;
    this.needed = needed;
    this.materialId = materialId;
    this.material = new LazyValue<>(() -> MaterialRegistry.getMaterial(materialId));
  }

  /* Basic */

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.MATERIAL;
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(TinkerTables.partBuilder);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.materialRecipeSerializer.get();
  }

  /* Material methods */

  @Override
  public boolean matches(ISingleItemInventory inv, World worldIn) {
    return this.ingredient.test(inv.getStack());
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(Ingredient.EMPTY, ingredient);
  }

  /**
   * Returns a material instance for this recipe
   * @return  Material for the recipe
   */
  public IMaterial getMaterial() {
    return material.getValue();
  }

  /**
   * Gets the amount of material present in the inventory as a float for display
   * @param inv  Inventory reference
   * @return  Number of material present as a float
   */
  public float getMaterialValue(ISingleItemInventory inv) {
    return inv.getStack().getCount() * this.value / (float)this.needed;
  }

  /**
   * Gets the number of items in order to craft a material with the given cost
   * @param itemCost  Cost of the item being crafted
   * @return  Number of the input to consume
   */
  public int getItemsUsed(int itemCost) {
    int needed = itemCost * this.needed;
    int cost = needed / this.value;
    if (needed % this.value != 0) {
      cost++;
    }
    return cost;
  }

  /**
   * Gets the number of leftover material from crafting a part with this material
   * @param itemCost  Cost of the item being crafted
   * @return  Number of input to consume
   */
  public int getRemainder(int itemCost) {
    return itemCost * this.needed % this.value;
  }

  /**
   * Gets the amount to repair per item for tool repair
   * @return  Float amount per item to repair
   */
  public float getRepairPerItem() {
    if (repairPerItem == null) {
      // total tool durability
      Optional<HeadMaterialStats> stats = MaterialRegistry.getInstance().getMaterialStats(materialId, HeadMaterialStats.ID);
      int durabilityPerUnit = stats.map(HeadMaterialStats::getDurability).orElse(0);
      // multiply by recipe value (iron block is 9x), divide by needed (nuggets need 9), divide again by 4 (vanilla ingots restore 25%)
      repairPerItem = this.getValue() * durabilityPerUnit / 4f / this.getNeeded();
    }
    return repairPerItem;
  }
}
