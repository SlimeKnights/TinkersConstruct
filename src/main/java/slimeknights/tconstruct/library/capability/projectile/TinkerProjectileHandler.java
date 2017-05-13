package slimeknights.tconstruct.library.capability.projectile;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.ranged.IAmmo;
import slimeknights.tconstruct.library.traits.IProjectileTrait;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.AmmoHelper;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TinkerProjectileHandler implements ITinkerProjectile, INBTSerializable<NBTTagCompound> {

  public static final String TAG_PARENT = "parent";
  public static final String TAG_LAUNCHER = "launcher";
  public static final String TAG_POWER = "power";
  private ItemStack parent = ItemStack.EMPTY;
  private ItemStack launcher = ItemStack.EMPTY;
  private List<IProjectileTrait> projectileTraitList = Lists.newArrayList();
  private float power = 1f;

  public TinkerProjectileHandler() {
  }

  @Override
  public ItemStack getItemStack() {
    return parent;
  }

  @Override
  public void setItemStack(ItemStack stack) {
    parent = stack;
    updateTraits();
  }

  @Override
  public ItemStack getLaunchingStack() {
    return launcher;
  }

  @Override
  public void setLaunchingStack(ItemStack launchingStack) {
    this.launcher = launchingStack;
  }

  @Override
  public List<IProjectileTrait> getProjectileTraits() {
    return projectileTraitList;
  }

  private void updateTraits() {
    if(parent != null) {
      projectileTraitList.clear();

      NBTTagList list = TagUtil.getTraitsTagList(parent);
      for(int i = 0; i < list.tagCount(); i++) {
        ITrait trait = TinkerRegistry.getTrait(list.getStringTagAt(i));
        if(trait instanceof IProjectileTrait) {
          projectileTraitList.add((IProjectileTrait) trait);
        }
      }
    }
  }

  @Override
  public boolean pickup(EntityLivingBase entity, boolean simulate) {
    ItemStack stack = AmmoHelper.getMatchingItemstackFromInventory(parent, entity, true);
    if(stack.getItem() instanceof IAmmo) {
      if(!simulate && ((IAmmo) parent.getItem()).getCurrentAmmo(parent) > 0) {
        ToolHelper.unbreakTool(stack);
        ((IAmmo) stack.getItem()).addAmmo(stack, entity);
      }
      return true;
    }

    return false;
  }

  @Override
  public void setPower(float power) {
    this.power = power;
  }

  @Override
  public float getPower() {
    return power;
  }


  @Override
  public NBTTagCompound serializeNBT() {
    NBTTagCompound tag = new NBTTagCompound();
    if(parent != null) {
      tag.setTag(TAG_PARENT, parent.writeToNBT(new NBTTagCompound()));
    }
    if(launcher != null) {
      tag.setTag(TAG_LAUNCHER, launcher.writeToNBT(new NBTTagCompound()));
    }
    tag.setFloat(TAG_POWER, power);
    return tag;
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {
    parent = new ItemStack(nbt.getCompoundTag(TAG_PARENT));
    // backwards compatibility
    if(parent.isEmpty()) {
      parent = new ItemStack(nbt);
    }
    launcher = new ItemStack(nbt.getCompoundTag(TAG_LAUNCHER));
    power = nbt.getFloat(TAG_POWER);
    updateTraits();
  }
}
