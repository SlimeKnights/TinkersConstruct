package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.tools.TinkerTools;

public class BroadSword extends ToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.web,
                      net.minecraft.block.material.Material.vine,
                      net.minecraft.block.material.Material.coral,
                      net.minecraft.block.material.Material.gourd,
                      net.minecraft.block.material.Material.leaves);

  public BroadSword() {
    this(PartMaterialType.handle(TinkerTools.toolRod),
         PartMaterialType.head(TinkerTools.swordBlade),
         PartMaterialType.extra(TinkerTools.wideGuard));
  }

  protected BroadSword(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.WEAPON);
  }

  @Override
  public boolean isEffective(Block block) {
    return effective_materials.contains(block.getMaterial());
  }

  @Override
  public boolean canUseSecondaryItem() {
    return false;
  }

  @Override
  public float damagePotential() {
    return 1.0f;
  }

  @Override
  public int attackSpeed() {
    return 0;
  }

  @Override
  public float miningSpeedModifier() {
    return 0.5f; // slooow, because it's a swooooord
  }

  // Blocking and sword things
  /**
   * returns the action that specifies what animation to play when the items is being used
   */
  public EnumAction getItemUseAction(ItemStack stack)
  {
    return EnumAction.BLOCK;
  }

  /**
   * How long it takes to use or consume an item
   */
  public int getMaxItemUseDuration(ItemStack stack)
  {
    return 72000;
  }

  /**
   * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
   */
  public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
  {
    playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
    return itemStackIn;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolNBT data = buildDefaultTag(materials);
    // 2 base damage, like vanilla swords
    data.attack += 2f;
    return data.get();
  }
}
