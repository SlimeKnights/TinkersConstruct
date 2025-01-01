package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/** Ingredient that only matches tools with a specific hook */
public class ToolHookIngredient extends AbstractIngredient {
  private final TagKey<Item> tag;
  private final ModuleHook<?> hook;

  protected ToolHookIngredient(TagKey<Item> tag, ModuleHook<?> hook) {
    super(Stream.of(new ToolHookValue(tag, hook)));
    this.tag = tag;
    this.hook = hook;
  }

  public static ToolHookIngredient of(TagKey<Item> tag, ModuleHook<?> hook) {
    return new ToolHookIngredient(tag, hook);
  }

  public static ToolHookIngredient of(ModuleHook<?> hook) {
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
  public IIngredientSerializer<? extends Ingredient> getSerializer() {
    return Serializer.INSTANCE;
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", Serializer.ID.toString());
    json.addProperty("tag", tag.location().toString());
    json.addProperty("hook", hook.getId().toString());
    return json;
  }

  @RequiredArgsConstructor
  public static class ToolHookValue implements Value {
    private final TagKey<Item> tag;
    private final ModuleHook<?> hook;

    @Override
    public Collection<ItemStack> getItems() {
      List<ItemStack> list = new ArrayList<>();

      // filtered version of tag values
      for(Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
        if (holder.value() instanceof IModifiable modifiable && modifiable.getToolDefinition().getData().getHooks().hasHook(hook)) {
          list.add(new ItemStack(modifiable));
        }
      }
      if (list.size() == 0) {
        list.add(new ItemStack(Blocks.BARRIER).setHoverName(Component.literal("Empty Tag: " + tag.location())));
      }
      return list;
    }

    @Override
    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      json.addProperty("id", Serializer.ID.toString());
      json.addProperty("tag", tag.location().toString());
      json.addProperty("hook", hook.getId().toString());
      return json;
    }
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
