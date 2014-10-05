package tconstruct.library.weaponry;

import tconstruct.weaponry.TinkerWeaponry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.tools.CustomMaterial;

public class ArrowShaftMaterial extends CustomMaterial {
    public final float durabilityModifier; // like the durability modifier of handles, but for arrows!
    public final float fragility; // 1.0 = breaks 100%, 0.0 = never breaks
    public final float weight; // baseline: 1.0 = default, smaller = lighter, higher = heavier

    public ArrowShaftMaterial(int materialID, int value, ItemStack input, ItemStack craftingItem, float durabilityModifier, float weight, float fragility, int color) {
        super(materialID, value, input, craftingItem, color);
        this.durabilityModifier = durabilityModifier;
        this.fragility = fragility;
        this.weight = weight;
    }

    public static ArrowShaftMaterial createMaterial(int id, Item input, float durabilityModifier, float weight, float fragility, int color)
    {
        return createMaterial(id, input, 0, durabilityModifier, weight, fragility, color);
    }

    public static ArrowShaftMaterial createMaterial(int id, Item input, int inputMeta, float durabilityModifier, float weight, float fragility, int color)
    {
        return new ArrowShaftMaterial(id, 2, new ItemStack(input, 1, inputMeta), new ItemStack(TinkerWeaponry.partArrowShaft, 0, id), durabilityModifier, weight, fragility, color);
    }
}
