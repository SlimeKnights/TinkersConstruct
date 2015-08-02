package tconstruct.tools.client;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tconstruct.common.client.gui.GuiElement;
import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.library.Util;
import tconstruct.tools.TinkerMaterials;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.tileentity.TileToolStation;

@SideOnly(Side.CLIENT)
public class GuiToolStation extends GuiTinkerStation {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/toolstation.png");

  private static final GuiElement ItemCover = new GuiElement(176, 18, 80, 64);
  private static final GuiElement SlotBackground = new GuiElement(176, 0, 18, 18, 256, 256);
  private static final GuiElement SlotBorder = new GuiElement(194, 0, 18, 18);

  public GuiToolStation(InventoryPlayer playerInv, World world, BlockPos pos, TileToolStation tile) {
    super(world, pos, (ContainerMultiModule) tile.createContainer(playerInv, world, pos));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    ItemStack
        back =
        TinkerTools.pickaxe
            .buildItem(ImmutableList.of(TinkerMaterials.netherrack, TinkerMaterials.wood, TinkerMaterials.stone));

    int xOff = -1;
    int yOff = 0;

    int x = 0;
    int y = 0;

    // the slot backgrounds
    for(int i = 1; i <= 6; i++) {
      Slot slot = inventorySlots.getSlot(i);
      SlotBackground.draw(x + this.cornerX + slot.xDisplayPosition - 1, y + this.cornerY + slot.yDisplayPosition - 1);
    }


    // draw the item background
    GlStateManager.scale(4.0f, 4.0f, 1.0f);
    //renderItemIntoGuiBackground(back, (this.cornerX + 15) / 4 + xOff, (this.cornerY + 18) / 4 + yOff);
    itemRender.renderItemIntoGUI(back, (this.cornerX + 15) / 4 + xOff, (this.cornerY + 18) / 4 + yOff);
    GlStateManager.scale(0.25f, 0.25f, 1.0f);

    // rebind gui texture
    this.mc.getTextureManager().bindTexture(BACKGROUND);

    // reset state after item drawing
    GlStateManager.enableAlpha();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableDepth();

    // draw the halftransparent "cover" over the item
    GlStateManager.color(1.0f, 1.0f, 1.0f, 0.6f);
    ItemCover.draw(this.cornerX + 7, this.cornerY + 18);

    // full opaque. Draw the borders of the slots
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    for(int i = 1; i <= 6; i++) {
      Slot slot = inventorySlots.getSlot(i);
      SlotBorder.draw(
          x + this.cornerX + slot.xDisplayPosition - 1, y + this.cornerY + slot.yDisplayPosition - 1);
    }

    // continue as usual and hope that the drawing state is not completely wrecked
    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }

  private void drawSlotBackgrounds(int x, int y) {
    for(int i = 1; i <= 6; i++) {
      Slot slot = inventorySlots.getSlot(i);
      SlotBackground.draw(x + this.cornerX + slot.xDisplayPosition - 1, y + this.cornerY + slot.yDisplayPosition - 1);
    }
  }

  // Basically the same as RenderItem.renderItemIntoGUI except we control alpha
  private void renderItemIntoGuiBackground(ItemStack item, int x, int y) {
    IBakedModel ibakedmodel = itemRender.getItemModelMesher().getItemModel(item);
    GlStateManager.pushMatrix();
    this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
    this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
    GlStateManager.enableRescaleNormal();
    //GlStateManager.enableAlpha();
    //GlStateManager.alphaFunc(516, 0.1F);
    //GlStateManager.enableBlend();
    //GlStateManager.blendFunc(770, 771);
    // setupGuiTransform BEGIN
    //itemRender.setupGuiTransform(x, y, ibakedmodel.isGui3d());
    GlStateManager.translate((float)x, (float)y, 100.0F + this.zLevel);
    GlStateManager.translate(8.0F, 8.0F, 0.0F);
    GlStateManager.scale(1.0F, 1.0F, -1.0F);
    GlStateManager.scale(0.5F, 0.5F, 0.5F);

    GlStateManager.scale(64.0F, 64.0F, 64.0F);
    GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
    GlStateManager.disableLighting();
    // setupGuiTransform END
    ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GUI);
    GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
    itemRender.renderItem(item, ibakedmodel);
    //GlStateManager.disableAlpha();
    //GlStateManager.disableRescaleNormal();
    //GlStateManager.disableLighting();
    GlStateManager.popMatrix();
    // rebind GUI texture
    this.mc.getTextureManager().bindTexture(BACKGROUND);
  }
}
