package exterminatorJeff.undergroundBiomes.api;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;

public class NamedItem extends Names {

    protected int id;
    protected Item item;

    public NamedItem(String internalName) {
        super(internalName);
    }

    public NamedItem(NamedBlock block) {
        this (block.internal());
    }

    public void register(int _id, Item _item) {

        // not reregistring in general for now
        //Registrar.instance.add(this,_id,_item);
        reRegister(_id,_item);
    }

    public void reRegister(int _id, Item _item) {
        id = _id;
        item = _item;
        Item current = Item.getItemById(_id);
        if (current != _item) {
            Item.itemRegistry.addObject(_id, this.internal(), _item);
        }
    }

    public Item cachedItem() {
        if (item == null) {
            item = (Item)(Item.itemRegistry.getObject(this.external()));
            if (item == null) throw new RuntimeException(this.internal()+ " has no item");
        }
        return item;
    }

    public Item registeredItem() {
        Item result =  (Item)(Item.itemRegistry.getObject(internal()));
        if (result == null) {
            result =  (Item)(Item.itemRegistry.getObject(external()));
            if (result == null) {
                for (Object key: Item.itemRegistry.getKeys()) {
                    //UndergroundBiomes.logger.info(key.toString());
                }
                //UndergroundBiomes.logger.info(external());
                throw new RuntimeException();
            }
        }
        return result;
    }

    public IIcon registerIcons(IIconRegister iconRegister) {
        return iconRegister.registerIcon(external());
    }

    public boolean matches(Item matched) {
        return item.equals(matched);
    }

}
