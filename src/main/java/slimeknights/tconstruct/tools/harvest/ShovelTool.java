package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ShovelItem;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.tools.ToolRegistry;

public class ShovelTool extends ToolCore {
  public static final ImmutableSet<Material> effective_materials =
    ImmutableSet.of(net.minecraft.block.material.Material.ORGANIC,
      net.minecraft.block.material.Material.EARTH,
      net.minecraft.block.material.Material.SAND,
      net.minecraft.block.material.Material.SNOW_BLOCK,
      net.minecraft.block.material.Material.SNOW,
      net.minecraft.block.material.Material.CLAY,
      net.minecraft.block.material.Material.CAKE);

  public ShovelTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);

    ToolRegistry.registerToolCrafting(this);
  }

  @Override
  public boolean isEffective(BlockState state) {
    return effective_materials.contains(state.getMaterial()) || ShovelItem.EFFECTIVE_ON.contains(state.getBlock());
  }

}
