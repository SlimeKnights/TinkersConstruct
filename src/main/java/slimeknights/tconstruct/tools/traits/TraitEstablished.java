package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitEstablished extends AbstractTrait {

  public TraitEstablished() {
    super("established", 0xffffff);

    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onXpDrop(LivingExperienceDropEvent event) {
    EntityPlayer player = event.getAttackingPlayer();
    if(player != null) {
      if(TinkerUtil.hasTrait(TagUtil.getTagSafe(player.getHeldItem()), identifier)) {
        float xp = event.getDroppedExperience();
        xp *= 1f + random.nextFloat()*0.5f;
        event.setDroppedExperience(Math.round(xp));
      }
    }
  }
}
