
package Zeno410Utils;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;

/**
 *
 * @author Zeno410
 */
public class Named<Type> {
    public String name;
    public Type object;
    /** Creates a new instance of Named */

    public Named(String theName, Type theObject) {
        name = theName;
        object = theObject;
    }

    public static <T> Named<T> from(String name,T object) {
        return new Named<T>(name,object);
    }

    public static <StreamerType> NamedStreamer<StreamerType> streamer(Streamer<StreamerType> streamer) {
        return new NamedStreamer<StreamerType>(streamer);
    }

    public static class NamedStreamer<Type> extends Streamer<Named<Type>> {
        private final Streamer<Type> streamer;
        public NamedStreamer(Streamer<Type> streamer) {
            this.streamer = streamer;
        }

        public Named<Type> readFrom(DataInput input) throws IOException {
            String name = input.readUTF();
            return new Named<Type>(name,streamer.readFrom(input));

        }
        public void writeTo(Named<Type> written, DataOutput output) throws IOException{
            output.writeUTF(written.name);
            streamer.writeTo(written.object, output);

        }

    }
}