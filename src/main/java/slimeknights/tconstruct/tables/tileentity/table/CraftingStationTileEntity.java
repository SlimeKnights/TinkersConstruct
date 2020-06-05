package slimeknights.tconstruct.tables.tileentity.table;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.tables.inventory.table.crafting.CraftingStationContainer;
import slimeknights.tconstruct.tables.inventory.table.crafting.CraftingStationItemHandler;
import slimeknights.tconstruct.tables.tileentity.TableTileEntity;
import slimeknights.tconstruct.tileentities.TablesTileEntities;

import javax.annotation.Nullable;

public class CraftingStationTileEntity extends TableTileEntity {

  public CraftingStationTileEntity() {
    super(TablesTileEntities.crafting_station.get(), "gui.tconstruct.crafting_station", 9);
    this.itemHandler = new CraftingStationItemHandler(this, true, false);
    this.itemHandlerCap = LazyOptional.of(() -> this.itemHandler);
  }

  @Nullable
  @Override
  public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new CraftingStationContainer(menuId, playerInventory, this);
  }
}
