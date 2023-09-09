package slimeknights.tconstruct.library.modifiers.modules;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.dynamic.EnchantmentModifier;
import slimeknights.tconstruct.library.modifiers.hook.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Modules that add enchantments to a tool.
 */
@RequiredArgsConstructor
public abstract class EnchantmentModule implements ModifierModule {
  protected final Enchantment enchantment;
  protected final int level;

  /** Loader shared logic for enchantment modules */
  public record Loader<T extends EnchantmentModule>(BiFunction<Enchantment, Integer, T> constructor) implements IGenericLoader<T> {
    @Override
    public T deserialize(JsonObject json) {
      Enchantment enchantment = JsonHelper.getAsEntry(ForgeRegistries.ENCHANTMENTS, json, "name");
      int level = JsonUtils.getIntMin(json, "level", 1);
      return constructor.apply(enchantment, level);
    }

    @Override
    public void serialize(T object, JsonObject json) {
      json.addProperty("name", Objects.requireNonNull(object.enchantment.getRegistryName()).toString());
      json.addProperty("level", object.level);
    }

    @Override
    public T fromNetwork(FriendlyByteBuf buffer) {
      Enchantment enchantment = buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS);
      int level = buffer.readVarInt();
      return constructor.apply(enchantment, level);
    }

    @Override
    public void toNetwork(T object, FriendlyByteBuf buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, object.enchantment);
      buffer.writeVarInt(object.level);
    }
  }

  /**
   * Module that adds an enchantment applied during harvesting. Used for silk touch and fortune.
   * Currently, does not support incremental.
   */
  public static class Harvest extends EnchantmentModule implements HarvestEnchantmentsModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.TOOL_HARVEST_ENCHANTMENTS, TinkerHooks.LEGGINGS_HARVEST_ENCHANTMENTS);
    public static final Loader<Harvest> LOADER = new Loader<>(Harvest::new);

    public Harvest(Enchantment enchantment, int level) {
      super(enchantment, level);
    }

    public Harvest(Enchantment enchantment) {
      this(enchantment, 1);
    }

    @Override
    public void applyHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
      consumer.accept(enchantment, level * modifier.getLevel());
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

  /**
   * Module that adds an enchantment applied during tool build. This will have a side-effect of deleting enchantments added from other sources.
   * If multiple modifiers add an enchantment this way, the highest level wins out.
   * TODO 1.19: switch to new hook to make this less of a hack
   */
  public static class Constant extends EnchantmentModule implements RawDataModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.RAW_DATA);
    public static final Loader<Constant> LOADER = new Loader<>(Constant::new);

    public Constant(Enchantment enchantment, int level) {
      super(enchantment, level);
    }

    public Constant(Enchantment enchantment) {
      this(enchantment, 1);
    }

    @Override
    public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
      EnchantmentModifier.addEnchantmentData(tag, enchantment, modifier.getLevel() * level);
    }

    @Override
    public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {
      EnchantmentModifier.removeEnchantmentData(tag, enchantment);
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
