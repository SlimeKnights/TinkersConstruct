package slimeknights.tconstruct.tools.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tools.client.module.GuiGeneric;
import slimeknights.tconstruct.tools.inventory.ContainerPatternChest;
import slimeknights.tconstruct.tools.inventory.ContainerTinkerStation;
import slimeknights.tconstruct.tools.tileentity.TilePatternChest;

@SideOnly(Side.CLIENT)
public class GuiPatternChest extends GuiTinkerStation {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/blank.png");

  protected static final GuiElementScalable background = GuiGeneric.slotEmpty;

  public GuiDynInventory guiInventory;

  public GuiPatternChest(InventoryPlayer playerInv, World world, BlockPos pos, TilePatternChest tile) {
    super(world, pos, (ContainerTinkerStation)tile.createContainer(playerInv, world, pos));

    // we use the sideinventory class for the inventory itself
    // it doesn't contain the player inventory
    guiInventory = new GuiDynInventory(this, container.getSubContainer(ContainerPatternChest.SideInventory.class));
    addModule(guiInventory);
    //guiInventory.setPosition(16, 16);
    //guiInventory.setSize(162, 54);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
