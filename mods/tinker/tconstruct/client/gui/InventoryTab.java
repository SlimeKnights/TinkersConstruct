package mods.tinker.tconstruct.client.gui;

import mods.tinker.tconstruct.client.TControls;
import mods.tinker.tconstruct.client.TProxyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InventoryTab extends GuiButton
{
    int textureX;
    String texture = "/gui/allitems.png";
    ItemStack renderStack;

    public InventoryTab(int id, int posX, int posY, ItemStack stack, int texX)
    {
        super(id, posX, posY, 28, 32, "");
        textureX = texX;
        renderStack = stack;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.drawButton)
        {            
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(texture);
            
            int yTexPos = this.enabled ? 0 : 32;
            int ySize = this.enabled ? 28 : 32;
            
            this.drawTexturedModalRect(this.xPosition, this.yPosition, textureX*28, yTexPos, 28, ySize);

            RenderHelper.enableGUIStandardItemLighting();
            this.zLevel = 100.0F;
            TProxyClient.itemRenderer.zLevel = 100.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            TProxyClient.itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, renderStack, xPosition+6, yPosition+8);
            TProxyClient.itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, renderStack, xPosition+6, yPosition+8);
            GL11.glDisable(GL11.GL_LIGHTING);
            TProxyClient.itemRenderer.zLevel = 0.0F;
            this.zLevel = 0.0F;
            RenderHelper.disableStandardItemLighting();
        }
    }
    
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        boolean inWindow = this.enabled && this.drawButton && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        if (inWindow)
        {
            if (this.id == 2)
                TProxyClient.openInventoryGui();
            if (this.id == 3)
                TControls.openArmorGui();
            if (this.id == 4)
                TControls.openKnapsackGui();
        }
        return inWindow;
    }
}
