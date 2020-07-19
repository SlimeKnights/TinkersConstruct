package slimeknights.tconstruct.tables.inventory.table.toolstation;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.library.recipe.inventory.InventoryIterator;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.toolstation.IToolStationInventory;
import slimeknights.tconstruct.tables.tileentity.table.ToolStationTileEntity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ToolStationInventoryWrapper implements IToolStationInventory {

  private final ToolStationTileEntity toolStation;
  private final World world;
  private final Iterable<ItemStack> inputStacks;

  public ToolStationInventoryWrapper(ToolStationTileEntity toolStation) {
    this.toolStation = toolStation;
    this.world = toolStation.getWorld();
    this.inputStacks = () -> new InventoryIterator(toolStation, 0, 5);
  }

  @Override
  public Iterable<ItemStack> getInputStacks() {
    return this.inputStacks;
  }

  @Override
  public ItemStack getToolStack() {
    return this.toolStation.getStackInSlot(ToolStationTileEntity.TOOL_SLOT);
  }

  @Override
  public int getSizeInventory() {
    return this.toolStation.getSizeInventory();
  }

  @Override
  public boolean isEmpty() {
    for (int i = 0; i < this.toolStation.getSizeInventory(); i++) {
      if (!this.toolStation.getStackInSlot(i).isEmpty()) {
        return false;
      }
    }

    return true;
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    return index >= 0 && index < this.toolStation.getSizeInventory() ? this.toolStation.getStackInSlot(index) : ItemStack.EMPTY;
  }
}
