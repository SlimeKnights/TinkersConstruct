package tconstruct.plugins.fmp;

import codechicken.microblock.BlockMicroMaterial;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.TConstruct;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;

@Pulse(id = "Tinkers FMP Compatibility", description = "Makes Tinkers Blocks Multipart compatible", modsRequired = "ForgeMultipart", forced = true)
public class TinkerFMP {
    @Handler
    public void init(FMLInitializationEvent event)
    {

        TConstruct.logger.info("ForgeMultipart detected. Registering TConstruct decorative blocks with FMP.");

        //make Tconstruct blocks multipartable!
        if(TinkerWorld.metalBlock != null) {
            // metal blocks
            for (int i = 0; i < 11; i++)
                BlockMicroMaterial.createAndRegister(TinkerWorld.metalBlock, i);
        }

        if(TinkerSmeltery.smeltery != null) {
            // smeltery bricks
            for (int i = 2; i < 12; i++) {
                if (i == 3)
                    continue;
                BlockMicroMaterial.createAndRegister(TinkerSmeltery.smeltery, i);
                BlockMicroMaterial.createAndRegister(TinkerSmeltery.smelteryNether, i, TinkerSmeltery.smelteryNether.getUnlocalizedName() + "Nether");
            }

            // brownstone
            for (int i = 0; i < 7; i++)
                BlockMicroMaterial.createAndRegister(TinkerSmeltery.speedBlock, i);

            // clear glass
            BlockMicroMaterial.createAndRegister(TinkerSmeltery.clearGlass, 0);
            for (int i = 0; i < 16; i++)
                BlockMicroMaterial.createAndRegister(TinkerSmeltery.stainedGlassClear, i);
        }

        if(TinkerTools.multiBrick != null) {
            // chisel bricks
            for (int i = 0; i < 14; i++)
                BlockMicroMaterial.createAndRegister(TinkerTools.multiBrick, i);

            for (int i = 0; i < 16; i++)
                BlockMicroMaterial.createAndRegister(TinkerTools.multiBrickFancy, i);
        }

    }
}
