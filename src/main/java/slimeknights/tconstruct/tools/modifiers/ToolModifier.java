package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;

import java.util.List;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.modifiers.IModifierDisplay;
import slimeknights.tconstruct.library.modifiers.Modifier;

/** A modifier that gets applied manually rather than being a trait */
public abstract class ToolModifier extends Modifier implements IModifierDisplay {

  protected int color;

  public ToolModifier(String identifier, int color) {
    super(identifier);

    this.color = color;
  }

  @Override
  public int getColor() {
    return color;
  }

  @Override
  public List<List<ItemStack>> getItems() {
    ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();

    for(RecipeMatch rm : items) {
      List<ItemStack> in = rm.getInputs();
      if(!in.isEmpty()) {
        builder.add(in);
      }
    }

    return builder.build();
  }
}
