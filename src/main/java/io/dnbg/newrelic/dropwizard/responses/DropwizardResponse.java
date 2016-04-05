package io.dnbg.newrelic.dropwizard.responses;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class DropwizardResponse<T> {
    private static final int NO_STATUS_CODE = -1;

    public static <T> DropwizardResponse<T> of(int httpStatusCode, T result) {
        return new DropwizardResponse<T>(httpStatusCode, checkNotNull(result), null);
    }

    public static <T> DropwizardResponse<T> of(Throwable t) {
        return new DropwizardResponse<T>(NO_STATUS_CODE, null, checkNotNull(t));
    }

    private int httpStatusCode;
    private T result;
    private Throwable error;

    private DropwizardResponse(int httpStatusCode, T result, Throwable error) {
        this.httpStatusCode = httpStatusCode;
        this.result = result;
        this.error = error;
    }

    public boolean serverResponded() {
        return error == null && httpStatusCode != NO_STATUS_CODE;
    }

    public int getHttpStatusCode() {
        assertServerResponded();
        return httpStatusCode;
    }

    public T getResult() {
        assertServerResponded();
        return result;
    }

    private void assertServerResponded() {
        checkState(serverResponded(), "Server did not respond");
    }

    @Override public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("httpStatusCode", httpStatusCode)
                .add("result", result)
                .add("error", error)
                .toString();
    }
}
