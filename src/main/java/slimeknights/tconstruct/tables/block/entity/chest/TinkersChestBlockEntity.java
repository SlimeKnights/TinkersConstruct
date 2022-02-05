package slimeknights.tconstruct.tables.block.entity.chest;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.entity.inventory.IChestItemHandler;

import javax.annotation.Nullable;

/**
 * Chest holding 64 slots of 16 items each
 */
public class TinkersChestBlockEntity extends AbstractChestBlockEntity {
  /** NBT tag for colors of the chest */
  public static final String TAG_CHEST_COLOR = "color";
  /** Default color for a chest */
  public static final int DEFAULT_COLOR = 0x407686;
  public static final Component NAME = TConstruct.makeTranslation("gui", "tinkers_chest");

  /** Current display color for the chest */
  @Getter
  private int color = DEFAULT_COLOR;
  /** If true, a custom color was set */
  @Getter @Accessors(fluent = true)
  private boolean hasColor = false;

  public TinkersChestBlockEntity(BlockPos pos, BlockState state) {
    super(TinkerTables.tinkersChestTile.get(), pos, state, NAME, new TinkersChestItemHandler());
  }

  /** Sets the color of the chest */
  public void setColor(int color) {
    this.color = color;
    this.hasColor = true;
  }

  @Override
  public boolean canInsert(Player player, ItemStack heldItem) {
    return false;
  }

  @Override
  public void saveSynced(CompoundTag tags) {
    super.saveSynced(tags);
    if (hasColor) {
      tags.putInt(TAG_CHEST_COLOR, color);
    }
  }

  @Override
  public void load(CompoundTag tags) {
    super.load(tags);
    if (tags.contains(TAG_CHEST_COLOR, Tag.TAG_ANY_NUMERIC)) {
      setColor(tags.getInt(TAG_CHEST_COLOR));
    }
  }

  /** Item handler for tinkers chests */
  public static class TinkersChestItemHandler extends ItemStackHandler implements IChestItemHandler {
    @Setter @Nullable
    private MantleBlockEntity parent;
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

    @Override
    protected void onContentsChanged(int slot) {
      if (parent != null) {
        parent.setChangedFast();
      }
    }
  }
}
