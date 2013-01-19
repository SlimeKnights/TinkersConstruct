package tinker.tconstruct.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

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
    String texture;

    public GuiButtonTool(int id, int posX, int posY, int texX, int texY, String tex)
    {
        super(id, posX, posY, 18, 18, "");
        textureX = texX;
        textureY = texY;
        texture = tex;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.drawButton)
        {
            boolean var4 = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(mc.renderEngine.getTexture(texture));

            this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_82253_i);
            int index = 18 * getHoverState(field_82253_i);
            //System.out.println("Hover: "+getHoverState(enabled));
            this.drawTexturedModalRect(this.xPosition, this.yPosition, index, 0, 18, 18);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, textureX*18, textureY*18, 18, 18);
        }
    }
}
