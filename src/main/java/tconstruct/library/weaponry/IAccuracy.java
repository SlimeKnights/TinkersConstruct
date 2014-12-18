package tconstruct.library.weaponry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IAccuracy {
    public float minAccuracy(ItemStack itemStack);
    public float maxAccuracy(ItemStack itemStack);
    public float getAccuracy(ItemStack itemStack, int time);
}
