package tconstruct.weaponry.client.item;

import tconstruct.client.FlexibleToolRenderer;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.weaponry.ammo.BoltAmmo;
import tconstruct.weaponry.entity.ArrowEntity;
import tconstruct.weaponry.entity.BoltEntity;
import tconstruct.weaponry.weapons.Crossbow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class CrossbowRenderer extends FlexibleToolRenderer {
    private static final BoltEntity dummy = new BoltEntity(null);

    @Override
    protected void specialAnimation(ItemRenderType type, ItemStack item) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        Crossbow crossbow = (Crossbow)item.getItem();

        GL11.glTranslatef(0.5f, 0.5f, 0);
        GL11.glScalef(0.5f, 0.5f, 0.5f);

        GL11.glScalef(1.5f, 1.5f, 1.5f);

        if(type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            // we're crazy, so.. render the arrow =D
            ItemStack ammo = crossbow.getLoadedAmmo(item);

            if(crossbow.isLoaded(item))
            {
                GL11.glTranslatef(0.0f, 0.0f, -0.3f);
                GL11.glRotatef(80f, 1f, 0f, 0f);
                GL11.glRotatef(15f, 0f, 1f, 0f);
                GL11.glRotatef(-20, 0, 0, 1);
            }
            else {
                GL11.glScalef(1.1f, 1.1f, 1.1f);
                GL11.glTranslatef(0.1f, 0f, 0f);
                GL11.glRotatef(50f, 1f, 0f, 0f);
            }

            if(ammo != null && ammo.hasTagCompound()) {
                //float progress = crossbow.getWindupProgress(item, player);
                float progress = 1f;
                dummy.returnStack = ammo;
                if(!(ammo.getItem() instanceof BoltAmmo))
                    dummy.returnStack = TinkerWeaponry.creativeBolt;

                Render renderer = RenderManager.instance.getEntityClassRenderObject(BoltEntity.class);

                GL11.glPushMatrix();
                // adjust position
                //GL11.glScalef(2, 2, 2); // bigger
                GL11.glRotatef(95, 0, 1, 0); // rotate it into the same direction as the bow
                //GL11.glRotatef(15, 0, 1, 0); // rotate it a bit more so it's not directly inside the bow
                GL11.glRotatef(-45, 1, 0, 0); // sprite is rotated by 45° in the graphics, correct that
                GL11.glTranslatef(0.05f, 0, 0); // same as the not-inside-bow-rotation

                // move the arrow with the charging process
                float offset = -0.15f;

                GL11.glTranslatef(0, 0, offset);

                // render iiit
                renderer.doRender(dummy, 0, 0, 0, 0, 0);
                GL11.glPopMatrix();
            }
        }


        if(type == ItemRenderType.EQUIPPED)
        {
            GL11.glTranslatef(0.25f, 0, 0);
            GL11.glRotatef(45.0F, 0.0F, 0.0F, 1.0F);
        }

        GL11.glTranslatef(-0.5f, -0.5f, 0f);
    }
}
