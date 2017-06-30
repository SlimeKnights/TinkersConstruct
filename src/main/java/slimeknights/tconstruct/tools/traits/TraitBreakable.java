package slimeknights.tconstruct.tools.traits;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.events.ProjectileEvent;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitBreakable extends AbstractTrait {

  private final float BREAKCHANCE = 0.5f;

  public TraitBreakable() {
    super("breakable", 0xffffff);

    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onHitBlock(ProjectileEvent.OnHitBlock event) {
    if(event.projectile != null && !event.projectile.getEntityWorld().isRemote) {

      ItemStack itemStack = event.projectile.tinkerProjectile.getItemStack();
      if(TinkerUtil.hasTrait(TagUtil.getTagSafe(itemStack), this.getModifierIdentifier()) && random.nextFloat() < BREAKCHANCE) {
        event.projectile.setDead();
      }
    }
  }
}
