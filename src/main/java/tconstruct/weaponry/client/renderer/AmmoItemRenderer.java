package boni.tinkersweaponry.client.renderer;

import boni.tinkersweaponry.library.weaponry.IAmmo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class AmmoItemRenderer extends FlexibleToolRenderer {
    //public static FontRenderer fontRenderer;

    public AmmoItemRenderer() {
//        super(true);
        //fontRenderer = new FontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation(Reference.RESOURCE, "textures/font/border_numbers.png"), Minecraft.getMinecraft().renderEngine, false);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if(type == ItemRenderType.INVENTORY)
            return true;

        return super.handleRenderType(item, type);
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        // render the item regularly
        super.renderItem(type, item, data);
        //RenderItem.getInstance().renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, item, 0,0);

        if(item.getTagCompound() == null || type != ItemRenderType.INVENTORY)
            return;

        // render custom stacksize
        renderAmmoCount(item);
    }

    public void renderAmmoCount(ItemStack item)
    {
        if(!(item.getItem() instanceof IAmmo))
            return;
        int amount = ((IAmmo) item.getItem()).getAmmoCount(item);
        String str = String.valueOf(amount);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glScalef(0.7f, 0.7f, 0.7f);
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawStringWithShadow(str, 7 + 19 - 2 - fontRenderer.getStringWidth(str), 7 + 6 + 3, 16777215);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
