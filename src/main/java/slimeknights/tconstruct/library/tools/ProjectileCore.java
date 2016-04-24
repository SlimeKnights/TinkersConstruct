package slimeknights.tconstruct.library.tools;

import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.library.tinkering.PartMaterialType;

public abstract class ProjectileCore extends ToolCore implements IProjectileStats {

  public ProjectileCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);
  }

  // 1/10th durability by default. because 1000 ammo is ridiculous ;o
  @Override
  public int getMaxDamage(ItemStack stack) {
    return Math.max(1, Math.round((float) super.getMaxDamage(stack) / 10f));
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
    // projectiles behave like regular items
    return false;
  }

  @Override
  public double attackSpeed() {
    return 100f;
  }

  @Override
  public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
    // no special attributes for ranged weapons
    return this.getItemAttributeModifiers(slot);
  }

  @Override
  public Multimap<String, AttributeModifier> getProjectileAttributeModifier(ItemStack stack) {
    // return the standard damage map
    return super.getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack);
  }
}
