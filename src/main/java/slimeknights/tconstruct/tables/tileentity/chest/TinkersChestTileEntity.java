package slimeknights.tconstruct.tables.tileentity.chest;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.ItemStackHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.client.inventory.library.IScalingInventory;

/**
 * Chest holding 64 slots of 16 items each
 */
public class TinkersChestTileEntity extends ChestTileEntity {
  /** NBT tag for colors of the chest */
  public static final String TAG_CHEST_COLOR = "color";
  /** Default color for a chest */
  public static final int DEFAULT_COLOR = 0x407686;

  /** Current display color for the chest */
  @Getter
  private int color = DEFAULT_COLOR;
  /** If true, a custom color was set */
  @Getter @Accessors(fluent = true)
  private boolean hasColor = false;

  public TinkersChestTileEntity() {
    super(TinkerTables.tinkersChestTile.get(), TConstruct.makeTranslationKey("gui", "tinkers_chest"), new TinkersChestItemHandler());
  }

  /** Sets the color of the chest */
  public void setColor(int color) {
    this.color = color;
    this.hasColor = true;
  }

  @Override
  public boolean canInsert(PlayerEntity player, ItemStack heldItem) {
    return false;
  }

  @Override
  public void writeSynced(CompoundNBT tags) {
    super.writeSynced(tags);
    if (hasColor) {
      tags.putInt(TAG_CHEST_COLOR, color);
    }
  }

  @Override
  public void read(BlockState blockState, CompoundNBT tags) {
    super.read(blockState, tags);
    if (tags.contains(TAG_CHEST_COLOR, NBT.TAG_ANY_NUMERIC)) {
      setColor(tags.getInt(TAG_CHEST_COLOR));
    }
  }

  /** Item handler for tinkers chests */
  public static class TinkersChestItemHandler extends ItemStackHandler implements IScalingInventory {
    public TinkersChestItemHandler() {
      super(64);
    }

    @Override
    public int getSlotLimit(int slot) {
      return 16;
    }

    @Override
    public int getVisualSize() {
      return getSlots();
    }
  }
}
