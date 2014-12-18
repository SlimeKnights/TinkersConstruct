package tconstruct.weaponry.client;

import tconstruct.library.weaponry.ProjectileWeapon;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class AmmoSlotHandler {
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event)
    {
        if(event.type != RenderGameOverlayEvent.ElementType.HOTBAR)
            return;

        EntityPlayer player =  Minecraft.getMinecraft().thePlayer;
        ItemStack equipped = player.getCurrentEquippedItem();

        if(equipped == null || equipped.getItem() == null)
            return;
        if(!(equipped.getItem() instanceof ProjectileWeapon))
            return;

        // find ammo
        // todo: cache this somehow?
        ItemStack ammo = ((ProjectileWeapon) equipped.getItem()).searchForAmmo(player, equipped);
        if(ammo == null)
            return;

        // check if it's in the inventory
        int slot = -1;
        for(int i = 0; i < 9; i++)
            if(player.inventory.mainInventory[i] == ammo)
            {
                slot = i;
                break;
            }

        // not in the hotbar
        if(slot == -1)
            return;

        int x = event.resolution.getScaledWidth() / 2 - 90 + slot * 20 + 2;
        int z = event.resolution.getScaledHeight() - 16 - 3;

        int col = 0;
        col |= 120 << 16; // red
        col |= 150 <<  8; // green
        col |= 200 <<  0; // blue
        col |= 200 << 24; // alpha

        // render a cool underlay thing
        Gui.drawRect(x,z, x+16, z+16, col);
    }
}
