package tconstruct.tools.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
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
    public int index = 0;
    private ResourceLocation background;// = new
                                               // ResourceLocation("tinker",
                                               // "textures/gui/armorextended.png");

    public GuiButtonPattern (int id, int posX, int posY, PatternGuiElement e)
    {
        super(id, posX, posY, 16, 16, "");
        element = e;
        texture = e.texture;
        domain = e.domain;
        background = new ResourceLocation(domain, texture + ".png");

        width = 16;
        height = 16;
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
            if(!enabled)
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
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            //GL11.glEnable(GL11.GL_BLEND);
            //OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            mc.getTextureManager().bindTexture(background);
            //mc.renderEngine.get

            //int var5 = this.getHoverState(this.field_146123_n);
            //int index = 18 * getHoverState(field_146123_n);

            //this.drawTexturedModalRect(this.xPosition, this.yPosition, 0 , 0, 16, 16);

            //DrawTexturedModalRect is strange.
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(xPosition , yPosition + height, 0, 0.0, 1.0);
            tessellator.addVertexWithUV(xPosition + width, yPosition + height, 0, 1.0, 1.0);
            tessellator.addVertexWithUV(xPosition + width, yPosition , 0, 1.0, 0.0);
            tessellator.addVertexWithUV(xPosition , yPosition , 0, 0.0, 0.0);
            tessellator.draw();
        }
    }
}
