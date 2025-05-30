package slimeknights.tconstruct.library.modifiers.modules.build;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;

import java.util.List;

/**
 * Module for setting tool's display name rarity
 * TODO: consider modifier level/tool conditions
 */
public record RarityModule(Rarity rarity) implements VolatileDataModifierHook, ModifierModule {
  /** Volatile data key storing rarity */
  public static final ResourceLocation RARITY = TConstruct.getResource("rarity");

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<RarityModule>defaultHooks(ModifierHooks.VOLATILE_DATA);
  public static final RecordLoadable<RarityModule> LOADER = RecordLoadable.create(new EnumLoadable<>(Rarity.class).requiredField("rarity", RarityModule::rarity), RarityModule::new);

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ToolDataNBT volatileData) {
    setRarity(volatileData, rarity);
  }

  @Override
  public RecordLoadable<RarityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Gets the rarity for the given stack */
  public static Rarity getRarity(ItemStack stack) {
    int rarity = ModifierUtil.getVolatileInt(stack, RARITY);
    Rarity[] values = Rarity.values();
    return values[Mth.clamp(rarity, 0, values.length)];
  }

  /**
   * Sets the rarity of the stack
   * @param volatileData     NBT
   * @param rarity  Rarity, largest index will be kept (so modded rarities tend to beat out vanilla).
   */
  public static void setRarity(ModDataNBT volatileData, Rarity rarity) {
    int current = volatileData.getInt(RARITY);
    // TODO: consider a sorted list of rarity values that JSON can set
    if (rarity.ordinal() > current) {
      volatileData.putInt(RARITY, rarity.ordinal());
    }
  }
}
