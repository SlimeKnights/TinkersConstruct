package slimeknights.tconstruct.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.items.ToolItems;
import slimeknights.tconstruct.items.ToolParts;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;

public class ToolDebugScreen extends ContainerScreen<ToolDebugContainer> {

  public ToolDebugScreen(ToolDebugContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
  }

  @Override
  protected void init() {
    super.init();

    addButton(new Button(guiLeft, guiTop, 20, 20, "test", button -> {
      ItemStack st = ToolParts.pickaxe_head.get().getItemstackWithMaterial(MaterialRegistry.getMaterial(new MaterialId("tconstruct:stone")));
      ItemStack st2 = ToolParts.tool_rod.get().getItemstackWithMaterial(MaterialRegistry.getMaterial(new MaterialId("tconstruct:wood")));
      ItemStack st3 = ToolParts.small_binding.get().getItemstackWithMaterial(MaterialRegistry.getMaterial(new MaterialId("tconstruct:ardite")));
      // TODO: Get rid of cast
      ItemStack itemStack = ToolBuildHandler.buildItemFromStacks(NonNullList.from(ItemStack.EMPTY, st, st2, st3), ToolItems.pickaxe.get());
      container.inventory.setInventorySlotContents(0, itemStack);
    }));
  }

  @Override
  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    this.renderBackground();
    super.render(p_render_1_, p_render_2_, p_render_3_);
    this.renderHoveredToolTip(p_render_1_, p_render_2_);
    //GlStateManager.disableLighting();
    //GlStateManager.disableBlend();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.minecraft.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/anvil.png"));
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    this.blit(i, j, 0, 0, this.xSize, this.ySize);
  }

}
