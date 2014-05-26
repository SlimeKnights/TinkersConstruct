package tconstruct.client;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.items.armor.TravelGear;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.modifier.IModifyable;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TClientTickHandler implements ITickHandler
{
    Minecraft mc = Minecraft.getMinecraft();
    TControls controlInstance = ((TProxyClient) TConstruct.proxy).controlInstance;
    ItemStack prevFeet;
    double prevMotionY;
    boolean morphed;

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        TContent.oreBerry.setGraphicsLevel(Block.leaves.graphicsLevel);
        TContent.oreBerrySecond.setGraphicsLevel(Block.leaves.graphicsLevel);
        TContent.slimeLeaves.setGraphicsLevel(Block.leaves.graphicsLevel);
        EntityPlayer player = getPlayer();

        if (player != null)
        {
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
            if (mc.thePlayer.onGround)
            {
                controlInstance.landOnGround();
            }
            if (stats.climbWalls && player.isCollidedHorizontally && !player.isSneaking())
            {
                player.motionY = 0.1176D;
                player.fallDistance = 0.0f;
            }
            ItemStack feet = player.getCurrentArmor(0);
            if (feet != null && feet.getItem() instanceof IModifyable)
            {
                if (!player.isSneaking())
                {
                    NBTTagCompound tag = feet.getTagCompound().getCompoundTag(((IModifyable) feet.getItem()).getBaseTagName());
                    int sole = tag.getInteger("Slimy Soles");
                    if (sole > 0)
                    {
                        if (!player.isSneaking() && player.onGround && prevMotionY < -0.4)
                            player.motionY = -prevMotionY * (Math.min(0.99, sole * 0.2));
                    }
                }
                prevMotionY = player.motionY;
            }
            if (feet != prevFeet)
            {
                if (prevFeet != null && prevFeet.getItem() instanceof TravelGear)
                    player.stepHeight -= 0.6f;
                if (feet != null && feet.getItem() instanceof TravelGear)
                    player.stepHeight += 0.6f;
                prevFeet = feet;
            }
            if (!player.isPlayerSleeping() && !morphed)
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
                        player.setSize(Math.max(0.1F, 0.6F - (dodge * 0.1f)), 1.8F - (dodge * 0.04f));
                    }
                }
            }
        }
    }

    EntityPlayer getPlayer ()
    {
        return mc.thePlayer;
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public String getLabel ()
    {
        return null;
    }
}
