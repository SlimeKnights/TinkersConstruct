
package Zeno410Utils;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;


/**
 * This class permits a relatively type-safe facade for the Forge Configuration system
 * to allow behind-the-scenes manipulations of the configurations; currently world-specific
 * configs and change tracking; in the future access to a GUI system.
 *
 * Streaming has an implicit and not-currently-enforced contract to have a fixed order of Settings
 * @author Zeno410
 */
abstract public class Settings implements Streamable {
    private ArrayList<Setting> settings = new ArrayList<Setting>();
    private HashMap<String,Category> categories = new HashMap<String,Category>();

    public void readFrom(Configuration source) {
        for (Setting setting: settings) setting.readFrom(source);
    }

    public void copyTo(Configuration target) {
        for (Setting setting: settings) setting.copyTo(target);
    }

    public void readFrom(DataInput input) throws IOException {
        for (Setting setting: settings) setting.readFrom(input);
    }

    public void writeTo(DataOutput output) throws IOException {
        for (Setting setting: settings) setting.writeTo(output);
    }

    public Category category(String name) {
        Category result = categories.get(name);
        if (result != null) return result;
        result = new Category(name);
        categories.put(name, result);
        return result;
    }

    public Category general() {return category(Configuration.CATEGORY_GENERAL);}

    protected class BooleanSetting extends Setting<Boolean> {

        public BooleanSetting(Category category, String key,String comment, boolean defaultValue) {
            super(category,key,comment,defaultValue);
        }

        public void copyTo(Configuration target) {
            propertyFrom(target).set(value());
        }

        public Property propertyFrom(Configuration source) {
            if (comment.equals("")) return source.get(category.name, key, defaultValue);
            return source.get(category.name, key, defaultValue, comment);
        }

        @Override
        public Boolean dataFrom(Property source) {
            return source.getBoolean(defaultValue);
        }

        @Override
        public void readFrom(DataInput input) throws IOException {
            Boolean oldValue = value();
            set = input.readBoolean();
            value = input.readBoolean();
            defaultValue = input.readBoolean();
            if ((oldValue ==null&&value()!=null)||(!oldValue.equals(value()))) {
                update(value());
            }
        }

        @Override
        public void writeTo(DataOutput output) throws IOException {
            output.writeBoolean(set);
            output.writeBoolean(value);
            output.writeBoolean(defaultValue);
        }
    }

    protected class IntSetting extends Setting<Integer> {

        public IntSetting(Category category, String key,String comment, Integer defaultValue) {
            super(category,key,comment,defaultValue);
        }

        public void copyTo(Configuration target) {
            propertyFrom(target).set(value());
        }

        public Property propertyFrom(Configuration source) {
            if (comment.equals("")) return source.get(category.name, key, defaultValue);
            return source.get(category.name, key, defaultValue, comment);
        }

        @Override
        public Integer dataFrom(Property source) {
            return source.getInt(defaultValue);
        }

        @Override
        public void readFrom(DataInput input) throws IOException {
            Integer oldValue = value();
            set = input.readBoolean();
            value = input.readInt();
            defaultValue = input.readInt();
            if ((oldValue ==null&&value()!=null)||(!oldValue.equals(value()))) {
                update(value());
            }
        }

        @Override
        public void writeTo(DataOutput output) throws IOException {
            output.writeBoolean(set);
            output.writeInt(value);
            output.writeInt(defaultValue);
        }
    }

    protected class StringSetting extends Setting<String> {

        public StringSetting(Category category, String key,String comment, String defaultValue) {
            super(category,key,comment,defaultValue);
        }

        public void copyTo(Configuration target) {
            propertyFrom(target).set(value());
        }

        public Property propertyFrom(Configuration source) {
            if (comment.equals("")) return source.get(category.name, key, defaultValue);
            return source.get(category.name, key, defaultValue, comment);
        }

        @Override
        public String dataFrom(Property source) {
            return source.getString();
        }

        @Override
        public void readFrom(DataInput input) throws IOException {
            String oldValue = value();
            set = input.readBoolean();
            value = input.readUTF();
            defaultValue = input.readUTF();
            if ((oldValue ==null&&value()!=null)||(!oldValue.equals(value()))) {
                update(value());
            }
        }

