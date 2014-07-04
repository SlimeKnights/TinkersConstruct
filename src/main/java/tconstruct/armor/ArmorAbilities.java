package tconstruct.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ArmorAbilities
{
    //Abilities
    boolean morphed;
    boolean morphLoaded = Loader.isModLoaded("Morph");

    @SubscribeEvent
    public void playerTick (TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        TPlayerStats stats = TPlayerStats.get(player);
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
        //TODO: Proper minimap support
        /*ItemStack stack = player.inventory.getStackInSlot(8);
        if (stack != null && stack.getItem() instanceof ItemMap)
        {
            stack.getItem().onUpdate(stack, player.worldObj, player, 8, true);
        }*/
        if (!player.isPlayerSleeping())
        {
            ItemStack chest = player.getCurrentArmor(2);
            if (chest == null || !(chest.getItem() instanceof IModifyable))
            {
                if (!morphLoaded || !morphed)
                    PlayerAbilityHelper.setEntitySize(player, 0.6F, 1.8F);
            }
            else
            {
                NBTTagCompound tag = chest.getTagCompound().getCompoundTag(((IModifyable) chest.getItem()).getBaseTagName());
                int dodge = tag.getInteger("Perfect Dodge");
                if (dodge > 0)
                {
                    if (!morphLoaded || !morphed)
                        PlayerAbilityHelper.setEntitySize(player, Math.max(0.15F, 0.6F - (dodge * 0.09f)), 1.8F - (dodge * 0.04f));
                }
            }
        }
    }
}
