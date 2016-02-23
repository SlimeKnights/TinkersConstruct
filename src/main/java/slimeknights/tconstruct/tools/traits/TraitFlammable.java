package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitFlammable extends AbstractTrait {

  public TraitFlammable() {
    super("flammable", 0xffffff);
  }

  @Override
  public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
    // set attacker on fire
    if(event.source.getEntity() != null) {
      event.source.getEntity().setFire(3);
    }

    // block fire damage
    if(event.source.isFireDamage()) {
      event.setCanceled(true);
      ToolHelper.damageTool(tool, 3, player);
    }
  }
}
