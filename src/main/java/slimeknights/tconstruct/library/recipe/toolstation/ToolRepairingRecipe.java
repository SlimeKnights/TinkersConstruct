package slimeknights.tconstruct.library.recipe.toolstation;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.IRepairable;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class ToolRepairingRecipe implements IToolStationRecipe {

  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.toolRepairingRecipeSerializer.get();
  }

  @Override
  public boolean matches(IToolStationInventory inv, World worldIn) {
    ItemStack toolStack = inv.getToolStack();

    if (toolStack.isEmpty() || !(toolStack.getItem() instanceof IRepairable)) {
      return false;
    }

    return ((IRepairable) toolStack.getItem()).needsRepair(toolStack);
  }

  @Override
  public ItemStack getCraftingResult(IToolStationInventory inv) {
    ItemStack tool = inv.getToolStack();

    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  /**
   * Calculates the repair amount that should be applied to a tool.
   * @param tool the tool stack
   * @param amount the standard amount from the materials before the modifiers
   * @return the amount to repair a tool by
   */
  protected int calculateRepair(ItemStack tool, int amount) {
    float ordinalDurability = ToolData.from(tool).getStats().durability;
    float actualDurability = ToolCore.getCurrentDurability(tool);

    // calculate in modifiers that change the total durability of a tool, like diamond
    // they should not punish the player with higher repair costs
    float durabilityFactor = actualDurability / ordinalDurability;
    float increase = amount * Math.min(10f, durabilityFactor);

    increase = Math.max(increase, actualDurability / 64f);

    int modifiersFree = ToolData.from(tool).getStats().freeModifiers;
    float mods = 1.0f;

    increase *= mods;

    return (int) Math.ceil(increase);
  }

  protected int calculateRepairAmount(List<IMaterial> materials) {
    Set<IMaterial> materialsMatched = Sets.newHashSet();
    float durability = 0f;

    durability *= 1f + ((float) materialsMatched.size() - 1) / 9f;

    return (int) durability;
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ToolRepairingRecipe> {

    @Override
    public ToolRepairingRecipe read(ResourceLocation recipeId, JsonObject json) {
      String group = JSONUtils.getString(json, "group", "");

      return new ToolRepairingRecipe(recipeId, group);
    }

    @Nullable
    @Override
    public ToolRepairingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
      try {
        String group = buffer.readString(32767);

        return new ToolRepairingRecipe(recipeId, group);
      }
      catch (Exception e) {
        TConstruct.log.error("Error reading tool repairing recipe from packet.", e);
        throw e;
      }
    }

    @Override
    public void write(PacketBuffer buffer, ToolRepairingRecipe recipe) {
      try {
        buffer.writeString(recipe.group);
      }
      catch (Exception e) {
        TConstruct.log.error("Error writing tool repairing recipe to packet.", e);
        throw e;
      }
    }
  }
}
