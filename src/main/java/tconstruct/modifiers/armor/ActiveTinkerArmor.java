package tconstruct.modifiers.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.ArmorPart;
import tconstruct.library.modifier.ActiveArmorMod;
import tconstruct.library.modifier.IModifyable;
import tconstruct.util.player.TPlayerStats;

public class ActiveTinkerArmor extends ActiveArmorMod
{
    @Override
    public void onArmorTick (World world, EntityPlayer player, ItemStack itemStack, ArmorCore armor, ArmorPart type)
    {
        if (type == ArmorPart.Head)
        {
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
            if (stats.activeGoggles)
            {
                NBTTagCompound tag = itemStack.getTagCompound().getCompoundTag(((IModifyable) itemStack.getItem()).getBaseTagName());
                if (tag.getBoolean("Night Vision"))
                    player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
            }
        }
        if (type == ArmorPart.Chest)
        {
            NBTTagCompound tag = itemStack.getTagCompound().getCompoundTag(armor.getBaseTagName());
            if (player.isSneaking() && tag.getBoolean("Stealth"))
                player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 2, 0, true));
        }
        if (type == ArmorPart.Feet)
        {
            NBTTagCompound tag = itemStack.getTagCompound().getCompoundTag(armor.getBaseTagName());
            if (player.isInWater())
            {
                if (!player.isSneaking() && tag.getBoolean("WaterWalk") && player.motionY <= 0)
                {
                    player.motionY = 0;
                }
                if (tag.getBoolean("LeadBoots"))
                {
                    if (player.motionY > 0)
                        player.motionY *= 0.5f;
                    else if (player.motionY < 0)
                        player.motionY *= 1.5f;
                }
            }
        }
    }
}
