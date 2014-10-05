package boni.tinkersweaponry.library.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSourceIndirect;

public class PiercingArrowDamage extends EntityDamageSourceIndirect {
    public PiercingArrowDamage(String p_i1568_1_, Entity p_i1568_2_, Entity p_i1568_3_) {
        super(p_i1568_1_, p_i1568_2_, p_i1568_3_);
        this.setDamageBypassesArmor();
        this.setProjectile();
    }
}
