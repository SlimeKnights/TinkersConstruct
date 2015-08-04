package tconstruct.tools.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.library.Util;
import tconstruct.tools.client.module.GuiSideInventory;
import tconstruct.tools.inventory.ContainerPartBuilder;
import tconstruct.tools.inventory.ContainerPatternChest;
import tconstruct.tools.inventory.ContainerSideInventory;
import tconstruct.tools.tileentity.TilePartBuilder;

@SideOnly(Side.CLIENT)
public class GuiPartBuilder extends GuiTinkerStation {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/partbuilder.png");

  public GuiPartBuilder(InventoryPlayer playerInv, World world, BlockPos pos, TilePartBuilder tile) {
    super(world, pos, (ContainerMultiModule)tile.createContainer(playerInv, world, pos));

    if(inventorySlots instanceof ContainerPartBuilder) {
      ContainerPartBuilder container = (ContainerPartBuilder) inventorySlots;
      ContainerSideInventory chestContainer = container.getSubContainer(ContainerPatternChest.SideInventory.class);
      if(chestContainer != null) {
        this.addModule(new GuiSideInventory(this, chestContainer, chestContainer.getSlotCount(), chestContainer.columns));
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
