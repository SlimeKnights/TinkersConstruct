package slimeknights.tconstruct.shared.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;

/** Container that triggers the criteria instance */
public class TriggeringBaseContainer<TILE extends TileEntity> extends BaseContainer<TILE> {
  protected TriggeringBaseContainer(ContainerType<?> type, int id, @Nullable PlayerInventory inv, @Nullable TILE tile) {
    super(type, id, inv, tile);
    TinkerCommons.CONTAINER_OPENED_TRIGGER.trigger(tile, inv);
  }
}
