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

import static slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity.INPUT_SLOT;
import static slimeknights.tconstruct.tables.tileentity.table.tinkerstation.TinkerStationTileEntity.TINKER_SLOT;

public class TinkerStationInventoryWrapper implements IMutableTinkerStationInventory {
  private final TinkerStationTileEntity station;
  /** Cache of the material recipes found in each slot */
  private MaterialRecipe[] materials;
  /** Cache of whether each slot has been searched for a material */
  private boolean[] searchedMaterial;

  private MaterialRecipe lastMaterialRecipe;
  @Nullable
  private PlayerEntity player;

  /**
   * Creates a new wrapper instance for the station
   * @param station  Station instance
   */
  public TinkerStationInventoryWrapper(TinkerStationTileEntity station) {
    this.station = station;
    int count = station.getInputCount();
    this.materials = new MaterialRecipe[count];
    this.searchedMaterial = new boolean[count];
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
    if (slot >= INPUT_SLOT && slot < station.getInputCount() + INPUT_SLOT) {
      this.materials[slot - 1] = null;
      this.searchedMaterial[slot - 1] = false;
    }
  }

  /** Refreshes the size of this based on the size of the tinker station */
  public void resize() {
    int count = station.getInputCount();
    this.materials = new MaterialRecipe[count];
    this.searchedMaterial = new boolean[count];
  }

  @Override
  public ItemStack getTinkerableStack() {
    return this.station.getStackInSlot(TINKER_SLOT);
  }

  @Override
  public ItemStack getInput(int index) {
    if (index < 0 || index >= station.getInputCount()) {
      return ItemStack.EMPTY;
    }
    return this.station.getStackInSlot(index + TinkerStationTileEntity.INPUT_SLOT);
  }

  @Override
  public int getInputCount() {
    return station.getInputCount();
  }

  @Nullable
  @Override
  public MaterialRecipe getInputMaterial(int index) {
    if (index < 0 || index >= station.getInputCount()) {
      return null;
    }
    if (!searchedMaterial[index]) {
      materials[index] = findMaterialRecipe(getInput(index));
      searchedMaterial[index] = true;
    }
    return materials[index];
  }

  @Override
  public void setInput(int index, ItemStack stack) {
    if (index >= 0 && index < station.getInputCount()) {
      this.station.setInventorySlotContents(index + TinkerStationTileEntity.INPUT_SLOT, stack);
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
