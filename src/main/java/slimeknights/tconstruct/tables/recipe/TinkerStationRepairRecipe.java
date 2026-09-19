package slimeknights.tconstruct.tables.recipe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.modifiers.hook.behavior.RepairFactorModifierHook;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.display.RecipeSlot;
import slimeknights.tconstruct.library.recipe.display.RecipeSlots;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolModification;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairToolHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IRepairKitItem;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * Recipe for repairing tools in the tinker station or anvil.
 * Handles calls to {@link slimeknights.tconstruct.library.modifiers.hook.behavior.MaterialRepairModifierHook} and {@link MaterialRepairToolHook}.
 * @see CraftingTableRepairKitRecipe
 * @see slimeknights.mantle.recipe.helper.SimpleFinishedRecipe
 */
@RequiredArgsConstructor
public class TinkerStationRepairRecipe implements ITinkerStationRecipe, IMultiRecipe<IDisplayToolModification> {
  public static final Component TITLE = TConstruct.makeTranslation("recipe", "tool_repair");
  private static final Component TOOLTIP = TConstruct.makeTranslation("recipe", "tool_repair.tooltip");
  public static final Component REPAIRED = TConstruct.makeTranslation("recipe", "tool_repair.fully_repaired");
  protected static final RecipeResult<LazyToolStack> FULLY_REPAIRED = RecipeResult.failure(REPAIRED);
  /** No action int consumer for recipe result */
  private static final IntConsumer NO_ACTION = i -> {};

  @Getter
  private final ResourceLocation id;

  /**
   * Gets the material for the given slot
   * @param inv   Inventory instance
   * @param slot  Slot
   * @return  Material amount
   */
  protected static MaterialId getMaterialFrom(ITinkerStationContainer inv, int slot) {
    // try repair kit first
    ItemStack item = inv.getInput(slot);
    if (item.getItem() instanceof IRepairKitItem kit) {
      return kit.getMaterial(item).getId();
    }
    // material recipe fallback
    MaterialRecipe recipe = inv.getInputMaterial(slot);
    if (recipe != null) {
      return recipe.getMaterial().getId();
    }
    return MaterialId.UNKNOWN;
  }

  /** Gets the amount to repair given the passed tool */
  protected float getRepairAmount(IToolStackView tool, MaterialId repairMaterial) {
    return MaterialRepairToolHook.repairAmount(tool, repairMaterial);
  }

  /** Gets the amount to repair per item */
  protected float getRepairPerItem(ToolStack tool, ITinkerStationContainer inv, int slot, MaterialId repairMaterial) {
    // repair stat may be null in the modifier repair recipe
    float amount = getRepairAmount(tool, repairMaterial);
    if (amount > 0) {
      ItemStack stack = inv.getInput(slot);
      // repair kit first
      if (stack.getItem() instanceof IRepairKitItem kit) {
        // multiply by repair kit value, divide again by the repair factor to get the final percent
        return amount * kit.getRepairAmount() / MaterialRecipe.INGOTS_PER_REPAIR;
      } else {
        // material recipe fallback
        MaterialRecipe recipe = inv.getInputMaterial(slot);
        if (recipe != null) {
          return recipe.scaleRepair(amount);
        }
      }
    }
    return 0;
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    // must be repairable
    ItemStack tinkerable = inv.getTinkerableStack();
    // must be repairable and multipart to use this recipe
    // if its not multipart, different recipe will be used to repair it (as it has a dedicated repair item)
    if (tinkerable.isEmpty() || !tinkerable.is(TinkerTags.Items.DURABILITY)) {
      return false;
    }
    // cannot repair if not damaged, this allows it to defer to other recipes such as part swapping
    ToolStack tool = inv.getTinkerable();
    if (!tool.isBroken() && tool.getDamage() == 0) {
      return false;
    }

    // validate materials
    MaterialId material = null;
    for (int i = 0; i < inv.getInputCount(); i++) {
      // skip empty slots
      ItemStack stack = inv.getInput(i);
      if (stack.isEmpty()) {
        continue;
      }

      // ensure we have a material
      MaterialId inputMaterial = getMaterialFrom(inv, i);
      if (inputMaterial.equals(MaterialId.UNKNOWN)) {
        return false;
      }

      // on first match, store and validate the material. For later matches, just ensure material matches
      if (material == null) {
        material = inputMaterial;
        if (!MaterialRepairToolHook.canRepairWith(tool, material)) {
          return false;
        }
      } else if (!material.equals(inputMaterial)) {
        return false;
      }
    }

    // must have a material (will only be null if all slots were empty at this point)
    return material != null;
  }

