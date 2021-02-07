package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.PickaxeItem;
import slimeknights.tconstruct.library.tinkering.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;

public class PickaxeTool extends ToolCore implements IAoeTool {

  public static final ImmutableSet<Material> EFFECTIVE_MATERIALS =
    ImmutableSet.of(net.minecraft.block.material.Material.IRON,
      net.minecraft.block.material.Material.ANVIL,
      net.minecraft.block.material.Material.ROCK,
      net.minecraft.block.material.Material.ICE,
      net.minecraft.block.material.Material.GLASS,
      net.minecraft.block.material.Material.PACKED_ICE,
      net.minecraft.block.material.Material.PISTON);

  public PickaxeTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean canHarvestBlock(BlockState state) {
    return EFFECTIVE_MATERIALS.contains(state.getMaterial()) || PickaxeItem.EFFECTIVE_ON.contains(state.getBlock());
  }
}
