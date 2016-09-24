package info.batey.cassandra.load.drivers;

import org.HdrHistogram.Histogram;

public class Result {
    private final Histogram histogram;
    private long success;
    private long fail;
    private boolean finished;

    public Result(Histogram histogram) {
        this.histogram = histogram;
        this.finished = false;
    }

    private Result() {
        this.histogram = new Histogram(3);
        this.finished = true;
    }

    void success() {
        success++;
    }

    void fail() {
        fail++;
    }

    public Histogram getHistogram() {
        return histogram;
    }

    public long getSuccess() {
        return success;
    }

    public long getFail() {
        return fail;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", fail=" + fail +
                ", finished=" + finished +
                '}';
    }
}
