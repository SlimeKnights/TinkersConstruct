package tconstruct.plugins.imc;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.TConstruct;
import tconstruct.smeltery.TinkerSmeltery;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers Buildcraft addon", description = "The Builcraft addon for Tinkers.", modsRequired = "BuildCraft|Transport", forced = true)
public class TinkerBuildcraft {
    
    @Handler
    public void init (FMLInitializationEvent event)
    {
        TConstruct.logger.info("[BC|Transport] Registering facades.");
        // Smeltery Blocks
        addFacade(TinkerSmeltery.smeltery, 2);
        for (int sc = 4; sc < 11; sc++)
        {
            addFacade(TinkerSmeltery.smeltery, sc);
        }

        addFacade(TinkerSmeltery.searedBlock, 0);
        addFacade(TinkerSmeltery.searedBlockNether, 0);
    }
    
    private void addFacade (Block b, int meta)
    {
        FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", new ItemStack(b, 1, meta));
    }
    
}
