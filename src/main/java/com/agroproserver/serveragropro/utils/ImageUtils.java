package com.agroproserver.serveragropro.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * The Class ImageUtils.
 */
public class ImageUtils {

    /**
     * Compress image.
     *
     * @param data the data
     * @return the byte[]
     */
    public static byte[] compressImage(byte[] data) {

        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
        }
        
        return outputStream.toByteArray();
    }

    /**
     * Decompress image.
     *
     * @param data the data
     * @return the byte[]
     */
    public static byte[] decompressImage(byte[] data) {

        if (data == null || data.length == 0) {
            return null;
        }

        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception exception) {
            return null;
        }

        return outputStream.toByteArray();
    }

}
