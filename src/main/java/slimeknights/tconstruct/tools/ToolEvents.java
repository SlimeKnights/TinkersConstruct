package slimeknights.tconstruct.tools;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.events.TinkerToolEvent;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import java.util.List;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ToolEvents {

  @SubscribeEvent
  static void onExtraBlockBreak(TinkerToolEvent.ExtraBlockBreak event) {
    List<ModifierNBT> modifiers = ToolData.from(event.getItemStack()).getModifiers().getCurrentModifiers();
    boolean width = false;
    boolean height = false;
    for (ModifierNBT modifier : modifiers) {
      if (modifier.getIdentifier().equals(TinkerModifiers.widthHarvestSizeModifier.get().getRegistryName())) {
        width = true;
      }
      else if (modifier.getIdentifier().equals(TinkerModifiers.heightHarvestSizeModifier.get().getRegistryName())) {
        height = true;
      }
    }

    if (!width && !height) {
      return;
    }

    if (event.getTool() == TinkerTools.pickaxe.get() ||
      event.getTool() == TinkerTools.axe.get() ||
      event.getTool() == TinkerTools.shovel.get() ||
      event.getTool() == TinkerTools.kama.get()) {
      event.setWidth(event.getWidth() + (width ? 1 : 0));
      event.setHeight(event.getHeight() + (height ? 1 : 0));
    }
    /*else if(event.getTool() == TinkerTools.mattock) { todo mattock
      int c = 0;

      if(width) {
        c++;
      }

      if(height) {
        c++;
      }

      event.setWidth(event.getWidth() + c);
      event.setHeight(event.getHeight() + c);
    }*/
    else if (event.getTool() == TinkerTools.hammer.get() ||
      event.getTool() == TinkerTools.excavator.get()) { //todo lumber axe and scythe
      event.setWidth(event.getWidth() + (width ? 2 : 0));
      event.setHeight(event.getHeight() + (height ? 2 : 0));
      event.setDistance(3);
    }
  }
}
