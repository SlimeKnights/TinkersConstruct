package slimeknights.tconstruct.library.tools.definition.module.mining;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.compat.neoforged.neoforge.common.TierSortingRegistry;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Makes the tool effective based on the passed predicate */
public record IsEffectiveModule(IJsonPredicate<BlockState> predicate, boolean ignoreTier) implements IsEffectiveToolHook, ToolModule {
  public static final RecordLoadable<IsEffectiveModule> LOADER = RecordLoadable.create(
    BlockPredicate.LOADER.directField("predicate_type", IsEffectiveModule::predicate),
    BooleanLoadable.INSTANCE.defaultField("ignore_tier", false, false, IsEffectiveModule::ignoreTier),
    IsEffectiveModule::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<IsEffectiveModule>defaultHooks(ToolHooks.IS_EFFECTIVE);

  public IsEffectiveModule(IJsonPredicate<BlockState> predicate) {
    this(predicate, false);
  }

  /** Module for effectiveness based on a tag */
  public static IsEffectiveModule tag(TagKey<Block> tag) {
    return new IsEffectiveModule(BlockPredicate.tag(tag), false);
  }

  @Override
  public RecordLoadable<IsEffectiveModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean isToolEffective(IToolStackView tool, BlockState state) {
    return predicate.matches(state) && (ignoreTier || TierSortingRegistry.isCorrectTierForDrops(MiningTierToolHook.getTier(tool), state));
  }
}
