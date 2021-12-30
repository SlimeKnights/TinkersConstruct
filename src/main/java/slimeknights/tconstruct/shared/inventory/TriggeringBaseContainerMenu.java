package slimeknights.tconstruct.shared.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.inventory.BaseContainerMenu;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;

/** Container that triggers the criteria instance */
public class TriggeringBaseContainerMenu<TILE extends BlockEntity> extends BaseContainerMenu<TILE> {
  protected TriggeringBaseContainerMenu(MenuType<?> type, int id, @Nullable Inventory inv, @Nullable TILE tile) {
    super(type, id, inv, tile);
    TinkerCommons.CONTAINER_OPENED_TRIGGER.trigger(tile, inv);
  }
}
