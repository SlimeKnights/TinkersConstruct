package slimeknights.tconstruct.library.tools.definition.module.aoe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.loadable.mapping.ConditionalLoadable.ConditionalObject;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

/**
 * Datagen helper for making conditional {@link AreaOfEffectIterator} in {@link ConditionalAOEIterator}.
 * For standard tool module usage, see {@link slimeknights.tconstruct.library.tools.definition.module.ConditionalToolModule}
 * @param ifTrue      Supplier to use if all conditions are true.
 * @param ifFalse     Supplier to use if any condition is false. Defaults to {@link ArmorTextureSupplier#EMPTY}
 * @param conditions  Conditions to evaluate.
 */
@SuppressWarnings("unused") // API
public record LoadConditionAOEIterator(Loadable ifTrue, Loadable ifFalse, ICondition... conditions) implements AreaOfEffectIterator.Loadable, ConditionalObject<AreaOfEffectIterator.Loadable> {
  @Override
  public RecordLoadable<? extends AreaOfEffectIterator.Loadable> getLoader() {
    return AreaOfEffectIterator.LOADER.getConditionalLoader();
  }

  @Override
  public Iterable<BlockPos> getBlocks(IToolStackView tool, UseOnContext context, BlockState state, AOEMatchType matchType) {
    AreaOfEffectIterator iterator = Util.testConditions(conditions) ? ifTrue : ifFalse;
    return iterator.getBlocks(tool, context, state, matchType);
  }
}
