package info.batey.cassandra.load.distributions;

public class SingleTextVariable implements VariableGenerator<String> {

    private String fixed;

    public SingleTextVariable(String fixed) {
        this.fixed = fixed;
    }

    @Override
    public String next() {
        return fixed;
    }
}
