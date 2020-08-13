package slimeknights.tconstruct.tools.harvest;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResultType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

public class ShovelTool extends ToolCore {

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
  public boolean isEffective(BlockState state) {
    return EFFECTIVE_MATERIALS.contains(state.getMaterial()) || ShovelItem.EFFECTIVE_ON.contains(state.getBlock());
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    PlayerEntity playerentity = context.getPlayer();
    ItemStack itemStack = playerentity.getHeldItem(context.getHand());

    if (ToolData.from(itemStack).getStats().broken) {
      return ActionResultType.FAIL;
    }

    ActionResultType resultType = Items.DIAMOND_SHOVEL.onItemUse(context);
    if (resultType == ActionResultType.SUCCESS) {
      //TODO event
    }

    return resultType;
  }
}
