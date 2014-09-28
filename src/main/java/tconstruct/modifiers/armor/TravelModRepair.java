package tconstruct.modifiers.armor;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.armor.items.TravelGear;
import tconstruct.library.modifier.ItemModifier;

public class TravelModRepair extends ItemModifier
{

    public TravelModRepair()
    {
        super(new ItemStack[0], 0, "");
    }

    @Override
    public boolean matches (ItemStack[] recipe, ItemStack input)
    {
        return canModify(input, recipe);
    }

    @Override
    protected boolean canModify (ItemStack input, ItemStack[] recipe)
    {
        if (input.getItem() instanceof TravelGear)
        {
            TravelGear gear = (TravelGear) input.getItem();
            NBTTagCompound tags = input.getTagCompound().getCompoundTag(gear.getBaseTagName());
            int damage = tags.getInteger("Damage");
            if (damage > 0)
            {
                boolean validOutput = true;
                int outputs = 0;
                for (ItemStack curInput : recipe)
                {
                    if (curInput == null)
                        continue;

                    if (areItemStacksEquivalent(curInput, gear.getRepairMaterial(input)))
                        outputs++;
                    else
                        validOutput = false;
                }
                return validOutput && outputs > 0;
            }
        }
        return false;
    }

    private int calculateIncrease (ItemStack tool, NBTTagCompound tags, int materialValue, int itemsUsed)
    {
        int damage = tags.getInteger("Damage");
        int dur = tags.getInteger("BaseDurability");
        int increase = (int) (50 * itemsUsed + (dur * 0.4f * materialValue));

        int modifiers = tags.getInteger("Modifiers");
        float mods = 1.0f;
        if (modifiers == 2)
            mods = 0.9f;
        else if (modifiers == 1)
            mods = 0.8f;
        else if (modifiers == 0)
            mods = 0.7f;

        increase *= mods;

        int repair = tags.getInteger("RepairCount");
        float repairCount = (100 - repair) / 100f;
        if (repairCount < 0.5f)
            repairCount = 0.5f;
        increase *= repairCount;
        //increase /= ((ToolCore) tool.getItem()).getRepairCost();
        return increase;
    }

    @Override
    public void modify (ItemStack[] recipe, ItemStack input)
    {
        TravelGear gear = (TravelGear) input.getItem();
        NBTTagCompound tags = input.getTagCompound().getCompoundTag(gear.getBaseTagName());
        tags.setBoolean("Broken", false);
        int damage = tags.getInteger("Damage");
        int dur = tags.getInteger("BaseDurability");
        int itemsUsed = 0;

        int materialValue = 0;
        for (ItemStack modify : recipe)
        {
            if (modify != null)
            {
                materialValue += 2;
                itemsUsed++;
            }
        }

        int increase = calculateIncrease(input, tags, materialValue, itemsUsed);
        int repair = tags.getInteger("RepairCount");
        repair += itemsUsed;
        tags.setInteger("RepairCount", repair);

        damage -= increase;
        if (damage < 0)
            damage = 0;
        tags.setInteger("Damage", damage);
        input.setItemDamage(damage);
    }

    @Override
    public void addMatchingEffect (ItemStack tool)
    {
        //Nope
    }
}
