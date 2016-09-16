package info.batey.cassandra.load;

public class TextColumnGenerator implements ColumnGenerator<String> {

    private String fixed;

    public TextColumnGenerator(String fixed) {
        this.fixed = fixed;
    }

    @Override
    public String next() {
        return fixed;
    }
}
