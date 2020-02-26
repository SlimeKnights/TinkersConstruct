package slimeknights.tconstruct.library.tools.ranged;

import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;
import slimeknights.tconstruct.tools.traits.TraitEnderference;

/**
 * This class is a tool that has ammo.
 * Durability works like usually, but ammo is abstracted on top of durability.
 * So durability controls the interaction with materials, and ammo-ratio controls the interaction betweer durability and ammo
 */
public abstract class ProjectileCore extends TinkerToolCore implements IProjectile, IAmmo {

  public static final String DAMAGE_TYPE_PROJECTILE = "arrow";
  protected int durabilityPerAmmo;

  public ProjectileCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);
    durabilityPerAmmo = 10;
  }

  /* Ammo Handling */

  protected void setDurabilityPerAmmo(int durabilityPerAmmo) {
    this.durabilityPerAmmo = durabilityPerAmmo;
  }

  public int getDurabilityPerAmmo() {
    return durabilityPerAmmo;
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {
    // this is inverted, we return how DAMAGED the tool is, not how healthy
    return (double) (getMaxAmmo(stack) - getCurrentAmmo(stack)) / (double) getMaxAmmo(stack);
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    return getMaxAmmo(stack) != getCurrentAmmo(stack) && super.showDurabilityBar(stack);
  }

  @Override
  public int getCurrentAmmo(ItemStack stack) {
    if(ToolHelper.isBroken(stack)) {
      return 0;
    }
    return ToolHelper.getCurrentDurability(stack) / durabilityPerAmmo;
  }

  @Override
  public int getMaxAmmo(ItemStack stack) {
    return ToolHelper.getMaxDurability(stack) / durabilityPerAmmo;
  }

  @Override
  public void setAmmo(int count, ItemStack stack) {
    // we are setting ammo remaining, but damage of 0 is full ammo
    stack.setItemDamage((getMaxAmmo(stack) - count) * durabilityPerAmmo);
  }

  @Override
  public boolean addAmmo(ItemStack stack, EntityLivingBase player) {
    int ammo = getCurrentAmmo(stack);
    if(ammo < getMaxAmmo(stack)) {
      ToolHelper.healTool(stack, durabilityPerAmmo, null);
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public boolean useAmmo(ItemStack stack, @Nullable EntityLivingBase player) {
    int ammo = getCurrentAmmo(stack);
    if(ammo > 0) {
      ToolHelper.damageTool(stack, durabilityPerAmmo, player);
      int newAmmo = getCurrentAmmo(stack);
      if(newAmmo <= 0) {
        ToolHelper.breakTool(stack, player);
      }
      // in case we're creative or a trait like obsidian's prevented the damage
      return newAmmo < ammo;
    }
    else {
      return false;
    }
  }

  /* Tool stuff */

  protected ItemStack getProjectileStack(ItemStack itemStack, World world, EntityPlayer player, boolean usedAmmo) {
    ItemStack reference = itemStack.copy();
    reference.setCount(1);
    setAmmo(1, reference);

    // prevent a positive feedback loop with picking up ammo + durability retaining modifiers like reinforced
    if(!player.capabilities.isCreativeMode && !world.isRemote && !usedAmmo) {
      setAmmo(0, reference);
    }

    // never broken
    ToolHelper.unbreakTool(reference);
    return reference;
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
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    return false;
  }

  @Override
  public boolean dealDamageRanged(ItemStack stack, Entity projectile, EntityLivingBase player, Entity entity, float damage) {
    DamageSource damageSource = new EntityDamageSourceIndirect(DAMAGE_TYPE_PROJECTILE, projectile, player).setProjectile();

    // friggin vanilla hardcode
    if(entity instanceof EntityEnderman && ((EntityEnderman) entity).getActivePotionEffect(TraitEnderference.Enderference) != null) {
      damageSource = new DamageSourceProjectileForEndermen(DAMAGE_TYPE_PROJECTILE, projectile, player);
    }

    return entity.attackEntityFrom(damageSource, damage);
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
    info.addAccuracy();

    if(ToolHelper.getFreeModifiers(stack) > 0) {
      info.addFreeModifiers();
    }

    if(detailed) {
      info.addModifierInfo();
    }

    return info.getTooltip();
  }

  @Nonnull
  @Override
  public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack) {
    // no special attributes for ranged weapons
    return this.getItemAttributeModifiers(slot);
  }

  @Override
  public Multimap<String, AttributeModifier> getProjectileAttributeModifier(ItemStack stack) {
    // return the standard damage map
    return super.getAttributeModifiers(EntityEquipmentSlot.MAINHAND, stack);
  }

  @Override
  public abstract ProjectileNBT buildTagData(List<Material> materials);

  public static class DamageSourceProjectileForEndermen extends EntityDamageSource {

    public final Entity projectile;

    public DamageSourceProjectileForEndermen(String damageTypeIn, Entity projectile, Entity damageSourceEntityIn) {
      super(damageTypeIn, damageSourceEntityIn);
      this.projectile = projectile;
    }

    @Nullable
    @Override
    public Entity getImmediateSource() {
      return projectile;
    }
  }
}
