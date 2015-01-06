package Zeno410Utils;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

/**
 *
 * @author Zeno410
 */
abstract public class Streamer<Type> {

    abstract public Type readFrom(DataInput input) throws IOException ;
    abstract public void writeTo(Type written, DataOutput output) throws IOException;

    public static Streamer<String> ofString() {
        return new Streamer<String>() {
            public String readFrom(DataInput input) throws IOException {
                return input.readUTF();
            }
            public void writeTo(String written,DataOutput output) throws IOException {
                output.writeUTF(written);
            }
        };
    }

   public static Streamer<Integer> ofInt() {
        return new Streamer<Integer>() {
            public Integer readFrom(DataInput input) throws IOException {
                return input.readInt();
            }
            public void writeTo(Integer written,DataOutput output) throws IOException {
                output.writeInt(written);
            }
        };
    }

}
