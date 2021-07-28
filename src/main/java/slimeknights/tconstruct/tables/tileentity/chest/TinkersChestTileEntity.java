package slimeknights.tconstruct.tables.tileentity.chest;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.TinkerTables;

public class TinkersChestTileEntity extends ChestTileEntity {
  public static final String TAG_CHEST_COLOR = "color";
  public static final int DEFAULT_COLOR = 0x407686;

  @Getter
  private int color = DEFAULT_COLOR;
  @Getter @Accessors(fluent = true)
  private boolean hasColor = false;
  public TinkersChestTileEntity() {
    // max 64 stacks for the modifier chest
    super(TinkerTables.tinkersChestTile.get(), TConstruct.makeTranslationKey("gui", "tinkers_chest"), 64, 16);
  }

  public void setColor(int color) {
    this.color = color;
    this.hasColor = true;
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    // no duplicate limit, this chest just limits total slots
    return true;
  }

  @Override
  public int getVisualSize() {
    // always show all slots
    return getSizeInventory();
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
}
