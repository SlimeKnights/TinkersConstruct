package slimeknights.tconstruct.tables.tileentity.table.tinkerstation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;

import javax.annotation.Nullable;
import java.util.Optional;

public class TinkerStationInventoryWrapper implements IMutableTinkerStationInventory {
  private static final int INPUT_COUNT = 5;

  private final TinkerStationTileEntity station;
  private final MaterialRecipe[] materials = new MaterialRecipe[INPUT_COUNT];
  private final boolean[] foundMaterial = new boolean[INPUT_COUNT];

  private MaterialRecipe lastMaterialRecipe;
  @Nullable
  private PlayerEntity player;

  /**
   * Creates a new wrapper instance for the station
   * @param station  Station instance
   */
  public TinkerStationInventoryWrapper(TinkerStationTileEntity station) {
    this.station = station;
  }

  /**
   * Finds a material recipe for the given slot
   * @param stack  Stack in slot
   * @return  Material recipe found, or null if missing
   */
  @Nullable
  private MaterialRecipe findMaterialRecipe(ItemStack stack) {
    // must have world
    World world = station.getWorld();
    if (world == null) {
      return null;
    }
    // try last recipe
    ISingleItemInventory inv = () -> stack;
    if (lastMaterialRecipe != null && lastMaterialRecipe.matches(inv, world)) {
      return lastMaterialRecipe;
    }
    // try to find a new recipe
    Optional<MaterialRecipe> newRecipe = world.getRecipeManager().getRecipe(RecipeTypes.MATERIAL, inv, world);
    if (newRecipe.isPresent()) {
      lastMaterialRecipe = newRecipe.get();
      return lastMaterialRecipe;
    }
    // if none found, return null
    return null;
  }

  /**
   * Clears the cached inputs
   */
  public void refreshInput(int slot) {
    if (slot >= 0 && slot < INPUT_COUNT) {
      this.materials[slot] = null;
      this.foundMaterial[slot] = false;
    }
  }

  @Override
  public ItemStack getTinkerableStack() {
    return this.station.getStackInSlot(TinkerStationTileEntity.TINKER_SLOT);
  }

  @Override
  public ItemStack getInput(int index) {
    if (index < 0 || index >= INPUT_COUNT) {
      return ItemStack.EMPTY;
    }
    return this.station.getStackInSlot(index);
  }

  @Override
  public int getInputCount() {
    return INPUT_COUNT;
  }

  @Nullable
  @Override
  public MaterialRecipe getInputMaterial(int index) {
    if (index < 0 || index >= INPUT_COUNT) {
      return null;
    }
    if (!foundMaterial[index]) {
      materials[index] = findMaterialRecipe(getInput(index));
      foundMaterial[index] = true;
    }
    return materials[index];
  }

  @Override
  public void setInput(int index, ItemStack stack) {
    if (index >= 0 && index < INPUT_COUNT) {
      this.station.setInventorySlotContents(index, stack);
    }
  }

  @Override
  public void giveItem(ItemStack stack) {
    if (player != null && !player.inventory.addItemStackToInventory(stack)) {
      player.dropItem(stack, false);
    }
  }

  /**
   * Updates the current player of this inventory
   * @param player  Player
   */
  public void setPlayer(@Nullable PlayerEntity player) {
    this.player = player;
  }
}
