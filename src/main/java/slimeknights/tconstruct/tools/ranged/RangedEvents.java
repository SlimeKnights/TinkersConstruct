package slimeknights.tconstruct.tools.ranged;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import slimeknights.tconstruct.library.events.TinkerEvent;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.ranged.item.BoltCore;


@Mod.EventBusSubscriber(Side.CLIENT)
public class RangedEvents {

  @SubscribeEvent
  public static void onToolPartReplacement(TinkerEvent.OnToolPartReplacement event) {
    if(event.toolStack.getItem() == TinkerRangedWeapons.bolt) {
      List<ItemStack> extraParts = event.replacementParts.stream()
                                                         .filter(Objects::nonNull)
                                                         .filter(stack -> stack.getItem() == TinkerTools.boltCore)
                                                         .map(BoltCore::getHeadStack)
                                                         .collect(Collectors.toList());
      event.replacementParts.addAll(new ArrayList<>(extraParts));
    }
  }
}
