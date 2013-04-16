package mods.tinker.tconstruct.server;

import mods.tinker.tconstruct.TProxyCommon;
import mods.tinker.tconstruct.player.TCommonTickHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class TProxyServer extends TProxyCommon
{
    public void registerTickHandler()
    {
        //TickRegistry.registerTickHandler(new TCommonTickHandler(), Side.SERVER);
    }
}
