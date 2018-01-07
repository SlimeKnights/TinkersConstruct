package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitFlammable extends AbstractTrait {

  public TraitFlammable() {
    super("flammable", 0xffffff);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  public void onPlayerHurt(ItemStack tool, EntityPlayer player, EntityLivingBase attacker, LivingHurtEvent event) {
    attacker.setFire(3);
  }

  @Override
  public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
    // block fire damage
    if(event.getSource().isFireDamage()) {
      event.setCanceled(true);
      ToolHelper.damageTool(tool, 3, player);
    }

    if(event.getSource().getTrueSource() != null) {
      event.getSource().getTrueSource().setFire(3);
    }
  }
}
