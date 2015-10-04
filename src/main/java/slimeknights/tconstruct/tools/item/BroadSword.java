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
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
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
    super(new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.swordBlade),
          new PartMaterialType.ToolPartType(TinkerTools.wideGuard));

    addCategory(Category.WEAPON);
  }

  @Override
  public boolean isEffective(Block block) {
    return effective_materials.contains(block.getMaterial());
  }

  @Override
  public float damagePotential() {
    return 1.0f;
  }

  @Override
  public int attackSpeed() {
    return 1;
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
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats guard = materials.get(2).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT(head);

    // sword has 1.5 hearts base damage!
    data.attack += 3f;

    // attack damage: blade, modified 10% by handle and 20% by guard
    data.attack *= 0.7f + 0.1f*handle.handleQuality + 0.2f*guard.extraQuality;

    // durability: guard adds a bit to it, handle has minimal impact
    data.durability += 0.1f * guard.durability * guard.extraQuality;
    data.durability *= 0.95f + 0.05f*handle.handleQuality;

    // 3 free modifiers
    data.modifiers = DEFAULT_MODIFIERS;

    return data.get();
  }
}
