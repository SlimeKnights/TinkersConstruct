package tconstruct.weaponry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.weaponry.IAmmo;

public class WeaponryActiveToolMod extends ActiveToolMod {
    @Override
    public boolean damageTool(ItemStack stack, int damage, EntityLivingBase entity) {
        // does not proc on tool 'healing'
        if(stack.getItem() == TinkerWeaponry.javelin && stack.hasTagCompound() && damage > 0) {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            IAmmo ammo =(IAmmo)stack.getItem();
            if(tags.getInteger("Damage") == 0)
            {
                int rem = ammo.consumeAmmo(1, stack);
                if(rem > 0)
                    return true;
            }
            else if(ammo.getAmmoCount(stack) > 0)
            {
                int d = tags.getInteger("Damage") + damage;
                int max = tags.getInteger("TotalDurability");
                if(d > max)
                {
                    tags.setInteger("Damage", 0);
                    return true;
                }
            }
        }
        // all other ammo items can't be damaged
        else if(stack.getItem() instanceof IAmmo)
            return true;

        return false;
    }
}