  @Override
  public int shrinkToolSlotBy() {
    return 1;
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    ToolStack tool = inv.getTinkerable();
    if (tool.getDefinition() == ToolDefinition.EMPTY) {
      return RecipeResult.pass();
    }
    // ensure input needs repair
    if (!tool.isBroken() && tool.getDamage() == 0) {
      return FULLY_REPAIRED;
    }

    // first, determine how much we can repair
    int repairNeeded = tool.getDamage();
    int repairRemaining = repairNeeded;

    // iterate stacks, adding up amount we can repair, assumes the material is correct per #matches()
    for (int i = 0; i < inv.getInputCount() && repairRemaining > 0; i++) {
      repairRemaining -= repairFromSlot(tool, inv, repairRemaining, i, NO_ACTION);
    }

    // did we actually repair something?
    if (repairRemaining < repairNeeded) {
      tool = tool.copy();
      ToolDamageUtil.repair(tool, repairNeeded - repairRemaining);

      // repair remaining can be negative
      return LazyToolStack.successCopy(tool, 1, inv.getTinkerableStack());
    }

    // for some odd reason, did not repair anything
    return RecipeResult.pass();
  }

  @Override
  public void updateInputs(LazyToolStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    ToolStack inputTool = ToolStack.from(inv.getTinkerableStack());

    // iterate stacks, removing items as we repair
    int repairRemaining = inputTool.getDamage() - result.getTool().getDamage();
    for (int i = 0; i < inv.getInputCount() && repairRemaining > 0; i++) {
      final int slot = i;
      repairRemaining -= repairFromSlot(inputTool, inv, repairRemaining, i, count -> inv.shrinkInput(slot, count));
    }

    if (repairRemaining > 0) {
      TConstruct.LOG.error("Recipe repair on {} consumed too few items. {} durability unaccounted for", result, repairRemaining);
    }
  }

  /**
   * Gets the amount to repair from the given slot
   * @param tool            Tool instance
   * @param inv             Inventory instance
   * @param repairNeeded    Amount of remaining repair needed
   * @param slot            Input slot
   * @param amountConsumer  Action to perform on repair, input is the amount consumed
   * @return  Repair from this slot
   */
  protected int repairFromSlot(ToolStack tool, ITinkerStationContainer inv, int repairNeeded, int slot, IntConsumer amountConsumer) {
    ItemStack stack = inv.getInput(slot);
    if (!stack.isEmpty()) {
      // we have a recipe with matching stack, find out how much we can repair
      MaterialId repairMaterial = getMaterialFrom(inv, slot);
      if (!repairMaterial.equals(MaterialId.UNKNOWN)) {
        float durabilityPerItem = getRepairPerItem(tool, inv, slot, repairMaterial);
        if (durabilityPerItem > 0) {
          // adjust the factor based on modifiers
          // main example is wood, +25% per level
          durabilityPerItem = RepairFactorModifierHook.getRepairFactor(tool, durabilityPerItem);
          if (durabilityPerItem <= 0) return 0;

          // apply this recipe as many times as we need (if stack has more than enough to repair) or can (if stack will not fully repair)
          int applied = Math.min(stack.getCount(), (int)Math.ceil(repairNeeded / durabilityPerItem));
          amountConsumer.accept(applied);
          return (int)(applied * durabilityPerItem);
        }
      }
    }

    return 0;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.tinkerStationRepairSerializer.get();
  }


