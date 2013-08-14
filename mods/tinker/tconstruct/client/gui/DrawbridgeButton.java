package mods.tinker.tconstruct.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DrawbridgeButton extends GuiButton
{
    /**
     * True for pointing right (next page), false for pointing left (previous page).
     */
    int uPos;
    int vPos;
    public DrawbridgeButton(int id, int xPos, int yPos, int u, int v, int xSize, int ySize)
    {
        super(id, xPos, yPos, xSize, ySize, "");
        uPos = u;
        vPos = v;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton (Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            boolean hovering = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            par1Minecraft.renderEngine.bindTexture("/mods/tinker/textures/gui/drawbridge.png");
            int offset = 0;
            if (!this.enabled)
                offset = 46;
            else if (hovering)
                offset = 92;

            this.drawTexturedModalRect(this.xPosition, this.yPosition, uPos, vPos + offset, width, height);
        }
    }
}
