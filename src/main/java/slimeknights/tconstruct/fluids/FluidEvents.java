package slimeknights.tconstruct.fluids;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.capability.FluidMilkBucketWrapper;
import slimeknights.tconstruct.library.Util;

@EventBusSubscriber(modid=TConstruct.modID, bus=Bus.FORGE)
public class FluidEvents {
  @SubscribeEvent
  static void initCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
    ItemStack stack = event.getObject();
    if (stack.getItem() == Items.MILK_BUCKET) {
      event.addCapability(Util.getResource("milk"), new FluidMilkBucketWrapper(stack));
    }
  }
}
