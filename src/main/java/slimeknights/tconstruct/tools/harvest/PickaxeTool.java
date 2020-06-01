package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import java.util.List;

public class PickaxeTool extends ToolCore {

  public static final ImmutableSet<Material> effective_materials =
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
  public boolean isEffective(BlockState state) {
    return effective_materials.contains(state.getMaterial()) || PickaxeItem.EFFECTIVE_ON.contains(state.getBlock());
  }

  @Override
  public void getTooltip(ItemStack stack, List<String> tooltips) {
  }
}
