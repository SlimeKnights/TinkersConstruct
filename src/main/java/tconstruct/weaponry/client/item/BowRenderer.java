package tconstruct.weaponry.client.item;

import tconstruct.client.FlexibleToolRenderer;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.weaponry.entity.ArrowEntity;
import tconstruct.library.weaponry.ProjectileWeapon;
import tconstruct.library.weaponry.IWindup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import tconstruct.weaponry.entity.BoltEntity;

public class BowRenderer extends FlexibleToolRenderer {
    private static final ArrowEntity dummy = new ArrowEntity(null);

    @Override
    protected void specialAnimation(ItemRenderType type, ItemStack item) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        GL11.glTranslatef(0.5f, 0.5f, 0);
        GL11.glScalef(0.5f, 0.5f, 0.5f);

        if(type == ItemRenderType.EQUIPPED)
        {
            //GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
            GL11.glTranslatef(0.2F, -0.4F, 0.2f);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            //GL11.glScalef(f1, -f1, f1);
            //GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        }

        // windup animation
        if(type == ItemRenderType.EQUIPPED_FIRST_PERSON && player.isUsingItem())
        {
            float progress = ((IWindup) item.getItem()).getWindupProgress(item, player);

            //GL11.glScalef(1.2f, 1.2f, 1.2f);
            //float progress = ((IWindup) item.getItem()).getWindupProgress(item, Minecraft.getMinecraft().thePlayer);;
            GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F); // tilts the bow forward a bit
            GL11.glRotatef(-6.0F, 0.0F, 1.0F, 0.0F); // rotates the bow so it faces more forward
            GL11.glRotatef(8.0F, 1.0F, 0.0F, 0.0F); // rotates the bow forward
            GL11.glTranslatef(-0.9F, 0.2F, -1.0F);


            // this does the "zoom closer to the bow" thing
            GL11.glTranslatef(progress * -0.1f, progress * -0.125f, 0.0F);



            // we're crazy, so.. render the arrow =D
            ItemStack ammo = ((ProjectileWeapon) item.getItem()).searchForAmmo(player, item); // we know it's a projectile weapon, it's a bow!
            if(ammo != null) {
                if(ammo.getItem() == Items.arrow)
                    dummy.returnStack = ArrowAmmo.vanillaArrow;
                else
                    dummy.returnStack = ammo;
                Render renderer = RenderManager.instance.getEntityClassRenderObject(ArrowEntity.class);

                GL11.glPushMatrix();
                // adjust position
                GL11.glScalef(2, 2, 2); // bigger
                GL11.glRotatef(90, 0, 1, 0); // rotate it into the same direction as the bow
                GL11.glRotatef(15, 0, 1, 0); // rotate it a bit more so it's not directly inside the bow
                GL11.glRotatef(-45, 1, 0, 0); // sprite is rotated by 45° in the graphics, correct that
                GL11.glTranslatef(0.05f, 0, 0); // same as the not-inside-bow-rotation

                // move the arrow with the charging process
                float offset = 0.075f - 0.15f * progress;

                GL11.glTranslatef(0, 0, offset);

                // render iiit
                renderer.doRender(dummy, 0, 0, 0, 0, 0);
                GL11.glPopMatrix();
            }
        }
        else if(type == ItemRenderType.EQUIPPED && player.isUsingItem())
        {
            // we're crazy, so.. render the arrow =D
            ItemStack ammo = ((ProjectileWeapon) item.getItem()).searchForAmmo(player, item); // we know it's a projectile weapon, it's a bow!
            if(ammo != null) {
                if(ammo.getItem() == Items.arrow || !(ammo.getItem() instanceof ArrowAmmo))
                    dummy.returnStack = ArrowAmmo.vanillaArrow;
                else
                    dummy.returnStack = ammo;
                Render renderer = RenderManager.instance.getEntityClassRenderObject(ArrowEntity.class);

                GL11.glPushMatrix();
                // adjust position
                GL11.glScalef(2, 2, 2); // bigger
                GL11.glRotatef(90, 0, 1, 0); // rotate it into the same direction as the bow
                GL11.glRotatef(15, 0, 1, 0); // rotate it a bit more so it's not directly inside the bow
                GL11.glRotatef(-45, 1, 0, 0); // sprite is rotated by 45° in the graphics, correct that
                GL11.glTranslatef(0.05f, 0, 0); // same as the not-inside-bow-rotation

                // move the arrow with the charging process
                float offset = 0.075f - 0.15f * 1;

                GL11.glTranslatef(0, 0, offset);

                // render iiit
                renderer.doRender(dummy, 0, 0, 0, 0, 0);
                GL11.glPopMatrix();
            }
        }


        if(item.getItem() == TinkerWeaponry.longbow)
            GL11.glScalef(2.3f, 2.3f, 1.0f);
        else if(item.getItem() == TinkerWeaponry.shortbow)
            GL11.glScalef(1.7f, 1.7f, 1.0f);

        GL11.glTranslatef(-0.5f, -0.5f, 0f);
    }
}
