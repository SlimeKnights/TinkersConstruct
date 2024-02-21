package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Modules that add enchantments to a tool.
 */
public interface EnchantmentModule extends ModifierModule {
  /** Gets the enchantment for this module */
  Enchantment enchantment();
  /** Gets the level for this module */
  int level();

  ModifierModuleCondition condition();

  /** Creates a builder for a constant enchantment */
  static Builder constant(Enchantment enchantment) {
    return new Builder(Constant::new, enchantment);
  }

  /** Creates a builder for a harvest enchantment */
  static Builder harvest(Enchantment enchantment) {
    return new Builder(Harvest::new, enchantment);
  }

  /** Shared builder instance */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  class Builder extends ModifierModuleCondition.Builder<Builder> {
    private final EnchantmentModuleConstructor constructor;
    private final Enchantment enchantment;
    @Setter
    @Accessors(fluent = true)
    private int level = 1;

    /** Builds this module */
    public EnchantmentModule build() {
      return constructor.apply(enchantment, level, condition);
    }
  }

  /** Constructor for the loader for enchantment modules */
  @FunctionalInterface
  interface EnchantmentModuleConstructor {
    EnchantmentModule apply(Enchantment enchantment, int level, ModifierModuleCondition condition);
  }

  /** Loader shared logic for enchantment modules */
  record Loader(EnchantmentModuleConstructor constructor) implements IGenericLoader<EnchantmentModule> {
    @Override
    public EnchantmentModule deserialize(JsonObject json) {
      return constructor.apply(
        JsonHelper.getAsEntry(ForgeRegistries.ENCHANTMENTS, json, "name"),
        JsonUtils.getIntMin(json, "level", 1),
        ModifierModuleCondition.deserializeFrom(json));
    }

    @Override
    public void serialize(EnchantmentModule object, JsonObject json) {
      object.condition().serializeInto(json);
      json.addProperty("name", Objects.requireNonNull(object.enchantment().getRegistryName()).toString());
      json.addProperty("level", object.level());
    }

    @Override
    public EnchantmentModule fromNetwork(FriendlyByteBuf buffer) {
      return constructor.apply(
        buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS),
        buffer.readVarInt(),
        ModifierModuleCondition.fromNetwork(buffer));
    }

    @Override
    public void toNetwork(EnchantmentModule object, FriendlyByteBuf buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, object.enchantment());
      buffer.writeVarInt(object.level());
      object.condition().toNetwork(buffer);
    }
  }

  /**
   * Module that adds an enchantment applied during harvesting. Used for silk touch and fortune.
   * Incremental modules are handled by flooring the level
   */
  record Harvest(Enchantment enchantment, int level, ModifierModuleCondition condition) implements EnchantmentModule, HarvestEnchantmentsModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.TOOL_HARVEST_ENCHANTMENTS, TinkerHooks.LEGGINGS_HARVEST_ENCHANTMENTS);
    public static final Loader LOADER = new Loader(Harvest::new);

    @Override
    public void applyHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
      if (condition.matches(tool, modifier)) {
        int level = Mth.floor(modifier.getEffectiveLevel(tool) * this.level);
        if (level > 0) {
          consumer.accept(enchantment, level);
        }
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

  /**
   * Removes the passed enchantment from the tool
   * @param list          Tag list
   * @param enchantment   Enchantment to remove
   */
  private static void removeEnchantment(ListTag list, Enchantment enchantment) {
    String id = Objects.requireNonNull(enchantment.getRegistryName()).toString();
    Iterator<Tag> iterator = list.iterator();
    while (iterator.hasNext()) {
      Tag iteratorTag = iterator.next();
      if (iteratorTag.getId() == Tag.TAG_COMPOUND) {
        CompoundTag enchantmentTag = (CompoundTag)iteratorTag;
        if (id.equals(enchantmentTag.getString("id"))) {
          iterator.remove();
        }
      }
    }
  }

  /**
   * Sets the enchantment in the given tag to the given level. Will ignore whatever it was before.
   * Designed to be used in {@link RawDataModifierHook#addRawData(IToolStackView, ModifierEntry, RestrictedCompoundTag)}
   */
  static void setEnchantmentLevel(RestrictedCompoundTag tag, Enchantment enchantment, int level) {
    // first, find the enchantment tag
    ListTag enchantments;
    if (tag.contains(ModifierUtil.TAG_ENCHANTMENTS, Tag.TAG_LIST)) {
      enchantments = tag.getList(ModifierUtil.TAG_ENCHANTMENTS, Tag.TAG_COMPOUND);
      removeEnchantment(enchantments, enchantment);
    } else {
      enchantments = new ListTag();
      tag.put(ModifierUtil.TAG_ENCHANTMENTS, enchantments);
    }
    // add the enchantment
    enchantments.add(EnchantmentHelper.storeEnchantment(enchantment.getRegistryName(), level));
  }

  /**
   * Removes the given enchantment from the tag
   * Designed to be used in {@link RawDataModifierHook#removeRawData(IToolStackView, Modifier, RestrictedCompoundTag)}
   */
  static void removeEnchantment(RestrictedCompoundTag tag, Enchantment enchantment) {
    if (tag.contains(ModifierUtil.TAG_ENCHANTMENTS, Tag.TAG_LIST)) {
      ListTag enchantments = tag.getList(ModifierUtil.TAG_ENCHANTMENTS, Tag.TAG_COMPOUND);
      removeEnchantment(enchantments, enchantment);
      if (enchantments.isEmpty()) {
        tag.remove(ModifierUtil.TAG_ENCHANTMENTS);
      }
    }
  }

  /**
   * Module that adds an enchantment applied during tool build. This will have a side-effect of deleting enchantments added from other sources.
   * If multiple modifiers add an enchantment this way, the last modifier in priority will win out.
   * TODO 1.19: switch to new hook to make this less of a hack
   */
  record Constant(Enchantment enchantment, int level, ModifierModuleCondition condition) implements EnchantmentModule, RawDataModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.RAW_DATA);
    public static final Loader LOADER = new Loader(Constant::new);

    @Override
    public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
      if (condition.matches(tool, modifier)) {
        int level =  Mth.floor(this.level * modifier.getEffectiveLevel(tool));
        if (level > 0) {
          setEnchantmentLevel(tag, enchantment, level);
          return;
        }
      }
      // if the modifier is not currently applying, treat it as if it was removed
      // prevents it not applying to the current tool from leaving bad NBT around
      removeEnchantment(tag, enchantment);
    }

    @Override
    public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {
      removeEnchantment(tag, enchantment);
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
