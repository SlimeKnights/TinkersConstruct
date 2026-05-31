package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import slimeknights.mantle.compat.neoforged.neoforge.common.crafting.IIngredientSerializer;
import net.neoforged.neoforge.common.crafting.IngredientType;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/** Ingredient that only matches tools with a specific hook */
public class ToolHookIngredient implements ICustomIngredient {
  private final TagKey<Item> tag;
  private final ModuleHook<?> hook;
  @Nullable
  private ItemStack[] items;

  protected ToolHookIngredient(TagKey<Item> tag, ModuleHook<?> hook) {
    this.tag = tag;
    this.hook = hook;
  }

  public static Ingredient of(TagKey<Item> tag, ModuleHook<?> hook) {
    return new ToolHookIngredient(tag, hook).toVanilla();
  }

  public static Ingredient of(ModuleHook<?> hook) {
    return of(TinkerTags.Items.MODIFIABLE, hook);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && stack.is(tag) && stack.getItem() instanceof IModifiable modifiable && modifiable.getToolDefinition().getData().getHooks().hasHook(hook);
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  @Override
  public Stream<ItemStack> getItems() {
    if (items == null) {
      List<ItemStack> list = new ArrayList<>();
      for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
        if (holder.value() instanceof IModifiable modifiable && modifiable.getToolDefinition().getData().getHooks().hasHook(hook)) {
          list.add(new ItemStack(modifiable));
        }
      }
      if (list.isEmpty()) {
        ItemStack barrier = new ItemStack(Blocks.BARRIER);
        barrier.set(DataComponents.CUSTOM_NAME, Component.literal("Empty Tag: " + tag.location()));
        list.add(barrier);
      }
      items = list.toArray(ItemStack[]::new);
    }
    return Stream.of(items);
  }

  @Override
  public IngredientType<?> getType() {
    return TinkerTools.toolHookIngredient.get();
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", Serializer.ID.toString());
    json.addProperty("tag", tag.location().toString());
    json.addProperty("hook", hook.getId().toString());
    return json;
  }

  @Override
  public boolean equals(Object object) {
    return this == object || object instanceof ToolHookIngredient that && tag.equals(that.tag) && hook.equals(that.hook);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tag, hook);
  }

  /** Serializer instance */
  public enum Serializer implements IIngredientSerializer<ToolHookIngredient> {
    INSTANCE;

    public static final ResourceLocation ID = TConstruct.getResource("tool_hook");

    @Override
    public ToolHookIngredient parse(JsonObject json) {
      return new ToolHookIngredient(
        Loadables.ITEM_TAG.getOrDefault(json, "tag", TinkerTags.Items.MODIFIABLE),
        ToolHooks.LOADER.getIfPresent(json, "hook")
      );
    }

    @Override
    public ToolHookIngredient parse(FriendlyByteBuf buffer) {
      return new ToolHookIngredient(
        Loadables.ITEM_TAG.decode(buffer),
        ToolHooks.LOADER.decode(buffer)
      );
    }

    @Override
    public void write(FriendlyByteBuf buffer, ToolHookIngredient ingredient) {
      Loadables.ITEM_TAG.encode(buffer, ingredient.tag);
      ToolHooks.LOADER.encode(buffer, ingredient.hook);
    }
  }
}
