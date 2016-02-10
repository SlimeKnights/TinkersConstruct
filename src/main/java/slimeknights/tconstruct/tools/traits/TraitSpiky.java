package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitSpiky extends AbstractTrait {

  public TraitSpiky() {
    super("spiky", EnumChatFormatting.DARK_GREEN);
  }

  @Override
  public void onBlock(ItemStack tool, EntityPlayer player, LivingHurtEvent event) {
    Entity target = event.source.getEntity();
    if(target instanceof EntityLivingBase && target.isEntityAlive()) {
      float damage = ToolHelper.getActualDamage(tool, player)/3f; // 1/3rd of weapon damage
      EntityDamageSource damageSource = new EntityDamageSource(DamageSource.cactus.damageType, player);
      damageSource.setDamageBypassesArmor();
      damageSource.setDamageIsAbsolute();

      // reset hurt resistance time from being hit before
      target.hurtResistantTime = 0;
      target.attackEntityFrom(damageSource, damage);
      target.hurtResistantTime = 4; // very short invulv time from that
    }
  }
}
