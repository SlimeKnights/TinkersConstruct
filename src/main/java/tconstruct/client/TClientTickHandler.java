package tconstruct.client;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import tconstruct.TConstruct;
import tconstruct.world.TinkerWorld;
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
        TinkerWorld.oreBerry.setGraphicsLevel(Blocks.leaves.field_150121_P);
        TinkerWorld.oreBerrySecond.setGraphicsLevel(Blocks.leaves.field_150121_P);
        TinkerWorld.slimeLeaves.setGraphicsLevel(Blocks.leaves.field_150121_P);
        if (mc.thePlayer != null && mc.thePlayer.onGround)
            controlInstance.landOnGround();
    }

    /*
     * @Override public EnumSet<TickType> ticks () { return
     * EnumSet.of(TickType.RENDER); }
     */
}
