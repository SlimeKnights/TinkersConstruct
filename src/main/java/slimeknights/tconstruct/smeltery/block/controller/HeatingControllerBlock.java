package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import slimeknights.mantle.block.RetexturedBlock;
import slimeknights.mantle.inventory.BaseContainerMenu;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.MultiblockResult;
import slimeknights.tconstruct.smeltery.network.StructureErrorPositionPacket;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Multiblock that displays the error from the tile entity on right click
 */
public abstract class HeatingControllerBlock extends ControllerBlock {
  protected HeatingControllerBlock(Properties builder) {
    super(builder);
  }

  @Override
  protected boolean openGui(Player player, Level world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);
    boolean opened = false;
    if (state.getBlock() == this) {
      if (canOpenGui(state)) {
        if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
          MenuProvider container = this.getMenuProvider(state, world, pos);
          if (container != null) {
            serverPlayer.openMenu(container, buffer -> {
              buffer.writeBlockPos(pos);
              BlockEntityHelper.get(HeatingStructureBlockEntity.class, world, pos)
                .ifPresent(te -> buffer.writeVarInt(te.getMeltingInventory().getSlots()));
            });
            if (player.containerMenu instanceof BaseContainerMenu<?> menu) {
              menu.syncOnOpen(serverPlayer);
            }
          }
        }
        opened = true;
      } else {
        opened = displayStatus(player, world, pos, state);
      }
    }

    // only need to update if holding the proper items
    if (!world.isClientSide) {
      BlockEntityHelper.get(HeatingStructureBlockEntity.class, world, pos).ifPresent(te -> {
        MultiblockResult result = te.getStructureResult();
        if (!result.isSuccess() && te.showDebugBlockBorder(player)) {
          TinkerNetwork.getInstance().sendTo(new StructureErrorPositionPacket(pos, result.getPos()), player);
        }
      });
    }
    return opened;
  }

  @Override
  protected boolean displayStatus(Player player, Level world, BlockPos pos, BlockState state) {
    if (!world.isClientSide) {
      BlockEntityHelper.get(HeatingStructureBlockEntity.class, world, pos).ifPresent(te -> {
        MultiblockResult result = te.getStructureResult();
        if (!result.isSuccess()) {
          player.displayClientMessage(result.getMessage(), true);
          TinkerNetwork.getInstance().sendTo(new StructureErrorPositionPacket(pos, result.getPos()), player);
        }
      });
    }
    return true;
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag pFlag) {
    RetexturedHelper.addTooltip(stack, tooltip);
  }

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(world, pos, state, placer, stack);
    RetexturedBlock.updateTextureBlock(world, pos, stack);
  }

  @Override
  public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) {
    return RetexturedBlock.getPickBlock(world, pos, state);
  }
}
