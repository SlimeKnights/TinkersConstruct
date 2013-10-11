package tconstruct.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tconstruct.blocks.logic.AdaptiveSmelteryLogic;
import tconstruct.inventory.ActiveContainer;
import tconstruct.inventory.AdaptiveSmelteryContainer;

public class AdaptiveSmelteryGui extends NewContainerGui
{
    public AdaptiveSmelteryLogic logic;
    String username;
    boolean isScrolling = false;
    boolean wasClicking;
    float currentScroll = 0.0F;
    int slotPos = 0;
    int prevSlotPos = 0;

    public AdaptiveSmelteryGui(InventoryPlayer inventoryplayer, AdaptiveSmelteryLogic smeltery, World world, int x, int y, int z)
    {
        super((ActiveContainer) smeltery.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = smeltery;
        username = inventoryplayer.player.username;
        xSize = 248;
        //smeltery.updateFuelDisplay();
    }

    public void drawScreen (int mouseX, int mouseY, float par3)
    {
        super.drawScreen(mouseX, mouseY, par3);
        updateScrollbar(mouseX, mouseY, par3);
    }

    protected void updateScrollbar (int mouseX, int mouseY, float par3)
    {
        if (logic.getSizeInventory() > 24)
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

                int s = ((AdaptiveSmelteryContainer) this.container).scrollTo(this.currentScroll);
                if (s != -1)
                    slotPos = s;
            }
        }
    }

    protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY)
    {
        fontRenderer.drawString(StatCollector.translateToLocal("crafters.Smeltery"), 86, 5, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 90, (ySize - 96) + 2, 0x404040);

        int base = 0;
        int cornerX = (width - xSize) / 2 + 36;
        int cornerY = (height - ySize) / 2;
        FluidTankInfo[] info = logic.getTankInfo(ForgeDirection.UNKNOWN);

        int capacity = 0;

        for (int i = 0; i < info.length - 1; i++)
        {
            FluidStack liquid = info[i].fluid;
            if (liquid != null)
                capacity += info[i].capacity;
        }

        for (int i = 0; i < info.length - 1; i++)
        {
            FluidStack liquid = info[i].fluid;
            int basePos = 54;
            int initialLiquidSize = 0;
            int liquidSize = 0;
            if (capacity > 0)
            {
                liquidSize = liquid.amount * 52 / capacity;
                if (liquidSize == 0)
                    liquidSize = 1;
                base += liquidSize;

            }

            int leftX = cornerX + basePos;
            int topY = (cornerY + 68) - base;
            int sizeX = 52;
            int sizeY = liquidSize;
            if (mouseX >= leftX && mouseX <= leftX + sizeX && mouseY >= topY && mouseY < topY + sizeY)
            {
                drawFluidStackTooltip(liquid, mouseX - cornerX + 36, mouseY - cornerY);

            }
        }
        /*if (logic.fuelGague > 0)
        {
            int leftX = cornerX + 117;
            int topY = (cornerY + 68) - logic.getScaledFuelGague(52);
            int sizeX = 12;
            int sizeY = logic.getScaledFuelGague(52);
            if (mouseX >= leftX && mouseX <= leftX + sizeX && mouseY >= topY && mouseY < topY + sizeY)
            {
                drawFluidStackTooltip(new FluidStack(-37, logic.fuelAmount), mouseX - cornerX + 36, mouseY - cornerY);
            }
        }*/
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/smeltery.png");
    private static final ResourceLocation backgroundSide = new ResourceLocation("tinker", "textures/gui/smelteryside.png");
    private static final ResourceLocation terrain = new ResourceLocation("terrain.png");

    protected void drawGuiContainerBackgroundLayer (float f, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = (width - xSize) / 2 + 36;
        int cornerY = (height - ySize) / 2;
        drawTexturedModalRect(cornerX + 46, cornerY, 0, 0, 176, ySize);

        //Fuel - Lava
        /*this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        if (logic.fuelGague > 0)
        {
            Icon lavaIcon = Block.lavaStill.getIcon(0, 0);
            int fuel = logic.getScaledFuelGague(52);
            int count = 0;
            while (fuel > 0)
            {
                int size = fuel >= 16 ? 16 : fuel;
                fuel -= size;
                drawLiquidRect(cornerX + 117, (cornerY + 68) - size - 16 * count, lavaIcon, 12, size);
                count++;
            }
        }*/

        FluidTankInfo[] info = logic.getTankInfo(ForgeDirection.UNKNOWN);
        int capacity = 0;

        for (int i = 0; i < info.length - 1; i++)
        {
            FluidStack liquid = info[i].fluid;
            if (liquid != null)
                capacity += info[i].capacity;
        }

        //Liquids - molten metal
        int base = 0;
        for (int i = 0; i < info.length - 1; i++)
        {
            FluidStack liquid = info[i].fluid;
            Icon renderIndex = liquid.getFluid().getStillIcon();
            int basePos = 54;
            if (capacity > 0)
            {
                int liquidSize = liquid.amount * 52 / capacity;
                if (liquidSize == 0)
                    liquidSize = 1;
                while (liquidSize > 0)
                {
                    int size = liquidSize >= 16 ? 16 : liquidSize;
                    drawLiquidRect(cornerX + basePos, (cornerY + 68) - size - base, renderIndex, 16, size);
                    drawLiquidRect(cornerX + basePos + 16, (cornerY + 68) - size - base, renderIndex, 16, size);
                    drawLiquidRect(cornerX + basePos + 32, (cornerY + 68) - size - base, renderIndex, 16, size);
                    drawLiquidRect(cornerX + basePos + 48, (cornerY + 68) - size - base, renderIndex, 4, size);
                    liquidSize -= size;
                    base += size;

                }
            }
        }

        //Liquid gague
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(cornerX + 54, cornerY + 16, 176, 76, 52, 52);

        //Side inventory
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(backgroundSide);
        //if (logic.layers > 0)
        {
            /*if (logic.layers == 1)
            {
                drawTexturedModalRect(cornerX - 46, cornerY, 0, 0, 98, 43);
                drawTexturedModalRect(cornerX - 46, cornerY + 43, 0, 133, 98, 25);
            }
            else if (logic.layers == 2)
            {
                drawTexturedModalRect(cornerX - 46, cornerY, 0, 0, 98, 61);
                drawTexturedModalRect(cornerX - 46, cornerY + 61, 0, 97, 98, 61);
            }
            else*/
            {
                drawTexturedModalRect(cornerX - 46, cornerY, 0, 0, 98, ySize - 8);
            }
            drawTexturedModalRect(cornerX + 32, (int) (cornerY + 8 + 127 * currentScroll), 98, 0, 12, 15);
        }

        //Temperature
        int slotSize = logic.getSizeInventory();
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
    }

    protected void drawFluidStackTooltip (FluidStack par1ItemStack, int par2, int par3)
    {
        this.zLevel = 100;
        List list = getLiquidTooltip(par1ItemStack, this.mc.gameSettings.advancedItemTooltips);

        for (int k = 0; k < list.size(); ++k)
        {
            list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
        }

        this.drawToolTip(list, par2, par3);
        this.zLevel = 0;
    }

    public List getLiquidTooltip (FluidStack liquid, boolean par2)
    {
        ArrayList list = new ArrayList();
        if (liquid.fluidID == -37)
        {
            list.add("\u00A7fFuel");
            int mB = liquid.amount;
            if (mB > 0)
                list.add("mB: " + mB);
        }
        else
        {
            String name = StatCollector.translateToLocal(FluidRegistry.getFluidName(liquid));
            list.add("\u00A7f" + name);
            if (name.equals("liquified emerald"))
            {
                float emeralds = liquid.amount / 320f;
                list.add("Emeralds: " + emeralds);
            }
            else if (name.contains("Molten"))
            {
                int ingots = liquid.amount / 144;
                if (ingots > 0)
                    list.add("Ingots: " + ingots);
                int mB = liquid.amount % 144;
                if (mB > 0)
                {
                    if (mB % 72 == 0)
                        list.add("Chunks: " + liquid.amount % 144 / 72);
                    else if (mB % 16 == 0)
                        list.add("Nuggets: " + liquid.amount % 144 / 16);
                    else
                        list.add("mB: " + mB);
                }
            }
            else if (name.equals("Seared Stone"))
            {
                int ingots = liquid.amount / 144;
                if (ingots > 0)
                    list.add("Blocks: " + ingots);
                int mB = liquid.amount % 144;
                if (mB > 0)
                {
                    list.add("mB: " + mB);
                }
            }
            else if (name.equals("Molten Glass"))
            {
                int ingots = liquid.amount / 1000;
                if (ingots > 0)
                    list.add("Blocks: " + ingots);
                int mB = liquid.amount % 144;
                if (mB > 0)
                {
                    list.add("mB: " + mB);
                }
            }
            else
            {
                int mB = liquid.amount;
                list.add("mB: " + mB);
            }
        }
        return list;
    }

    protected void drawToolTip (List par1List, int par2, int par3)
    {
        if (!par1List.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = par1List.iterator();

            while (iterator.hasNext())
            {
                String s = (String) iterator.next();
                int l = this.fontRenderer.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int i1 = par2 + 12;
            int j1 = par3 - 12;
            int k1 = 8;

            if (par1List.size() > 1)
            {
                k1 += 2 + (par1List.size() - 1) * 10;
            }

            if (i1 + k > this.width)
            {
                i1 -= 28 + k;
            }

            if (j1 + k1 + 6 > this.height)
            {
                j1 = this.height - k1 - 6;
            }

            this.zLevel = 300.0F;
            itemRenderer.zLevel = 300.0F;
            int l1 = -267386864;
            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

            for (int k2 = 0; k2 < par1List.size(); ++k2)
            {
                String s1 = (String) par1List.get(k2);
                this.fontRenderer.drawStringWithShadow(s1, i1, j1, -1);

                if (k2 == 0)
                {
                    j1 += 2;
                }

                j1 += 10;
            }

            this.zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
        }
    }

    public void drawLiquidRect (int startU, int startV, Icon par3Icon, int endU, int endV)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (startU + 0), (double) (startV + endV), (double) this.zLevel, (double) par3Icon.getMinU(), (double) par3Icon.getMaxV());//Bottom left
        tessellator.addVertexWithUV((double) (startU + endU), (double) (startV + endV), (double) this.zLevel, (double) par3Icon.getMaxU(), (double) par3Icon.getMaxV());//Bottom right
        tessellator.addVertexWithUV((double) (startU + endU), (double) (startV + 0), (double) this.zLevel, (double) par3Icon.getMaxU(), (double) par3Icon.getMinV());//Top right
        tessellator.addVertexWithUV((double) (startU + 0), (double) (startV + 0), (double) this.zLevel, (double) par3Icon.getMinU(), (double) par3Icon.getMinV()); //Top left
        tessellator.draw();
    }

    /*@Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	
    	int base = 0;
        int cornerX = (width - xSize) / 2 + 36;
        int cornerY = (height - ySize) / 2;
        int fluidToBeBroughtUp = -1;
        
        for (FluidStack liquid : logic.moltenMetal)
        {
            int basePos = 54;
            int initialLiquidSize = 0;
            int liquidSize = 0;//liquid.amount * 52 / liquidLayers;
            if (logic.getCapacity() > 0)
            {
                int total = logic.getTotalLiquid();
                int liquidLayers = (total / 20000 + 1) * 20000;
                if (liquidLayers > 0)
                {
                    liquidSize = liquid.amount * 52 / liquidLayers;
                    if (liquidSize == 0)
                        liquidSize = 1;
                    base += liquidSize;
                }
            }

            int leftX = cornerX + basePos;
            int topY = (cornerY + 68) - base;
            int sizeX = 52;
            int sizeY = liquidSize;
            if (mouseX >= leftX && mouseX <= leftX + sizeX && mouseY >= topY && mouseY < topY + sizeY)
            {
                fluidToBeBroughtUp = liquid.fluidID;
                	
                Packet250CustomPayload packet = new Packet250CustomPayload();
                	
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                	
                try
                {
            		dos.write(11);
            		
            		dos.writeInt(logic.worldObj.provider.dimensionId);
            		dos.writeInt(logic.xCoord);
            		dos.writeInt(logic.yCoord);
            		dos.writeInt(logic.zCoord);
            		
            		dos.writeBoolean(this.isShiftKeyDown());
            		
            		dos.writeInt(fluidToBeBroughtUp);
            	}
            	catch (Exception e)
            	{
            		e.printStackTrace();
            	}
            	
            	packet.channel = "TConstruct";
            	packet.data = bos.toByteArray();
            	packet.length = bos.size();
            	
            	PacketDispatcher.sendPacketToServer(packet);
            }
        }
    }*/
}