  /* JEI */

  private List<IDisplayToolModification> displayRecipes;

  @Override
  public List<IDisplayToolModification> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      CompoundTag stats = StatsNBT.builder().set(ToolStats.DURABILITY, 1000).build().serializeToNBT();
      MaterialNBT displayMaterials = new MaterialNBT(IntStream.range(0, 5).mapToObj(i -> MaterialVariant.of(ToolBuildHandler.getRenderMaterial(i))).toList());
      IRepairKitItem repairKit = TinkerToolParts.repairKit.get();

      // list of materials to try for repair
      List<IDisplayToolModification> recipes = new ArrayList<>();
      Set<Item> dynamicTools = new HashSet<>();
      List<ItemStack> dynamicToolStacks = new ArrayList<>();
      for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(TinkerTags.Items.DURABILITY)) {
        if (holder.isBound() && holder.get() instanceof IModifiable modifiable) {
          CompoundTag tag = new CompoundTag();
          ToolStack tool = ToolStack.from(modifiable.asItem(), modifiable.getToolDefinition(), tag);
          // assign display materials if needed
          if (tool.hasTag(TinkerTags.Items.MULTIPART_TOOL)) {
            tool.setMaterials(displayMaterials);
            // if using render materials, ensure it has durability
            tag.put(ToolStack.TAG_STATS, stats);
          } else {
            // if no materials, just build its normal durability
            tool.rebuildStats();
          }
          // break the tool - tool stack will figure out how to do the durability later
          tag.putBoolean(ToolStack.TAG_BROKEN, true);
          ItemStack stack = tool.createStack();

          Set<MaterialId> repairMaterials = MaterialRepairToolHook.getRepairMaterials(tool);
          if (!repairMaterials.isEmpty()) {

            // if we have repair materials, find repair items
            List<ItemStack> inputs = new ArrayList<>();
            for (MaterialId repairMaterial : repairMaterials) {
              float amount = MaterialRepairToolHook.repairAmount(tool, repairMaterial);
              if (amount > 0) {
                // add the repair kit - we just assume it works if this material was chosen
                inputs.add(repairKit.withMaterial(repairMaterial));
              }
            }

            // did we actually find anything? add a fixed recipe for those materials
            if (!inputs.isEmpty()) {
              recipes.add(new FixedDisplayRecipe(id, modifiable.asItem(), List.copyOf(inputs), List.of(stack)));
              continue;
            }
          }
          // tool lacks constant repair options, add it to the set for the generic recipe
          dynamicTools.add(modifiable.asItem());
          dynamicToolStacks.add(stack);
        }
      }
      // if we have anything that needs materials, add the dynamic display recipe
      if (!dynamicTools.isEmpty()) {
        recipes.add(new DynamicDisplayRecipe(id, Set.copyOf(dynamicTools), List.copyOf(dynamicToolStacks)));
      }

      // build the final recipes list
      displayRecipes = List.copyOf(recipes);
    }
    return displayRecipes;
  }

  @RequiredArgsConstructor
  private static abstract class DisplayRecipe implements IDisplayToolModification {
    @Getter
    private final ResourceLocation recipeId;
    @Getter
    private final List<ItemStack> toolWithoutModifier;

    @Override
    public Component getTitle() {
      return TITLE;
    }

    @Override
    public Component getTooltip() {
      return TOOLTIP;
    }

    @Override
    public int getMaxToolSize() {
      return 1;
    }

    @Override
    public boolean isToolCatalyst() {
      return true;
    }

    @Override
    public int getInputCount() {
      return 1;
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot, ItemStack focus, boolean focusOutput) {
      // fill slot 0 with repair kits for each valid repair material on the focus
      if (slot == 0 && !focusOutput && isTool(focus)) {
        IRepairKitItem repairKit = TinkerToolParts.repairKit.get();
        return MaterialRepairToolHook.getRepairMaterials(ToolStack.from(focus)).stream().map(repairKit::withMaterialForDisplay).toList();
      }
      return getDisplayItems(slot);
    }

    @Override
    public List<ItemStack> getToolWithModifier() {
      // we are just going to repair dynamically, so no need to store a repaired tool here
      return toolWithoutModifier;
    }

    @Override
    public RecipeResult<ItemStack> onFocused(ItemStack focus) {
      if (focus.getDamageValue() == 0) {
        return RecipeResult.failure(TinkerStationRepairRecipe.REPAIRED);
      }
      // will handle repairing dynamically as it needs to match the current input stack
      return RecipeResult.success(focus.copy());
    }


    @Override
    public boolean isSlotsDynamic() {
      return true;
    }

    @Override
    public void onDisplayUpdate(RecipeSlot<ItemStack> toolSlot, RecipeSlots<ItemStack> inputs, RecipeSlot<ItemStack> outputSlot) {
      // nothing in the output means we could not repair
      if (!outputSlot.get().isEmpty()) {
        // fetch info on the input item, will change how we repair later
        ItemStack materialItem = inputs.get(0);
        IRepairKitItem repairKit = TinkerToolParts.repairKit.get();
        MaterialId material = repairKit.getMaterial(materialItem).getId();
        // did we find a material?
        if (!MaterialId.UNKNOWN.equals(material)) {
          ItemStack toolStack = toolSlot.get();
          ToolStack tool = ToolStack.from(toolStack);
          if (MaterialRepairToolHook.canRepairWith(tool, material)) {
            // scale by repair factor
            float amount = MaterialRepairToolHook.repairAmount(tool, material);
            float factor = RepairFactorModifierHook.getRepairFactor(tool, 1);
            if (amount > 0 && factor > 0) {
              amount *= factor * repairKit.getRepairAmount() / MaterialRecipe.INGOTS_PER_REPAIR;

              // we made it, we are actually repairing
              tool = tool.copy();
              ToolDamageUtil.repair(tool, (int) amount);
              outputSlot.set(tool.copyStack(toolStack));
            }
          }
        }
      }
    }
  }

  /** Display recipe for tools with constant repair options, such as staffs. */
  private static class FixedDisplayRecipe extends DisplayRecipe {
    private final Item tool;
    private final List<ItemStack> inputs;
    public FixedDisplayRecipe(ResourceLocation id, Item tool, List<ItemStack> inputs, List<ItemStack> toolWithoutModifier) {
      super(id, toolWithoutModifier);
      this.tool = tool;
      this.inputs = inputs;
    }

    @Override
    public boolean isTool(ItemStack check) {
      return check.is(tool);
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot) {
      return slot == 0 ? inputs : List.of();
    }
  }

  /** Recipe showing repair info for your specific tool. */
  private static class DynamicDisplayRecipe extends DisplayRecipe {
    private final Set<Item> tools;

    public DynamicDisplayRecipe(ResourceLocation id, Set<Item> tools, List<ItemStack> toolWithoutModifier) {
      super(id, toolWithoutModifier);
      this.tools = tools;
    }

    @Override
    public boolean isTool(ItemStack check) {
      return tools.contains(check.getItem());
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot) {
      return List.of();
    }

    @Override
    public boolean showUnfocused() {
      // this recipe only makes sense if we have materials
      return false;
    }

    @Override
    public boolean isVisibleFromItem(ItemStack focus, boolean output) {
      // only show if it has a repair material - display stacks may bring us here otherwise
      return !output && isTool(focus) && !MaterialRepairToolHook.getRepairMaterials(ToolStack.from(focus)).isEmpty();
    }
  }
}
