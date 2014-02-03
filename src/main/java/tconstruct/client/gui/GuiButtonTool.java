package tconstruct.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import tconstruct.library.client.ToolGuiElement;

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
    private static ResourceLocation background;// = new ResourceLocation("tinker", "textures/gui/armorextended.png");

    public GuiButtonTool(int id, int posX, int posY, int texX, int texY, String domain, String tex, ToolGuiElement e)
    {
        super(id, posX, posY, 18, 18, "");
        textureX = texX;
        textureY = texY;
        texture = tex;
        element = e;
        background = new ResourceLocation(domain, tex);
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton (Minecraft mc, int mouseX, int mouseY)
    {
        if (this.field_146125_m)
        {
            boolean var4 = mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(background);

            this.field_146123_n = mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g;
            int var5 = this.func_146114_a(this.field_146123_n);
            int index = 18 * func_146114_a(field_146123_n);
            this.drawTexturedModalRect(this.field_146128_h, this.field_146129_i, 144 + index * 2, 216, 18, 18);
            this.drawTexturedModalRect(this.field_146128_h, this.field_146129_i, textureX * 18, textureY * 18, 18, 18);
        }
    }
}
