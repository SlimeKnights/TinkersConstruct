package mods.tinker.tconstruct.client.gui;

import mods.tinker.tconstruct.library.client.ToolGuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonTool extends GuiButton
{
    /**
     * True for pointing right (next page), false for pointing left (previous page).
     */
    int textureX;
    int textureY;
    public String texture;
    public ToolGuiElement element;

    private ResourceLocation background;
    
    public GuiButtonTool(int id, int posX, int posY, int texX, int texY, String tex, ToolGuiElement e)
    {
        super(id, posX, posY, 18, 18, "");
        textureX = texX;
        textureY = texY;
        texture = tex;
        element = e;
        background = new ResourceLocation(texture);
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton (Minecraft mc, int mouseX, int mouseY)
    {
        if (this.drawButton)
        {
            boolean var4 = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.func_110577_a(background);

            this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_82253_i);
            int index = 18 * getHoverState(field_82253_i);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 144 + index * 2, 216, 18, 18);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, textureX * 18, textureY * 18, 18, 18);
        }
    }
}
