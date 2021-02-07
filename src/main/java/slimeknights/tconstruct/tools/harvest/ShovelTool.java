package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tinkering.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AoeToolInteractionUtil;

public class ShovelTool extends ToolCore implements IAoeTool {

  public static final ImmutableSet<Material> EFFECTIVE_MATERIALS =
    ImmutableSet.of(Material.ORGANIC,
      Material.EARTH,
      Material.SAND,
      Material.SNOW_BLOCK,
      Material.SNOW,
      Material.CLAY,
      Material.CAKE);

  public ShovelTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean canHarvestBlock(BlockState state) {
    return EFFECTIVE_MATERIALS.contains(state.getMaterial()) || ShovelItem.EFFECTIVE_ON.contains(state.getBlock());
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    return AoeToolInteractionUtil.tillBlocks(context, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN);
  }
}
