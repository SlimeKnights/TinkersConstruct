package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.shared.client.ParticleEffect;
import slimeknights.tconstruct.tools.TinkerTools;

public class ModFiery extends ModifierTrait {

  public ModFiery() {
    super("fiery", 0xea9e32, 5, 25);
  }

  @Override
  public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    dealFireDamage(tool, player, target);
  }

  protected void dealFireDamage(ItemStack tool, EntityLivingBase attacker, EntityLivingBase target) {
    NBTTagCompound tag = TinkerUtil.getModifierTag(tool, identifier);
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(tag);

    int duration = getFireDuration(data);
    target.setFire(duration);

    // one heart fire damage per 15
    float fireDamage = getFireDamage(data);
    if(attackEntitySecondary(new EntityDamageSource("onFire", attacker).setFireDamage(), fireDamage, target, false, true)) {
      int count = Math.round(fireDamage);
      TinkerTools.proxy.spawnEffectParticle(ParticleEffect.Type.HEART_FIRE, target, count);
    }
  }

  private float getFireDamage(ModifierNBT.IntegerNBT data) {
    return (float) data.current / 15f;
  }

  private int getFireDuration(ModifierNBT.IntegerNBT data) {
    return 1 + data.current / 8;
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getIdentifier());
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);
    int duration = getFireDuration(data);
    float dmg = getFireDamage(data);

    return ImmutableList.of(
        Util.translateFormatted(loc, Util.df.format(dmg)),
        Util.translateFormatted(loc + 2, Util.df.format(duration))
    );
  }
}
