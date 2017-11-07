package android_serialport_api.sample;

/**
 * Created by Percy.
 */
public interface SerialPortListener {
    void spDataReceived(final byte[] buffer, final int size);
    void spError(String errorLog);
}
