package tconstruct.client.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public abstract class AbstractTab extends GuiButton
{
    ResourceLocation texture = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    ItemStack renderStack;
    RenderItem itemRenderer = new RenderItem();

    public AbstractTab(int id, int posX, int posY, ItemStack renderStack)
    {
        super(id, posX, posY, 28, 32, "");
        this.renderStack = renderStack;
    }

    @Override
    public void drawButton (Minecraft mc, int mouseX, int mouseY)
    {
        if (this.field_146125_m)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            int yTexPos = this.field_146124_l ? 0 : 32;
            int ySize = this.field_146124_l ? 28 : 32;
            int xOffset = this.field_146127_k == 2 ? 0 : 1;

            mc.renderEngine.bindTexture(this.texture);
            this.drawTexturedModalRect(this.field_146128_h, this.field_146129_i, xOffset * 28, yTexPos, 28, ySize);

            RenderHelper.enableGUIStandardItemLighting();
            this.zLevel = 100.0F;
            this.itemRenderer.zLevel = 100.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            this.itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, renderStack, field_146128_h + 6, field_146129_i + 8);
            this.itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, renderStack, field_146128_h + 6, field_146129_i + 8);
            GL11.glDisable(GL11.GL_LIGHTING);
            this.itemRenderer.zLevel = 0.0F;
            this.zLevel = 0.0F;
            RenderHelper.disableStandardItemLighting();
        }
    }

    @Override
    public boolean mousePressed (Minecraft mc, int mouseX, int mouseY)
    {
        boolean inWindow = this.field_146124_l && this.field_146125_m && mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g;

        if (inWindow)
        {
            this.onTabClicked();
        }

        return inWindow;
    }

    public abstract void onTabClicked ();

    public abstract boolean shouldAddToList ();
}
