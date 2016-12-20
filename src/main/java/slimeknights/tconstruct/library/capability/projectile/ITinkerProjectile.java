package slimeknights.tconstruct.library.capability.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.traits.IProjectileTrait;
import slimeknights.tconstruct.library.traits.ITrait;

public interface ITinkerProjectile extends INBTSerializable<NBTTagCompound> {

  /** The itemstack that represents the projectile */
  ItemStack getItemStack();

  void setItemStack(ItemStack stack);

  /** The itemstack the projectile has been launched with */
  @Nullable
  ItemStack getLaunchingStack();

  void setLaunchingStack(ItemStack launchingStack);

  List<IProjectileTrait> getProjectileTraits();

  boolean pickup(EntityLivingBase entity, boolean simulate);

  void setPower(float power);

  /** This basically represents how far the bow was drawn back, or equivalent for other things */
  float getPower();
}
