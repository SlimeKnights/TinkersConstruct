package tconstruct.client.gui;

import cpw.mods.fml.common.network.PacketDispatcher;
import java.io.*;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import tconstruct.blocks.logic.AdvancedDrawbridgeLogic;
import tconstruct.inventory.AdvancedDrawbridgeContainer;

public class AdvDrawbridgeGui extends GuiContainer
{
    public AdvancedDrawbridgeLogic logic;

    public boolean isGuiExpanded = false;
    public boolean containerNeglectMouse = false;

    public AdvDrawbridgeGui(EntityPlayer player, AdvancedDrawbridgeLogic frypan, World world, int x, int y, int z)
    {
        super(frypan.getGuiContainer(player.inventory, world, x, y, z));
        this.inventorySlots = new AdvancedDrawbridgeContainer(player.inventory, frypan, this);
        player.openContainer = this.inventorySlots;
        logic = frypan;
    }

    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        fontRenderer.drawString("Advanced Drawbridge", 8, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/drawbridgeAdvanced.png");

    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = (width - xSize) / 2;
        int cornerY = (height - ySize) / 2;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
        if (!isGuiExpanded)
        {
            drawTexturedModalRect(cornerX + 34, cornerY + 35, 238, 0, 18, 18);
        }
        else
        {
            for (int index = 0; index < 16; index++)
            {
                drawTexturedModalRect(index < 8 ? cornerX + 9 + 20 * index : cornerX + 9 + 20 * (index - 8), cornerY + 34 + (int) Math.floor(index / 8) * 18 + (index < 8 ? 0 : 1), 238, 0, 18, 18);
                if (index != 15)
                {
                    drawTexturedModalRect(index < 8 ? cornerX + 26 + 20 * index : cornerX + 26 + 20 * (index - 8), cornerY + 34 + (int) Math.floor(index / 8) * 18 + (index < 8 ? 0 : 1) + 6, 253, 18,
                            3, 5);
                }
            }
            //        	drawTexturedModalRect(cornerX - 7, cornerY + 29, 0, 167, 10, 40);
            //        	drawTexturedModalRect(cornerX - 7, cornerY + 29, 0, 167, 10, 40);
            //        	drawTexturedModalRect(cornerX + 173, cornerY + 29, 10, 167, 10, 40);
        }
    }

    public void initGui ()
    {
        super.initGui();
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;

        this.setExpanded(false);
        this.buttonList.clear();
        GuiButton button = new DrawbridgeButton(0, cornerX + 131, cornerY + 18, 176, 0, 21, 22);
        if (logic.getPlacementDirection() == 0)
            button.enabled = false;
        this.buttonList.add(button);
        button = new DrawbridgeButton(1, cornerX + 146, cornerY + 34, 199, 23, 22, 21);
        if (logic.getPlacementDirection() == 1)
            button.enabled = false;
        this.buttonList.add(button);
        button = new DrawbridgeButton(2, cornerX + 132, cornerY + 48, 199, 0, 21, 22);
        if (logic.getPlacementDirection() == 2)
            button.enabled = false;
        this.buttonList.add(button);
        button = new DrawbridgeButton(3, cornerX + 117, cornerY + 34, 178, 23, 22, 21);
        if (logic.getPlacementDirection() == 3)
            button.enabled = false;
        this.buttonList.add(button);
        button = new DrawbridgeButton(4, cornerX + 135, cornerY + 40, 217, 0, 10, 10);
        if (logic.getPlacementDirection() == 4)
            button.enabled = false;
        this.buttonList.add(button);

        this.buttonList.add(new AdvDrawbridgeButton(5, this.width / 2 - 13, this.height / 2 - 52, this.width / 2 + 58, this.height / 2 - 79, 26, 26, "Inv"));
    }

    public void setExpanded (boolean flag)
    {
        this.isGuiExpanded = flag;
        this.containerNeglectMouse = true;
        Iterator<GuiButton> i1 = this.buttonList.iterator();
        while (i1.hasNext())
        {
            GuiButton b = i1.next();
            if (b instanceof AdvDrawbridgeButton)
            {
                ((AdvDrawbridgeButton) b).isGuiExpanded = flag;
            }
            else
            {
                b.drawButton = !flag;
            }
        }
        ((AdvancedDrawbridgeContainer) this.inventorySlots).updateContainerSlots();
    }

    protected void actionPerformed (GuiButton button)
    {
        if (button.id == 5)
        {
            setExpanded(!isGuiExpanded);
            return;
        }

        for (Object o : buttonList)
        {
            GuiButton b = (GuiButton) o;
            b.enabled = true;
        }
        button.enabled = false;

        logic.setPlacementDirection((byte) button.id);
        updateServer((byte) button.id);
    }

    void updateServer (byte direction)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(5);
            outputStream.writeInt(logic.worldObj.provider.dimensionId);
            outputStream.writeInt(logic.xCoord);
            outputStream.writeInt(logic.yCoord);
            outputStream.writeInt(logic.zCoord);
            outputStream.writeByte(direction);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "TConstruct";
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        PacketDispatcher.sendPacketToServer(packet);
    }

    @Override
    public void updateScreen ()
    {
        super.updateScreen();
        this.containerNeglectMouse = false;
    }
}
