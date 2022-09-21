package slimeknights.tconstruct.tables.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderContainer;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.entity.inventory.PartBuilderContainerWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/** Recipe to break a tool into tool parts */
@RequiredArgsConstructor
public class PartBuilderToolRecycle implements IPartBuilderRecipe {
  /** Title for the screen */
  private static final Component TOOL_RECYCLING = TConstruct.makeTranslation("recipe", "tool_recycling");
  /** General instructions for recycling */
  private static final List<Component> INSTRUCTIONS = Collections.singletonList(TConstruct.makeTranslation("recipe", "tool_recycling.info"));
  /** Error for trying to recycle a tool that cannot be */
  private static final List<Component> NO_MODIFIERS = Collections.singletonList(TConstruct.makeTranslation("recipe", "tool_recycling.no_modifiers").withStyle(ChatFormatting.RED));

  /** Should never be needed, but just in case better than null */
  private static final Pattern ERROR = new Pattern(TConstruct.MOD_ID, "missingno");
  @Getter
  private final ResourceLocation id;
  private final Ingredient pattern;

  @Override
  public Pattern getPattern() {
    return ERROR;
  }

  @Override
  public Stream<Pattern> getPatterns(IPartBuilderContainer inv) {
    return ToolStack.from(inv.getStack()).getDefinition().getData().getParts().stream()
               .map(PartRequirement::getPart)
               .filter(Objects::nonNull)
               .map(part -> part.asItem().getRegistryName())
               .filter(Objects::nonNull)
               .distinct()
               .map(Pattern::new);
  }

  @Override
  public int getCost() {
    return 0;
  }

  @Override
  public int getItemsUsed(IPartBuilderContainer inv) {
    return 1;
  }

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    return pattern.test(inv.getPatternStack()) && inv.getStack().is(TinkerTags.Items.MULTIPART_TOOL);
  }

  @Override
  public boolean matches(IPartBuilderContainer inv, Level pLevel) {
    return partialMatch(inv) && ToolStack.from(inv.getStack()).getUpgrades().isEmpty();
  }

  @Override
  public ItemStack assemble(IPartBuilderContainer inv, Pattern pattern) {
    ToolStack tool = ToolStack.from(inv.getStack());
    // first, try to find a matching part
    IToolPart match = null;
    int matchIndex = -1;
    List<PartRequirement> requirements = tool.getDefinition().getData().getParts();
    for (int i = 0; i < requirements.size(); i++) {
      IToolPart part = requirements.get(i).getPart();
      if (part != null && pattern.equals(part.asItem().getRegistryName())) {
        matchIndex = i;
        match = part;
        break;
      }
    }
    // failed to find part? should never happen but safety return
    if (match == null) {
      return ItemStack.EMPTY;
    }
    return match.withMaterial(tool.getMaterial(matchIndex).getVariant());
  }

  @Override
  public ItemStack getLeftover(PartBuilderContainerWrapper inv, Pattern pattern) {
    ToolStack tool = ToolStack.from(inv.getStack());

    // if the tool is damaged, it we only have a chance of a second tool part
    int damage = tool.getDamage();
    if (damage > 0) {
      int max = tool.getStats().getInt(ToolStats.DURABILITY);
      if (TConstruct.RANDOM.nextInt(max) < damage) {
        return ItemStack.EMPTY;
      }
    }

    // find all parts that did not match the pattern
    List<IToolPart> parts = new ArrayList<>();
    IntList indices = new IntArrayList();
    boolean found = false;
    List<PartRequirement> requirements = tool.getDefinition().getData().getParts();
    for (int i = 0; i < requirements.size(); i++) {
      IToolPart part = requirements.get(i).getPart();
      if (part != null) {
        if (found || !pattern.equals(part.asItem().getRegistryName())) {
          parts.add(part);
          indices.add(i);
        } else {
          found = true;
        }
      }
    }
    if (parts.isEmpty()) {
      return ItemStack.EMPTY;
    }
    int index = TConstruct.RANDOM.nextInt(parts.size());
    return parts.get(index).withMaterial(tool.getMaterial(indices.getInt(index)).getVariant());
  }

  /** @deprecated use {@link #assemble(IPartBuilderContainer, Pattern)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
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

  /** Serializer instance */
  public static class Serializer extends LoggingRecipeSerializer<PartBuilderToolRecycle> {
    @Override
    public PartBuilderToolRecycle fromJson(ResourceLocation id, JsonObject json) {
      return new PartBuilderToolRecycle(id, Ingredient.fromJson(JsonHelper.getElement(json, "pattern")));
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, PartBuilderToolRecycle recipe) {
      recipe.pattern.toNetwork(buffer);
    }

    @Nullable
    @Override
    protected PartBuilderToolRecycle fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      return new PartBuilderToolRecycle(id, Ingredient.fromNetwork(buffer));
    }
  }

  @RequiredArgsConstructor
  public static class Finished implements FinishedRecipe {
    @Getter
    private final ResourceLocation id;
    private final Ingredient pattern;

    @Override
    public void serializeRecipeData(JsonObject json) {
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
