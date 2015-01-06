package exterminatorJeff.undergroundBiomes.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import cpw.mods.fml.common.registry.GameRegistry;

public class NamedBlock extends Names {

    protected int id;
    public Block block;

    public final static String modid = "undergroundBiomes";
    public NamedBlock(String internalName) {
        super(internalName);
    }

    public void gameRegister(Block _block, Class itemClass) {
        block = _block;
        GameRegistry.registerBlock(block, itemClass, internal());
    }

    public void register(int _id, Block _block) {
        if (block != null) {
            throw duplicateRegistry();
        }
        //Registrar.instance.add(this,_id,_block);
        reRegister(_id,_block);
    }

    public void reRegister(int _id, Block _block) {
        id = _id;
        block = _block;
        block.setBlockName(external());
        Block current = Block.getBlockById(_id);
        if (current != block) {
            //UndergroundBiomes.logger.info(this.internal() + "was missing ");
            if (current != null) {
                throw new RuntimeException(this.internal()+ " has been replaced by "+current.toString());
            }
            Block.blockRegistry.addObject(id, this.internal(), _block);
        }
    }

    public Block block() {
        return block;
    }

    public boolean matches(Item compared) {
        if (compared instanceof ItemBlock) {
            return (((ItemBlock)compared).field_150939_a.equals(block));
        }
        return false;
    }

    public boolean matches(Block compared) {
        return compared.equals(this.block());
    }

    public int ID() {return Block.getIdFromBlock(block());}

    public Item matchingItem(Block block) {
        return Item.getItemById(Block.getIdFromBlock(block));
    }
}
