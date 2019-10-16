package slimeknights.tconstruct.gadgets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.capability.piggyback.TinkerPiggybackSerializer;

public class GadgetEvents {

  @SubscribeEvent
  public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    if (event.getObject() instanceof PlayerEntity) {
      event.addCapability(Util.getResource("piggyback"), new TinkerPiggybackSerializer((PlayerEntity) event.getObject()));
    }
  }

}
