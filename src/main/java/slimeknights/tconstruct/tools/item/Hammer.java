package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

public class Hammer extends ToolCore implements IAoeTool {

	public Hammer(){
		super(new PartMaterialType.ToolPartType(TinkerTools.toughToolRod),
				new PartMaterialType.ToolPartType(TinkerTools.hammerHead),
				new PartMaterialType.ToolPartType(TinkerTools.largePlate),
				new PartMaterialType.ToolPartType(TinkerTools.largePlate));

		addCategory(Category.HARVEST);

		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public boolean isEffective(Block block) {
		return Pickaxe.effective_materials.contains(block.getMaterial()) || ItemPickaxe.EFFECTIVE_ON.contains(block);
	}

	@Override
	public NBTTagCompound buildTag(List<Material> materials) {
		if (materials.size() < requiredComponents.length){
			return new NBTTagCompound();
		}
		return ToolBuilder.buildSimpleTool(materials.get(0), materials.get(1), materials.get(2), materials.get(3));
	}

	@Override public float damagePotential() {
		// TODO, Assign actual value.
		return 0;
	}

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    for(BlockPos extraPos : getExtraBlocksToBreak(itemstack, player.worldObj, player, pos))
    	ToolHelper.breakExtraBlock(itemstack, player.worldObj, player, extraPos, pos);

    return super.onBlockStartBreak(itemstack, pos, player);
  }

  @Override
  public ImmutableList<BlockPos> getExtraBlocksToBreak(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
    return ToolHelper.calcAOEBlocks(stack, world, player, origin, 3,3,1);
  }
}
