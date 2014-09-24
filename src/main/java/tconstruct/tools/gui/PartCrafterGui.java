package tconstruct.tools.gui;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.library.util.HarvestLevels;
import tconstruct.smeltery.inventory.ActiveContainer;
import tconstruct.tools.inventory.PartCrafterChestContainer;
import tconstruct.tools.logic.PartBuilderLogic;

@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class PartCrafterGui extends GuiContainer implements INEIGuiHandler
{
    PartBuilderLogic logic;
    String title, otherTitle = "";
    boolean drawChestPart;
    boolean hasTop, hasBottom;
    ItemStack topMaterial, bottomMaterial;
    ToolMaterial topEnum, bottomEnum;

    private static final int CRAFT_WIDTH = 176;
    private static final int CRAFT_HEIGHT = 166;
    private static final int DESC_WIDTH = 126;
    private static final int DESC_HEIGHT = 166;
    private static final int CHEST_WIDTH = 122;
    private static final int CHEST_HEIGHT = 114;

    // Panel positions

    private int craftingLeft = 0;
    private int craftingTop = 0;
    private int craftingTextLeft = 0;

    private int descLeft = 0;
    private int descTop = 0;
    private int descTextLeft = 0;

    private int chestLeft = 0;
    private int chestTop = 0;

    public PartCrafterGui(InventoryPlayer inventoryplayer, PartBuilderLogic partlogic, World world, int x, int y, int z)
    {
        super((ActiveContainer) partlogic.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = partlogic;
        drawChestPart = inventorySlots instanceof PartCrafterChestContainer;

        title = "\u00A7n" + (StatCollector.translateToLocal("gui.partcrafter1"));
    }

    @Override
    public void initGui ()
    {
        super.initGui();

        this.xSize = CRAFT_WIDTH + DESC_WIDTH;
        this.ySize = CRAFT_HEIGHT;

        this.craftingLeft = (this.width - CRAFT_WIDTH) / 2;
        this.craftingTop = (this.height - CRAFT_HEIGHT) / 2;

        this.guiLeft = this.craftingLeft;
        this.guiTop = this.craftingTop;

        this.descLeft = this.craftingLeft + CRAFT_WIDTH;
        this.descTop = this.craftingTop;

        if (drawChestPart)
        {
            this.xSize += CHEST_WIDTH - 6;
            this.guiLeft -= CHEST_WIDTH - 6;
            
            this.chestLeft = this.guiLeft;
            this.chestTop = this.guiTop + 11;
        }
        
        this.craftingTextLeft = this.craftingLeft - this.guiLeft;
        this.descTextLeft = this.descLeft - this.guiLeft;
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        if (drawChestPart)
        {
            this.fontRendererObj.drawString(StatCollector.translateToLocal("inventory.PatternChest"), 14, 17, 4210752);
        }

        this.fontRendererObj.drawString(StatCollector.translateToLocal("crafters.PartBuilder"), craftingTextLeft + 6, 6, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), craftingTextLeft + 8, this.ySize - 96 + 2, 4210752);

        drawMaterialInformation();
    }

    void drawDefaultInformation ()
    {
        title = "\u00A7n" + StatCollector.translateToLocal("gui.partcrafter2");
        this.drawCenteredString(fontRendererObj, title, descTextLeft + DESC_WIDTH / 2, 8, 16777215);
        fontRendererObj.drawSplitString(StatCollector.translateToLocal("gui.partcrafter3"), descTextLeft + 8, 24, 115, 16777215);
    }

    void drawMaterialInformation ()
    {
        ItemStack top = logic.getStackInSlot(2);
        //ItemStack topResult = logic.getStackInSlot(4);
        ItemStack bottom = logic.getStackInSlot(3);
        //ItemStack bottomResult = logic.getStackInSlot(6);
        if (topMaterial != top)
        {
            topMaterial = top;
            int topID = PatternBuilder.instance.getPartID(top);

            if (topID != Short.MAX_VALUE)// && topResult != null)
            {
                topEnum = TConstructRegistry.getMaterial(topID);
                hasTop = true;
                title = "\u00A7n" + topEnum.localizedName();
            }
            else
                hasTop = false;
        }

        if (bottomMaterial != bottom)
        {
            bottomMaterial = bottom;
            int bottomID = PatternBuilder.instance.getPartID(bottom);

            if (bottomID != Short.MAX_VALUE)// && bottomResult != null)
            {
                bottomEnum = TConstructRegistry.getMaterial(bottomID);
                hasBottom = true;
                otherTitle = "\u00A7n" + bottomEnum.localizedName();
            }
            else
                hasBottom = false;
        }

        int offset = 8;
        if (hasTop)
        {
            this.drawCenteredString(fontRendererObj, title, descTextLeft + DESC_WIDTH / 2, offset, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter4") + topEnum.durability(), descTextLeft + 8, offset + 16, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter5") + topEnum.handleDurability() + "x", descTextLeft + 8, offset + 27, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter6") + topEnum.toolSpeed() / 100f, descTextLeft + 8, offset + 38, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter7") + HarvestLevels.getHarvestLevelName(topEnum.harvestLevel()), descTextLeft + 8, offset + 49, 16777215);

            int attack = topEnum.attack();
            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2 + heart, descTextLeft + 8, offset + 60, 0xffffff);
            else
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2f + heart, descTextLeft + 8, offset + 60, 0xffffff);
        }

        offset = 90;
        if (hasBottom)
        {
            this.drawCenteredString(fontRendererObj, otherTitle, descTextLeft + DESC_WIDTH / 2, offset, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter4") + bottomEnum.durability(), descTextLeft + 8, offset + 16, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter5") + bottomEnum.handleDurability() + "x", descTextLeft + 8, offset + 27, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter6") + bottomEnum.toolSpeed() / 100f, descTextLeft + 8, offset + 38, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter7") + HarvestLevels.getHarvestLevelName(bottomEnum.harvestLevel()), descTextLeft + 8, offset + 49, 16777215);
            int attack = bottomEnum.attack();
            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2 + heart, descTextLeft + 8, offset + 60, 0xffffff);
            else
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2f + heart, descTextLeft + 8, offset + 60, 0xffffff);
        }

        if (!hasTop && !hasBottom)
            drawDefaultInformation();
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/toolparts.png");
    private static final ResourceLocation minichest = new ResourceLocation("tinker", "textures/gui/patternchestmini.png");
    private static final ResourceLocation description = new ResourceLocation("tinker", "textures/gui/description.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
    {
        // Draw the background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        this.drawTexturedModalRect(craftingLeft, craftingTop, 0, 0, CRAFT_WIDTH, CRAFT_HEIGHT);

        // Draw Slots
        this.drawTexturedModalRect(craftingLeft + 39, craftingTop + 26, 0, 166, 98, 36);
        if (!logic.isStackInSlot(0))
        {
            this.drawTexturedModalRect(craftingLeft + 39, craftingTop + 26, 176, 0, 18, 18);
        }
        if (!logic.isStackInSlot(2))
        {
            this.drawTexturedModalRect(craftingLeft + 57, craftingTop + 26, 176, 18, 18, 18);
        }
        if (!logic.isStackInSlot(1))
        {
            this.drawTexturedModalRect(craftingLeft + 39, craftingTop + 44, 176, 0, 18, 18);
        }
        if (!logic.isStackInSlot(3))
        {
            this.drawTexturedModalRect(craftingLeft + 57, craftingTop + 44, 176, 36, 18, 18);
        }

        // Draw chest
        if (drawChestPart)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(minichest);
            this.drawTexturedModalRect(chestLeft, chestTop, 0, 0, CHEST_WIDTH, CHEST_HEIGHT);
        }

        // Draw description
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(description);
        this.drawTexturedModalRect(descLeft, descTop, DESC_WIDTH, 0, DESC_WIDTH, DESC_HEIGHT);
    }

    @Override
    public VisiblityData modifyVisiblity (GuiContainer gui, VisiblityData currentVisibility)
    {
        return currentVisibility;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots (GuiContainer gui, ItemStack item)
    {
        return null;
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas (GuiContainer gui)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean handleDragNDrop (GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button)
    {
        return false;
    }

    @Override
    public boolean hideItemPanelSlot (GuiContainer gui, int x, int y, int w, int h)
    {
        if (y + h - 4 < guiTop || y + 4 > guiTop + ySize)
            return false;

        if (x + 4 > guiLeft + xSize)
            return false;

        return true;
    }

}