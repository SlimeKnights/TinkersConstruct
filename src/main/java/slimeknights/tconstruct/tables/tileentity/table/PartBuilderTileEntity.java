package slimeknights.tconstruct.tables.tileentity.table;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.table.partbuilder.PartBuilderContainer;

public class PartBuilderTileEntity extends RetexturedTableTileEntity implements ExtendedScreenHandlerFactory {
  public static final int MATERIAL_SLOT = 0;
  public static final int PATTERN_SLOT = 1;
  public static final int LEFTOVER_SLOT = 2;

  public PartBuilderTileEntity() {
    super(TinkerTables.partBuilderTile, "gui.tconstruct.part_builder", 3);
//    this.itemHandler = new ConfigurableInvWrapperCapability(this, false, false);
//    this.itemHandlerCap = Optional.of(() -> this.itemHandler);
  }

  @Nullable
  @Override
  public ScreenHandler createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new PartBuilderContainer(menuId, playerInventory, this);
  }

  @Override
  public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
    buf.writeBlockPos(getPos());
  }
}
