package tconstruct.plugins.imc;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraftforge.fluids.Fluid;
import tconstruct.world.TinkerWorld;

import static tconstruct.smeltery.TinkerSmeltery.*;
import static tconstruct.smeltery.TinkerSmeltery.moltenEnderiumFluid;

@Pulse(id = "Tinkers RF-Tools Compatibility", forced = true, modsRequired = TinkerRfTools.modid)
public class TinkerRfTools {
    final static String modid = "rftools";

    @Handler
    public void preInit(FMLPreInitializationEvent event) {
        blacklist();

        configure(moltenObsidianFluid, 3000, 2500, 400, 3);
        configure(moltenGlassFluid, 2000, 2000, 250, 3);
        configure(moltenCopperFluid, 7500, 5000, 500, 4);
        configure(moltenAluminumFluid, 8500, 6000, 600, 4);
        configure(moltenTinFluid, 9000, 6000, 500, 4);
        configure(moltenIronFluid, 10000, 7500, 1000, 5);
        configure(moltenSteelFluid, 25000, 12500, 2000, 6);
        configure(moltenGoldFluid, 50000, 15000, 2500, 6);

        // precious fluids
        configure(moltenEnderFluid, 300000, 40000, 7000, 6);
        configure(moltenArditeFluid, 400000, 45000, 8000, 6);
        configure(moltenCobaltFluid, 500000, 50000, 10000, 6);
        configure(moltenEmeraldFluid, 350000, 40000, 5000, 6);

        // TE fluids
        configure(moltenLeadFluid, 15000, 1000, 800, 5);
        configure(moltenNickelFluid, 17000, 1027, 900, 5);
        configure(moltenSilverFluid, 45678, 14321, 1234, 6);

        // configure availability
        preventLoot(moltenSteelFluid, moltenGoldFluid, moltenEnderFluid, moltenArditeFluid, moltenCobaltFluid, moltenSilverFluid, moltenEmeraldFluid);
        preventGen(moltenCobaltFluid, moltenArditeFluid, moltenSteelFluid, moltenGoldFluid, moltenEmeraldFluid, moltenIronFluid);

        // configure ores
        if(TinkerWorld.oreSlag != null) {
            FMLInterModComms.sendMessage(modid, "dimlet_configure", String.format("Material.%s=%d,%d,%d,%d", "tile.tconstruct.stoneore1", 500000, 25000, 15000, 5));
            FMLInterModComms.sendMessage(modid, "dimlet_preventloot", String.format("Material.%s", "tile.tconstruct.stoneore1"));
            FMLInterModComms.sendMessage(modid, "dimlet_configure", String.format("Material.%s=%d,%d,%d,%d", "tile.tconstruct.stoneore2", 400000, 20000, 10000, 5));
            FMLInterModComms.sendMessage(modid, "dimlet_preventloot", String.format("Material.%s", "tile.tconstruct.stoneore2"));
            FMLInterModComms.sendMessage(modid, "dimlet_configure", String.format("Material.%s=%d,%d,%d,%d", "tile.tconstruct.stoneore3", 10000, 3000,   1000, 3));
            FMLInterModComms.sendMessage(modid, "dimlet_configure", String.format("Material.%s=%d,%d,%d,%d", "tile.tconstruct.stoneore4", 12000, 3333, 5000, 3));
            FMLInterModComms.sendMessage(modid, "dimlet_configure", String.format("Material.%s=%d,%d,%d,%d", "tile.tconstruct.stoneore5", 10000, 2800, 500, 3));
        }
    }

    private void configure(Fluid fluid, int create, int maintain, int ticks, int rarity) {
        if(fluid == null)
            return;

        FMLInterModComms.sendMessage(modid, "dimlet_configure", String.format("Liquid.%s=%d,%d,%d,%d", fluid.getName(), create, maintain, ticks, rarity));
    }

    private void preventLoot(Fluid... fluids) {
        for(Fluid fluid : fluids) {
            if (fluid == null)
                continue;

            FMLInterModComms.sendMessage(modid, "dimlet_preventloot", String.format("Liquid.%s", fluid.getName()));
        }
    }

    private void preventGen(Fluid... fluids) {
        for(Fluid fluid : fluids) {
            if (fluid == null)
                continue;

            FMLInterModComms.sendMessage(modid, "dimlet_preventworldgen", String.format("Liquid.%s", fluid.getName()));
        }
    }

