package tconstruct.smeltery.gui;

import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import tconstruct.TConstruct;
import tconstruct.client.gui.NewContainerGui;
import tconstruct.smeltery.inventory.*;
import tconstruct.smeltery.logic.SmelteryLogic;
import tconstruct.util.network.SmelteryPacket;

public class SmelteryGui extends NewContainerGui
{
    public SmelteryLogic logic;
    private boolean isScrolling = false;
    private boolean wasClicking;
    private float currentScroll = 0.0F;
    private int slotPos = 0;
    private int prevSlotPos = 0;

    private final int columns;
    public static final int maxRows = 8;

    public SmelteryGui(InventoryPlayer inventoryplayer, SmelteryLogic smeltery, World world, int x, int y, int z)
    {
        super((ActiveContainer) smeltery.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = smeltery;
        xSize = 248;
        smeltery.updateFuelDisplay();

        columns = ((SmelteryContainer)this.container).columns;
    }

    @Override
    public void initGui ()
    {
        super.initGui();

        if (logic != null)
            logic.updateFuelGague();
    }

    @Override
    public void drawScreen (int mouseX, int mouseY, float par3)
    {
        super.drawScreen(mouseX, mouseY, par3);
        updateScrollbar(mouseX, mouseY, par3);
    }

    protected void updateScrollbar (int mouseX, int mouseY, float par3)
    {
        if (logic.getBlockCapacity() > columns*maxRows)
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
                this.currentScroll = (mouseY - yScroll - 7.5F) / (scrollHeight - yScroll - 15.0F);

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

    @Override
    protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY)
    {
        fontRendererObj.drawString(StatCollector.translateToLocal("crafters.Smeltery"), 86, 5, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 90, (ySize - 96) + 2, 0x404040);

        int base = 0;
        int cornerX = (width - xSize) / 2 + 36;
        int cornerY = (height - ySize) / 2;

        // molten metals
        for (FluidStack liquid : logic.moltenMetal)
        {
            int basePos = 54;
            int initialLiquidSize = 0;
            int liquidSize = 0;// liquid.amount * 52 / liquidLayers;
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
                drawFluidStackTooltip(liquid, mouseX - cornerX + 36, mouseY - cornerY);

            }
        }
        // lava/fuel
        if (logic.fuelGague > 0)
        {
            int leftX = cornerX + 117;
            int topY = (cornerY + 68) - logic.getScaledFuelGague(52);
            int sizeX = 12;
            int sizeY = logic.getScaledFuelGague(52);
            if (mouseX >= leftX && mouseX <= leftX + sizeX && mouseY >= topY && mouseY < topY + sizeY)
            {
                drawFluidStackTooltip(new FluidStack(-37, logic.fuelAmount), mouseX - cornerX + 36, mouseY - cornerY);
            }
        }
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/smeltery.png");
    private static final ResourceLocation backgroundSide = new ResourceLocation("tinker", "textures/gui/smelteryside.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float f, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = (width - xSize) / 2 + 36;
        int cornerY = (height - ySize) / 2;
        drawTexturedModalRect(cornerX + 46, cornerY, 0, 0, 176, ySize);

        // Fuel - Lava
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        if (logic.fuelGague > 0)
        {
            IIcon lavaIcon = logic.getFuelIcon();
            int fuel = logic.getScaledFuelGague(52);
            int count = 0;
            while (fuel > 0)
            {
                int size = fuel >= 16 ? 16 : fuel;
                fuel -= size;
                drawLiquidRect(cornerX + 117, (cornerY + 68) - size - 16 * count, lavaIcon, 12, size);
                count++;
            }
        }

        // Liquids - molten metal
        int base = 0;
        for (FluidStack liquid : logic.moltenMetal)
        {
            IIcon renderIndex = liquid.getFluid().getStillIcon();
            int basePos = 54;
            if (logic.getCapacity() > 0)
            {
                int total = logic.getTotalLiquid();
                int liquidLayers = (total / 20000 + 1) * 20000;
                if (liquidLayers > 0)
                {
                    int liquidSize = liquid.amount * 52 / liquidLayers;
                    if (liquidSize == 0)
                        liquidSize = 1;
                    while (liquidSize > 0)
                    {
                        int size = liquidSize >= 16 ? 16 : liquidSize;
                        if (renderIndex != null)
                        {
                            drawLiquidRect(cornerX + basePos, (cornerY + 68) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 16, (cornerY + 68) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 32, (cornerY + 68) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 48, (cornerY + 68) - size - base, renderIndex, 4, size);
                        }
                        liquidSize -= size;
                        base += size;
                    }
                }
            }
        }

        // Liquid gague
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(cornerX + 54, cornerY + 16, 176, 76, 52, 52);

        // Side inventory
        int xleft = 46;
        xleft += 22 * (columns-3); // we have to shift the whole thing to the left if we have more than 3 columns
        int h = logic.getBlockCapacity()/columns;
        if(logic.getBlockCapacity()%columns != 0)
            h++;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(backgroundSide);
        if (logic.getBlockCapacity() > 0)
        {
            if(h >= 8)
            {
                // standard 3 slots
                drawTexturedModalRect(cornerX - xleft, cornerY, 0, 0, 72, ySize - 8);
                // additional slots
                for(int i = 0; i < columns-3; i++)
                    drawTexturedModalRect(cornerX - xleft + 72 + i*22, cornerY, 50, 0, 22, ySize - 8);
                // right end
                drawTexturedModalRect(cornerX - 46 + 72, cornerY, 72, 0, 25, ySize - 8);

                int sx = cornerX + 32;
                int sy = (int) (cornerY + 8 + 127 * currentScroll);
                // highlighted scroll bar
                if(isScrolling || ( mouseX >= sx && mouseX <= sx + 12 && mouseY >= sy && mouseY <= sy + 15))
                    drawTexturedModalRect(sx, sy, 122, 0, 12, 15);
                // scroll bar
                else
                    drawTexturedModalRect(sx, sy, 98, 0, 12, 15);
            }
            else
            {
                int yd = 43 + 18*(h-3);
                // slots
                // standard 3 slots
                drawTexturedModalRect(cornerX - xleft, cornerY, 0, 0, 72, yd);
                // additional slots
                for(int i = 0; i < columns-3; i++)
                    drawTexturedModalRect(cornerX - xleft + 72 + i*22, cornerY, 50, 0, 22, yd);
                // right end
                drawTexturedModalRect(cornerX - 46 + 72, cornerY, 72, 0, 25, yd);



                // bottom end
                drawTexturedModalRect(cornerX - xleft, cornerY + yd, 0, 133, 72, 25);
                for(int i = 0; i < columns-3; i++)
                    drawTexturedModalRect(cornerX - xleft + 72 + i*22, cornerY + yd, 50, 133, 22, 25);
                drawTexturedModalRect(cornerX - 46 + 72, cornerY + yd, 72, 133, 25, 25);

                // grayed out scroll bar
                drawTexturedModalRect(cornerX + 32, (int) (cornerY + 8 + 127 * currentScroll), 110, 0, 12, 15);
            }
        }

        xleft -= 8;
        // Temperature
        int slotSize = logic.getBlockCapacity();
        if (slotSize > columns*maxRows)
            slotSize = columns*maxRows;
        int iter;
        for (iter = 0; iter < slotSize && iter + slotPos*columns < logic.getBlockCapacity(); iter++)
        {
            int slotTemp = logic.getTempForSlot(iter + slotPos * columns) - 20;
            int maxTemp = logic.getMeltingPointForSlot(iter + slotPos * columns) - 20;
            if (slotTemp > 0 && maxTemp > 0)
            {
                int size = 16 * slotTemp / maxTemp + 1;
                drawTexturedModalRect(cornerX - xleft + (iter % columns * 22), cornerY + 8 + (iter / columns * 18) + 16 - size, 98, 15 + 16 - size, 5, size);
            }
        }

        // hide nonexistant slots
        int maxSlots = Math.min(maxRows, h) * columns;
        for (; iter < maxSlots; iter++)
        {
            drawTexturedModalRect(cornerX - xleft + (iter % columns * 22)-1, cornerY + 8 + (iter / columns * 18)-1, 98, 47, 22, 18);
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
            list.add("\u00A7f" + StatCollector.translateToLocal("gui.smeltery.fuel"));
            list.add("mB: " + liquid.amount);
        }
        else
        {
            String name = liquid.getFluid().getLocalizedName();
            list.add("\u00A7f" + name);
            if (name.equals(StatCollector.translateToLocal("fluid.emerald.liquid")))
            {
                list.add(StatCollector.translateToLocal("gui.smeltery.emerald") + liquid.amount / 640f);
            }
            else if (name.equals(StatCollector.translateToLocal("fluid.glass.molten")))
            {
                int blocks = liquid.amount / 1000;
                if (blocks > 0)
                    list.add(StatCollector.translateToLocal("gui.smeltery.glass.block") + blocks);
                int panels = (liquid.amount % 1000) / 250;
                if (panels > 0)
                    list.add(StatCollector.translateToLocal("gui.smeltery.glass.pannel") + panels);
                int mB = (liquid.amount % 1000) % 250;
                if (mB > 0)
                    list.add("mB: " + mB);
            }
            else if (name.equals(StatCollector.translateToLocal("fluid.stone.seared")))
            {
                int ingots = liquid.amount / TConstruct.ingotLiquidValue;
                if (ingots > 0)
                    list.add(StatCollector.translateToLocal("gui.smeltery.glass.block") + ingots);
                int mB = liquid.amount % TConstruct.ingotLiquidValue;
                if (mB > 0)
                    list.add("mB: " + mB);
            }
            else if (isMolten(name))
            {
                int ingots = liquid.amount / TConstruct.ingotLiquidValue;
                if (ingots > 0)
                    list.add(StatCollector.translateToLocal("gui.smeltery.metal.ingot") + ingots);
                int mB = liquid.amount % TConstruct.ingotLiquidValue;
                if (mB > 0)
                {
                    int nuggets = mB / TConstruct.nuggetLiquidValue;
                    int junk = (mB % TConstruct.nuggetLiquidValue);
                    if (nuggets > 0)
                        list.add(StatCollector.translateToLocal("gui.smeltery.metal.nugget") + nuggets);
                    if (junk > 0)
                        list.add("mB: " + junk);
                }
            }
            else
            {
                list.add("mB: " + liquid.amount);
            }
        }
        return list;
    }

    private boolean isMolten (String fluidName)
    {
        boolean molten = false;
        String[] moltenNames = StatCollector.translateToLocal("gui.smeltery.molten.check").split(",");

        for (int i = 0; i < moltenNames.length; i++)
        {
            if (fluidName.contains(moltenNames[i].trim()))
            {
                molten = true;
                break;
            }
        }

        return molten;
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
                int l = this.fontRendererObj.getStringWidth(s);

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
                this.fontRendererObj.drawStringWithShadow(s1, i1, j1, -1);

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

    public void drawLiquidRect (int startU, int startV, IIcon icon, int endU, int endV)
    {
        float top = icon.getInterpolatedV(16 - endV);
        float bottom = icon.getMaxV();
        float left = icon.getMinU();
        float right = icon.getInterpolatedU(endU);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(startU + 0, startV + endV, this.zLevel, left, bottom);//Bottom left
        tessellator.addVertexWithUV(startU + endU, startV + endV, this.zLevel, right, bottom);//Bottom right
        tessellator.addVertexWithUV(startU + endU, startV + 0, this.zLevel, right, top);//Top right
        tessellator.addVertexWithUV(startU + 0, startV + 0, this.zLevel, left, top); //Top left
        tessellator.draw();
    }

    @Override
    public void mouseClicked (int mouseX, int mouseY, int mouseButton)
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
            int liquidSize = 0;// liquid.amount * 52 / liquidLayers;
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

                TConstruct.packetPipeline.sendToServer(new SmelteryPacket(logic.getWorldObj().provider.dimensionId, logic.xCoord, logic.yCoord, logic.zCoord, this.isShiftKeyDown(), fluidToBeBroughtUp));
            }
        }
    }
}
