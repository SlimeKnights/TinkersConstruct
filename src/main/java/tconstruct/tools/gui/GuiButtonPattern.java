package tconstruct.tools.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tconstruct.library.client.PatternGuiElement;
import tconstruct.library.client.ToolGuiElement;

@SideOnly(Side.CLIENT)
public class GuiButtonPattern extends GuiButton
{
    public String texture;
    protected String domain;
    public PatternGuiElement element;
    public boolean pressed = false;
    private static ResourceLocation background;// = new
                                               // ResourceLocation("tinker",
                                               // "textures/gui/armorextended.png");

    public GuiButtonPattern (int id, int posX, int posY, PatternGuiElement e)
    {
        super(id, posX, posY, 18, 18, "");
        element = e;
        texture = e.texture;
        domain = e.domain;
        background = new ResourceLocation(domain, texture);
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton (Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            if(pressed)
            {
                if(this.field_146123_n)
                {
                    //Pressed, hovered.
                    GL11.glColor4f(0.75F, 0.75F, 0.75F, 1.0F);
                }
                else
                {
                    //Pressed, mouse off.
                    GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
                }
            }
            else
            {
                if(this.field_146123_n)
                {
                    //Up, hovered.
                    GL11.glColor4f(1.25F, 1.25F, 1.25F, 1.0F);
                }
                else
                {
                    //Up, mouse off.
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
            mc.getTextureManager().bindTexture(background);

            //int var5 = this.getHoverState(this.field_146123_n);
            //int index = 18 * getHoverState(field_146123_n);
            //this.drawTexturedModalRect(this.xPosition, this.yPosition, 144 + index * 2, 216, 18, 18);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, 18, 18);
        }
    }
}
