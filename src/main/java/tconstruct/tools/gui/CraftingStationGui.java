package tconstruct.tools.gui;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.accessory.AccessoryCore;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.tools.*;
import tconstruct.library.util.HarvestLevels;
import tconstruct.tools.logic.CraftingStationLogic;

@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class CraftingStationGui extends GuiContainer implements INEIGuiHandler
{
    public boolean active;
    public String toolName;
    public GuiTextField text;
    public String title, body = "";
    CraftingStationLogic logic;
    
    public static final int CHEST_WIDTH = 116;

    // Panel positions

    private int craftingLeft = 0;
    private int craftingTop = 0;
    private int craftingTextLeft = 0;

    private int descLeft = 0;
    private int descTop = 0;
    private int descTextLeft = 0;

    private int chestLeft = 0;
    private int chestTop = 0;

    public CraftingStationGui(InventoryPlayer inventory, CraftingStationLogic logic, World world, int x, int y, int z)
    {
        super(logic.getGuiContainer(inventory, world, x, y, z));
        this.logic = logic;
        // text = new GuiTextField(this.fontRendererObj, this.xSize / 2 - 5, 8,
        // 30, 12);
        // this.text.setText("");
        title = "\u00A7n" + StatCollector.translateToLocal("gui.toolforge1");
        body = StatCollector.translateToLocal("gui.toolforge2");
        toolName = "";
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

        if (logic.tinkerTable)
        {
            this.descLeft = this.guiLeft + 176;
            this.descTop = this.craftingTop;
        }

        if (logic.chest != null)
        {
            this.xSize += CHEST_WIDTH;
            this.guiLeft -= CHEST_WIDTH;
            this.chestLeft = this.guiLeft;
            this.chestTop = this.craftingTop;
            if (logic.doubleChest != null)
                this.ySize = 187;
        }

        this.craftingTextLeft = this.craftingLeft - this.guiLeft;
        this.descTextLeft = this.descLeft - this.guiLeft;
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        if (logic.chest != null)
        {
            this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.chest.get().getInventoryName()), 8, 6, 0x202020);
        }

        this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.tinkerTable ? "crafters.TinkerTable" : logic.getInvName()), craftingTextLeft + 8, 6, 0x202020);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), craftingTextLeft + 8, 72, 0x202020);

        // this.fontRendererObj.drawString(toolName + "_", this.xSize / 2 - 18,
        // 8, 0xffffff);

        if (logic.tinkerTable)
        {
            if (logic.isStackInSlot(0)) // output slot = modified item
                drawToolStats(logic.getStackInSlot(0));
            else if (logic.isStackInSlot(5)) // center slot if no output item
            {
                // other slots empty?
                if(!logic.isStackInSlot(1) && !logic.isStackInSlot(2) && !logic.isStackInSlot(3) && !logic.isStackInSlot(4)
                && !logic.isStackInSlot(6) && !logic.isStackInSlot(7) && !logic.isStackInSlot(8) && !logic.isStackInSlot(9))
                    drawToolStats(logic.getStackInSlot(5));
                else
                    drawToolInformation();
            }
            else
                drawToolInformation();
        }
    }

    void drawToolStats (ItemStack stack)
    {
        if(stack.getItem() instanceof IModifyable)
            ToolStationGuiHelper.drawToolStats(stack, descTextLeft + 10, 0);


        int matID = PatternBuilder.instance.getPartID(stack);

        if (matID != Short.MAX_VALUE && matID > 0)
        {
            ToolMaterial material = TConstructRegistry.getMaterial(matID);

            if(material != null)
                drawMaterialStats(material);
        }
    }
    
    protected void drawMaterialStats(ToolMaterial materialEnum)
    {
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

    void drawToolInformation ()
    {
        int offsetX = descTextLeft + 63;
        
        this.drawCenteredString(fontRendererObj, title, offsetX, 8, 0xffffff);
        fontRendererObj.drawSplitString(body, offsetX - 56, 24, 115, 0xffffff);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/tinkertable.png");
    private static final ResourceLocation description = new ResourceLocation("tinker", "textures/gui/description.png");
    private static final ResourceLocation icons = new ResourceLocation("tinker", "textures/gui/icons.png");
    private static final ResourceLocation chest = new ResourceLocation("tinker", "textures/gui/chestside.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
    {
        // Draw the background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        this.drawTexturedModalRect(this.craftingLeft, this.craftingTop, 0, 0, 176, 166);

        if (active)
        {
            this.drawTexturedModalRect(this.craftingLeft + 62, this.craftingTop, 0, 166, 112, 22);
        }

        this.mc.getTextureManager().bindTexture(icons);
        // Draw the slots

        if (logic.tinkerTable && !logic.isStackInSlot(5))
            this.drawTexturedModalRect(this.craftingLeft + 47, this.craftingTop + 33, 0, 233, 18, 18);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);

        // Draw chest side
        if (logic.chest != null)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(chest);

            if (logic.doubleChest == null)
                this.drawTexturedModalRect(this.chestLeft, this.chestTop, 0, 0, 122, 114);
            else
                this.drawTexturedModalRect(this.chestLeft, this.chestTop, 125, 0, 122, 187);
        }

        // Draw description
        if (logic.tinkerTable)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(description);
            this.drawTexturedModalRect(this.descLeft, this.descTop, 0, 0, 126, 172);
        }

    }

    @Override
    public VisiblityData modifyVisiblity (GuiContainer gui, VisiblityData currentVisibility)
    {
        if (width - xSize < 107)
        {
            currentVisibility.showWidgets = false;
        }
        else
        {
            currentVisibility.showWidgets = true;
        }

        if (guiLeft < 58)
        {
            currentVisibility.showStateButtons = false;
        }

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

        if (x + 4 > guiLeft + xSize + (logic.tinkerTable ? 126 : 0))
            return false;

        return true;
    }
    
    public boolean hasChest()
    {
        return logic.chest != null;
    }

}
