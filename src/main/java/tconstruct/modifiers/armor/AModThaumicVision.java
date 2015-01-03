package tconstruct.modifiers.armor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import tconstruct.armor.TinkerArmor;
import tconstruct.library.armor.ArmorMod;
import tconstruct.library.armor.ArmorPart;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;

public class AModThaumicVision extends ArmorMod {
    private final AModBoolean thaumicSensesMod;
    private final AModBoolean thaumicVisionMod;

    private final Item thaumometer;

    public AModThaumicVision(ItemStack thaumometer) {
        super(-1, "", EnumSet.of(ArmorPart.Head), new ItemStack[]{thaumometer});
        //super(effect, "Thaumic Vision", EnumSet.of(ArmorPart.Head), new ItemStack[]{}, EnumChatFormatting.DARK_PURPLE.toString(), "Thaumic Vision");

        this.thaumometer = thaumometer.getItem();
        thaumicSensesMod = new AModBoolean(1, StatCollector.translateToLocal("armor.thaumicsenses"), EnumSet.of(ArmorPart.Head), new ItemStack[0], EnumChatFormatting.DARK_PURPLE.toString(), StatCollector.translateToLocal("armor.thaumicsenses"));
        thaumicVisionMod = new AModBoolean(2, StatCollector.translateToLocal("armor.thaumicvision"), EnumSet.of(ArmorPart.Head), new ItemStack[0], EnumChatFormatting.DARK_PURPLE.toString(), StatCollector.translateToLocal("armor.thaumicvision"));
    }

    @Override
    protected boolean canModify(ItemStack armor, ItemStack[] recipe) {
        // return true if one of the two hasn't been applied yet. bam.
        return thaumicSensesMod.canModify(armor, recipe) || thaumicVisionMod.canModify(armor, recipe);
    }

    @Override
    public void modify(ItemStack[] recipe, ItemStack input) {
        int effect = -1;
        int count = countThaumometers(recipe);
        if(thaumicSensesMod.canModify(input, recipe)) {
            // if we add the thaumic vision too, we need to add the graphical effect for the senses too
            if(count > 1)
                thaumicSensesMod.addMatchingEffect(input);

            thaumicSensesMod.modify(recipe, input);
            count--; // one thaumometer used up
            effect = thaumicSensesMod.effectIndex;
        }

        // if we still have 1 thaumometer left, we apply vision
        if(count > 0)
        {
            // step 1: remove the thaumic senses tooltip
            // we re-add the tooltip to find out its index
            int tipNum = addToolTip(input, thaumicSensesMod.color + thaumicSensesMod.tooltipName, thaumicSensesMod.color + thaumicSensesMod.key);
            // and now we kill it \o/
            NBTTagCompound tipTags = input.getTagCompound().getCompoundTag(getTagName(input));
            tipTags.removeTag("Tooltip" + tipNum);
            tipTags.removeTag("ModifierTip" + tipNum);

            // step 2: we add a modifier, because thaumic vision is free since we already used a modifier for the senses
            NBTTagCompound tags = getModifierTag(input);
            tags.setInteger("Modifiers", tags.getInteger("Modifiers") + 1);

            // step 3: we simply apply thaumic vision now. it should replace the just removed tooltip and the just added modifier
            thaumicVisionMod.modify(recipe, input);

            effect = thaumicVisionMod.effectIndex;
        }

        // fix the effect display
        NBTTagCompound tags = input.getTagCompound().getCompoundTag(getTagName(input));
        int i = 1;
        for(; i <= 6; i++)
            if(tags.getInteger("Effect" + i) == -1)
            {
                tags.setInteger("Effect" + i, effect);
                break;
            }
    }

    private boolean hasThaumicSenses(ItemStack stack)
    {
        return stack != null && stack.hasTagCompound() && stack.getTagCompound().getCompoundTag(TinkerArmor.travelGoggles.getBaseTagName()).getBoolean(thaumicSensesMod.key);
    }

    public boolean matches (ItemStack[] recipe, ItemStack input)
    {
        if (!canModify(input, recipe))
            return false;

        // check how many thaumometers there are
        int count = countThaumometers(recipe);

        // none? what are you doing!
        // more than 2? HOW MANY EYES DO YOU HAVE?
        if(count == 0 || count > 2)
            return false;

        // one? np since we can modify
        if(count == 1)
            return true;

        // 2 thaumometers.. only ok if it has no senses
        return !hasThaumicSenses(input);
    }

    private int countThaumometers(ItemStack[] recipe)
    {
        int count = 0;
        for(ItemStack stack : recipe) {
            if(stack != null && stack.getItem() == thaumometer)
                count++;
        }
        return count;
    }
}
