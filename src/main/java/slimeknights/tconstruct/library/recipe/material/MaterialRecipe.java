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
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.container.ISingleStackContainer;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Recipe to get the material from an ingredient
 */
public class MaterialRecipe implements ICustomOutputRecipe<ISingleStackContainer> {
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

  /** Durability restored per item input, lazy loaded */
  @Nullable
  private Float repairPerItem;

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
    this.leftover = leftover;
  }

  /* Basic */

  @Override
  public RecipeType<?> getType() {
    return RecipeTypes.MATERIAL;
  }

  @Override
  public ItemStack getToastSymbol() {
    return new ItemStack(TinkerTables.partBuilder);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.materialRecipeSerializer.get();
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
   * Gets the amount of material present in the inventory as a float for display
   * @param inv  Inventory reference
   * @return  Number of material present as a float
   */
  public float getMaterialValue(ISingleStackContainer inv) {
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
   * @param data     Tool defintion data for fallback
   * @param statsId  Preferred stats ID, if null no preference
   * @return  Float amount per item to repair
   */
  public float getRepairPerItem(ToolDefinitionData data, @Nullable MaterialStatsId statsId) {
    if (repairPerItem == null) {
      // multiply by recipe value (iron block is 9x), divide by needed (nuggets need 9), divide again by ingots per repair
      repairPerItem = this.getValue() * getRepairDurability(data, material.getId(), statsId) / INGOTS_PER_REPAIR / this.getNeeded();
    }
    return repairPerItem;
  }

  /**
   * Gets the head durability for the given material
   * @param toolData      Stats fallback for missing tool materials
   * @param materialId    Material
   * @param statsId       Stats to use for repair, if null uses the first found stats with durability
   * @return  Head durability
   */
  public static int getRepairDurability(ToolDefinitionData toolData, MaterialId materialId, @Nullable MaterialStatsId statsId) {
    Optional<IMaterialStats> optional;
    if (statsId != null) {
      // if given an ID, use that stat type
      optional = MaterialRegistry.getInstance().getMaterialStats(materialId, statsId).filter(stats -> stats instanceof IRepairableMaterialStats);
    } else {
      // if no ID given, just find the first repairable stats
      optional = MaterialRegistry.getInstance().getAllStats(materialId).stream().filter(stats -> stats instanceof IRepairableMaterialStats).findFirst();
    }
    return optional.map(stats -> ((IRepairableMaterialStats)stats).getDurability()).orElseGet(() -> toolData.getBaseStat(ToolStats.DURABILITY).intValue());
  }

  /**
   * Gets a copy of the leftover stack for this recipe
   * @return  Leftover stack
   */
  public ItemStack getLeftover() {
    return this.leftover.get().copy();
  }
}
