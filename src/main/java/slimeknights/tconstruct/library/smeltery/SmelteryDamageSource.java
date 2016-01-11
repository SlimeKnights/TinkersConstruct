package slimeknights.tconstruct.library.smeltery;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;

public class SmelteryDamageSource extends DamageSource {

  public static final SmelteryDamageSource instance = new SmelteryDamageSource();

  public SmelteryDamageSource() {
    super("smeltery");
    setFireDamage();
  }

  @Override
  public IChatComponent getDeathMessage(EntityLivingBase p_151519_1_) {

    // todo: custom death messages
    return super.getDeathMessage(p_151519_1_);
  }
}
