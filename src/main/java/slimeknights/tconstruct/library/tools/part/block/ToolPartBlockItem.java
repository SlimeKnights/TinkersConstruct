package slimeknights.tconstruct.library.tools.part.block;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import javax.annotation.Nullable;
import java.util.List;

/** Implementation of {@link ToolPartItem} for {@link net.minecraft.world.item.BlockItem}. */
public class ToolPartBlockItem extends MaterialBlockItem implements IToolPart {
  @Getter
  public final MaterialStatsId statType;
  public ToolPartBlockItem(Block block, Properties properties, MaterialStatsId statType) {
    super(block, properties);
    this.statType = statType;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    ToolPartItem.appendHoverText(this, stack, tooltip, flag);
  }
}
