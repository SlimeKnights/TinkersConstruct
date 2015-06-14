package tconstruct.tools;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.modifiers.Modifier;
import tconstruct.library.modifiers.ModifierAspect;
import tconstruct.library.modifiers.ModifierNBT;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;
import tconstruct.library.utils.TinkerUtil;

public class RedstoneModifier extends Modifier {
  private final int max;

  public RedstoneModifier(int max) {
    super("Redstone");

    this.max = max;

    addItem(Items.redstone);
    addItem(Blocks.redstone_block, 1);

    addAspects(new ModifierAspect.MultiAspect(this, EnumChatFormatting.DARK_RED, 5, max, 1));
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    // taken care of by the aspect
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);

    NBTTagCompound tag = TagUtil.getToolTag(rootCompound);
    float speed = tag.getFloat(Tags.MININGSPEED);
    speed += data.current/10f;
    tag.setFloat(Tags.MININGSPEED, speed);
  }
}
