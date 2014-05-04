package tconstruct.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import tconstruct.client.TProxyClient;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonItem extends GuiButton
{
    /**
     * True for pointing right (next page), false for pointing left (previous page).
     */
    public ItemStack element;
    private static ResourceLocation background = new ResourceLocation("tinker", "textures/gui/icons.png");;
    private static mantle.client.RenderItemCopy renderitem = new mantle.client.RenderItemCopy();
    private static mantle.client.block.SmallFontRenderer fonts = TProxyClient.smallFontRenderer;
    private static Minecraft mc = Minecraft.getMinecraft();

    public GuiButtonItem(int id, int posX, int posY, ItemStack stack)
    {
        super(id, posX, posY, 18, 18, "");
        element = stack;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton (Minecraft mc, int mouseX, int mouseY)
    {
        if (this.enabled)
        {
            boolean var4 = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(background);

            this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_82253_i);
            int index = 18 * getHoverState(field_82253_i);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 144 + index * 2, 216, 18, 18);
            RenderHelper.enableGUIStandardItemLighting();
            renderitem.renderItemAndEffectIntoGUI(fonts, mc.renderEngine, element, this.xPosition + 1, this.yPosition + 1);
            RenderHelper.disableStandardItemLighting();
        }
    }
}
