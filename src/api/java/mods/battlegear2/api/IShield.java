package mods.battlegear2.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public interface IShield {

    /**
     * Gets the decay rate for the stamina bar when the shield is in use.
     * The value should be between 0 and 1. The duration of maximum blocking can be calculated
     * by 1/decayRate/20.
     *
     * @param shield The {@link #ItemStack} representing the shield
     * @return a value between 0 & 1 representing the decay rate per tick
     */
    public float getDecayRate(ItemStack shield);
    
    /**
     * Gets the recovery rate for the stamina bar when the shield is not in use.
     * The value should be between 0 and 1.
     *
     * @param shield The {@link #ItemStack} representing the shield
     * @return a value between 0 & 1 representing the recovery rate per tick
     */
    public float getRecoveryRate(ItemStack shield);

    /**
     * Returns true if the current shield can and should block the given damage source
     *
     * @param shield The {@link #ItemStack} representing the shield
     * @param source The {@link #DamageSource} representing the current damage
     * @return true if the shield can block the given damage type
     */
    public boolean canBlock(ItemStack shield, DamageSource source);

    /**
     * Gets the extra decay rate to the stamina bar when the shield is damaged
     *
     * @param shield The {@link #ItemStack} representing the shield
     * @param amount The amount of damage the shield has absorbed
     * @return a value between 0 & 1 representing the decay rate
     */
    public float getDamageDecayRate(ItemStack shield, float amount);

    /**
     * Returns the block angle in degrees that the shield can block.
     * This angle is taken as 0 degrees being directly in front of the player. The shield
     * will block between -blockAngle to blockangle
     * @param shield The {@link #ItemStack} representing the shield
     * @return The maximum angle the shield should be able to block at
     */
    public float getBlockAngle(ItemStack shield);


    /**
     * Returns the time a shield bash should take to be preformed. A shield bash will disallow actions
     * for the number of ticks given and will knockback an oponent at time/2
     *
     * @return The amount of ticks the shield bash animation will play
     */
    public int getBashTimer(ItemStack shield);
}
