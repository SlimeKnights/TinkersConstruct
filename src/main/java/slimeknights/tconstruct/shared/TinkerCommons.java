package slimeknights.tconstruct.shared;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.ObjectHolder;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.common.conditions.PulseLoadedCondition;
import slimeknights.tconstruct.items.CommonItems;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;

/**
 * Contains items and blocks and stuff that is shared by multiple pulses, but might be required individually
 */
@Pulse(id = TinkerPulseIds.TINKER_COMMONS_PULSE_ID, forced = true)
@ObjectHolder(TConstruct.modID)
public class TinkerCommons extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_COMMONS_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> CommonsClientProxy::new, () -> ServerProxy::new);

  @SubscribeEvent
  public void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    CraftingHelper.register(ConfigOptionEnabledCondition.Serializer.INSTANCE);
    CraftingHelper.register(PulseLoadedCondition.Serializer.INSTANCE);
  }

  @SubscribeEvent
  public void preInit(final FMLCommonSetupEvent event) {
    proxy.preInit();
  }

  @SubscribeEvent
  public void init(final InterModEnqueueEvent event) {
    proxy.init();
  }

  @SubscribeEvent
  public void postInit(final InterModProcessEvent event) {
    proxy.postInit();
    TinkerRegistry.tabGeneral.setDisplayIcon(new ItemStack(CommonItems.blue_slime_ball));
  }
}
