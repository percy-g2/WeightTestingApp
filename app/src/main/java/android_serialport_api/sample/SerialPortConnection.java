package android_serialport_api.sample;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * Created by Percy.
 */
public class SerialPortConnection {

    private static final String TAG = "SerialPortConnection";
    protected Application mApplication;
    protected SerialPort mSerialPort;
    public OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SerialPortListener genieSerialPortListener = null;

    private static SerialPortConnection spInstance;

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                try {
                    if (mInputStream == null) return;
                    byte[] bytes = getBytesFromInputStream(mInputStream);
                    if (bytes != null) {
                        int numb_of_bytes = bytes.length;
                        if (numb_of_bytes > 0) {
                            genieSerialPortListener.spDataReceived(bytes, numb_of_bytes);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public static SerialPortConnection onConnect(Context context) {

        if (spInstance == null) {
            spInstance = new SerialPortConnection(context);
        }
        return spInstance;
    }

    private SerialPortConnection(Context context) {
        genieSerialPortListener = (SerialPortListener) context;
        mApplication = new Application();
        try {
            mSerialPort = mApplication.getSerialPort(context);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException");
            genieSerialPortListener.spError("SecurityException");
            //DisplayError(R.string.error_security);
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            genieSerialPortListener.spError("IOException");
            //DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            Log.e(TAG, "InvalidParameterException");
            genieSerialPortListener.spError("InvalidParameterException");
            //DisplayError(R.string.error_configuration);
        }
    }

    @Nullable
    public static byte[] getBytesFromInputStream(InputStream inStream)
            throws IOException {

        // Get the size of the file
        long streamLength = inStream.available();

        if (streamLength > Integer.MAX_VALUE) {
            // File is too large
            return null;
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) streamLength];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = inStream.read(bytes,
                offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file ");
        }

        // Close the input stream and return bytes
        //inStream.close();
        return bytes;
    }

    public void onDisconnect() {
        if (mReadThread != null)
            mReadThread.interrupt();
        mApplication.closeSerialPort();
        mSerialPort = null;
    }
}
