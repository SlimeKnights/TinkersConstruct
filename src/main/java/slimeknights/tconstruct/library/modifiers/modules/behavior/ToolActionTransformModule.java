package slimeknights.tconstruct.library.modifiers.modules.behavior;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.BlockTransformModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

/**
 * Module which transforms a block using a tool action
 */
public record ToolActionTransformModule(ToolAction action, SoundEvent sound, boolean requireGround, int eventId, ModifierCondition<IToolStackView> condition) implements BlockTransformModule, ToolActionModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ToolActionTransformModule>defaultHooks(ModifierHooks.BLOCK_INTERACT, ModifierHooks.TOOL_ACTION, ModifierHooks.AOE_HIGHLIGHT, ModifierHooks.DISPLAY_NAME);
  public static final RecordLoadable<ToolActionTransformModule> LOADER = RecordLoadable.create(
    Loadables.TOOL_ACTION.requiredField("tool_action", ToolActionTransformModule::action),
    Loadables.SOUND_EVENT.requiredField("sound", ToolActionTransformModule::sound),
    BooleanLoadable.INSTANCE.requiredField("require_ground", ToolActionTransformModule::requireGround),
    IntLoadable.FROM_MINUS_ONE.defaultField("event_id", -1, ToolActionTransformModule::eventId),
    ModifierCondition.TOOL_FIELD,
    ToolActionTransformModule::new);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    return condition.matches(tool, modifier) && this.action == toolAction;
  }

  @Override
  public boolean shouldHighlight(IToolStackView tool, ModifierEntry modifier, UseOnContext context, BlockPos offset, BlockState state) {
    return condition.matches(tool, modifier) && state.getToolModifiedState(Util.offset(context, offset), action, true) != null;
  }

  @Override
  public boolean transform(IToolStackView tool, UseOnContext context, BlockState original, boolean playSound) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockPos above = pos.above();

    // hoes and shovels: air or plants above
    if (requireGround) {
      // TODO: more planty checks?
      BlockState state = level.getBlockState(above);
      if (!state.canBeReplaced()) {
        return false;
      }
    }

    // normal action transform
    Player player = context.getPlayer();
    BlockState transformed = original.getToolModifiedState(context, action, false);
    if (transformed != null) {
      if (playSound) {
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (eventId != -1) {
          level.levelEvent(player, eventId, pos, 0);
        }
      }
      if (!level.isClientSide) {
        level.setBlock(pos, transformed, Block.UPDATE_ALL_IMMEDIATE);
        if (requireGround) {
          level.destroyBlock(above, true);
        }
        BlockTransformModifierHook.afterTransformBlock(tool, context, original, pos, action);
      }
      return true;
    }
    return false;
  }

  @Override
  public RecordLoadable<ToolActionTransformModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  public static Builder builder(ToolAction action, SoundEvent sound) {
    return new Builder(action, sound);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<Builder> {
    private final ToolAction action;
    private final SoundEvent sound;
    private boolean requireGround;
    /**
     * Event ID to play upon success
     * @see Level#levelEvent(int, BlockPos, int)
     * @see net.minecraft.world.level.block.LevelEvent
     */
    @Setter
    @Accessors(fluent = true)
    private int eventId = -1;

    /** Sets the module to require the block above to be empty */
    public Builder requireGround() {
      this.requireGround = true;
      return this;
    }

    /** Builds the module */
    public ToolActionTransformModule build() {
      return new ToolActionTransformModule(action, sound, requireGround, eventId, condition);
    }
  }
}
