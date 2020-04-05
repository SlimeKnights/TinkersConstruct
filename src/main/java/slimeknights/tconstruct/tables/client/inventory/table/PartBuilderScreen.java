package slimeknights.tconstruct.tables.client.inventory.table;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.inventory.table.PartBuilderContainer;
import slimeknights.tconstruct.tables.inventory.table.crafting.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

public class PartBuilderScreen extends TinkerStationScreen<PartBuilderTileEntity, TinkerStationContainer<PartBuilderTileEntity>> {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/partbuilder.png");

  public static final int COLUMN_COUNT = 4;

  public PartBuilderScreen(TinkerStationContainer<PartBuilderTileEntity> container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    if(this.container instanceof PartBuilderContainer) {

    }
  }

  public void updateButtons() {
    if(this.buttons != null) {
      Minecraft.getInstance().execute(() -> buttons.updatePosition(cornerX, cornerY, realWidth, realHeight))
    }
  }
}
