package slimeknights.tconstruct.library.capability.projectile;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

import javax.annotation.Nullable;

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
  private ItemStack parent;
  private ItemStack launcher;
  private List<IProjectileTrait> projectileTraitList = Lists.newArrayList();

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

  @Nullable
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
    if(stack != null && stack.getItem() instanceof IAmmo) {
      if(!simulate && parent.stackSize > 0) {
        ToolHelper.unbreakTool(stack);
        ((IAmmo) stack.getItem()).addAmmo(stack, entity);
      }
      return true;
    }

    return false;
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
    return tag;
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {
    parent = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(TAG_PARENT));
    // backwards compatibility
    if(parent == null) {
      parent = ItemStack.loadItemStackFromNBT(nbt);
    }
    launcher = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(TAG_LAUNCHER));
    updateTraits();
  }
}
