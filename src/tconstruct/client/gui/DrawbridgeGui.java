package tconstruct.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.DrawbridgeLogic;
import cpw.mods.fml.common.network.PacketDispatcher;

public class DrawbridgeGui extends GuiContainer
{
    public DrawbridgeLogic logic;

    public DrawbridgeGui(InventoryPlayer inventoryplayer, DrawbridgeLogic frypan, World world, int x, int y, int z)
    {
        super(frypan.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = frypan;
    }

    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        //fontRenderer.drawString(StatCollector.translateToLocal("aggregator.glowstone"), 60, 6, 0x404040);
        fontRenderer.drawString("Drawbridge", 8, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/drawbridge.png");

    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = (width - xSize) / 2;
        int cornerY = (height - ySize) / 2;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
    }

    public void initGui ()
    {
        super.initGui();
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;

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
    }

    protected void actionPerformed (GuiButton button)
    {
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
}
