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
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.BlockTransformModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition.ConditionalModifierModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module which transforms a block using a tool action
 */
public record ToolActionTransformModule(ToolAction action, SoundEvent sound, boolean requireGround, int eventId, ModifierModuleCondition condition) implements BlockTransformModule, ToolActionModifierHook, ConditionalModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.BLOCK_INTERACT, TinkerHooks.TOOL_ACTION);
  public static final RecordLoadable<ToolActionTransformModule> LOADER = RecordLoadable.create(
    Loadables.TOOL_ACTION.field("tool_action", ToolActionTransformModule::action),
    Loadables.SOUND_EVENT.field("sound", ToolActionTransformModule::sound),
    BooleanLoadable.INSTANCE.field("require_ground", ToolActionTransformModule::requireGround),
    IntLoadable.FROM_MINUS_ONE.defaultField("event_id", -1, ToolActionTransformModule::eventId),
    ModifierModuleCondition.FIELD,
    ToolActionTransformModule::new);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    return condition.matches(tool, modifier) && this.action == toolAction;
  }

  @Override
  public boolean transform(IToolStackView tool, UseOnContext context, BlockState original, boolean playSound) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockPos above = pos.above();

    // hoes and shovels: air or plants above
    if (requireGround) {
      Material material = level.getBlockState(above).getMaterial();
      if (!material.isReplaceable() && material != Material.PLANT) {
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
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  public static Builder builder(ToolAction action, SoundEvent sound) {
    return new Builder(action, sound);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModifierModuleCondition.Builder<Builder> {
    private final ToolAction action;
    private final SoundEvent sound;
    private boolean requireGround;
    /**
     * Event ID to play upon success
     * @see Level#levelEvent(int, BlockPos, int)
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
