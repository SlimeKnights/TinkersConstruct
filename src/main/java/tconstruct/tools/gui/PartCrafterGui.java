package tconstruct.tools.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.library.util.HarvestLevels;
import tconstruct.smeltery.inventory.ActiveContainer;
import tconstruct.tools.inventory.PartCrafterChestContainer;
import tconstruct.tools.logic.PartBuilderLogic;

public class PartCrafterGui extends GuiContainer
{
    PartBuilderLogic logic;
    String title, otherTitle = "";
    boolean drawChestPart;
    boolean hasTop, hasBottom;
    ItemStack topMaterial, bottomMaterial;
    ToolMaterial topEnum, bottomEnum;

    // Panel positions

    int craftingLeft = 0;
    int craftingTop = 0;

    int descLeft = 0;
    int descTop = 0;

    int chestLeft = 0;
    int chestTop = 0;

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

        this.xSize = 176;
        this.ySize = 166;

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.craftingLeft = this.guiLeft;
        this.craftingTop = this.guiTop;
        this.descTop = this.craftingTop;
        this.descLeft = (this.width + this.xSize) / 2;

        if (drawChestPart)
        {
            this.xSize += 122;
            this.guiLeft -= 122;
            this.chestLeft = this.guiLeft + 6;
            this.chestTop = this.guiTop + 11;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        int offsetX = 0;

        if (drawChestPart)
        {
            this.fontRendererObj.drawString(StatCollector.translateToLocal("inventory.PatternChest"), 14, 17, 4210752);
            offsetX = 122;
        }

        this.fontRendererObj.drawString(StatCollector.translateToLocal("crafters.PartBuilder"), offsetX + 6, 6, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), offsetX + 8, this.ySize - 96 + 2, 4210752);

        drawMaterialInformation();
    }

    void drawDefaultInformation ()
    {
        title = "\u00A7n" + StatCollector.translateToLocal("gui.partcrafter2");
        this.drawCenteredString(fontRendererObj, title, xSize + 63, 8, 16777215);
        fontRendererObj.drawSplitString(StatCollector.translateToLocal("gui.partcrafter3"), xSize + 8, 24, 115, 16777215);
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
            this.drawCenteredString(fontRendererObj, title, xSize + 63, offset, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter4") + topEnum.durability(), xSize + 8, offset + 16, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter5") + topEnum.handleDurability() + "x", xSize + 8, offset + 27, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter6") + topEnum.toolSpeed() / 100f, xSize + 8, offset + 38, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter7") + HarvestLevels.getHarvestLevelName(topEnum.harvestLevel()), xSize + 8, offset + 49, 16777215);

            int attack = topEnum.attack();
            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2 + heart, xSize + 8, offset + 60, 0xffffff);
            else
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2f + heart, xSize + 8, offset + 60, 0xffffff);
        }

        offset = 90;
        if (hasBottom)
        {
            this.drawCenteredString(fontRendererObj, otherTitle, xSize + 63, offset, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter4") + bottomEnum.durability(), xSize + 8, offset + 16, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter5") + bottomEnum.handleDurability() + "x", xSize + 8, offset + 27, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter6") + bottomEnum.toolSpeed() / 100f, xSize + 8, offset + 38, 16777215);
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter7") + HarvestLevels.getHarvestLevelName(bottomEnum.harvestLevel()), xSize + 8, offset + 49, 16777215);
            int attack = bottomEnum.attack();
            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2 + heart, xSize + 8, offset + 60, 0xffffff);
            else
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2f + heart, xSize + 8, offset + 60, 0xffffff);
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
        this.drawTexturedModalRect(craftingLeft, craftingTop, 0, 0, 176, 166);

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
            this.drawTexturedModalRect(chestLeft, chestTop, 0, 0, 122, 114);
        }

        // Draw description
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(description);
        this.drawTexturedModalRect(descLeft, descTop, 126, 0, 126, this.ySize);
    }

}