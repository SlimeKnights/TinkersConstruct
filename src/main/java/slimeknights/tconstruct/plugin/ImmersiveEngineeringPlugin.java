package slimeknights.tconstruct.plugin;

import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect_Potion;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect_RandomTeleport;
import net.minecraft.fluid.Fluid;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ITag;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.fluids.TinkerFluids;

/** Event handlers to run when Immersive Engineering is present */
public class ImmersiveEngineeringPlugin {
  @SubscribeEvent
  public void commonSetup(FMLCommonSetupEvent event) {
    ChemthrowerHandler.registerFlammable(TinkerFluids.blazingBlood.getLocalTag());
    registerChemEffect(TinkerFluids.earthSlime.getForgeTag(), Effects.SLOWNESS, 140);
    registerChemEffect(TinkerFluids.skySlime.getLocalTag(), Effects.JUMP_BOOST, 200);
    registerChemEffect(TinkerFluids.enderSlime.getLocalTag(), Effects.LEVITATION, 100);
    registerChemEffect(TinkerFluids.blood.getLocalTag(), Effects.MINING_FATIGUE, 100);
    registerChemEffect(TinkerFluids.venom.getLocalTag(), Effects.POISON, 300);
    registerChemEffect(TinkerFluids.magma.getForgeTag(), Effects.FIRE_RESISTANCE, 200);
    registerChemEffect(TinkerFluids.liquidSoul.getForgeTag(), Effects.BLINDNESS, 100);
    ChemthrowerHandler.registerEffect(TinkerFluids.moltenEnder.getForgeTag(), new ChemthrowerEffect_RandomTeleport(null, 0, 1));
    registerChemEffect(TinkerFluids.moltenUranium.getLocalTag(), Effects.POISON, 200);
  }

  /** Shorthand to register a chemical potion effect */
  private static void registerChemEffect(ITag<Fluid> tag, Effect effect, int duration) {
    ChemthrowerHandler.registerEffect(tag, new ChemthrowerEffect_Potion(null, 0, effect, duration, 0));
  }
}