    private void blacklist() {
        // blacklist alloys
        final Fluid[] fluidBlacklist = {
                // all alloys
                pigIronFluid,
                moltenBronzeFluid,
                moltenAlumiteFluid,
                moltenAlubrassFluid,
                moltenManyullynFluid,
                moltenInvarFluid,
                moltenElectrumFluid,
                moltenSignalumFluid,
                moltenLumiumFluid,
                moltenMithrilFluid,
                moltenEnderiumFluid,
                // and shiny
                moltenShinyFluid
        };

        for(Fluid fluid : fluidBlacklist) {
            if(fluid == null)
                continue;
            FMLInterModComms.sendMessage(modid, "dimlet_blacklist", "Liquid." + fluid.getName());
            FMLInterModComms.sendMessage(modid, "dimlet_configure", "Liquid." + fluid.getName() + "=999999,999999,999999,6");
            FMLInterModComms.sendMessage(modid, "dimlet_preventworldgen", "Liquid." + fluid.getName());
            FMLInterModComms.sendMessage(modid, "dimlet_preventloot", "Liquid." + fluid.getName());
        }

        // blacklist oreberry bushes
        if(TinkerWorld.oreBerry != null) {
            FMLInterModComms.sendMessage(modid, "dimlet_configure", "Material." + "tile.ore.berries.one=123456,123456,123456,6");
            FMLInterModComms.sendMessage(modid, "dimlet_configure", "Material." + "tile.ore.berries.one1=123456,123456,123456,6");
            FMLInterModComms.sendMessage(modid, "dimlet_configure", "Material." + "tile.ore.berries.one2=123456,123456,123456,6");
            FMLInterModComms.sendMessage(modid, "dimlet_configure", "Material." + "tile.ore.berries.one3=123456,123456,123456,6");
            FMLInterModComms.sendMessage(modid, "dimlet_blacklist", "Material." + "tile.ore.berries.one");
            FMLInterModComms.sendMessage(modid, "dimlet_blacklist", "Material." + "tile.ore.berries.one1");
            FMLInterModComms.sendMessage(modid, "dimlet_blacklist", "Material." + "tile.ore.berries.one2");
            FMLInterModComms.sendMessage(modid, "dimlet_blacklist", "Material." + "tile.ore.berries.one3");
            FMLInterModComms.sendMessage(modid, "dimlet_preventworldgen", "Material." + "tile.ore.berries.one");
            FMLInterModComms.sendMessage(modid, "dimlet_preventworldgen", "Material." + "tile.ore.berries.one1");
            FMLInterModComms.sendMessage(modid, "dimlet_preventworldgen", "Material." + "tile.ore.berries.one2");
            FMLInterModComms.sendMessage(modid, "dimlet_preventworldgen", "Material." + "tile.ore.berries.one3");
            FMLInterModComms.sendMessage(modid, "dimlet_preventloot", "Material." + "tile.ore.berries.one");
            FMLInterModComms.sendMessage(modid, "dimlet_preventloot", "Material." + "tile.ore.berries.one1");
            FMLInterModComms.sendMessage(modid, "dimlet_preventloot", "Material." + "tile.ore.berries.one2");
            FMLInterModComms.sendMessage(modid, "dimlet_preventloot", "Material." + "tile.ore.berries.one3");
        }
        if(TinkerWorld.oreBerrySecond != null) {
            FMLInterModComms.sendMessage(modid, "dimlet_configure", "Material." + "tile.ore.berries.two=123456,123456,123456,6");
            FMLInterModComms.sendMessage(modid, "dimlet_configure", "Material." + "tile.ore.berries.two1=123456,123456,123456,6");
            FMLInterModComms.sendMessage(modid, "dimlet_blacklist", "Material." + "tile.ore.berries.two");
            FMLInterModComms.sendMessage(modid, "dimlet_blacklist", "Material." + "tile.ore.berries.two1");
            FMLInterModComms.sendMessage(modid, "dimlet_preventworldgen", "Material." + "tile.ore.berries.two");
            FMLInterModComms.sendMessage(modid, "dimlet_preventworldgen", "Material." + "tile.ore.berries.two1");
            FMLInterModComms.sendMessage(modid, "dimlet_preventloot", "Material." + "tile.ore.berries.two");
            FMLInterModComms.sendMessage(modid, "dimlet_preventloot", "Material." + "tile.ore.berries.two1");
        }
    }
}
