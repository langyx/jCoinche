import com.google.gson.Gson;
import org.apache.commons.lang3.SerializationUtils;

import java.util.Base64;

public class Serializer
{

    public Serializer()
    {

    }

    public static byte[] serialize(String[] args, CommandType cmdType)
    {
        Gson gson = new Gson();
        Command myCmd = new Command(cmdType, args);
        String json = gson.toJson(myCmd);
        byte[] encodedBytes = Base64.getEncoder().withoutPadding().encode(json.getBytes());
        return encodedBytes;
    }

    public static byte[] serialize(String args, CommandType cmdType)
    {
        String[] argsArray = {args};
        return Serializer.serialize(argsArray, cmdType);
    }

    public static Command deserializeToCommand(byte[] data)
    {
        Gson gson = new Gson();
        byte[] decodedBytes = Base64.getMimeDecoder().decode(data);
        String dataDecoded = new String(decodedBytes);
        Command newCommand = gson.fromJson(dataDecoded, Command.class);
        return newCommand;
    }

    public static String deserialize(byte[] data)
    {
        return String.join(" ", Serializer.deserializeToCommand(data).getArgs());
    }
}
