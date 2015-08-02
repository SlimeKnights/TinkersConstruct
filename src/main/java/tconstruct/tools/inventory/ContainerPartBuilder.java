package tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.tools.tileentity.TilePartBuilder;

public class ContainerPartBuilder extends ContainerMultiModule<TilePartBuilder> {

  public ContainerPartBuilder(InventoryPlayer playerInventory, TilePartBuilder tile) {
    super(tile);

    this.addPlayerInventory(playerInventory, 8, 84);
  }
}
