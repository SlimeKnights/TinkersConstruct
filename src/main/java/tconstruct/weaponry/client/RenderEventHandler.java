package boni.tinkersweaponry.client;

import boni.tinkersweaponry.TinkerWeaponry;
import boni.tinkersweaponry.library.weaponry.BowBaseAmmo;
import boni.tinkersweaponry.library.weaponry.IWindup;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class RenderEventHandler {
    @SubscribeEvent
    public void onAimZoom(FOVUpdateEvent event)
    {
        if(!event.entity.isUsingItem())
            return;

        if(!(event.entity.getItemInUse().getItem() instanceof IWindup))
            return;

        ItemStack weapon = event.entity.getItemInUse();
        IWindup item = (IWindup) weapon.getItem();

        if(item.zoomOnWindup(weapon))
            event.newfov = event.fov / (event.fov + (item.getZoom(weapon)-1.0f) * item.getWindupProgress(weapon, event.entity));
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event)
    {
        if(event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == TinkerWeaponry.javelin) {

        }

        if(event.entityPlayer.getItemInUse() == null)
            return;

        if(event.entityPlayer.getItemInUse().getItem() instanceof BowBaseAmmo) {
            event.renderer.modelBipedMain.aimedBow = true;
            event.renderer.modelArmor.aimedBow = true;
            event.renderer.modelArmorChestplate.aimedBow = true;
        }
    }
}
