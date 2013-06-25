package mods.tinker.tconstruct.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mods.tinker.tconstruct.blocks.logic.SmelteryLogic;
import mods.tinker.tconstruct.inventory.ActiveContainer;
import mods.tinker.tconstruct.inventory.SmelteryContainer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
        smeltery.updateFuelDisplay();
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

    protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY)
    {
        fontRenderer.drawString(StatCollector.translateToLocal("crafters.Smeltery"), 86, 5, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 90, (ySize - 96) + 2, 0x404040);
        
        int base = 0;
        int cornerX = (width - xSize) / 2 + 36;
        int cornerY = (height - ySize) / 2;
        for (LiquidStack liquid : logic.moltenMetal)
        {
            int basePos = 54;
            int initialLiquidSize = 0;
            int liquidSize = 0;//liquid.amount * 52 / liquidLayers;
            if (logic.getCapacity() > 0)
            {
                int total = logic.getTotalLiquid();
                int liquidLayers = (total / 20000 + 1) * 20000;
                liquidSize = liquid.amount * 52 / liquidLayers;
                base += liquidSize;
            }
            
            int leftX = cornerX + basePos;
            int topY =  (cornerY + 68) - base;
            int sizeX = 52;
            int sizeY = liquidSize;
            if (mouseX >= leftX && mouseX <= leftX + sizeX && mouseY >= topY && mouseY < topY + sizeY)
            {
                drawLiquidStackTooltip(liquid, mouseX - cornerX + 36, mouseY - cornerY);
            }
        }
        
        if (logic.fuelGague > 0)
        {
            int leftX = cornerX + 117;
            int topY =  (cornerY + 68) - logic.getScaledFuelGague(52);
            int sizeX = 12;
            int sizeY = logic.getScaledFuelGague(52);
            if (mouseX >= leftX && mouseX <= leftX + sizeX && mouseY >= topY && mouseY < topY + sizeY)
            {
                drawLiquidStackTooltip(new LiquidStack(Block.lavaStill.blockID, logic.fuelAmount, 0), mouseX - cornerX + 36, mouseY - cornerY);
            }
            /*this.mc.renderEngine.bindTexture("/terrain.png");
            Icon lavaIcon = Block.lavaStill.getIcon(0, 0);
            int fuel = logic.getScaledFuelGague(52);
            int count = 0;
            while (fuel > 0)
            {
                int size = fuel >= 16 ? 16 : fuel;
                fuel -= size;
                drawTexturedModelRectFromIcon(cornerX + 117, (cornerY + 68) - size - 16 * count, lavaIcon, 12, size);
                count++;
            }*/
        }
    }

    protected void drawGuiContainerBackgroundLayer (float f, int mouseX, int mouseY)
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

            int basePos = 54;
            if (logic.getCapacity() > 0)
            {
                int total = logic.getTotalLiquid();
                int liquidLayers = (total / 20000 + 1) * 20000;
                int liquidSize = liquid.amount * 52 / liquidLayers;
                while (liquidSize > 0)
                {
                    int size = liquidSize >= 16 ? 16 : liquidSize;
                    drawTexturedModelRectFromIcon(cornerX + basePos, (cornerY + 68) - size - base, renderIndex, 16, size);
                    drawTexturedModelRectFromIcon(cornerX + basePos + 16, (cornerY + 68) - size - base, renderIndex, 16, size);
                    drawTexturedModelRectFromIcon(cornerX + basePos + 32, (cornerY + 68) - size - base, renderIndex, 16, size);
                    drawTexturedModelRectFromIcon(cornerX + basePos + 48, (cornerY + 68) - size - base, renderIndex, 4, size);
                    liquidSize -= size;
                    base += size;
                }
            }
            
            /*int leftX = cornerX + basePos;
            int topY =  (cornerY + 68) - base;
            int sizeX = 52;
            int sizeY = base;
            if (mouseX >= leftX && mouseX <= leftX + sizeX && mouseY >= topY && mouseY <= topY + sizeY)
            {
                drawLiquidStackTooltip(liquid, mouseX, mouseY);
            }*/
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
    }
    
    protected void drawLiquidStackTooltip (LiquidStack par1ItemStack, int par2, int par3)
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
    
    public List getLiquidTooltip(LiquidStack liquid, boolean par2)
    {
        ArrayList list = new ArrayList();
        if (liquid.itemID == Block.lavaStill.blockID)
        {
            list.add("\u00A7fFuel");
            int mB = liquid.amount;
            if (mB > 0)
                list.add("mB: "+mB);
        }
        else
        {
        list.add("\u00A7f"+LiquidDictionary.findLiquidName(liquid));
        int ingots = liquid.amount / 144;
        if (ingots > 0)
            list.add("Ingots: "+ingots);
        int mB = liquid.amount % 144;
        if (mB > 0)
            list.add("mB: "+mB);
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
            /*GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);*/
        }
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
