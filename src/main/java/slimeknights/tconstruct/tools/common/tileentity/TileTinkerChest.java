package slimeknights.tconstruct.tools.common.tileentity;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootContext.Builder;
import slimeknights.tconstruct.library.loot.ChestLootTable;
import slimeknights.tconstruct.shared.tileentity.TileTable;

// The pattern and part chests
// Technically not tables, but we reuse its code minus the "its legs are block X"
public class TileTinkerChest extends TileTable {

  public static final int MAX_INVENTORY = 256;
  // how big the 'perceived' inventory is
  public int actualSize;

  public TileTinkerChest(String name) {
    this(name, MAX_INVENTORY);
  }

  public TileTinkerChest(String name, int inventorySize) {
    super(name, inventorySize);
    this.actualSize = 1;
  }

  public TileTinkerChest(String name, int inventorySize, int maxStackSize) {
    super(name, inventorySize, maxStackSize);
    this.actualSize = 1;
  }

  @Override
  public int getSizeInventory() {
    return actualSize;
  }

  @Override
  public void readInventoryFromNBT(NBTTagCompound tag) {
    // we need to set it to max because the loading code uses getSizeInventory and we want to load all stacks
    actualSize = MAX_INVENTORY;
    super.readInventoryFromNBT(tag);

    // recalculate actual size from inventory:
    // decrease until it matches
    while(actualSize > 0 && getStackInSlot(actualSize - 1).isEmpty()) {
      actualSize--;
    }
    actualSize++; // add empty slot
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    // adjustment from possible external stuff (looking at you there, hoppers >:()
    if(slot > actualSize && !itemstack.isEmpty()) {
      actualSize = slot + 1;
    }

    // non-null and gets put into the last slot?
    if(slot == actualSize - 1 && !itemstack.isEmpty() && itemstack.getCount() > 0) {
      // expand slots until the last visible slot is empty (could be something was in there through faulty state)
      do {
        actualSize++;
      }
      while(!getStackInSlot(actualSize - 1).isEmpty());
    }
    // null, gets taken from the slot before the last visible slot?
    else if(slot >= actualSize - 2 && itemstack.isEmpty()) {
      // decrease inventory size so that 1 free slot after the last non-empty slot is left
      while(actualSize - 2 >= 0 && getStackInSlot(actualSize - 2).isEmpty()) {
        actualSize--;
      }
    }

    // actually put the thing in/out
    super.setInventorySlotContents(slot, itemstack);
    //      doResize(slot, getSizeInventory()+1);
  }

  @Override
  public void fillWithLootFromTable(@Nullable EntityPlayer player) {
    if(this.lootTable != null && this.getWorld() instanceof WorldServer) {
      LootTable loottable = this.getWorld().getLootTableManager().getLootTableFromLocation(this.lootTable);
      this.lootTable = null;
      Random random;

      if(this.lootTableSeed == 0L) {
        random = new Random();
      }
      else {
        random = new Random(this.lootTableSeed);
      }

      Builder builder = new Builder((WorldServer) this.getWorld());

      if(player != null) {
        builder.withLuck(player.getLuck());
      }

      List<ItemStack> list = loottable.generateLootForPools(random, builder.build());

      actualSize = list.size();

      ChestLootTable newLootTable = new ChestLootTable(list);

      newLootTable.fillInventory(this, random, builder.build());
    }
  }
}
