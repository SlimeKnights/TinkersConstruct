package tconstruct.client;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.items.armor.TravelGear;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TClientTickHandler implements ITickHandler
{
    Minecraft mc = Minecraft.getMinecraft();
    TControls controlInstance = ((TProxyClient) TConstruct.proxy).controlInstance;
    ItemStack prevFeet;

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
            if (feet != prevFeet)
            {
                if (prevFeet != null && prevFeet.getItem() instanceof TravelGear)
                    player.stepHeight -= 0.6f;
                if (feet != null && feet.getItem() instanceof TravelGear)
                    player.stepHeight += 0.6f;
                prevFeet = feet;
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
