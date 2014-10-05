package tconstruct.weaponry.client.renderer;

import tconstruct.weaponry.library.weaponry.IWindup;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class JavelinRenderer extends AmmoItemRenderer {

    @Override
    protected void specialAnimation(ItemRenderType type, ItemStack item) {
        float progress = ((IWindup)item.getItem()).getWindupProgress(item, Minecraft.getMinecraft().thePlayer); // 0.5 while not winding

        if(type == ItemRenderType.EQUIPPED)
            GL11.glRotatef(90, 0, 0, 1);

        GL11.glRotatef(-45, 0, 0, 1);
        GL11.glScalef(1.5F, 2F, 1.5F);

        if(type == ItemRenderType.EQUIPPED_FIRST_PERSON)
            GL11.glTranslatef(0, progress * 0.4f, 0);
        GL11.glRotatef(45, 0, 0, 1);
    }
}
