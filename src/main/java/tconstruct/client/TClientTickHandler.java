package tconstruct.client;

import java.util.EnumSet;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TClientTickHandler implements ITickHandler
{
    Minecraft mc = Minecraft.getMinecraft();
    TControls controlInstance = ((TProxyClient) TConstruct.proxy).controlInstance;

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

            //GL11.glFogf(GL11.GL_FOG_DENSITY, 0.01F);
        }
    }

    EntityPlayer getPlayer ()
    {
        return mc.thePlayer;
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.RENDER);
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
