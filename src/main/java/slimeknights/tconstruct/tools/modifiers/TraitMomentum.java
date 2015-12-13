package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.potion.TinkerPotion;

public class TraitMomentum extends AbstractTrait {

  public static final TinkerPotion Momentum = new TinkerPotion(Util.getResource("momentum"), false, false);

  public TraitMomentum() {
    super("momentum", EnumChatFormatting.BLUE);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    float boost = Momentum.getLevel(event.entityPlayer);
    boost /= 80f; // 40% boost max

    event.newSpeed += event.originalSpeed * boost;
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    int level = 1;
    level += Momentum.getLevel(player);

    level = Math.min(32, level);
    int duration = (int) ((10f / ToolHelper.getMiningSpeed(tool)) * 1.5f * 20f);

    Momentum.apply(player, duration, level);
  }
}
