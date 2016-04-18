package slimeknights.tconstruct.common.config;

import com.google.common.collect.Lists;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.List;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacket;

public class ConfigSyncPacket extends AbstractPacket {

  public List<ConfigCategory> categories = Lists.newLinkedList();

  public ConfigSyncPacket() {
  }

  @Override
  public IMessage handleClient(NetHandlerPlayClient netHandler) {
    ConfigSync.syncConfig(categories);
    return null;
  }

  @Override
  public IMessage handleServer(NetHandlerPlayServer netHandler) {
    return null;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    short categoryCount = buf.readShort();
    for(short i = 0; i < categoryCount; i++) {
      int propCount = buf.readInt();
      String categoryName = ByteBufUtils.readUTF8String(buf);
      ConfigCategory category = new ConfigCategory(categoryName);
      categories.add(category);
      for(int j = 0; j < propCount; j++) {
        String name = ByteBufUtils.readUTF8String(buf);
        char type = buf.readChar();
        String value = ByteBufUtils.readUTF8String(buf);
        category.put(name, new Property(name, value, Property.Type.tryParse(type)));
      }
    }

  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeShort(categories.size());
    for(ConfigCategory category : categories) {
      buf.writeInt(category.values().size());
      ByteBufUtils.writeUTF8String(buf, category.getName());
      for(Property prop : category.values()) {
        ByteBufUtils.writeUTF8String(buf, prop.getName());
        buf.writeChar(prop.getType().getID());
        ByteBufUtils.writeUTF8String(buf, prop.getString()); // always has string representation of the value
      }
    }
  }
}
