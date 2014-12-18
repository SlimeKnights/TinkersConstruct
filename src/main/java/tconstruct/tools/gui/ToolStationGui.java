package tconstruct.tools.gui;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import tconstruct.TConstruct;
import tconstruct.library.accessory.AccessoryCore;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.client.*;
import tconstruct.library.tools.*;
import tconstruct.library.util.HarvestLevels;
import tconstruct.smeltery.inventory.ActiveContainer;
import tconstruct.tools.inventory.ToolStationContainer;
import tconstruct.tools.logic.ToolStationLogic;
import tconstruct.util.network.ToolStationPacket;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class ToolStationGui extends GuiContainer implements INEIGuiHandler
{
    public ToolStationLogic logic;
    public ToolStationContainer toolSlots;
    public GuiTextField text;
    public String toolName;
    public int guiType;
    public int[] slotX, slotY, iconX, iconY;
    public boolean active;
    public String title, body = "";

    public ToolStationGui(InventoryPlayer inventoryplayer, ToolStationLogic stationlogic, World world, int x, int y, int z)
    {
        super((ActiveContainer) stationlogic.getGuiContainer(inventoryplayer, world, x, y, z));
        this.logic = stationlogic;
        toolSlots = (ToolStationContainer) inventorySlots;
        text = new GuiTextField(this.fontRendererObj, 83, 8, 30, 12);
        this.text.setMaxStringLength(40);
        this.text.setEnableBackgroundDrawing(false);
        this.text.setVisible(true);
        this.text.setCanLoseFocus(false);
        this.text.setFocused(true);
        this.text.setTextColor(0xffffff);
        toolName = "";
        resetGui();
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void mouseClicked (int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0)
        {
            int gLeft = this.guiLeft + 68 + 110;
            int gTop = this.guiTop + 6;
            int gwidth = 102;
            int gheight = 12;
            active = mouseX > gLeft && mouseX < gLeft + gwidth && mouseY > gTop && mouseY < gTop + gheight;
        }
    }

    void resetGui ()
    {
        this.text.setText("");
        guiType = 0;
        setSlotType(0);
        iconX = new int[] { 0, 1, 2 };
        iconY = new int[] { 13, 13, 13 };
        title = "\u00A7n" + StatCollector.translateToLocal("gui.toolforge1");
        body = StatCollector.translateToLocal("gui.toolforge2");
    }

    @Override
    public void initGui ()
    {
        super.initGui();
        this.xSize = 176 + 110;
        this.guiLeft = (this.width - 176) / 2 - 110;

        this.buttonList.clear();
        ToolGuiElement repair = TConstructClientRegistry.toolButtons.get(0);
        GuiButtonTool repairButton = new GuiButtonTool(0, this.guiLeft, this.guiTop, repair.buttonIconX, repair.buttonIconY, repair.domain, repair.texture, repair); // Repair
        repairButton.enabled = false;
        this.buttonList.add(repairButton);

        for (int iter = 1; iter < TConstructClientRegistry.toolButtons.size(); iter++)
        {
            ToolGuiElement element = TConstructClientRegistry.toolButtons.get(iter);
            GuiButtonTool button = new GuiButtonTool(iter, this.guiLeft + 22 * (iter % 5), this.guiTop + 22 * (iter / 5), element.buttonIconX, element.buttonIconY, repair.domain, element.texture, element);
            this.buttonList.add(button);
        }
    }

    @Override
    protected void actionPerformed (GuiButton button)
    {
        ((GuiButton) this.buttonList.get(guiType)).enabled = true;
        guiType = button.id;
        button.enabled = false;

        ToolGuiElement element = TConstructClientRegistry.toolButtons.get(guiType);
        setSlotType(element.slotType);
        iconX = element.iconsX;
        iconY = element.iconsY;
        title = "\u00A7n" + element.title;
        body = StatCollector.translateToLocal(element.body);
        if(body != null) {
            int i;
            // for some really weird reason replaceAll doesn't find "\\n", but indexOf does. We have to replace manually.
            while((i = body.indexOf("\\n")) >= 0)
            {
                body = body.substring(0, i) + '\n' + body.substring(i+2);
            }
        }
    }

    void setSlotType (int type)
    {
        switch (type)
        {
        case 0:
            slotX = new int[] { 56, 38, 38 }; // Repair
            slotY = new int[] { 37, 28, 46 };
            break;
        case 1:
            slotX = new int[] { 56, 56, 56 }; // Three parts
            slotY = new int[] { 19, 55, 37 };
            break;
        case 2:
            slotX = new int[] { 56, 56, 14 }; // Two parts
            slotY = new int[] { 28, 46, 37 };
            break;
        case 3:
            slotX = new int[] { 38, 47, 56 }; // Double head
            slotY = new int[] { 28, 46, 28 };
            break;
        case 7:
            slotX = new int[] { 56, 56, 56 }; // Three parts reverse
            slotY = new int[] { 19, 37, 55 };
            break;
        }
        toolSlots.resetSlots(slotX, slotY);
    }

    @Override
    public void updateScreen ()
    {
        super.updateScreen();
        this.text.updateCursorCounter();
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.getInvName()), 116, 8, 0x000000);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 118, this.ySize - 96 + 2, 0x000000);
        this.fontRendererObj.drawString(toolName + "_", 180, 8, 0xffffff);

        if (logic.isStackInSlot(0))
            ToolStationGuiHelper.drawToolStats(logic.getStackInSlot(0), 294, 0);
        else
            drawToolInformation();
    }

    protected void drawToolInformation ()
    {
        this.drawCenteredString(fontRendererObj, title, 349, 8, 0xffffff);
        fontRendererObj.drawSplitString(body, 294, 24, 115, 0xffffff);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/toolstation.png");
    private static final ResourceLocation icons = new ResourceLocation("tinker", "textures/gui/icons.png");
    private static final ResourceLocation description = new ResourceLocation("tinker", "textures/gui/description.png");

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
    {
        // Draw the background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        final int cornerX = this.guiLeft + 110;
        this.drawTexturedModalRect(cornerX, this.guiTop, 0, 0, 176, this.ySize);

        if (active)
        {
            this.drawTexturedModalRect(cornerX + 62, this.guiTop, 0, this.ySize, 112, 22);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        // Draw the slots

        for (int i = 0; i < slotX.length; i++)
        {
            this.drawTexturedModalRect(cornerX + slotX[i], this.guiTop + slotY[i], 144, 216, 18, 18);
            if (!logic.isStackInSlot(i + 1))
            {
                this.drawTexturedModalRect(cornerX + slotX[i], this.guiTop + slotY[i], 18 * iconX[i], 18 * iconY[i], 18, 18);
            }
        }

        // Draw description
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(description);
        this.drawTexturedModalRect(cornerX + 176, this.guiTop, 0, 0, 126, this.ySize + 30);

    }

    @Override
    protected void keyTyped (char par1, int keyCode)
    {
        if (keyCode == 1 || (!active && keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()))
        {
            logic.setToolname("");
            updateServer("");
            Keyboard.enableRepeatEvents(false);
            this.mc.thePlayer.closeScreen();
        }
        else if (active)
        {
            text.textboxKeyTyped(par1, keyCode);
            toolName = text.getText().trim();
            logic.setToolname(toolName);
            updateServer(toolName);
        }
    }

    void updateServer (String name)
    {
        /*
         * ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
         * DataOutputStream outputStream = new DataOutputStream(bos); try {
         * outputStream.writeByte(1);
         * outputStream.writeInt(logic.getWorld().provider.dimensionId);
         * outputStream.writeInt(logic.xCoord);
         * outputStream.writeInt(logic.yCoord);
         * outputStream.writeInt(logic.zCoord); outputStream.writeUTF(name); }
         * catch (Exception ex) { ex.printStackTrace(); }
         * 
         * Packet250CustomPayload packet = new Packet250CustomPayload();
         * packet.channel = "TConstruct"; packet.data = bos.toByteArray();
         * packet.length = bos.size();
         * 
         * PacketDispatcher.sendPacketToServer(packet);
         */

        TConstruct.packetPipeline.sendToServer(new ToolStationPacket(logic.xCoord, logic.yCoord, logic.zCoord, name));
    }

    /*
     * protected void mouseClicked(int par1, int par2, int par3) {
     * super.mouseClicked(par1, par2, par3); text.mouseClicked(par1, par2,
     * par3); }
     */

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

        if (x + 4 > guiLeft + xSize + 126)
            return false;

        return true;
    }
}
