package mods.tinker.tconstruct.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TurnPageButton extends GuiButton
{
    /**
     * True for pointing right (next page), false for pointing left (previous page).
     */
    private final boolean nextPage;

    public TurnPageButton(int par1, int par2, int par3, boolean par4)
    {
        super(par1, par2, par3, 23, 13, "");
        this.nextPage = par4;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            boolean var4 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            par1Minecraft.renderEngine.bindTexture("/mods/tinker/textures/gui/bookleft.png");
            int var5 = 0;
            int var6 = 192;

            if (var4)
            {
                var5 += 23;
            }

            if (!this.nextPage)
            {
                var6 += 13;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, var5, var6, 23, 13);
        }
    }
}
