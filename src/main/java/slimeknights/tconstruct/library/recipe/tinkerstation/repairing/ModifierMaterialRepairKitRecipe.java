package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierMaterialRepairSerializer.IModifierMaterialRepairRecipe;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.recipe.CraftingTableRepairKitRecipe;
import slimeknights.tconstruct.tools.TinkerModifiers;

/** Recipe for using a repair kit in a crafting station for a specialized tool */
public class ModifierMaterialRepairKitRecipe extends CraftingTableRepairKitRecipe implements IModifierMaterialRepairRecipe {
  /** Tool that can be repaired with this recipe */
  @Getter
  private final ModifierId modifier;
  /** ID of material used in repairing */
  @Getter
  private final MaterialId repairMaterial;
  public ModifierMaterialRepairKitRecipe(ResourceLocation id, ModifierId modifier, MaterialId repairMaterial) {
    super(id);
    this.modifier = modifier;
    this.repairMaterial = repairMaterial;
  }

  @Override
  protected boolean toolMatches(ItemStack stack) {
    return stack.is(TinkerTags.Items.MODIFIABLE) && ModifierUtil.getModifierLevel(stack, modifier) > 0;
  }

  @Override
  public boolean matches(CraftingContainer inv, Level worldIn) {
    Pair<ToolStack, ItemStack> inputs = getRelevantInputs(inv);
    return inputs != null && repairMaterial.equals(IMaterialItem.getMaterialFromStack(inputs.getSecond()).getId());
  }

  @Override
  protected float getRepairAmount(IToolStackView tool, ItemStack repairStack) {
    return super.getRepairAmount(tool, repairStack) * tool.getModifierLevel(modifier);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.craftingModifierMaterialRepair.get();
  }
}
