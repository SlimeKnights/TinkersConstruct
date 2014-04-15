package tconstruct.client;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
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
        if (player != null && mc.thePlayer.onGround)
        {
            controlInstance.landOnGround();
        }
        
        if (player != null && player.isCollidedHorizontally && !player.isSneaking())
        {
            player.motionY = 0.1176D;
            player.fallDistance = 0.0f;
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
