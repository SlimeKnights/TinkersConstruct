package slimeknights.tconstruct.tables.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialRepairModule;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Recipe that replaces a tool part with another
 */
@AllArgsConstructor
public class TinkerStationPartSwapping implements ITinkerStationRecipe {
  private static final RecipeResult<LazyToolStack> TOO_MANY_PARTS = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "part_swapping.too_many_parts"));

  @Getter
  protected final ResourceLocation id;

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    ItemStack tinkerable = inv.getTinkerableStack();
    if (tinkerable.isEmpty() || !tinkerable.is(TinkerTags.Items.MULTIPART_TOOL)|| !(tinkerable.getItem() instanceof IModifiable modifiable)) {
      return false;
    }
    // get the list of parts, empty means its not multipart
    List<IToolPart> parts = ToolPartsHook.parts(modifiable.getToolDefinition());
    if (parts.isEmpty()) {
      return false;
    }

    // we have two concerns on part swapping:
    // part must be valid in the tool, and only up to one part can be swapped at once
    boolean foundItem = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        // too many items
        if (foundItem) {
          return false;
        }
        // part not in list
        Item item = stack.getItem();
        if (!(item instanceof IToolPart) || parts.stream().noneMatch(p -> p.asItem() == item)) {
          return false;
        }
        foundItem = true;
      }
    }
    return foundItem;
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    // copy the tool NBT to ensure the original tool is intact
    ToolStack original = inv.getTinkerable();
    List<IToolPart> parts = ToolPartsHook.parts(original.getDefinition());

    // prevent part swapping on large tools in small tables
    if (parts.size() > inv.getInputCount()) {
      return TOO_MANY_PARTS;
    }

    // actual part swap logic
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        // not tool part, should never happen
        Item item = stack.getItem();
        if (!(item instanceof IToolPart part)) {
          return RecipeResult.pass();
        }

        // ensure the part is valid
        MaterialVariantId partVariant = part.getMaterial(stack);
        if (partVariant.equals(IMaterial.UNKNOWN_ID)) {
          return RecipeResult.pass();
        }

        // we have a part and its not at this index, find the first copy of this part
        // means slot only matters if a tool uses a part twice
        int index = i;
        if (i >= parts.size() || parts.get(i).asItem() != item) {
          index = IntStream.range(0, parts.size())
                           .filter(pi -> parts.get(pi).asItem() == item)
                           .findFirst().orElse(-1);
          if (index == -1) {
            return RecipeResult.pass();
          }
        }

        // ensure there is a change in the part or we are repairing the tool, note we compare variants so you could swap oak head for birch head
        MaterialVariant toolVariant = original.getMaterial(index);
        boolean didChange = !toolVariant.sameVariant(partVariant);
        float repairDurability = MaterialRepairModule.getDurability(null, partVariant.getId(), part.getStatType());
        if (!didChange && (original.getDamage() == 0 || repairDurability == 0)) {
          return RecipeResult.pass();
        }

        // actual update
        ToolStack tool = original.copy();

        // determine which modifiers are going to be removed
        if (didChange) {
          // do the actual part replacement
          tool.replaceMaterial(index, partVariant);
        }

        // if swapping in a new head, repair the tool (assuming the give stats type can repair)
        // ideally we would validate before repairing, but don't want to create the stack before repairing
        if (repairDurability > 0) {
          // must have a registered recipe
          int cost = MaterialCastingLookup.getItemCost(part);
          if (cost > 0) {
            // takes 3 ingots for a full repair, however count the head cost in the repair amount
            repairDurability *= cost / MaterialRecipe.INGOTS_PER_REPAIR;
            if (repairDurability > 0) {
              for (ModifierEntry entry : tool.getModifierList()) {
                repairDurability = entry.getHook(ModifierHooks.REPAIR_FACTOR).getRepairFactor(tool, entry, repairDurability);
                if (repairDurability <= 0) {
                  break;
                }
              }
            }
            if (repairDurability > 0) {
              ToolDamageUtil.repair(tool, (int)repairDurability);
            }
          }
        }

        // ensure no modifier problems after removing
        // modifier validation, handles modifier requirements
        Component error = tool.tryValidate();
        if (error != null) {
          return RecipeResult.failure(error);
        }
        if (didChange) {
          error = ModifierRemovalHook.onRemoved(original, tool);
          if (error != null) {
            return RecipeResult.failure(error);
          }
        }
        // everything worked, so good to go
        return RecipeResult.success(LazyToolStack.from(tool, Math.min(inv.getTinkerableSize(), shrinkToolSlotBy())));
      }
    }
    // no item found, should never happen
    return RecipeResult.pass();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.tinkerStationPartSwappingSerializer.get();
  }
}
