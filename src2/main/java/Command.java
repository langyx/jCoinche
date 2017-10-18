public class Command
{
    private CommandType type;
    private String[] data;

    public Command(CommandType _type, String[] _args)
    {
        this.type = _type;
        this.data = _args;
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public String[] getArgs() {
        return data;
    }

    public void setArgs(String[] args) {
        this.data = args;
    }
}
