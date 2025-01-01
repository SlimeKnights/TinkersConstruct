package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.mining.RemoveBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;

public class GlowingModifier extends NoLevelsModifier implements BlockInteractionModifierHook, RemoveBlockModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ShowOffhandModule.DISALLOW_BROKEN);
    hookBuilder.addHook(this, ModifierHooks.BLOCK_INTERACT, ModifierHooks.REMOVE_BLOCK);
  }

  @Override
  public int getPriority() {
    return 75; // after bucketing
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return DualOptionInteraction.formatModifierName(tool, this, super.getDisplayName(tool, entry, access));
  }
  
  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    if (tool.getCurrentDurability() >= 10 && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      Player player = context.getPlayer();
      if (!context.getLevel().isClientSide) {
        Level world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos().relative(face);
        if (TinkerCommons.glow.get().addGlow(world, pos, face.getOpposite())) {
          // damage the tool, showing animation if relevant
          if (ToolDamageUtil.damage(tool, 10, player, context.getItemInHand()) && player != null) {
            player.broadcastBreakEvent(source.getSlot(context.getHand()));
          }
          world.playSound(null, pos, world.getBlockState(pos).getSoundType(world, pos, player).getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
      }
      return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
    return InteractionResult.PASS;
  }

  @Nullable
  @Override
  public Boolean removeBlock(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
    if (context.getState().is(TinkerCommons.glow.get()) && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, getId(), InteractionSource.LEFT_CLICK)) {
      return false;
    }
    return null;
  }
}
