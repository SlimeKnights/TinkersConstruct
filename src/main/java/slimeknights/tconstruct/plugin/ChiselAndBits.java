package slimeknights.tconstruct.plugin;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerWorld;

@Pulse(id = ChiselAndBits.PulseId, modsRequired = ChiselAndBits.modid, defaultEnable = true)
public class ChiselAndBits {

  public static final String modid = "chiselsandbits";
  public static final String PulseId = modid + "Integration";

  @Subscribe
  public void init(FMLInitializationEvent event) {
    imc(TinkerSmeltery.searedBlock);
    imc(TinkerCommons.blockClearGlass);
    imc(TinkerCommons.blockClearStainedGlass);
    imc(TinkerCommons.blockSlime);
    imc(TinkerCommons.blockSlimeCongealed);
    imc(TinkerWorld.slimeLeaves);
  }

  protected void imc(Block block) {
    if(block != null) {
      FMLInterModComms.sendMessage(modid, "ignoreblocklogic", block.getRegistryName());
    }
  }
}
