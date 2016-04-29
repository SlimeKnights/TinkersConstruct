package slimeknights.tconstruct.library.tools;

import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;

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
  protected String getBrokenTooltip(ItemStack itemStack) {
    return Util.translate(TooltipBuilder.LOC_Empty);
  }

  @Override
  public List<String> getInformation(ItemStack stack, boolean detailed) {
    TooltipBuilder info = new TooltipBuilder(stack);

    info.addAmmo(!detailed);
    info.addAttack();

    if(ToolHelper.getFreeModifiers(stack) > 0) {
      info.addFreeModifiers();
    }

    if(detailed) {
      info.addModifierInfo();
    }

    return info.getTooltip();
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
