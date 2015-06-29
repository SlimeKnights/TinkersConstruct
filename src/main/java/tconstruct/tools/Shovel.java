package tconstruct.tools;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.materials.Material;
import tconstruct.library.tinkering.Category;
import tconstruct.library.tinkering.PartMaterialType;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.utils.ToolBuilder;

public class Shovel extends ToolCore{
	
	
	public Shovel() {
		super(new PartMaterialType.ToolPartType(TinkerTools.shovelHead), 
				new PartMaterialType.ToolPartType(TinkerTools.toolrod));
		addCategory(Category.HARVEST);
		setHarvestLevel("shovel", 0);
	}
	
	
	@Override
	public NBTTagCompound buildTag(List<Material> materials) {
		if (materials.size() < requiredComponents.length) {
			return new NBTTagCompound();
		}
		return ToolBuilder.buildSimpleTool(materials.get(0), materials.get(1));
	}
}
