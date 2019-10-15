package slimeknights.tconstruct.gadgets;

import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class WoodenHopperGUIDrawEvent {

  private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation(TConstruct.modID, "textures/gui/hopper.png");

  @SubscribeEvent
  public static void addTooltip(ItemTooltipEvent itemTooltipEvent) {
    if( itemTooltipEvent.getItemStack().getItem() == Item.getItemFromBlock(TinkerGadgets.woodenHopper)) {
      itemTooltipEvent.getToolTip().add(Util.translate("item.tconstruct.wooden_hopper.tooltip"));
    }
  }

  @SubscribeEvent
  public static void onWoodenHopperDrawGui(GuiOpenEvent guiOpenEvent) {
    if(guiOpenEvent.getGui() instanceof GuiHopper) {
      GuiHopper gui = (GuiHopper) guiOpenEvent.getGui();
      if(gui.hopperInventory.getSizeInventory() == 1) {
        guiOpenEvent.setGui(new GuiWoodenHopper((InventoryPlayer) gui.playerInventory, gui.hopperInventory));
      }
    }
  }

  @SideOnly(Side.CLIENT)
  private static class GuiWoodenHopper extends GuiHopper {

    public GuiWoodenHopper(InventoryPlayer playerInv, IInventory hopperInv) {
      super(playerInv, hopperInv);

      inventorySlots.getSlot(0).xPos += 18*2;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
  }
}
