package mods.tinker.tconstruct.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import mods.tinker.tconstruct.container.ActiveContainer;
import mods.tinker.tconstruct.container.SmelteryContainer;
import mods.tinker.tconstruct.logic.SmelteryLogic;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.network.PacketDispatcher;

public class SmelteryGui extends NewContainerGui
{
    public SmelteryLogic logic;
    String username;
    boolean isScrolling = false;
    boolean wasClicking;
    float currentScroll = 0.0F;
    int slotPos = 0;
    int prevSlotPos = 0;

    public SmelteryGui(InventoryPlayer inventoryplayer, SmelteryLogic smeltery, World world, int x, int y, int z)
    {
        super((ActiveContainer) smeltery.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = smeltery;
        username = inventoryplayer.player.username;
        xSize = 248;
    }

    public void drawScreen (int mouseX, int mouseY, float par3)
    {
        super.drawScreen(mouseX, mouseY, par3);
        updateScrollbar(mouseX, mouseY, par3);
    }

    protected void updateScrollbar (int mouseX, int mouseY, float par3)
    {
        if (logic.layers > 2)
        {
            boolean mouseDown = Mouse.isButtonDown(0);
            int lefto = this.guiLeft;
            int topo = this.guiTop;
            int xScroll = lefto + 67;
            int yScroll = topo + 8;
            int scrollWidth = xScroll + 14;
            int scrollHeight = yScroll + 144;

            if (!this.wasClicking && mouseDown && mouseX >= xScroll && mouseY >= yScroll && mouseX < scrollWidth && mouseY < scrollHeight)
            {
                this.isScrolling = true;
            }

            if (!mouseDown)
            {
                this.isScrolling = false;
            }

            if (wasClicking && !isScrolling && slotPos != prevSlotPos)
            {
                prevSlotPos = slotPos;
            }

            this.wasClicking = mouseDown;

            if (this.isScrolling)
            {
                this.currentScroll = ((float) (mouseY - yScroll) - 7.5F) / ((float) (scrollHeight - yScroll) - 15.0F);

                if (this.currentScroll < 0.0F)
                {
                    this.currentScroll = 0.0F;
                }

                if (this.currentScroll > 1.0F)
                {
                    this.currentScroll = 1.0F;
                }

                int s = ((SmelteryContainer) this.container).scrollTo(this.currentScroll);
                if (s != -1)
                    slotPos = s;
            }
        }
    }

    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        fontRenderer.drawString(StatCollector.translateToLocal("crafters.Smeltery"), 86, 5, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 90, (ySize - 96) + 2, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture("/mods/tinker/textures/gui/smeltery.png");
        int cornerX = (width - xSize) / 2 + 36;
        int cornerY = (height - ySize) / 2;
        drawTexturedModalRect(cornerX + 46, cornerY, 0, 0, 176, ySize);

        //Fuel - Lava
        if (logic.fuelGague > 0)
        {
            this.mc.renderEngine.bindTexture("/terrain.png");
            Icon lavaIcon = Block.lavaStill.getIcon(0, 0);
            int fuel = logic.getScaledFuelGague(52);
            int count = 0;
            while (fuel > 0)
            {
                int size = fuel >= 16 ? 16 : fuel;
                fuel -= size;
                drawTexturedModelRectFromIcon(cornerX + 117, (cornerY + 68) - size - 16 * count, lavaIcon, 12, size);
                count++;
            }
        }

        //Liquids - molten metal
        int base = 0;
        for (LiquidStack liquid : logic.moltenMetal)
        {
            Icon renderIndex;
            if (liquid.itemID < 4096) //Block
            {
                Block liquidBlock = Block.blocksList[liquid.itemID];
                this.mc.renderEngine.bindTexture("/terrain.png");
                renderIndex = liquidBlock.getIcon(0, liquid.itemMeta);
            }
            else
            //Item
            {
                Item liquidItem = Item.itemsList[liquid.itemID];
                this.mc.renderEngine.bindTexture("/gui/items.png");
                renderIndex = liquidItem.getIconFromDamage(liquid.itemMeta);
            }

            if (logic.getCapacity() > 0)
            {
                int liquidSize = liquid.amount * 52 / logic.getCapacity();
                while (liquidSize > 0)
                {
                    int size = liquidSize >= 16 ? 16 : liquidSize;
                    int basePos = 54;
                    drawTexturedModelRectFromIcon(cornerX + basePos, (cornerY + 68) - size - base, renderIndex, 16, size);
                    drawTexturedModelRectFromIcon(cornerX + basePos + 16, (cornerY + 68) - size - base, renderIndex, 16, size);
                    drawTexturedModelRectFromIcon(cornerX + basePos + 32, (cornerY + 68) - size - base, renderIndex, 16, size);
                    drawTexturedModelRectFromIcon(cornerX + basePos + 48, (cornerY + 68) - size - base, renderIndex, 4, size);
                    liquidSize -= size;
                    base += size;
                }
            }
        }

        //Liquid gague
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        mc.renderEngine.bindTexture("/mods/tinker/textures/gui/smeltery.png");
        drawTexturedModalRect(cornerX + 54, cornerY + 16, 176, 76, 52, 52);

        //Side inventory
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture("/mods/tinker/textures/gui/smelteryside.png");
        if (logic.layers > 0)
        {
            if (logic.layers == 1)
            {
                drawTexturedModalRect(cornerX - 46, cornerY, 0, 0, 98, 43);
                drawTexturedModalRect(cornerX - 46, cornerY + 43, 0, 133, 98, 25);
            }
            else if (logic.layers == 2)
            {
                drawTexturedModalRect(cornerX - 46, cornerY, 0, 0, 98, 61);
                drawTexturedModalRect(cornerX - 46, cornerY + 61, 0, 97, 98, 61);
            }
            else
            {
                drawTexturedModalRect(cornerX - 46, cornerY, 0, 0, 98, ySize - 8);
            }
            drawTexturedModalRect(cornerX + 32, (int) (cornerY + 8 + 127 * currentScroll), 98, 0, 12, 15);
        }

        //Temperature
        int slotSize = logic.layers * 9;
        if (slotSize > 24)
            slotSize = 24;
        for (int iter = 0; iter < slotSize; iter++)
        {
            int slotTemp = logic.getTempForSlot(iter + slotPos * 3) - 20;
            int maxTemp = logic.getMeltingPointForSlot(iter + slotPos * 3) - 20;
            if (slotTemp > 0 && maxTemp > 0)
            {
                int size = 16 * slotTemp / maxTemp + 1;
                drawTexturedModalRect(cornerX - 38 + (iter % 3 * 22), cornerY + 8 + (iter / 3 * 18) + 16 - size, 98, 15 + 16 - size, 5, size);
            }
        }

        //fontRenderer.drawString("slotPos: "+slotPos, 140, 2, 0xFFFFFF);
        /*fontRenderer.drawString("Scrolling: "+isScrolling, 140, 12, 0xFFFFFF);
        fontRenderer.drawString("Scroll: "+currentScroll, 140, 22, 0xFFFFFF);*/
    }
    
    public void drawLiquidRect(int par1, int par2, Icon par3Icon, int par4, int par5)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par5), (double)this.zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMaxV());
        tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + par5), (double)this.zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMaxV());
        tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + 0), (double)this.zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMinV());
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMinV());
        tessellator.draw();
    }
}
