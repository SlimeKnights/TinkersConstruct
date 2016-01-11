package slimeknights.tconstruct.plugin;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

@Pulse(id = ChiselAndBits.PulseId, modsRequired = ChiselAndBits.modid, defaultEnable = true)
public class ChiselAndBits {

  public static final String modid = "chiselsandbits";
  public static final String PulseId = modid + "Integration";

  @Subscribe
  public void init(FMLInitializationEvent event) {
    imc(TinkerSmeltery.searedBlock);
  }

  protected void imc(Block block) {
    if(block != null) {
      FMLInterModComms.sendMessage(modid, "ignoreblocklogic", block.getRegistryName());
    }
  }
}
