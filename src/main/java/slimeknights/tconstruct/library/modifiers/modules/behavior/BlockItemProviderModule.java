package slimeknights.tconstruct.library.modifiers.modules.behavior;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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

import javax.annotation.Nullable;
import java.util.List;

/**
 * A module that uses {@link slimeknights.tconstruct.library.tools.capability.BlockItemProviderCapability BlockItemProviderCapability} via {@link ToolBlockItemProviderHook} to provide BlockItems to modifiers like exchanging at the cost of durability.
 * Note this does not let the tool place blocks, it only exposes this capability. See {@link slimeknights.tconstruct.tools.modules.interaction.PlaceGlowModule PlaceGlowModule} for an example of a custom module that lets the tool place blocks.
 * @param item The BlockItem to provide, wrapped in an ItemStack
 * @param damage The amount of damage it takes to provide one block (can be 0)
 * @param condition Other conditions that you might want to condition the providing on, such as only happening on certain tool types.
 */
public record BlockItemProviderModule(ItemStack item, int damage, ModifierCondition<IToolStackView> condition) implements ModifierModule, ToolBlockItemProviderHook, ModifierCondition.ConditionalModule<IToolStackView> {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = List.of(ModifierHooks.BLOCK_ITEM_PROVIDER);
    public static final RecordLoadable<BlockItemProviderModule> LOADER = RecordLoadable.create(
            TinkerLoadables.BLOCK_ITEM.flatComap(ItemStack::new, (i, e) -> {
                if (i.getItem() instanceof BlockItem item)
                    return item;
                throw e.create(String.format("Expected item %s to be instance of BlockItem, but was %s instead", BuiltInRegistries.ITEM.getKey(i.getItem()), i.getItem().getClass().getName()));
            }).requiredField("item", BlockItemProviderModule::item),
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

    @Nullable
    @Override
    public ItemStack getBlockItemStack(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity entity) {
        return !tool.isBroken() && condition.matches(tool, modifier) ? item : ItemStack.EMPTY;
    }

    @Override
    public boolean consumeBlockItem(IToolStackView tool, ItemStack toolStack, ModifierEntry modifier, ItemStack backingStack, @Nullable LivingEntity entity) {
        // if this is not our item, then we did not provide it so we should avoid consuming
        if (item != backingStack) return false;

        // we did provide it, so damage and show animation if possible
        if (ToolDamageUtil.damage(tool, damage, entity, toolStack) && entity != null) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (entity.getItemBySlot(slot) == toolStack) {
                    entity.broadcastBreakEvent(slot);
                    break;
                }
            }
        }
        return true;
    }
}
