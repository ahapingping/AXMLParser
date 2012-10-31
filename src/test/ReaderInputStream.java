package test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class ReaderInputStream extends InputStream {
    protected Reader reader;
    protected ByteArrayOutputStream byteArrayOut;
    protected Writer writer;
    protected char[] chars;
    protected byte[] buffer;
    protected int index, length;
 
      /**
       * 带Reader参数构造函数
       *
       * @param reader - InputStream使用的Reader
       */
      public ReaderInputStream(Reader reader) {
        this.reader = reader;
        byteArrayOut = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(byteArrayOut);
        chars = new char[1024];
      }
 
      /**
       * 带Reader和字符编码格式参数的构造函数
       *
       * @param reader   - InputStream使用的Reader
       * @param encoding - InputStream使用的字符编码格式.
       * @throws 如果字符编码格式不支持,则抛UnsupportedEncodingException异常
       */
      public ReaderInputStream(Reader reader, String encoding) 
          throws UnsupportedEncodingException {
        this.reader = reader;
        byteArrayOut = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(byteArrayOut, encoding);
        chars = new char[1024];
      }
 
      /**
       * @see java.io.InputStream#read()
       */
      public int read() throws IOException {
        if (index >= length)
          fillBuffer();
        if (index >= length)
          return -1;
        return 0xff & buffer[index++];
      }
 
      protected void fillBuffer() throws IOException {
        if (length < 0)
          return;
        int numChars = reader.read(chars);
        if (numChars < 0) {
          length = -1;
        } else {
          byteArrayOut.reset();
          writer.write(chars, 0, numChars);
          writer.flush();
          buffer = byteArrayOut.toByteArray();
          length = buffer.length;
          index = 0;
        }
      }
 
      /**
       * @see java.io.InputStream#read(byte[], int, int)
       */
      public int read(byte[] data, int off, int len) throws IOException {
        if (index >= length)
          fillBuffer();
        if (index >= length)
          return -1;
        int amount = Math.min(len, length - index);
        System.arraycopy(buffer, index, data, off, amount);
        index += amount;
        return amount;
      }
 
      /**
       * @see java.io.InputStream#available()
       */
      public int available() throws IOException {
        return (index < length) ? length - index :
            ((length >= 0) && reader.ready()) ? 1 : 0;
      }
 
      /**
       * @see java.io.InputStream#close()
       */
      public void close() throws IOException {
        reader.close();
      }
 
}