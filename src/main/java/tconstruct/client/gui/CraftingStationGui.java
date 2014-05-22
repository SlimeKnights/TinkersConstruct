package tconstruct.client.gui;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.CraftingStationLogic;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.tools.HarvestTool;
import tconstruct.library.tools.ToolCore;

public class CraftingStationGui extends GuiContainer
{
    public boolean active;
    public String toolName;
    public GuiTextField text;
    public String title, body = "";
    CraftingStationLogic logic;

    public CraftingStationGui(InventoryPlayer inventory, CraftingStationLogic logic, World world, int x, int y, int z)
    {
        super(logic.getGuiContainer(inventory, world, x, y, z));
        this.logic = logic;
        //text = new GuiTextField(this.fontRenderer, this.xSize / 2 - 5, 8, 30, 12);
        //this.text.setText("");
        title = "\u00A7n" + StatCollector.translateToLocal("gui.toolforge1");
        body = StatCollector.translateToLocal("gui.toolforge2");
        toolName = "";
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal(logic.tinkerTable ? "crafters.TinkerTable" : logic.getInvName()), 8, 6, 0x202020);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x202020);
        if (logic.chest != null)
            this.fontRenderer.drawString(StatCollector.translateToLocal(logic.chest.get().getInvName()), -108, this.ySize - 160, 0x202020);
        //this.fontRenderer.drawString(toolName + "_", this.xSize / 2 - 18, 8, 0xffffff);

        if (logic.tinkerTable)
        {
            if (logic.isStackInSlot(0))
                ToolStationGui.drawToolStats(logic.getStackInSlot(0), fontRenderer, this.xSize);
            else
                ToolStationGui.drawToolInformation(fontRenderer, title, body, this.xSize);
        }
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
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);

        if (active)
        {
            this.drawTexturedModalRect(cornerX + 62, cornerY, 0, this.ySize, 112, 22);
        }

        this.mc.getTextureManager().bindTexture(icons);
        // Draw the slots

        if (logic.tinkerTable && !logic.isStackInSlot(5))
            this.drawTexturedModalRect(cornerX + 47, cornerY + 33, 0, 233, 18, 18);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);

        //Draw chest side
        if (logic.chest != null)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(chest);
            if (logic.doubleChest == null)
                this.drawTexturedModalRect(cornerX - 116, cornerY, 0, 0, 121, this.ySize);
            else
                this.drawTexturedModalRect(cornerX - 116, cornerY, 125, 0, 122, this.ySize + 21);
        }

        // Draw description
        if (logic.tinkerTable)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(description);
            cornerX = (this.width + this.xSize) / 2;
            cornerY = (this.height - this.ySize) / 2;
            this.drawTexturedModalRect(cornerX, cornerY, 0, 0, 126, this.ySize + 30);
        }

    }

}
