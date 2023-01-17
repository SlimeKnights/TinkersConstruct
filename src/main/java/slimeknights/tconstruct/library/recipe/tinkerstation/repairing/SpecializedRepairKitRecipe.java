package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.SpecializedRepairRecipeSerializer.ISpecializedRepairRecipe;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.recipe.CraftingTableRepairKitRecipe;

/** Recipe for using a repair kit in a crafting station for a specialized tool */
public class SpecializedRepairKitRecipe extends CraftingTableRepairKitRecipe implements ISpecializedRepairRecipe {
  /** Tool that can be repaired with this recipe */
  @Getter
  private final Ingredient tool;
  /** ID of material used in repairing */
  @Getter
  private final MaterialId repairMaterial;
  public SpecializedRepairKitRecipe(ResourceLocation id, Ingredient tool, MaterialId repairMaterial) {
    super(id);
    this.tool = tool;
    this.repairMaterial = repairMaterial;
  }

  @Override
  protected boolean toolMatches(ItemStack stack) {
    return tool.test(stack);
  }

  @Override
  public boolean matches(CraftingContainer inv, Level worldIn) {
    Pair<ToolStack, ItemStack> inputs = getRelevantInputs(inv);
    return inputs != null && repairMaterial.equals(IMaterialItem.getMaterialFromStack(inputs.getSecond()).getId());
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.specializedRepairKitSerializer.get();
  }
}
