package slimeknights.tconstruct.tools.recipe;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;
import slimeknights.tconstruct.tools.TinkerModifiers;

/** Beheading recipe that sets player skin */
public class PlayerBeheadingRecipe extends BeheadingRecipe {
  public PlayerBeheadingRecipe(ResourceLocation id) {
    super(id, EntityIngredient.of(EntityType.PLAYER), ItemOutput.fromItem(Items.PLAYER_HEAD));
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.playerBeheadingSerializer.get();
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
    if (entity instanceof PlayerEntity) {
      GameProfile gameprofile = ((PlayerEntity)entity).getGameProfile();
      stack.getOrCreateTag().put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
    }
    return stack;
  }
}
