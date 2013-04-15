package mods.tinker.tconstruct.client;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TTickHandler implements ITickHandler
{
    Minecraft mc = Minecraft.getMinecraft();
    EntityPlayer player;
    int zLevel = 0;
    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData) { }

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        player = mc.thePlayer;
        if (player == null || player.capabilities.isCreativeMode)
            return;
        
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/newhearts.png");
        
        int hp = player.getHealth();
        for (int iter = 0; iter < hp / 20; iter++)
        {
            int renderHearts = (hp - 20*(iter+1)) / 2;
            if (renderHearts > 10)
                renderHearts = 10;
            for (int i = 0; i < renderHearts; i++)
            {
                this.drawTexturedModalRect(122 + 8*i, 201, 0 + 18*iter, 0, 8, 8);
            }
            if (hp % 2 == 1 && renderHearts < 10)
            {
                this.drawTexturedModalRect(122 + 8*renderHearts, 201, 9 + 18*iter, 0, 8, 8);
            }
        }
        /*int renderHearts = (hp - 20) / 2;
        if (renderHearts > 10)
            renderHearts = 10;
        for (int i = 0; i < renderHearts; i++)
        {
            this.drawTexturedModalRect(122 + 8*i, 201, 0, 0, 8, 8);
        }
        
        renderHearts = (hp - 40) / 2;
        if (renderHearts > 10)
            renderHearts = 10;
        for (int i = 0; i < renderHearts; i++)
        {
            this.drawTexturedModalRect(122 + 8*i, 201, 18, 0, 8, 8);
        }
        
        renderHearts = (hp - 60) / 2;
        if (renderHearts > 10)
            renderHearts = 10;
        for (int i = 0; i < renderHearts; i++)
        {
            this.drawTexturedModalRect(122 + 8*i, 201, 36, 0, 8, 8);
        }
        
        renderHearts = (hp - 80) / 2;
        if (renderHearts > 10)
            renderHearts = 10;
        for (int i = 0; i < renderHearts; i++)
        {
            this.drawTexturedModalRect(122 + 8*i, 201, 54, 0, 8, 8);
        }*/
        //this.drawTexturedModalRect(178, 201, 9, 0, 8, 8);
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.RENDER);
        //return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel ()
    {
        return "Gui Overlay";
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
}
