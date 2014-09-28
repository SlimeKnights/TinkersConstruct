package tconstruct.armor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.Minecraft;
import tconstruct.client.ArmorControls;

public class ArmorTickHandler
{
    Minecraft mc = Minecraft.getMinecraft();

    ArmorControls controlInstance = ((ArmorProxyClient) TinkerArmor.proxy).controlInstance;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void tickEnd (ClientTickEvent event)
    {
        /*TinkerWorld.oreBerry.setGraphicsLevel(Blocks.leaves.field_150121_P);
        TinkerWorld.oreBerrySecond.setGraphicsLevel(Blocks.leaves.field_150121_P);
        TinkerWorld.slimeLeaves.setGraphicsLevel(Blocks.leaves.field_150121_P);*/
        if (mc.thePlayer != null && mc.thePlayer.onGround)
            controlInstance.landOnGround();
    }

    /*
     * @Override public EnumSet<TickType> ticks () { return
     * EnumSet.of(TickType.RENDER); }
     */
}