        @Override
        public void writeTo(DataOutput output) throws IOException {
            output.writeBoolean(set);
            output.writeUTF(value);
            output.writeUTF(defaultValue);
        }
    }

    protected class DoubleSetting extends Setting<Double> {

        public DoubleSetting(Category category, String key,String comment, Double defaultValue) {
            super(category,key,comment,defaultValue);
        }

        public void copyTo(Configuration target) {
            propertyFrom(target).set(value());
        }

        public Property propertyFrom(Configuration source) {
            if (comment.equals("")) return source.get(category.name, key, defaultValue);
            return source.get(category.name, key, defaultValue, comment);
        }

        @Override
        public Double dataFrom(Property source) {
            return source.getDouble(defaultValue);
        }

        @Override
        public void readFrom(DataInput input) throws IOException {
            Double oldValue = value();
            set = input.readBoolean();
            value = input.readDouble();
            defaultValue = input.readDouble();
            if ((oldValue ==null&&value()!=null)||(!oldValue.equals(value()))) {
                update(value());
            }
        }

        @Override
        public void writeTo(DataOutput output) throws IOException {
            output.writeBoolean(set);
            output.writeDouble(value);
            output.writeDouble(defaultValue);
        }
    }
    
    abstract protected class Setting<Type> implements Mutable<Type>, Streamable {
        protected boolean set = false;
        protected Type value;
        protected Type defaultValue;
        final Category category;
        final String key;
        final String comment;
        private final Trackers<Type> trackers = new Trackers<Type>();

        Setting(Category category, String key,String comment, Type defaultValue) {
            this.category = category;
            this.key = key;
            this.comment = comment;
            this.defaultValue = defaultValue;
            settings.add(this);
        }

        public void set(Type newValue) {
            value = newValue;
            set = true;
        }

        public Type value() {
            if (set) return value;
            return defaultValue;
        }


        public void update(Type updated) {
            trackers.update(updated);
        }

        public void informOnChange(Acceptor<Type> toInform) {
            trackers.informOnChange(toInform);
        }

        public void stopInforming(Acceptor<Type> dontInform) {
            trackers.stopInforming(dontInform);
        }

        public boolean hasBeenSet() {return set;}
        public boolean exists(Configuration tested) {
            return tested.hasKey(category.name, key);
        }

        public void setValue(Property inFile) {
            Type oldValue = value();
            set = inFile.wasRead();
            if (set) {
                value = dataFrom(inFile);
            } else {
                value = defaultValue;
            }
            if ((oldValue ==null&&value()!=null)) {
                ConfigManager.logger.info("updating null to "+value().toString());
                update(value());
            } else {
                if (!oldValue.equals(value())) {
                    ConfigManager.logger.info("updating "+this.key+ " to "+value().toString());
                    update(value());
                }
            }
        }

        public void readFrom(Configuration source) {
            setValue(propertyFrom(source));
        }

        abstract public Property propertyFrom(Configuration source);
        abstract public Type dataFrom(Property source);
        abstract public void copyTo(Configuration target);
    }

    public class Category {
        public final String name;
        private String description;
        public Category(String name) {this.name = name;}
        public String description() {
           return description;
        }
        public Mutable<Boolean> booleanSetting(String key,String comment, boolean defaultValue){
            return new BooleanSetting(this,key,comment,defaultValue);
        }

        public Mutable<Boolean> booleanSetting(String key, boolean defaultValue,String comment){
            return new BooleanSetting(this,key,comment,defaultValue);
        }

        public Mutable<Integer> intSetting(String key, Integer defaultValue,String comment){
            return new IntSetting(this,key,comment,defaultValue);
        }

        public Mutable<Integer> intSetting(String key, Integer defaultValue){
            return new IntSetting(this,key,"",defaultValue);
        }

        public Mutable<String> stringSetting(String key, String defaultValue,String comment){
            return new StringSetting(this,key,comment,defaultValue);
        }

        public Mutable<Double> doubleSetting(String key, Double defaultValue,String comment){
            return new DoubleSetting(this,key,comment,defaultValue);
        }
    }

}



