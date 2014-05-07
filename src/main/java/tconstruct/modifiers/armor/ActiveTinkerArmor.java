package tconstruct.modifiers.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.ArmorPart;
import tconstruct.library.modifier.ActiveArmorMod;

public class ActiveTinkerArmor extends ActiveArmorMod
{
    @Override
    public void onArmorTick (World world, EntityPlayer player, ItemStack itemStack, ArmorCore armor, ArmorPart type)
    {
        if (type == ArmorPart.Feet)
        {
            if (player.isInWater() && player.motionY <= 0)
            {
                player.motionY = 0;
            }
        }
    }
}
