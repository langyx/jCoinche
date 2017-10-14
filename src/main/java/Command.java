public class Command
{
    private CommandType type;
    private String[] args;

    public Command(CommandType _type, String[] _args)
    {
        this.type = _type;
        this.args = _args;
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
