package tconstruct.common;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import tconstruct.TConstruct;
import tconstruct.library.modifier.IModifyable;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class AbilityTick implements ITickHandler
{

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {

    }

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        for (TPlayerStats stats : TConstruct.playerTracker.getServerStatList().values())
        {
            EntityPlayer player = stats.player.get();
            if (stats.climbWalls)
            {
                double motionX = player.posX - player.lastTickPosX;
                double motionZ = player.posZ - player.lastTickPosZ;
                double motionY = player.posY - player.lastTickPosY - 0.762;
                if (motionY > 0.0D && (motionX == 0D || motionZ == 0D))
                {
                    player.fallDistance = 0.0F;
                }
            }
            ItemStack stack = player.inventory.getStackInSlot(8);
            if (stack != null && stack.getItem() instanceof ItemMap)
            {
                stack.getItem().onUpdate(stack, player.worldObj, player, 8, true);
            }
            if (!player.isPlayerSleeping())
            {
                ItemStack chest = player.getCurrentArmor(2);
                if (chest == null || !(chest.getItem() instanceof IModifyable))
                {
                    player.setSize(0.6F, 1.8F);
                }
                else
                {
                    NBTTagCompound tag = chest.getTagCompound().getCompoundTag(((IModifyable) chest.getItem()).getBaseTagName());
                    int dodge = tag.getInteger("Perfect Dodge");
                    if (dodge > 0)
                    {
                        player.setSize(Math.max(0.15F, 0.6F - (dodge * 0.09f)), 1.8F - (dodge * 0.04f));
                    }
                }
            }
        }
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.WORLD);
    }

    @Override
    public String getLabel ()
    {
        return null;
    }

}
