package slimeknights.tconstruct.library.modifiers.modules;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Module that adds an enchantment applied during harvesting. Used for silk touch and fortune.
 * Currently, does not support incremental.
 */
public record HarvestEnchantmentModule(Enchantment enchantment, int level) implements ModifierModule, HarvestEnchantmentsModifierHook {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.TOOL_HARVEST_ENCHANTMENTS, TinkerHooks.LEGGINGS_HARVEST_ENCHANTMENTS);

  public HarvestEnchantmentModule(Enchantment enchantment) {
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

  public static IGenericLoader<HarvestEnchantmentModule> LOADER = new IGenericLoader<>() {
    @Override
    public HarvestEnchantmentModule deserialize(JsonObject json) {
      Enchantment enchantment = JsonHelper.getAsEntry(ForgeRegistries.ENCHANTMENTS, json, "name");
      int level = JsonUtils.getIntMin(json, "level", 1);
      return new HarvestEnchantmentModule(enchantment, level);
    }

    @Override
    public void serialize(HarvestEnchantmentModule object, JsonObject json) {
      json.addProperty("name", Objects.requireNonNull(object.enchantment.getRegistryName()).toString());
      json.addProperty("level", object.level);
    }

    @Override
    public HarvestEnchantmentModule fromNetwork(FriendlyByteBuf buffer) {
      Enchantment enchantment = buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS);
      int level = buffer.readVarInt();
      return new HarvestEnchantmentModule(enchantment, level);
    }

    @Override
    public void toNetwork(HarvestEnchantmentModule object, FriendlyByteBuf buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, object.enchantment);
      buffer.writeVarInt(object.level);
    }
  };
}
