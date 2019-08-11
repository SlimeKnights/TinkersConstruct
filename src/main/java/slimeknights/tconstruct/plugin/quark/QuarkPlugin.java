package slimeknights.tconstruct.plugin.quark;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.shared.TinkerCommons;

@Pulse(id = QuarkPlugin.PulseId, modsRequired = QuarkPlugin.modid, defaultEnable = true)
public class QuarkPlugin extends TinkerPulse {
  public static final String modid = "quark";
  public static final String PulseId = modid + "Integration";

  @GameRegistry.ObjectHolder("quark:color_slime")
  public static final Block colorSlime = null;

  @SubscribeEvent
  public void registerBlocks(RegistryEvent.Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();
    TinkerCommons.blockSlime = registerBlock(registry, SlimeBlockProvider.get(), "slime");
  }
}
