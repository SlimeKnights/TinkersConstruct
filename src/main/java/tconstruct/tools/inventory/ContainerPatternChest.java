package tconstruct.tools.inventory;

import net.minecraft.entity.player.InventoryPlayer;

import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.common.inventory.SlotRestrictedItem;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.tileentity.TilePatternChest;

public class ContainerPatternChest extends ContainerMultiModule<TilePatternChest> {

  public ContainerPatternChest(int rows, int columns, InventoryPlayer playerInventory, TilePatternChest tile) {
    super(tile);

    int index = 0;

    // chest inventory
    for(int i = 0; i < rows; ++i) {
      for(int j = 0; j < columns; ++j) {
        // safety
        if(index > tile.getSizeInventory()) {
          break;
        }

        this.addSlotToContainer(new SlotRestrictedItem(TinkerTools.pattern, tile, index, 8 + j * 18, 18 + i * 18));
        index++;
      }
    }

    // player inventory
    addPlayerInventory(playerInventory, 17, 86);
  }
}
