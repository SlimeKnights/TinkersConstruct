package slimeknights.tconstruct.compat.neoforged.neoforge.common;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Compatibility shim for old Forge shearable blocks. */
public interface IForgeShearable {
  List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level world, BlockPos pos, int fortune);
}
