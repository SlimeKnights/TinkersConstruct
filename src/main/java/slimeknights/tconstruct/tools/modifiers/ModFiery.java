package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ModFiery extends ModifierTrait {

  public ModFiery() {
    super("fiery", 0xea9e32, 5, 25);
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    dealFireDamage(tool, target);
    return super.onHit(tool, player, target, damage, newDamage, isCritical);
  }

  protected void dealFireDamage(ItemStack tool, EntityLivingBase target) {
    NBTTagCompound tag = TinkerUtil.getModifierTag(tool, identifier);
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(tag);

    int duration = 1;
    duration += data.current/5;

    target.setFire(duration);

    // one heart fire damage per 15
    float fireDamage = (float)data.current/15f;
    target.attackEntityFrom(DamageSource.inFire, fireDamage);
  }
}
