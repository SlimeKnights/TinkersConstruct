package mods.battlegear2.api.weapons;


import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

import java.util.Map;

public interface IPotionEffect {


    /**
     * Returns a map containing the potion effects to apply to an entity hit by the weapon. Each effect
     * has a float value associated with the chance of applying the effect. Note that the "dice roll" only
     * occurs once so any value under this roll will be applied.
     *
     * @param entityHit The entity the effect will be applied to.
     * @param entityHitting
     * @return A Map of {@link PotionEffect} with chance value ranging from 0 to 1, to be dealt to the entityHit
     */
    public Map<PotionEffect,Float> getEffectsOnHit(EntityLivingBase entityHit, EntityLivingBase entityHitting);

}
