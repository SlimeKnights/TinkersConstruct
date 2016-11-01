package slimeknights.tconstruct.tools.traits;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitSplitting extends AbstractTrait {

  private static final float DOUBLESHOT_CHANCE = 0.5f;

  public TraitSplitting() {
    super("splitting", 0xffffff);

    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onBowShooting(TinkerToolEvent.OnBowShoot event) {
    if(TinkerUtil.hasTrait(TagUtil.getTagSafe(event.ammo), this.getModifierIdentifier()) && random.nextFloat() < DOUBLESHOT_CHANCE) {
      event.setProjectileCount(2);
      event.setConsumeAmmoPerProjectile(false);
      event.setConsumeDurabilityPerProjectile(false);
      event.setBonusInaccuracy(3f);
    }
  }
}
