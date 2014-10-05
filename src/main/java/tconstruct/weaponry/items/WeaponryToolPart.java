package boni.tinkersweaponry.items;

import boni.tinkersweaponry.TinkerWeaponry;
import boni.tinkersweaponry.util.Reference;
import tconstruct.library.tools.DynamicToolPart;

public class WeaponryToolPart extends DynamicToolPart {
    public WeaponryToolPart(String textureType, String name) {
        super(textureType, name, Reference.RESOURCE);

        this.setUnlocalizedName(Reference.prefix(name));
        this.setCreativeTab(TinkerWeaponry.creativeTab);
    }
}
