package slimeknights.tconstruct.tools.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

public class Hammer extends ToolCore{

	public Hammer(){
		super(new PartMaterialType.ToolPartType(TinkerTools.toughToolRod),
				new PartMaterialType.ToolPartType(TinkerTools.hammerHead),
				new PartMaterialType.ToolPartType(TinkerTools.largePlate),
				new PartMaterialType.ToolPartType(TinkerTools.largePlate));

		addCategory(Category.HARVEST);

		setHarvestLevel("hammer", 0);
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
}
