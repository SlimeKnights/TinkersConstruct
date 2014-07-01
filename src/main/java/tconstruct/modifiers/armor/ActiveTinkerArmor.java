package tconstruct.modifiers.armor;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
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
import tconstruct.library.tools.AbilityHelper;
import tconstruct.util.player.TPlayerStats;

public class ActiveTinkerArmor extends ActiveArmorMod
{
    @Override
    public void onArmorTick (World world, EntityPlayer player, ItemStack itemStack, ArmorCore armor, ArmorPart type)
    {
        NBTTagCompound tag = itemStack.getTagCompound().getCompoundTag(((IModifyable) itemStack.getItem()).getBaseTagName());
        if (tag.hasKey("Moss"))
        {
            int chance = tag.getInteger("Moss");
            int check = world.canBlockSeeTheSky((int) player.posX, (int) player.posY, (int) player.posZ) ? 350 : 1150;
            if (TConstruct.random.nextInt(check) < chance)
            {
                int current = tag.getInteger("Damage");
                if (current > 0)
                    tag.setInteger("Damage", current-1);
            }
        }
        if (type == ArmorPart.Head)
        {
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.getPersistentID());
            if (stats.activeGoggles)
            {
                if (tag.getBoolean("Night Vision"))
                    player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
            }
            
            /*List list = world.getEntitiesWithinAABB(EntityItem.class, player.boundingBox.addCoord(0.0D, 0.0D, 0.0D).expand(5.0D, 5.0D, 5.0D)); //TODO: Add modifier code
            for (int k = 0; k < list.size(); k++)
            {
                EntityItem entity = (EntityItem) list.get(k);
                entity.onCollideWithPlayer(player);
            }*/
        }
        if (type == ArmorPart.Chest)
        {
            if (player.isSneaking() && tag.getBoolean("Stealth"))
                player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 2, 0, true));

            /*int sprintboost = tag.getInteger("Sprint Assist");
            if (player.isSprinting() && sprintboost > 0)
                player.moveEntityWithHeading(-player.moveStrafing * 0.1f * sprintboost, player.moveForward * 0.2F * sprintboost); //Max of 0-1*/
        }
        if (type == ArmorPart.Feet)
        {
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
