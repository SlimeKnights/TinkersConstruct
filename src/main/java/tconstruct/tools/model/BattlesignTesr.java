package tconstruct.tools.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class BattlesignTesr extends TileEntitySpecialRenderer
{

    @Override
    public void renderTileEntityAt (TileEntity te, double x, double y, double z, float something)
    {
        GL11.glPushMatrix();

        float f = 0.016666668F * 0.6666667F;

        GL11.glTranslated(x, y, z);
        GL11.glScalef(f, -f, f);
        //        GL11.glRotatef(180F, 0F, 0F, 1F);

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        switch (te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord))
        {
        case 0:
            GL11.glRotatef(-90F, 0F, 1F, 0F);
            GL11.glTranslatef(5F, -97F, -37F);
            break;
        case 1:
            GL11.glRotatef(90F, 0F, 1F, 0F);
            GL11.glTranslatef(-85F, -97F, 53F);
            break;
        case 2:
            GL11.glTranslatef(5F, -97F, 53F);
            break;
        case 3:
            GL11.glRotatef(180F, 0F, 1F, 0F);
            GL11.glTranslatef(-85F, -97F, -37F);
            break;
        }

        String strings[] = { "Chicken nuggets", "Curly fries", "fuj1n", "mDiyo", "Frosty_Chicken" };
        float lum = calcLuminance(te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord).colorMultiplier(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord));

        for (int i = 0; i < strings.length; i++)
        {
            fr.drawString((lum >= 35F ? "" : lum >= 31F ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.WHITE) + strings[i], -fr.getStringWidth(strings[i]) / 2 + 40, 10 * i, 0);
        }

        GL11.glPopMatrix();
    }

    private float calcLuminance (int rgb)
    {
        int r = (rgb & 0xff0000) >> 16;
        int g = (rgb & 0xff00) >> 8;
        int b = (rgb & 0xff);

        return (r * 0.299f + g * 0.587f + b * 0.114f) / 3;
    }
}
