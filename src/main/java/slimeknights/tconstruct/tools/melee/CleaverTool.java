package slimeknights.tconstruct.tools.melee;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class CleaverTool extends BroadSwordTool {
  public CleaverTool(Settings properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  protected double getSweepRange(ToolStack tool) {
    return tool.getModifierLevel(TinkerModifiers.expanded) + 3;
  }

  public TypedActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
    // no offhand, look into cleaver blocking
    ItemStack itemStackIn = playerIn.getStackInHand(hand);
    return TypedActionResult.consume(itemStackIn);
  }
}
