package mods.tinker.tconstruct.items;

public class OreBerries extends CraftingItem
{
    static String[] names = new String[] { "iron", "gold", "copper", "tin", "aluminum", "silver" };
    static String[] tex = new String[] { "oreberry_iron", "oreberry_gold", "oreberry_copper", "oreberry_tin", "oreberry_aluminum", "oreberry_silver" };

    public OreBerries(int id)
    {
        super(id, names, tex, "oreberries/");
    }

}
