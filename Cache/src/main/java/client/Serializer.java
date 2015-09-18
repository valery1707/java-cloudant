package client;

/**
 * @author ArunIyengar
 *
 */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Arrays;

/*
 * For serializing and deserializing Java objects to/from byte arrays and files
 */
public class Serializer {

    public static <T> void serializeToDisk(String filename, T r) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(r);
            out.close();
            fileOut.close();
            // System.out.println("Serialized data is saved in " + filename);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static <T> T deserializeFromDisk(String filename) {
        T r = null;
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            r = (T) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out
                    .println("datalake.util.Serializer.y.deserializeFromDisk: class not found");
            c.printStackTrace();
            return null;
        }
        System.out.println("Deserialized data obtained from " + filename);
        return r;
    }

    // Serialize a single object to a byte array
    public static <T> byte[] serializeToByteArray(T r) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] bytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(r);
            bytes = bos.toByteArray();
        } catch (IOException ex) {
            // ignore close exception
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return bytes;
    }

    // deserialize a single object from a byte array
    public static <T> T deserializeFromByteArray(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        T r = null;
        try {
            in = new ObjectInputStream(bis);
            r = (T) in.readObject();
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out
                    .println("datalake.util.Serializer.deserializeFromByteArray: class not found");
            c.printStackTrace();
            return null;
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return r;
    }

    // Store byte array in a file
    public static void byteArrayToFile(byte[] data, String filename)
            throws Exception {
        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(data);
        fos.close();
    }

    // Read byte array from a file
    public static byte[] fileToByteArray(String filename) throws Exception {
        Path path = Paths.get(filename);
        return Files.readAllBytes(path);
    }

    // byte ranges from -128 to + 127
    public static void printByte(byte x) {
        // good way to convert byte to nonnegative integer between 0 and 255
        // inclusive
        System.out.println("byte value: " + x + ", long value: "
                + (long) (x + 128));
    }

    private static class FileOutputHandle {
        FileOutputStream fileOutStream;
        ObjectOutputStream objectOutputStream;
    }

    private static class FileInputHandle {
        FileInputStream fileInStream;
        ObjectInputStream objectInputStream;
    }

    private static class ArrayOutputHandle {
        ByteArrayOutputStream arrayOutStream;
        ObjectOutputStream objectOutputStream;
    }

    private static class ArrayInputHandle {
        ByteArrayInputStream arrayInStream;
        ObjectInputStream objectInputStream;
    }

    private static class Rectangle implements java.io.Serializable {
        int length;
        int width;
        private static final long serialVersionUID = 1;
        byte[] bytes;

        Rectangle(int x, int y, byte[] ba) {
            length = x;
            width = y;
            bytes = ba;
        }
    }

    private static FileOutputHandle openOutputFile(String filename) {
        ObjectOutputStream out = null;
        FileOutputHandle outputHandle = new FileOutputHandle();
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            out = new ObjectOutputStream(fileOut);
            outputHandle.fileOutStream = fileOut;
            outputHandle.objectOutputStream = out;
        } catch (IOException i) {
            i.printStackTrace();
        }
        return outputHandle;
    }

    private static void closeOutputFile(FileOutputHandle outputHandle) {
        try {
            outputHandle.objectOutputStream.close();
            outputHandle.fileOutStream.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private static <T> void serializeToStream(ObjectOutputStream out, T r) {
        try {
            out.writeObject(r);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private static FileInputHandle openInputFile(String filename) {
        ObjectInputStream in = null;
        FileInputHandle inputHandle = new FileInputHandle();
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            in = new ObjectInputStream(fileIn);
            inputHandle.fileInStream = fileIn;
            inputHandle.objectInputStream = in;
        } catch (IOException i) {
            i.printStackTrace();
        }
        return inputHandle;
    }

    private static void closeInputFile(FileInputHandle inputHandle) {
        try {
            inputHandle.objectInputStream.close();
            inputHandle.fileInStream.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private static <T> T deserializeFromStream(ObjectInputStream in) {
        T r = null;
        try {
            r = (T) in.readObject();
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out
                    .println("Error: Class not found in datalake.util.Serializer.deserializeFromStream");
            c.printStackTrace();
            return null;
        }
        return r;
    }

    private static ArrayOutputHandle openOutputArray() {
        ArrayOutputHandle outputHandle = new ArrayOutputHandle();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        outputHandle.arrayOutStream = bos;
        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            outputHandle.objectOutputStream = out;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return outputHandle;
    }

    private static byte[] closeOutputArray(ArrayOutputHandle outputHandle) {
        byte[] bytes = outputHandle.arrayOutStream.toByteArray();
        try {
            outputHandle.objectOutputStream.close();
            outputHandle.arrayOutStream.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return bytes;
    }

    private static ArrayInputHandle openInputArray(byte[] bytes) {
        ArrayInputHandle inputHandle = new ArrayInputHandle();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        inputHandle.arrayInStream = bis;
        try {
            ObjectInputStream in = new ObjectInputStream(bis);
            inputHandle.objectInputStream = in;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return inputHandle;
    }

    private static void closeInputArray(ArrayInputHandle inputHandle) {
        try {
            inputHandle.objectInputStream.close();
            inputHandle.arrayInStream.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private static void outputData(Rectangle r) {
        // System.out.println("Below is value of data for rectangle ");
        System.out.println("length: " + r.length);
        System.out.println("width: " + r.width);
        System.out.println("Byte array is: " + Arrays.toString(r.bytes) + "\n");
    }

    private static void test() {
        System.out.println("output for bytes ");
        byte x = -128;
        printByte(x);
        x = 127;
        printByte(x);
    }

    public static void main(String[] args) {
        Rectangle r1, r2, r3, r4, r5, data1;
        byte[] bytes;
        byte[] ba = { 1, 2, 3, 4, 6 };
        int length = 4;
        int width = 6;
        data1 = new Rectangle(length, width, ba);
        String filename = "C:\\temp\\junkrectangle.out";
        // default directory is Users>IBM_ADMIN>workspace>test1

        r1 = new Rectangle(10, 20, ba);
        r2 = new Rectangle(100, 200, ba);

        FileOutputHandle foh = openOutputFile(filename);
        serializeToStream(foh.objectOutputStream, data1);
        serializeToStream(foh.objectOutputStream, r1);
        serializeToStream(foh.objectOutputStream, r2);
        closeOutputFile(foh);

        FileInputHandle fih = openInputFile(filename);
        r3 = deserializeFromStream(fih.objectInputStream);
        r4 = deserializeFromStream(fih.objectInputStream);
        r5 = deserializeFromStream(fih.objectInputStream);
        closeInputFile(fih);

        System.out.println("Here is the deserialized data read from disk ");
        outputData(r3);
        outputData(r4);
        outputData(r5);

        ArrayOutputHandle aoh = openOutputArray();
        serializeToStream(aoh.objectOutputStream, data1);
        serializeToStream(aoh.objectOutputStream, r1);
        serializeToStream(aoh.objectOutputStream, r2);
        bytes = closeOutputArray(aoh);

        ArrayInputHandle aih = openInputArray(bytes);
        r3 = deserializeFromStream(aih.objectInputStream);
        r4 = deserializeFromStream(aih.objectInputStream);
        r5 = deserializeFromStream(aih.objectInputStream);
        closeInputArray(aih);

        System.out
                .println("Here is the data deserialized from the byte array ");
        outputData(r3);
        outputData(r4);
        outputData(r5);

        System.out
                .println("test of serializing/deserializing single object in one command");
        byte[] b1 = serializeToByteArray(r3);
        outputData(r3);
        Rectangle r10 = deserializeFromByteArray(b1);
        outputData(r10);
    }
}
