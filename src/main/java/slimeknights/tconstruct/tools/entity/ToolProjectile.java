package slimeknights.tconstruct.tools.entity;

import net.minecraft.world.item.ItemStack;

/** Interface to help with rendering a tool projectile */
public interface ToolProjectile {
  /** Gets the tool for display. Use {@link net.minecraft.world.entity.projectile.AbstractArrow#getPickupItem()} for pickup. */
  ItemStack getDisplayTool();
}
