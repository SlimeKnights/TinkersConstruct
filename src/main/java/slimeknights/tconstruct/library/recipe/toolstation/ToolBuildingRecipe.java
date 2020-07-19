package slimeknights.tconstruct.library.recipe.toolstation;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
public class ToolBuildingRecipe implements IToolStationRecipe {

  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  protected final ToolCore output;

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.toolBuildingRecipeSerializer.get();
  }

  @Override
  public boolean matches(IToolStationInventory inv, World worldIn) {
    List<ItemStack> stackList = StreamSupport.stream(inv.getInputStacks().spliterator(), false).filter((itemStack) -> !itemStack.isEmpty()).collect(Collectors.toList());

    return ToolBuildHandler.canToolBeBuilt(stackList.stream(), stackList.size(), this.output);
  }

  @Override
  public ItemStack getCraftingResult(IToolStationInventory inv) {
    List<IMaterial> materials = StreamSupport.stream(inv.getInputStacks().spliterator(), false).filter(stack -> !stack.isEmpty())
      .map(IMaterialItem::getMaterialFromStack)
      .collect(Collectors.toList());

    return ToolBuildHandler.buildItemFromMaterials(this.output, materials);
  }

  @Override
  public ItemStack getRecipeOutput() {
    return new ItemStack(this.output);
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ToolBuildingRecipe> {
    @Override
    public ToolBuildingRecipe read(ResourceLocation recipeId, JsonObject json) {
      String group = JSONUtils.getString(json, "group", "");
      // output fetch as a ToolCore item, its an error if it does not implement that interface
      String itemName = JSONUtils.getString(json, "output");
      Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));

      if (item == null) {
        throw new JsonSyntaxException("Unknown item '" + itemName + "'");
      }

      if (!(item instanceof ToolCore)) {
        throw new JsonSyntaxException("Invalid output, item does not implement ToolCore");
      }

      return new ToolBuildingRecipe(recipeId, group, (ToolCore) item);
    }

    @Nullable
    @Override
    public ToolBuildingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
      try {
        String group = buffer.readString(32767);
        // output must be a Tool Core item
        int itemId = buffer.readVarInt();
        Item item = Item.getItemById(itemId);

        if (!(item instanceof ToolCore)) {
          throw new DecoderException("Invalid item '" + item.getRegistryName() + "', must be a ToolCore");
        }

        return new ToolBuildingRecipe(recipeId, group, (ToolCore) item);
      }
      catch (Exception e) {
        TConstruct.log.error("Error reading tool building recipe from packet.", e);
        throw e;
      }
    }

    @Override
    public void write(PacketBuffer buffer, ToolBuildingRecipe recipe) {
      try {
        buffer.writeString(recipe.group);
        buffer.writeVarInt(Item.getIdFromItem(recipe.output.asItem()));
      }
      catch (Exception e) {
        TConstruct.log.error("Error writing tool building recipe to packet.", e);
        throw e;
      }
    }
  }
}
