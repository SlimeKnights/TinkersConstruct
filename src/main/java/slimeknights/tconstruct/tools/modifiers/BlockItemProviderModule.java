package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.ToolBlockItemProviderHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public record BlockItemProviderModule(BlockItem item, int damage, ModifierCondition<IToolStackView> condition) implements ModifierModule, ToolBlockItemProviderHook, ModifierCondition.ConditionalModule<IToolStackView> {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = List.of(ModifierHooks.BLOCK_ITEM_PROVIDER);
    public static final RecordLoadable<BlockItemProviderModule> LOADER = RecordLoadable.create(
            TinkerLoadables.BLOCK_ITEM.requiredField("item", BlockItemProviderModule::item),
            IntLoadable.FROM_ZERO.defaultField("tool_damage", 1, BlockItemProviderModule::damage),
            ModifierCondition.TOOL_FIELD,
            BlockItemProviderModule::new);

    @Override
    public RecordLoadable<BlockItemProviderModule> getLoader() {
        return LOADER;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public @Nullable BlockItem getBlockItem(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity, @Nullable ItemStack stack) {
        return !tool.isBroken() && condition.matches(tool, modifier) ? item : null;
    }

    @Override
    public void consume(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity, @Nullable ItemStack stack) {
        ToolDamageUtil.damage(tool, damage, entity, stack);
    }
}
