package slimeknights.tconstruct.library.modifiers.modules.build;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.util.ModuleWithKey;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for a extra slot modifier with multiple variants based on the slot type
 * @param key             Persistent data key containing the slot name. If null, uses the modifier ID.
 *                        Presently, changing this makes it incompatible with the swappable modifier recipe, this is added for future proofing.
 * @param slotCount       Number of slots to grant
 */
public record SwappableSlotModule(@Nullable ResourceLocation key, int slotCount) implements VolatileDataModifierHook, DisplayNameModifierHook, ModifierRemovalHook, ModifierModule, ModuleWithKey {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.VOLATILE_DATA, TinkerHooks.DISPLAY_NAME, TinkerHooks.REMOVE);
  /** Format key for swappable variant */
  private static final String FORMAT = TConstruct.makeTranslationKey("modifier", "extra_modifier.type_format");
  public static final RecordLoadable<SwappableSlotModule> LOADER = RecordLoadable.create(
    ModuleWithKey.FIELD,
    IntLoadable.ANY_SHORT.field("slots", SwappableSlotModule::slotCount),
    SwappableSlotModule::new);

  public SwappableSlotModule(int slotCount) {
    this(null, slotCount);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, Modifier modifier, int level, Component name) {
    String slotName = tool.getPersistentData().getString(getKey(modifier));
    if (!slotName.isEmpty()) {
      SlotType type = SlotType.getIfPresent(slotName);
      if (type != null) {
        return Component.translatable(FORMAT, name.plainCopy(), type.getDisplayName()).withStyle(style -> style.withColor(type.getColor()));
      }
    }
    return name;
  }

  @Override
  public Integer getPriority() {
    // show lower priority so they group together
    return 50;
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    String slotName = context.getPersistentData().getString(getKey(modifier.getModifier()));
    if (!slotName.isEmpty()) {
      SlotType type = SlotType.getIfPresent(slotName);
      if (type != null) {
        volatileData.addSlots(type, slotCount);
      }
    }
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(modifier.getId());
    return null;
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  /** Module to add (or remove) additional slots based on the given swappable slot type */
  public record BonusSlot(@Nullable ResourceLocation key, SlotType match, SlotType bonus, int slotCount) implements VolatileDataModifierHook, ModifierModule, ModuleWithKey {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.VOLATILE_DATA);
    public static final RecordLoadable<BonusSlot> LOADER = RecordLoadable.create(
      ModuleWithKey.FIELD,
      SlotType.LOADABLE.field("match", BonusSlot::match),
      SlotType.LOADABLE.field("bonus", BonusSlot::bonus),
      IntLoadable.ANY_SHORT.field("slots", BonusSlot::slotCount),
      BonusSlot::new);

    public BonusSlot(SlotType match, SlotType penalty, int slotCount) {
      this(null, match, penalty, slotCount);
    }

    @Override
    public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
      String slotName = context.getPersistentData().getString(getKey(modifier.getModifier()));
      if (!slotName.isEmpty() && match.getName().equals(slotName)) {
        volatileData.addSlots(bonus, slotCount);
      }
    }

    @Override
    public List<ModifierHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public IGenericLoader<? extends ModifierModule> getLoader() {
      return LOADER;
    }
  }
}
