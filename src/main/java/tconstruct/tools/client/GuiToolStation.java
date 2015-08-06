package tconstruct.tools.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.util.Point;

import tconstruct.common.client.gui.GuiElement;
import tconstruct.common.client.gui.GuiModule;
import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.library.Util;
import tconstruct.library.client.CustomTextureCreator;
import tconstruct.library.client.ToolBuildGuiInfo;
import tconstruct.library.mantle.RecipeMatch;
import tconstruct.library.tinkering.MaterialItem;
import tconstruct.library.tinkering.PartMaterialType;
import tconstruct.library.tinkering.TinkersItem;
import tconstruct.library.tools.IToolPart;
import tconstruct.tools.client.module.GuiButtonsToolStation;
import tconstruct.tools.client.module.GuiInfoPanel;
import tconstruct.tools.client.module.GuiSideButtons;
import tconstruct.tools.tileentity.TileToolStation;

@SideOnly(Side.CLIENT)
public class GuiToolStation extends GuiTinkerStation {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/toolstation.png");

  private static final GuiElement ItemCover = new GuiElement(176, 18, 80, 64, 256, 256);
  private static final GuiElement SlotBackground = new GuiElement(176, 0, 18, 18);
  private static final GuiElement SlotBorder = new GuiElement(194, 0, 18, 18);

  private static final GuiElement SlotSpace = new GuiElement(0, 174, 18, 4);
  private static final GuiElement SlotSpaceTop = new GuiElement(0, 174+2, 18, 2);
  private static final GuiElement SlotSpaceBottom = new GuiElement(0, 174, 18, 2);

  private static final int Table_slot_count = 6;

  protected GuiElement buttonDecoration = SlotSpace;
  protected GuiElement buttonDecorationTop = SlotSpaceTop;
  protected GuiElement buttonDecorationBot = SlotSpaceBottom;

  protected GuiButtonsToolStation buttons;
  protected int activeSlots; // how many of the available slots are active

  protected GuiInfoPanel toolInfo;
  protected GuiInfoPanel traitInfo;

  protected ToolBuildGuiInfo currentInfo;



  public GuiToolStation(InventoryPlayer playerInv, World world, BlockPos pos, TileToolStation tile) {
    super(world, pos, (ContainerMultiModule) tile.createContainer(playerInv, world, pos));

    buttons = new GuiButtonsToolStation(this, inventorySlots);
    this.addModule(buttons);
    toolInfo = new GuiInfoPanel(this, inventorySlots);
    this.addModule(toolInfo);
    traitInfo = new GuiInfoPanel(this, inventorySlots);
    this.addModule(traitInfo);

    toolInfo.yOffset = 5;
    traitInfo.yOffset = toolInfo.getYSize() + 9;

    this.ySize = 174;

    wood();
  }

  @Override
  public void initGui() {
    super.initGui();

    // workaround to line up the tabs on switching even though the GUI is a tad higher
    this.guiTop += 4;
    this.cornerY += 4;

    for(GuiModule module : modules) {
      module.guiTop += 4;
    }
  }

