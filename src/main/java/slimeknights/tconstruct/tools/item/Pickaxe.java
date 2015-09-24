package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Pickaxe extends ToolCore implements IAoeTool {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.iron,
                      net.minecraft.block.material.Material.anvil,
                      net.minecraft.block.material.Material.rock,
                      net.minecraft.block.material.Material.circuits,
                      net.minecraft.block.material.Material.ice,
                      net.minecraft.block.material.Material.glass,
                      net.minecraft.block.material.Material.packedIce,
                      net.minecraft.block.material.Material.piston);

  // Pick-head, binding, tool-rod
  public Pickaxe() {
    super(new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.pickHead),
          new PartMaterialType.ToolPartType(TinkerTools.binding));

    addCategory(Category.HARVEST);

    // set the toolclass, actual harvestlevel is done by the overridden callback
    this.setHarvestLevel("pickaxe", 0);
  }

  @Override
  public boolean isEffective(Block block) {
    return effective_materials.contains(block.getMaterial()) || ItemPickaxe.EFFECTIVE_ON.contains(block);
  }

  @Override
  public float damagePotential() {
    return 0.3f;
  }

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    for(BlockPos pos2 : getExtraBlocksToBreak(itemstack, player.worldObj, player, pos))
      ToolHelper.breakExtraBlock(itemstack, player.worldObj, player, pos2, pos);
    return super.onBlockStartBreak(itemstack, pos, player);
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats binding = materials.get(2).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT(head);

    // handle influences durability
    // binding quality influences how well the handle interacts with the head
    data.durability *= 0.2f + 0.8f*(handle.handleQuality * (1 + binding.extraQuality)/2);
    // handle also influences mining speed a bit (0-20% change)
    data.speed *= 0.8f + handle.handleQuality*0.2f;
    // binding adds a bit to the speed
    data.speed += (binding.miningspeed * binding.extraQuality)*0.14f;

    // 3 free modifiers
    data.modifiers = 3;

    return data.get();
  }

  @Override
  public ImmutableList<BlockPos> getExtraBlocksToBreak(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
    return ToolHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 1);
  }
}
