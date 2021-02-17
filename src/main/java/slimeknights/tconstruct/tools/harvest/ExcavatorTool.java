package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.AOEToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ToolCore;

public class ExcavatorTool extends ToolCore {
  public static final ImmutableSet<Material> EFFECTIVE_MATERIALS =
    ImmutableSet.of(Material.ORGANIC,
                    Material.EARTH,
                    Material.SAND,
                    Material.SNOW_BLOCK,
                    Material.SNOW,
                    Material.CLAY,
                    Material.CAKE);
  public ExcavatorTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public AOEToolHarvestLogic getToolHarvestLogic() {
    return AOEToolHarvestLogic.LARGE_TOOL;
  }

  @Override
  public boolean canHarvestBlock(BlockState state) {
    return EFFECTIVE_MATERIALS.contains(state.getMaterial()) || ShovelItem.EFFECTIVE_ON.contains(state.getBlock());
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    return getToolHarvestLogic().tillBlocks(context, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN);
  }

  /*
  @Override
  public float getRepairModifierForPart(int index) {
    return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.75f;
  }*/
}
