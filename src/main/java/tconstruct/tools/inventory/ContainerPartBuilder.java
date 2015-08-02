package tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.tools.tileentity.TilePartBuilder;
import tconstruct.tools.tileentity.TilePatternChest;

public class ContainerPartBuilder extends ContainerMultiModule<TilePartBuilder> {

  public ContainerPartBuilder(InventoryPlayer playerInventory, TilePartBuilder tile) {
    super(tile);

    this.addPlayerInventory(playerInventory, 8, 84);

    TilePatternChest chest = detectTE(TilePatternChest.class);
    // TE present?
    if(chest != null) {
      Container sideInventory = new ContainerPatternChest.SideInventory(chest, chest, -6, 8, 6);
      addSubContainer(sideInventory);
    }
  }
}
