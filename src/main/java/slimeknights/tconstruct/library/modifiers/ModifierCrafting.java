package slimeknights.tconstruct.library.modifiers;


import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public class ModifierCrafting implements ICraftMod {

  @Override
  public String getIdentifier() {
    return "test";
  }

  @Override
  public boolean canApply(ItemStack original, ItemStack[] inputs, int[] openSlots) {
    //System.out.println("Checking if dirt can apply to a tool");
    if (openSlots[0] <= 0)
      return false;

    //System.out.println("Plenty of slots");
    boolean hasDirt = false;
    for (int i = 0; i < inputs.length; i++) {
      if (inputs[i].getItem() == Items.DIRT)
        hasDirt = true;
      if (inputs[i] != null)
        System.out.println(inputs[i].getItem());
    }

    //System.out.println("Dirt? "+hasDirt);
    if (!hasDirt)
      return false;

    //System.out.println("Has modifier? "+hasModifier(original, getIdentifier()));
    return !hasModifier(original, getIdentifier());
  }

  @Override
  public void apply(ItemStack original, ItemStack output) {
    System.out.println("Applying dirt to a tool");

    //ToolData data = ToolData.from(original);
    //StatsNBT stats = data.getStats();
    CompoundNBT root = output.getOrCreateTag();
    CompoundNBT stats = root.getCompound(Tags.BASE);
    CompoundNBT mods = stats.getCompound(Tags.MODIFIERS);
    //System.out.println("Tag? "+root.getCompound("tic_stats") + ", Other tag? "+data.getCompound(Tags.TOOL_STATS)+ ", Base? "+data+", Root? "+root);
    System.out.println("Root tag: "+root);
    //CompoundNBT stats = root.getCompound("tic_stats");

    if (!hasModifier(root, getIdentifier())) {
      root.putBoolean(getIdentifier(), true);

      //CompoundNBT stats = TagUtil.getToolTag(root);
      int durability = stats.getInt(Tags.DURABILITY);
      durability += 50;
      stats.putInt(Tags.DURABILITY, durability);
      System.out.println("Durability?: "+stats.getInt(Tags.DURABILITY));
    }
  }

  public static boolean hasModifier(ItemStack stack, String identifier) {
    CompoundNBT root = TagUtil.getBaseTag(stack);
    return hasModifier(root, identifier);
  }

  public static boolean hasModifier(CompoundNBT root, String identifier) {
    ListNBT tagList = TagUtil.getBaseModifiersTagList(root);

    for (int i = 0; i < tagList.size(); i++) {
      if (identifier.equals(tagList.getString(i))) {
        return true;
      }
    }
    return false;
  }
}
