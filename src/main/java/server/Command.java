package server;

public class Command
{
    private server.CommandType type;
    private String[] data;

    public Command(server.CommandType _type, String[] _args)
    {
        this.type = _type;
        this.data = _args;
    }

    public server.CommandType getType() {
        return type;
    }

    public void setType(server.CommandType type) {
        this.type = type;
    }

    public String[] getArgs() {
        return data;
    }

    public void setArgs(String[] args) {
        this.data = args;
    }
}
