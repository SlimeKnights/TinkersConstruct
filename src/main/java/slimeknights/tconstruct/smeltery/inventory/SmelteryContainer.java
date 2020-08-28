package slimeknights.tconstruct.smeltery.inventory;

import lombok.Getter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import slimeknights.mantle.inventory.MultiModuleContainer;
import slimeknights.tconstruct.library.utils.IntArrayWrapper;
import slimeknights.tconstruct.library.utils.LambdaIntReference;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;

import javax.annotation.Nullable;

public class SmelteryContainer extends MultiModuleContainer<SmelteryTileEntity> {
  @Getter
  protected SideInventoryContainer<SmelteryTileEntity> smelteryInventory;

  public SmelteryContainer(int id, @Nullable PlayerInventory inv, @Nullable SmelteryTileEntity smeltery) {
    super(TinkerSmeltery.smelteryContainer.get(), id, inv, smeltery);

    // create slots
    if (smeltery != null) {
      this.smelteryInventory = new SideInventory(TinkerSmeltery.smelteryContainer.get(), id, inv, smeltery, 8, 18, 3);
      this.addSubContainer(smelteryInventory, true);
      this.addInventorySlots();
      this.trackInt(new LambdaIntReference(smeltery::getFuel, smeltery::setFuel));
      this.trackInt(new LambdaIntReference(smeltery::getTemperature, smeltery::setTemperature));
      this.trackIntArray(new IntArrayWrapper(smeltery::getItemTemperatures));
      this.trackIntArray(new IntArrayWrapper(smeltery::getItemTempRequired));
    } else {
      smelteryInventory = null;
    }
  }

  public SmelteryContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, SmelteryTileEntity.class));
  }

  public static class SideInventory extends SideInventoryContainer<SmelteryTileEntity> {
    public SideInventory(ContainerType<?> type, int id, PlayerInventory inv, @Nullable SmelteryTileEntity tile, int x, int y, int columns) {
      super(type, id, inv, tile, x, y, columns);
    }

    @Override
    protected Slot createSlot(IItemHandler itemHandler, int index, int x, int y) {
      return new SmelteryControllerSlot(itemHandler, index, x, y);
    }
  }

  public static class SmelteryControllerSlot extends SlotItemHandler {
    public SmelteryControllerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
      super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return true;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
      return 1;
    }
  }
}
