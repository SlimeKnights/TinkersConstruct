package slimeknights.tconstruct.tables.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderContainer;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.MaterialItemCache;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Recipe to break a tool into tool parts.
 * TODO 1.21: move to {@link slimeknights.tconstruct.library.recipe.partbuilder.recycle}.
 */
@RequiredArgsConstructor
public class PartBuilderToolRecycle implements IPartBuilderRecipe, IMultiRecipe<IDisplayPartBuilderRecipe> {
  /** Title for the screen */
  private static final Component TOOL_RECYCLING = TConstruct.makeTranslation("recipe", "tool_recycling");
  /** General instructions for recycling */
  private static final List<Component> INSTRUCTIONS = Collections.singletonList(TConstruct.makeTranslation("recipe", "tool_recycling.info"));
  /** Error for trying to recycle a tool that cannot be */
  public static final List<Component> NO_MODIFIERS = Collections.singletonList(TConstruct.makeTranslation("recipe", "tool_recycling.no_modifiers").withStyle(ChatFormatting.RED));
  /** Default tool field */
  public static final SizedIngredient DEFAULT_TOOLS = SizedIngredient.fromTag(TinkerTags.Items.MULTIPART_TOOL);

  /** Loader instance */
  public static final RecordLoadable<PartBuilderToolRecycle> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    SizedIngredient.LOADABLE.defaultField("tools", DEFAULT_TOOLS, true, r -> r.toolRequirement),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("pattern", r -> r.pattern),
    TinkerLoadables.MATERIAL_ITEM.list(0).defaultField("parts", List.of(), r -> r.parts),
    PartBuilderToolRecycle::new);

  @Getter
  private final ResourceLocation id;
  private final SizedIngredient toolRequirement;
  private final Ingredient pattern;
  private final List<IMaterialItem> parts;

  /** @deprecated use {@link FinishedRecipe} */
  @Deprecated(forRemoval = true)
  public PartBuilderToolRecycle(ResourceLocation id, SizedIngredient toolRequirement, Ingredient pattern) {
    this(id, toolRequirement, pattern, List.of());
  }

  @Override
  public Pattern getPattern() {
    return MISSING;
  }

  /** Gets the parts for the given recipe and tool */
  private List<? extends IMaterialItem> getParts(ToolDefinition tool) {
    return !this.parts.isEmpty() ? this.parts : ToolPartsHook.parts(tool);
  }

  @Override
  public Stream<Pattern> getPatterns(IPartBuilderContainer inv) {
    // use the parts override if set
    ItemStack tool = inv.getStack();
    List<? extends IMaterialItem> parts = getParts(IModifiable.getToolDefinition(tool.getItem()));
    MaterialIdNBT materials = MaterialIdNBT.from(tool);
    return IntStream.range(0, parts.size()).filter(i -> parts.get(i).canUseMaterial(materials.getMaterial(i).getId()))
      .mapToObj(i -> Pattern.fromItem(parts.get(i)))
      .distinct();
  }

  @Override
  public int getCost() {
    return 0;
  }

  @Override
  public int getItemsUsed(IPartBuilderContainer inv) {
    return toolRequirement.getAmountNeeded();
  }

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    return pattern.test(inv.getPatternStack()) && toolRequirement.test(inv.getStack());
  }

  @Override
  public boolean matches(IPartBuilderContainer inv, Level pLevel) {
    return partialMatch(inv) && ToolStack.from(inv.getStack()).getUpgrades().isEmpty();
  }

  @Override
  public ItemStack assemble(IPartBuilderContainer inv, RegistryAccess access, Pattern pattern) {
    ToolStack tool = ToolStack.from(inv.getStack());
    // find our parts list, either set or override
    ToolDefinition definition = tool.getDefinition();
    List<? extends IMaterialItem> parts = getParts(definition);
    // ensure parts list is not greater than material count
    int materialSize = Math.min(ToolMaterialHook.stats(definition).size(), parts.size());
    MaterialNBT materials = tool.getMaterials();
    // find part matching pattern
    IMaterialItem match = null;
    int matchIndex = -1;
    for (int i = 0; i < materialSize; i++) {
      IMaterialItem part = parts.get(i);
      if (pattern.matches(part) && part.canUseMaterial(materials.get(i).getId())) {
        matchIndex = i;
        match = part;
        break;
      }
    }
    // failed to find part? should never happen but safety return
    if (match == null) {
      return ItemStack.EMPTY;
    }
    // special handling for oddball cases with ancient tool recycling
    MaterialVariantId variant = tool.getMaterial(matchIndex).getVariant();
    if (!match.canUseMaterial(variant.getId())) {
      return ItemStack.EMPTY;
    }
    return match.withMaterial(variant);
  }

  @Override
  public ItemStack getLeftover(IPartBuilderContainer inv, Pattern pattern) {
    ToolStack tool = ToolStack.from(inv.getStack());

    // if the tool is damaged, we only have a chance of a second tool part
    int damage = tool.getDamage();
    if (damage > 0) {
      int max = tool.getStats().getInt(ToolStats.DURABILITY);
      if (TConstruct.RANDOM.nextInt(max) < damage) {
        return ItemStack.EMPTY;
      }
    }

    // find our parts list, either set or override
    ToolDefinition definition = tool.getDefinition();
    List<? extends IMaterialItem> requirements = getParts(definition);
    // ensure parts list is not greater than material count
    int materials = Math.min(ToolMaterialHook.stats(definition).size(), requirements.size());
    // find all parts that did not match the pattern
    IntList indices = new IntArrayList();
    boolean found = false;
    List<IMaterialItem> parts = new ArrayList<>();
    for (int i = 0; i < materials; i++) {
      IMaterialItem part = requirements.get(i);
      if (found || !pattern.matches(part)) {
        // ensure the part is actually craftable with the material. Works around issues with using repair kits for some partless types
        if (part.canUseMaterial(tool.getMaterial(i).getId())) {
          parts.add(part);
          indices.add(i);
        }
      } else {
        found = true;
      }
    }
    if (parts.isEmpty()) {
      return ItemStack.EMPTY;
    }
    int index = TConstruct.RANDOM.nextInt(parts.size());
    return parts.get(index).withMaterial(tool.getMaterial(indices.getInt(index)).getVariant());
  }

  /** @deprecated use {@link IPartBuilderRecipe#assemble(IPartBuilderContainer, RegistryAccess, Pattern)} */
  @Deprecated
  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.partBuilderToolRecycling.get();
  }

  @Nullable
  @Override
  public Component getTitle() {
    return TOOL_RECYCLING;
  }

  @Override
  public List<Component> getText(IPartBuilderContainer inv) {
    return ModifierUtil.hasUpgrades(inv.getStack()) ? NO_MODIFIERS : INSTRUCTIONS;
  }


  /* JEI */
  private List<IDisplayPartBuilderRecipe> displayRecipes;

  /** Gets the display parts for a given part list. */
  private static Object2IntMap<IMaterialItem> getDisplayParts(List<? extends IMaterialItem> parts) {
    Object2IntMap<IMaterialItem> map = new Object2IntArrayMap<>(parts.size());
    for (int i = 0; i < parts.size(); i++) {
      map.putIfAbsent(parts.get(i), i);
    }
    return map;
  }

  @Override
  public List<IDisplayPartBuilderRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      // if we have a parts override, might as well only compute this map once
      Object2IntMap<IMaterialItem> partsOverride = parts.isEmpty() ? Object2IntMaps.emptyMap() : getDisplayParts(parts);
      List<ItemStack> patternItems = List.of(this.pattern.getItems());
      displayRecipes = toolRequirement.getMatchingStacks().stream()
        .<IDisplayPartBuilderRecipe>flatMap(stack -> {
          // if we have a parts override, use that instead of the tool parts
          Object2IntMap<IMaterialItem> parts = !partsOverride.isEmpty() ? partsOverride : getDisplayParts(ToolPartsHook.parts(IModifiable.getToolDefinition(stack.getItem())));
          // may have no parts if no override and the tool lacks parts
          if (parts.isEmpty()) {
            return Stream.empty();
          }
          Collection<IMaterialItem> partItems = parts.keySet();
          return Stream.of(new DisplayRecipe(parts,
            partItems.stream().map(Pattern::fromItem).toList(),
            patternItems,
            List.of(IModifiableDisplay.getDisplayStack(stack)),
            parts.object2IntEntrySet().stream().map(pi -> {
              ItemStack part = pi.getKey().withMaterialForDisplay(ToolBuildHandler.getRenderMaterial(pi.getIntValue()));
              part.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
              return part;
            }).toList(),
            partItems.stream().flatMap(part -> MaterialItemCache.getAllMaterials(part).stream()).toList()
          ));
        })
        .toList();
    }
    return displayRecipes;
  }

  @Getter
  @RequiredArgsConstructor
  private class DisplayRecipe implements IDisplayPartBuilderRecipe.DisplayOnly {
    private final Object2IntMap<IMaterialItem> parts;
    private final List<Pattern> patterns;
    private final List<ItemStack> patternItems;
    private final List<ItemStack> materialItems;
    private final List<ItemStack> resultItems;
    private final List<ItemStack> hiddenOutputs;

    @Override
    public Component getDisplayTitle() {
      return TOOL_RECYCLING;
    }

    @Override
    public List<Component> getTooltip() {
      return INSTRUCTIONS;
    }

    @Override
    public Pattern getPattern() {
      return patterns.get(0);
    }

    @Override
    public ResourceLocation getId() {
      return id;
    }

    @Override
    public int getCost() {
      return 0;
    }


    /* Dynamic focusing */

    @Override
    public List<ItemStack> getMaterialItems(MaterialVariant focusMaterial, ItemStack focusStack, boolean focusOutput) {
      if (!focusStack.isEmpty()) {
        ItemStack displayTool = materialItems.get(0);
        if (focusOutput) {
          // focusing on a specific tool material, show that on the input tool
          int index = parts.getOrDefault(focusStack.getItem(), -1);
          if (index != -1) {
            MaterialVariantId material = IMaterialItem.getMaterialFromStack(focusStack);
            if (!MaterialId.UNKNOWN.equals(material)) {
              // create a copy of the tool with the material replaced
              MaterialIdNBT materials = MaterialIdNBT.from(displayTool);
              return List.of(materials.replaceMaterial(index, material).updateStack(new ItemStack(displayTool.getItem(), displayTool.getCount())));
            }
          }
        } else if (focusStack.is(displayTool.getItem())) {
          // if focusing on a tool, display the input as having the same tool materials. Don't copy other data as we need no modifiers to recycle
          return List.of(MaterialIdNBT.from(focusStack).updateStack(new ItemStack(displayTool.getItem(), displayTool.getCount())));
        }
      }
      return materialItems;
    }

    @Override
    public List<ItemStack> getResultItems(MaterialVariant focusMaterial, ItemStack focusStack, boolean focusOutput) {
      if (!focusStack.isEmpty()) {
        if (focusOutput) {
          // focusing on a specific tool material, make that our display provided its valid
          if (parts.getOrDefault(focusStack.getItem(), -1) != -1 && !MaterialId.UNKNOWN.equals(IMaterialItem.getMaterialFromStack(focusStack))) {
            // keep the other parts in the list so the focus link works, saves having to duplicate this logic for patterns
            return resultItems.stream().map(stack -> stack.is(focusStack.getItem()) ? focusStack.copyWithCount(1) : stack).toList();
          }
        } else if (focusStack.is(materialItems.get(0).getItem())) {
          // if focusing on a tool, display its actual parts as results
          MaterialIdNBT materials = MaterialIdNBT.from(focusStack);
          return parts.object2IntEntrySet().stream()
            .map(pi -> pi.getKey().withMaterialForDisplay(materials.getMaterial(pi.getIntValue())))
            .toList();
        }
      }
      return resultItems;
    }
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.recipe.partbuilder.recycle.PartBuilderToolRecycleBuilder} */
  @Deprecated(forRemoval = true)
  public record Finished(ResourceLocation getId, SizedIngredient tools, Ingredient pattern) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject json) {
      json.add("tools", SizedIngredient.LOADABLE.serialize(tools));
      json.add("pattern", pattern.toJson());
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerTables.partBuilderToolRecycling.get();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
      return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
      return null;
    }
  }
}
