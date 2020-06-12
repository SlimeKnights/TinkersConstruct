package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.plugin.jei.casting.CastingBasinCategory;
import slimeknights.tconstruct.plugin.jei.casting.CastingTableCategory;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Collection;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

  @Nullable
  private CastingBasinCategory castingBasinCategory;

  @Nullable
  CastingTableCategory castingTableCategory;

  @Override
  public ResourceLocation getPluginUid() {
    return TConstructRecipeCategoryUid.pluginUid;
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
    castingBasinCategory = new CastingBasinCategory(guiHelper);
    castingTableCategory = new CastingTableCategory(guiHelper);
    registry.addRecipeCategories(castingBasinCategory);
    registry.addRecipeCategories(castingTableCategory);
  }

  @Override
  public void registerRecipes(IRecipeRegistration register) {
    Collection<IRecipe<IInventory>> castingBasinRecipes = Minecraft.getInstance().world.getRecipeManager().getRecipes(TinkerSmeltery.basinRecipeType).values();
    Collection<IRecipe<IInventory>> castingTableRecipes = Minecraft.getInstance().world.getRecipeManager().getRecipes(TinkerSmeltery.tableRecipeType).values();
    register.addRecipes(castingBasinRecipes, TConstructRecipeCategoryUid.castingBasin);
    register.addRecipes(castingTableRecipes, TConstructRecipeCategoryUid.castingTable);
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.castingBasin), TConstructRecipeCategoryUid.castingBasin);
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.castingTable), TConstructRecipeCategoryUid.castingTable);
  }
}
