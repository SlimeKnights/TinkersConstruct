package tconstruct.client;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.common.TRepo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;

public class TClientTickHandler
{
    Minecraft mc = Minecraft.getMinecraft();

    //TControls controlInstance = ((TProxyClient)TConstruct.proxy).controlInstance;
    public TClientTickHandler()
    {
    }

    @SubscribeEvent
    public void onTick (ClientTickEvent event)
    {
        
        if (event.phase.equals(Phase.END) && event.type.equals(Type.RENDER))
        {
            TRepo.oreBerry.setGraphicsLevel(Blocks.leaves.graphicsLevel);
            TRepo.oreBerrySecond.setGraphicsLevel(Blocks.leaves.graphicsLevel);
            TRepo.slimeLeaves.setGraphicsLevel(Blocks.leaves.graphicsLevel);
            /*if (mc.thePlayer != null && !mc.thePlayer.isAirBorne)
                controlInstance.landOnGround();*/
        }
    }

  /*  @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.RENDER);
    }
*/
}
