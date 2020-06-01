package slimeknights.tconstruct.tables.tileentity.table;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.tables.inventory.table.PartBuilderContainer;
import slimeknights.tconstruct.tables.inventory.table.crafting.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.TableTileEntity;
import slimeknights.tconstruct.tileentities.TablesTileEntities;

import javax.annotation.Nullable;

public class PartBuilderTileEntity extends TableTileEntity {

  public PartBuilderTileEntity() {
    super(TablesTileEntities.part_builder, "gui.tconstruct.part_builder", 4);
    this.itemHandler = new ConfigurableInvWrapperCapability(this, false, false);
    this.itemHandlerCap = LazyOptional.of(() -> this.itemHandler);
  }

  @Nullable
  @Override
  public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new PartBuilderContainer(menuId, playerInventory, this);
  }
}