  public void onToolSelection(ToolBuildGuiInfo info) {
    activeSlots = Math.min(info.positions.size(), Table_slot_count);
    currentInfo = info;

    int i;
    for(i = 0; i < activeSlots; i++) {
      Point point = info.positions.get(i);

      Slot slot = inventorySlots.getSlot(i);
      slot.xDisplayPosition = point.getX();
      slot.yDisplayPosition = point.getY();
    }

    // remaining slots
    int stillFilled = 0;
    for(; i < Table_slot_count; i++) {
      Slot slot = inventorySlots.getSlot(i);
      if(slot.getHasStack()) {
        slot.xDisplayPosition = 87 + 20 * stillFilled;
        slot.yDisplayPosition = 62;
        stillFilled++;
      }
      else {
        // todo: slot.disable
        slot.xDisplayPosition = 0;
        slot.yDisplayPosition = 0;
      }
    }

    toolInfo.setText(new String[]{"Tool name", "Desc1",
                                  "This is a long desc with lorem ipsum blabla bla bla bla bla bla blabla lba bal bal balb al abl abla blablablablabal bla bla balbal bal ba laballbalbalbalalalb laballab mrgrhlomlbl amlm",
                                  "foobar"});
    traitInfo.setText(new String[]{"Traits", "Awesome",
                                   "This is a long desc with lorem ipsum blabla bla bla bla bla bla blabla lba bal bal balb al abl abla blablablablabal bla bla balbal bal ba laballbalbalbalalalb laballab mrgrhlomlbl amlm",
                                   "foobar"});
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    int xOff = 0;
    int yOff = 0;

    int x = 0;
    int y = 0;

    // draw the item background
    final float scale = 4.0f;
    GlStateManager.scale(scale, scale, 1.0f);
    //renderItemIntoGuiBackground(back, (this.cornerX + 15) / 4 + xOff, (this.cornerY + 18) / 4 + yOff);
    {
      int logoX = (this.cornerX + 10) / 4 + xOff;
      int logoY = (this.cornerY + 18) / 4 + yOff;

      if(currentInfo != null) {
        if(currentInfo.tool != null) {
          itemRender.renderItemIntoGUI(currentInfo.tool, logoX, logoY);
        }
        else if(currentInfo == GuiButtonRepair.info) {
          this.mc.getTextureManager().bindTexture(ICONS);
          ICON_Anvil.draw(logoX, logoY);
        }
      }
    }
    GlStateManager.scale(1f / scale, 1f / scale, 1.0f);

    // rebind gui texture
    this.mc.getTextureManager().bindTexture(BACKGROUND);

    // reset state after item drawing
    GlStateManager.enableBlend();
    GlStateManager.enableAlpha();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableDepth();

    // draw the halftransparent "cover" over the item
    GlStateManager.color(1.0f, 1.0f, 1.0f, 0.82f);
    ItemCover.draw(this.cornerX + 7, this.cornerY + 18);

    // the slot backgrounds
    GlStateManager.color(1.0f, 1.0f, 1.0f, 0.28f);
    for(int i = 0; i < activeSlots; i++) {
      Slot slot = inventorySlots.getSlot(i);
      SlotBackground.draw(x + this.cornerX + slot.xDisplayPosition - 1, y + this.cornerY + slot.yDisplayPosition - 1);
    }

    // full opaque. Draw the borders of the slots
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    for(int i = 0; i < activeSlots; i++) {
      Slot slot = inventorySlots.getSlot(i);
      SlotBorder.draw(
          x + this.cornerX + slot.xDisplayPosition - 1, y + this.cornerY + slot.yDisplayPosition - 1);
    }

    this.mc.getTextureManager().bindTexture(ICONS);

    // slot logos
    if(currentInfo == GuiButtonRepair.info) {
      drawRepairSlotIcons();
    }
    else if(currentInfo.tool != null && currentInfo.tool.getItem() instanceof TinkersItem) {
      PartMaterialType[] pmts = ((TinkersItem) currentInfo.tool.getItem()).requiredComponents;
      for(int i = 0; i < activeSlots; i++) {
        if(i >= pmts.length) {
          continue;
        }

        IToolPart part = pmts[i].getPossibleParts().iterator().next();
        if(!(part instanceof MaterialItem)) {
          continue;
        }

        ItemStack stack = ((MaterialItem) part).getItemstackWithMaterial(CustomTextureCreator.guiMaterial);
        Slot slot = inventorySlots.getSlot(i);
        itemRender.renderItemIntoGUI(stack, x + this.cornerX + slot.xDisplayPosition, y + this.cornerY + slot.yDisplayPosition);
      }
    }

    GlStateManager.enableDepth();

    // continue as usual and hope that the drawing state is not completely wrecked
    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }

  protected void drawRepairSlotIcons() {
    for(int i = 0; i < activeSlots; i++) {
      drawRepairSlotIcon(i);
    }
  }

  protected void drawRepairSlotIcon(int i) {
    GuiElement icon = null;
    Slot slot = inventorySlots.getSlot(i);

    if(i == 0) {
      icon = ICON_Pickaxe;
    }
    else if(i == 1) {
      icon = ICON_Dust;
    }
    else if(i == 2) {
      icon = ICON_Lapis;
    }
    else if(i == 3) {
      icon = ICON_Ingot;
    }
    else if(i == 4) {
      icon = ICON_Gem;
    }
    else if(i == 5) {
      icon = ICON_Quartz;
    }

    if(icon != null) {
      drawIcon(slot, icon);
    }
  }

  protected void wood() {
    toolInfo.wood();
    traitInfo.wood();

    buttonDecoration = SlotSpace.shift(SlotSpace.w,0);
    buttonDecorationTop = SlotSpaceTop.shift(SlotSpace.w,0);
    buttonDecorationBot = SlotSpaceBottom.shift(SlotSpace.w,0);

    buttons.wood();
  }

  protected void metal() {
    toolInfo.metal();
    traitInfo.metal();

    buttonDecoration = SlotSpace.shift(SlotSpace.w*2,0);
    buttonDecorationTop = SlotSpaceTop.shift(SlotSpace.w*2,0);
    buttonDecorationBot = SlotSpaceBottom.shift(SlotSpace.w*2,0);

    buttons.metal();
  }
}
