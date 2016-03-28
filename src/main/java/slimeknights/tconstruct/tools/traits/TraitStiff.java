package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitStiff extends AbstractTrait {

  public TraitStiff() {
    super("stiff", 0xffffff);
  }

  @Override
  public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
    event.setAmount(Math.max(1f, event.getAmount() - 1f));
  }
}
