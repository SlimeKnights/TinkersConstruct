package mods.tinker.tconstruct.client;

import java.util.EnumSet;

import mods.tinker.tconstruct.common.TContent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TClientTickHandler implements ITickHandler
{
    Minecraft mc = Minecraft.getMinecraft();
    EntityPlayer player;
    int zLevel = 0;
    int ticks = 0;

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        TContent.oreBerry.setGraphicsLevel(Block.leaves.graphicsLevel);
        TContent.oreBerrySecond.setGraphicsLevel(Block.leaves.graphicsLevel);
        
        player = mc.thePlayer;
        if (player == null || player.capabilities.isCreativeMode)
            return;
        
        /*ticks++;
        if (ticks >= 200)
        {
            player.setEntityHealth(50);
            ticks = 0;
        }*/
        
        ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int scaledWidth = scaledresolution.getScaledWidth();
        int scaledHeight = scaledresolution.getScaledHeight();
        int xBasePos = scaledWidth / 2 - 91;
        int yBasePos = scaledHeight - 39;
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/newhearts.png");
        
        int hp = player.getHealth();
        for (int iter = 0; iter < hp / 20; iter++)
        {
            int renderHearts = (hp - 20*(iter+1)) / 2;
            if (renderHearts > 10)
                renderHearts = 10;
            for (int i = 0; i < renderHearts; i++)
            {
                this.drawTexturedModalRect(xBasePos + 8*i, yBasePos, 0 + 18*iter, 0, 8, 8);
            }
            if (hp % 2 == 1 && renderHearts < 10)
            {
                this.drawTexturedModalRect(xBasePos + 8*renderHearts, yBasePos, 9 + 18*iter, 0, 8, 8);
            }
        }
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.RENDER);
        //return EnumSet.of(TickType.PLAYER);
    }

    public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + 0) * f), (double)((float)(par4 + par6) * f1));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + par5) * f), (double)((float)(par4 + par6) * f1));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + par5) * f), (double)((float)(par4 + 0) * f1));
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + 0) * f), (double)((float)(par4 + 0) * f1));
        tessellator.draw();
    }

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getLabel ()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
