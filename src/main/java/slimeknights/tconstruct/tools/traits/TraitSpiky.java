package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitSpiky extends AbstractTrait {

  public TraitSpiky() {
    super("spiky", EnumChatFormatting.DARK_GREEN);
  }

  @Override
  public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
    Entity source = event.source.getEntity();
    if(source instanceof EntityLivingBase && source.isEntityAlive()) {
      TraitPrickly.causeDamage(player, (EntityLivingBase) source);
    }
  }
}
