package boni.tinkersweaponry.library.weaponry;

import boni.tinkersweaponry.client.CrosshairType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * This class has a windup time, meaning it takes some time to reach full potency.
 * Windup progress is visualized with a special crosshair.
 */
public interface IWindup {
    public int getWindupTime(ItemStack itemStack); // how long it takes to fully wind up
    public float getWindupProgress(ItemStack itemStack, EntityPlayer player); // how far we've winded up, 0.0-1.0
    public float getMinWindupProgress(ItemStack itemStack); // how long it has been winded up at least to fire (0.0-1.0)

    public CrosshairType getCrosshairType();

    public boolean zoomOnWindup(ItemStack itemStack);
    public float getZoom(ItemStack itemStack);
}
