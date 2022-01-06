package tconstruct.tools.gui;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.library.util.HarvestLevels;
import tconstruct.tools.logic.CraftingStationLogic;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class CraftingStationGui extends GuiContainer implements INEIGuiHandler {
    /*
     * Slider/slots related.  Taken & adapted from Tinkers Construct 1.12 under the MIT License
     */
    private static final ResourceLocation gui_inventory = new ResourceLocation("tinker", "textures/gui/generic.png");
    
    public static final GuiElementScalable slotElement = new GuiElementScalable(7, 7, 18, 18, 64, 64);
    public static final GuiElementScalable slotEmptyElement = new GuiElementScalable(7 + 18, 7, 18, 18, 64, 64);

    private static final GuiElementDuex sliderNormal = new GuiElementDuex(7, 25, 10, 15, 64, 64);
    private static final GuiElementDuex sliderLow = new GuiElementDuex(17, 25, 10, 15, 64, 64);
    private static final GuiElementDuex sliderHigh = new GuiElementDuex(27, 25, 10, 15, 64, 64);
    private static final GuiElementDuex sliderTop = new GuiElementDuex(43, 7, 12, 1, 64, 64);
    private static final GuiElementDuex sliderBottom = new GuiElementDuex(43, 38, 12, 1, 64, 64);
    private static final GuiElementScalable sliderBackground = new GuiElementScalable(43, 8, 12, 30, 64, 64);
    private static final GuiElementScalable textBackground = new GuiElementScalable(7 + 18, 7, 18, 10, 64, 64);

    private final GuiSliderWidget slider = new GuiSliderWidget(sliderNormal, sliderHigh, sliderLow, sliderTop, sliderBottom, sliderBackground);
    private final GuiBorderWidget border = new GuiBorderWidget();

    private int firstSlotId;
    private int lastSlotId;
    private int chestSlotCount;

    /* end slider/slots */
    
    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/tinkertable.png");
    private static final ResourceLocation description = new ResourceLocation("tinker", "textures/gui/description.png");
    private static final ResourceLocation icons = new ResourceLocation("tinker", "textures/gui/icons.png");

    public boolean active;

    // Panel positions
    public String toolName;
    public GuiTextField text;
    public String title, body = "";
    CraftingStationLogic logic;
    
    private int craftingLeft = 0;
    private int craftingTop = 0;
    private int craftingTextLeft = 0;
    private int descLeft = 0;
    private int descTop = 0;
    private int descTextLeft = 0;
    
    private int chestLeft = 0;
    private int chestTop = 0;
    private int chestWidth = 0;
    private int chestHeight = 0;
    
    public CraftingStationGui(InventoryPlayer inventory, CraftingStationLogic logic, World world, int x, int y, int z) {
        super(logic.getGuiContainer(inventory, world, x, y, z));
        this.logic = logic;

        title = "\u00A7n" + StatCollector.translateToLocal("gui.toolforge1");
        body = StatCollector.translateToLocal("gui.toolforge2");
        toolName = "";
    }

    @Override
    public void initGui() {
        super.initGui();

        this.xSize = 176;
        this.ySize = 166;

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.craftingLeft = this.guiLeft;
        this.craftingTop = this.guiTop;

        if (logic.tinkerTable) {
            this.descLeft = this.guiLeft + 176;
            this.descTop = this.craftingTop;
        }

        if (logic.chest != null) {
            updateChest();
        } else {
            slider.hide();
        }

        this.craftingTextLeft = this.craftingLeft - this.guiLeft;
        this.descTextLeft = this.descLeft - this.guiLeft;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        if (logic.chest != null) {
            if (logic.chest.get() instanceof TileEntity) {
                TileEntity te = (TileEntity) logic.chest.get();
                if (te == null || te.getWorldObj().getTileEntity(te.xCoord, te.yCoord, te.zCoord) == null && te.getWorldObj().isRemote) {
                    mc.thePlayer.closeScreen();
                    return;
                }
            }
            this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.chest.get().getInventoryName()), 8, 6, 0x202020);
        }

        this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.tinkerTable ? "crafters.TinkerTable" : logic.getInvName()), craftingTextLeft + 8, 6, 0x202020);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), craftingTextLeft + 8, 72, 0x202020);

        if (logic.tinkerTable) {
            if (logic.isStackInSlot(0)) // output slot = modified item
                drawToolStats(logic.getStackInSlot(0));
            else if (logic.isStackInSlot(5)) { // center slot if no output item
                // other slots empty?
                if (!logic.isStackInSlot(1) && !logic.isStackInSlot(2) && !logic.isStackInSlot(3) && !logic.isStackInSlot(4)
                    && !logic.isStackInSlot(6) && !logic.isStackInSlot(7) && !logic.isStackInSlot(8) && !logic.isStackInSlot(9))
                    drawToolStats(logic.getStackInSlot(5));
                else
                    drawToolInformation();
            } else
                drawToolInformation();
        }
    }

    void drawToolStats(ItemStack stack) {
        if (stack.getItem() instanceof IModifyable)
            ToolStationGuiHelper.drawToolStats(stack, descTextLeft + 10, 0);

        int matID = PatternBuilder.instance.getPartID(stack);

        if (matID != Short.MAX_VALUE && matID > 0) {
            ToolMaterial material = TConstructRegistry.getMaterial(matID);

            if (material != null)
                drawMaterialStats(material);
        }
    }

    void drawToolInformation() {
        int offsetX = descTextLeft + 63;

        this.drawCenteredString(fontRendererObj, title, offsetX, 8, 0xffffff);
        fontRendererObj.drawSplitString(body, offsetX - 56, 24, 115, 0xffffff);
    }

    protected void drawMaterialStats(ToolMaterial materialEnum) {
        final int baseX = descTextLeft + 8;
        final int baseY = 8;

        String centerTitle = "\u00A7n" + materialEnum.localizedName();

        drawCenteredString(this.fontRendererObj, centerTitle, baseX + 55, baseY, 16777215);

        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter4") + materialEnum.durability(), baseX, baseY + 16, 16777215);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter5") + materialEnum.handleDurability() + "x", baseX, baseY + 27, 16777215);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter6") + materialEnum.toolSpeed() / 100f, baseX, baseY + 38, 16777215);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter7") + HarvestLevels.getHarvestLevelName(materialEnum.harvestLevel()), baseX, baseY + 49, 16777215);

        int attack = materialEnum.attack();
        String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
        if (attack % 2 == 0)
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2 + heart, baseX, baseY + 60, 0xffffff);
        else
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2f + heart, baseX, baseY + 60, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

        // Draw the background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        this.drawTexturedModalRect(this.craftingLeft, this.craftingTop, 0, 0, 176, 166);

        if (active) {
            this.drawTexturedModalRect(this.craftingLeft + 62, this.craftingTop, 0, 166, 112, 22);
        }

        this.mc.getTextureManager().bindTexture(icons);

        // Draw the slots
        if (logic.tinkerTable && !logic.isStackInSlot(5))
            this.drawTexturedModalRect(this.craftingLeft + 47, this.craftingTop + 33, 0, 233, 18, 18);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        
        this.mc.getTextureManager().bindTexture(gui_inventory);
        if(hasChest()) {
            chestLeft += border.w;
            chestTop += border.h;

            border.draw();

            int x = chestLeft;
            int y = chestTop;
            final int midW = chestWidth - border.w * 2;

            if (shouldDrawName()) {
                textBackground.drawScaledX(chestLeft, chestTop, midW);
                y += textBackground.h;
            }

            drawChestSlots(x, y);

            // slider
            if (slider.isEnabled()) {
                slider.update(mouseX, mouseY, !isMouseOverFullSlot(mouseX, mouseY) && isMouseInChest(mouseX, mouseY));
                slider.draw();

                updateChestSlots();
            }

            chestLeft -= border.w;
            chestTop -= border.h;
        }
        // Draw description
        if (logic.tinkerTable) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(description);
            this.drawTexturedModalRect(this.descLeft, this.descTop, 0, 0, 126, 172);
        }

    }

    @Override
    public VisiblityData modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) {
        currentVisibility.showWidgets = width - xSize >= 107;

        if (guiLeft < 58) {
            currentVisibility.showStateButtons = false;
        }

        return currentVisibility;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
        return null;
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleDragNDrop(GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button) {
        return false;
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
        final int cw = (logic.chest != null ? chestWidth : 0);
        if(logic.chest != null) {
            final Rectangle blah = new Rectangle(x, y, w, h);
            final Rectangle chestRectangle = new Rectangle(
                chestLeft, chestTop, chestWidth, chestHeight + (shouldDrawName() ? textBackground.y : 0) + (border.h * 2)
            );
            
            if(chestRectangle.intersects(blah))
                return true;
        }
        
        if (y + h - 4 < guiTop || y + 4 > guiTop + ySize)
            return false;
        
        return x - w - 4 >= guiLeft + cw && x + 4 <= guiLeft + xSize + cw + (logic.tinkerTable ? 126 : 0);
    }

    public boolean hasChest() {
        return logic.chest != null;
    }

    public boolean isMouseInChest(int mouseX, int mouseY) {
        final int xMod = (slider.isEnabled() ? slider.width : 0) + border.w;
        final int yMod = (shouldDrawName() ? textBackground.y : 0) + (border.h * 2);
        
        return mouseX >= (this.chestLeft - xMod) && mouseX < (chestLeft + chestWidth  + xMod) &&
               mouseY >= (this.chestTop  - yMod) && mouseY < (chestTop  + chestHeight + yMod);
    }

    public boolean isMouseOverFullSlot(int mouseX, int mouseY) {
        for(final Object slot : inventorySlots.inventorySlots) {
            if(isMouseOverSlot((Slot)slot, mouseX, mouseY) && ((Slot)slot).getHasStack()) {
                return true;
            }
        }
        return false;
    }

    protected boolean shouldDrawName() {
        return this.logic.chest != null && this.logic.chest.get().getInventoryName() != null && !this.logic.chest.get().getInventoryName().isEmpty();
    }
    
    @Override
    public void func_146977_a/*drawSlot*/(Slot slot) {
        if(!slot.func_111238_b/*isEnabled*/()) return;
        
        super.func_146977_a(slot);
    }
    
    public boolean shouldDrawSlot(Slot slot) {
        if(!(slot instanceof ChestSlot)) return true;

        if(slot.getSlotIndex() >= chestSlotCount) return false;

        // all visible
        if(!slider.isEnabled()) return true;

        return firstSlotId <= slot.getSlotIndex() && lastSlotId > slot.getSlotIndex();
    }

    @Override
    public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
        return super.isMouseOverSlot(slotIn, mouseX, mouseY) && shouldDrawSlot(slotIn);
    }
    
    private int getDisplayedRows() {
        return chestHeight / slotElement.h;
    }

    private int calcCappedYSize(int max) {
        int h = slotElement.h * logic.invRows;

        // not higher than the max
        while(h > max) {
            h -= slotElement.h;
        }
        return h;
    }

    public int getChestWidth() {
        return chestWidth;
    }

    // updatePosition
    public void updateChest() {
        chestHeight = calcCappedYSize(CraftingStationGui.slotElement.h * 10);
        chestSlotCount = logic.slotCount;

        // slider needed?
        if(getDisplayedRows() < logic.invRows) {
            slider.enable();
        }
        else {
            slider.disable();
        }

        chestWidth = logic.invColumns * CraftingStationGui.slotElement.w + 2 * border.w + (slider.isEnabled() ? slider.width : 0);

        chestLeft = guiLeft - chestWidth;
        chestTop = guiTop;

        // Leaving out the xsize increase by chestSize and adjusting where it's used, because otherwise it shifts both the bookmarks and item panel
        // way too far out.
        //xSize += guiLeft - chestLeft;
        guiLeft = chestLeft;

        if (logic.doubleChest != null)
            this.ySize = 187;

        border.setPosition(chestLeft, chestTop);
        border.setSize(chestWidth, chestHeight + (shouldDrawName() ? textBackground.h : 0) + 2 * border.h);

        slider.show();
        slider.setPosition(chestLeft + logic.invColumns * slotElement.w + border.w, chestTop + (shouldDrawName() ? textBackground.h : 0) + border.h);
        slider.setSize(chestHeight);
        slider.setSliderParameters(0, logic.invRows - getDisplayedRows(), 1);

        updateChestSlots();
    }
    
    // updates slot visibility
    protected void updateChestSlots() {
        if(!hasChest()) return;
        
        final IInventory secondInventory = logic.getSecondInventory();
        
        int xOffset = border.w;
        int yOffset = border.h;
        
        if(shouldDrawName()) {
            yOffset += textBackground.h;
        }
        
        firstSlotId = slider.getValue() * logic.invColumns;
        lastSlotId = Math.min(chestSlotCount, firstSlotId + getDisplayedRows() * logic.invColumns);
        
        for(Object o : inventorySlots.inventorySlots) {
            if(!(o instanceof ChestSlot)) continue;
            
            final ChestSlot slot = (ChestSlot)o;
            
            if(shouldDrawSlot(slot)) {
                slot.enable();
                // calc position of the slot
                final int offset = slot.getSlotIndex() + (slot.inventory == secondInventory ? 27 : 0) - firstSlotId;
                final int x = (offset % logic.invColumns) * CraftingStationGui.slotElement.w;
                final int y = (offset / logic.invColumns) * CraftingStationGui.slotElement.h;

                slot.xDisplayPosition = x + xOffset + 1 /*- this.chestWidth*/;
                slot.yDisplayPosition = y + yOffset + 1;
            }
            else {
                slot.disable();
                slot.xDisplayPosition = 0;
                slot.yDisplayPosition = 0;
            }
        }
    }
    

    // drawSlots
    protected void drawChestSlots(int xPos, int yPos) {
        if(!hasChest()) return;
        
        int width = logic.invColumns * slotElement.w;
        int height = chestHeight - border.h * 2;

        int fullRows = (lastSlotId - firstSlotId) / logic.invColumns;
        int slotsLeft = (lastSlotId - firstSlotId) % logic.invColumns;
        
        int y; // We use it after the loop
        for(y = 0; y < fullRows * slotElement.h && y < height; y += slotElement.h) {
            slotElement.drawScaledX(xPos, yPos + y, width);
        }

        // draw partial row and unused slots
        if(slotsLeft > 0) {
            slotElement.drawScaledX(xPos, yPos + y, slotsLeft * slotElement.w);
            // empty slots that don't exist
            slotEmptyElement.drawScaledX(xPos + slotsLeft * slotElement.w, yPos + y, width - slotsLeft * slotElement.w);
        }
    }
    

    /*
     * Hide the deprecated stuff at the bottom
     */
    @Deprecated
    public static final int CHEST_WIDTH = 116;
}
