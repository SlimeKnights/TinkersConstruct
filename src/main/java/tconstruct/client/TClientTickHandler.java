package tconstruct.client;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
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
        if (mc.thePlayer != null && mc.thePlayer.onGround)
        {
            controlInstance.landOnGround();
        }
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
