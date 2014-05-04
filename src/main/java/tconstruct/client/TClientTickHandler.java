package tconstruct.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import tconstruct.TConstruct;
import tconstruct.common.TRepo;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TClientTickHandler
{
    Minecraft mc = Minecraft.getMinecraft();

    TControls controlInstance = ((TProxyClient) TConstruct.proxy).controlInstance;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void tickEnd (ClientTickEvent event)
    {
        TRepo.oreBerry.setGraphicsLevel(Blocks.leaves.field_150121_P);
        TRepo.oreBerrySecond.setGraphicsLevel(Blocks.leaves.field_150121_P);
        TRepo.slimeLeaves.setGraphicsLevel(Blocks.leaves.field_150121_P);
        EntityPlayer player = getPlayer();
        if (player != null ){
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.getUniqueID());
            if (mc.thePlayer.onGround)
            {
                controlInstance.landOnGround();
            }
            if (stats.climbWalls && player.isCollidedHorizontally && !player.isSneaking())
            {
                player.motionY = 0.1176D;
                player.fallDistance = 0.0f;
            }

        }

    }

    EntityPlayer getPlayer ()
    {
        return mc.thePlayer;
    }
}
