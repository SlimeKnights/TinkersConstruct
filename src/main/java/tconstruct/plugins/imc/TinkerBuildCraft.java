package tconstruct.plugins.imc;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import tconstruct.TConstruct;
import tconstruct.smeltery.TinkerSmeltery;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers BuildCraft Compatibility", description = "Tinkers Construct compatibility for BC Transport", modsRequired = "BuildCraft|Transport")
public class TinkerBuildCraft
{
    @Handler
    public void init (FMLInitializationEvent event)
    {
        TConstruct.logger.info("BuildCraft detected. Registering facades.");
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
