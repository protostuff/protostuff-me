package io.protostuff.me;

import java.io.IOException;
import java.io.OutputStream;

/**
 * UTF-8 String serialization
 * 
 * @author David Yu
 * @created Feb 4, 2010
 */
public final class StreamedStringSerializer
{

    private StreamedStringSerializer()
    {
    }

    /**
     * Writes the stringified int into the {@link LinkedBuffer}.
     */
    public static LinkedBuffer writeInt(final int value, final WriteSession session,
            final OutputStream out, LinkedBuffer lb) throws IOException
    {
        if (value == Integer.MIN_VALUE)
        {
            final int valueLen = StringSerializer.INT_MIN_VALUE.length;
            if (lb.offset + valueLen > lb.buffer.length)
            {
                // not enough size
                out.write(lb.buffer, lb.start, lb.offset - lb.start);
                lb.offset = lb.start;
                // lb = new LinkedBuffer(session.nextBufferSize, lb);
            }

            System.arraycopy(StringSerializer.INT_MIN_VALUE, 0, lb.buffer, lb.offset, valueLen);

            lb.offset += valueLen;
            session.size += valueLen;

            return lb;
        }

        final int size = (value < 0) ? StringSerializer.stringSize(-value) + 1 : StringSerializer.stringSize(value);

        if (lb.offset + size > lb.buffer.length)
        {
            // not enough size
            out.write(lb.buffer, lb.start, lb.offset - lb.start);
            lb.offset = lb.start;
            // lb = new LinkedBuffer(session.nextBufferSize, lb);
        }

        StringSerializer.putBytesFromInt(value, lb.offset, size, lb.buffer);

        lb.offset += size;
        session.size += size;

        return lb;
    }

    /**
     * Writes the stringified long into the {@link LinkedBuffer}.
     */
    public static LinkedBuffer writeLong(final long value, final WriteSession session,
            final OutputStream out, LinkedBuffer lb) throws IOException
    {
        if (value == Long.MIN_VALUE)
        {
            final int valueLen = StringSerializer.LONG_MIN_VALUE.length;
            if (lb.offset + valueLen > lb.buffer.length)
            {
                // TODO space efficiency (slower path)
                // not enough size
                out.write(lb.buffer, lb.start, lb.offset - lb.start);
                lb.offset = lb.start;
                // lb = new LinkedBuffer(session.nextBufferSize, lb);
            }

            System.arraycopy(StringSerializer.LONG_MIN_VALUE, 0, lb.buffer, lb.offset, valueLen);

            lb.offset += valueLen;
            session.size += valueLen;

            return lb;
        }

        final int size = (value < 0) ? StringSerializer.stringSize(-value) + 1 : StringSerializer.stringSize(value);

        if (lb.offset + size > lb.buffer.length)
        {
            // TODO space efficiency (slower path)
            // not enough size
            out.write(lb.buffer, lb.start, lb.offset - lb.start);
            lb.offset = lb.start;
            // lb = new LinkedBuffer(session.nextBufferSize, lb);
        }

        StringSerializer.putBytesFromLong(value, lb.offset, size, lb.buffer);

        lb.offset += size;
        session.size += size;

        return lb;
    }

    /**
     * Writes the stringified float into the {@link LinkedBuffer}. TODO - skip string conversion and write directly to
     * buffer
     */
    public static LinkedBuffer writeFloat(final float value, final WriteSession session,
            final OutputStream out, final LinkedBuffer lb) throws IOException
    {
        return writeAscii(Float.toString(value), session, out, lb);
    }

    /**
     * Writes the stringified double into the {@link LinkedBuffer}. TODO - skip string conversion and write directly to
     * buffer
     */
    public static LinkedBuffer writeDouble(final double value, final WriteSession session,
            final OutputStream out, final LinkedBuffer lb) throws IOException
    {
        return writeAscii(Double.toString(value), session, out, lb);
    }

    /**
     * Writes the utf8-encoded bytes from the string into the {@link LinkedBuffer}.
     */
    public static LinkedBuffer writeUTF8(final String str, final WriteSession session,
            final OutputStream out, final LinkedBuffer lb) throws IOException
    {
        final int len = str.length();
        if (len == 0)
            return lb;

        final byte[] buffer = lb.buffer;
        int limit = buffer.length, offset = lb.offset, i = 0;

        char c;
        do
        {
            c = str.charAt(i++);
            if (c < 0x0080)
            {
                if (offset == limit)
                {
                    out.write(buffer, lb.start, offset - lb.start);
                    session.size += (offset - lb.offset);
                    lb.offset = offset = lb.start;
                }
                // ascii
                buffer[offset++] = (byte) c;
            }
            else if (c < 0x0800)
            {
                if (offset + 2 > limit)
                {
                    out.write(buffer, lb.start, offset - lb.start);
                    session.size += (offset - lb.offset);
                    lb.offset = offset = lb.start;
                }

                buffer[offset++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                buffer[offset++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
            else
            {
                if (offset + 3 > limit)
                {
                    out.write(buffer, lb.start, offset - lb.start);
                    session.size += (offset - lb.offset);
                    lb.offset = offset = lb.start;
                }

                buffer[offset++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                buffer[offset++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                buffer[offset++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        } while (i < len);

        session.size += (offset - lb.offset);
        lb.offset = offset;

        return lb;
    }

    /**
     * Writes the ascii bytes from the string into the {@link LinkedBuffer}. It is the responsibility of the caller to
     * know in advance that the string is 100% ascii. E.g if you convert a double/float to a string, you are sure it
     * only contains ascii chars.
     */
    public static LinkedBuffer writeAscii(final String str, final WriteSession session,
            final OutputStream out, final LinkedBuffer lb) throws IOException
    {
        final int len = str.length();
        if (len == 0)
            return lb;

        int offset = lb.offset;
        final int limit = lb.buffer.length;
        final byte[] buffer = lb.buffer;

        // actual size
        session.size += len;

        if (offset + len > limit)
        {
            // need to flush
            int index = 0, start = lb.start, bufSize = limit - start, available = limit - offset, remaining = len
                    - available, loops = remaining / bufSize, extra = remaining % bufSize;

            // write available space
            while (available-- > 0)
                buffer[offset++] = (byte) str.charAt(index++);

            // flush and reset
            out.write(buffer, start, bufSize);
            offset = start;

            while (loops-- > 0)
            {
                for (int i = 0; i < bufSize; i++)
                    buffer[offset++] = (byte) str.charAt(index++);

                // flush and reset
                out.write(buffer, start, bufSize);
                offset = start;
            }

            while (extra-- > 0)
            {
                buffer[offset++] = (byte) str.charAt(index++);
            }

        }
        else
        {
            // fast path
            for (int i = 0; i < len; i++)
                buffer[offset++] = (byte) str.charAt(i);
        }

        lb.offset = offset;

        return lb;
    }

    private static void flushAndReset(LinkedBuffer node, final OutputStream out)
            throws IOException
    {
        int len;
        do
        {
            if ((len = node.offset - node.start) > 0)
            {
                out.write(node.buffer, node.start, len);
                node.offset = node.start;
            }
        } while ((node = node.next) != null);
    }

    /**
     * The length of the utf8 bytes is written first (big endian) before the string - which is fixed 2-bytes. Same
     * behavior as {@link java.io.DataOutputStream#writeUTF(String)}.
     */
    public static LinkedBuffer writeUTF8FixedDelimited(final String str,
            final WriteSession session, final OutputStream out,
            LinkedBuffer lb) throws IOException
    {
        return writeUTF8FixedDelimited(str, false, session, out, lb);
    }

    /**
     * The length of the utf8 bytes is written first before the string - which is fixed 2-bytes.
     */
    public static LinkedBuffer writeUTF8FixedDelimited(final String str,
            final boolean littleEndian, final WriteSession session,
            final OutputStream out, final LinkedBuffer lb) throws IOException
    {
        int lastSize = session.size, len = str.length(), withIntOffset = lb.offset + 2;

        // the buffer could very well be almost-full.
        if (withIntOffset + len > lb.buffer.length)
        {
            // flush what we have.
            out.write(lb.buffer, lb.start, lb.offset - lb.start);
            lb.offset = lb.start;
            withIntOffset = lb.offset + 2;

            if (len == 0)
            {
                StringSerializer.writeFixed2ByteInt(0, lb.buffer, withIntOffset - 2, littleEndian);
                lb.offset = withIntOffset;
                // update size
                session.size += 2;
                return lb;
            }

            // if true, the string is too large to fit in the buffer
            if (withIntOffset + len > lb.buffer.length)
            {
                lb.offset = withIntOffset;

                // slow path
                /* final LinkedBuffer rb = */
                StringSerializer.writeUTF8(str, 0, len,
                        lb.buffer, withIntOffset, lb.buffer.length, session, lb);

                StringSerializer.writeFixed2ByteInt((session.size - lastSize), lb.buffer,
                        withIntOffset - 2, littleEndian);

                // update size
                session.size += 2;

                // assert rb != lb;
                // flush and reset nodes
                flushAndReset(lb, out);

                return lb;
            }
        }
        else if (len == 0)
        {
            StringSerializer.writeFixed2ByteInt(0, lb.buffer, withIntOffset - 2, littleEndian);
            lb.offset = withIntOffset;
            // update size
            session.size += 2;
            return lb;
        }

        // everything fits
        lb.offset = withIntOffset;

        final LinkedBuffer rb = StringSerializer.writeUTF8(str, 0, len, session, lb);

        StringSerializer.writeFixed2ByteInt((session.size - lastSize), lb.buffer,
                withIntOffset - 2, littleEndian);

        // update size
        session.size += 2;

        if (rb != lb)
        {
            // flush and reset nodes
            flushAndReset(lb, out);
        }

        return lb;
    }

    private static LinkedBuffer writeUTF8OneByteDelimited(final String str, final int index,
            final int len, final WriteSession session, final OutputStream out,
            final LinkedBuffer lb) throws IOException
    {
        int lastSize = session.size, withIntOffset = lb.offset + 1;

        // the buffer could very well be almost-full.
        if (withIntOffset + len > lb.buffer.length)
        {
            // flush what we have.
            out.write(lb.buffer, lb.start, lb.offset - lb.start);
            lb.offset = lb.start;
            withIntOffset = lb.offset + 1;
        }

        // everything fits
        lb.offset = withIntOffset;

        final LinkedBuffer rb = StringSerializer.writeUTF8(str, index, len, session, lb);

        lb.buffer[withIntOffset - 1] = (byte) (session.size - lastSize);

        // update size
        session.size++;

        if (rb != lb)
        {
            // flush and reset nodes
            flushAndReset(lb, out);
        }

        return lb;
    }

    private static LinkedBuffer writeUTF8VarDelimited(final String str, final int index,
            final int len, final int lowerLimit, int expectedSize,
            final WriteSession session, final OutputStream out, final LinkedBuffer lb)
            throws IOException
    {
        int lastSize = session.size, offset = lb.offset, withIntOffset = offset + expectedSize;

        // the buffer could very well be almost-full.
        if (withIntOffset + len > lb.buffer.length)
        {
            // flush what we have.
            out.write(lb.buffer, lb.start, lb.offset - lb.start);
            offset = lb.start;
            withIntOffset = offset + expectedSize;

            // if true, the string is too large to fit in the buffer
            if (withIntOffset + len > lb.buffer.length)
            {
                // not enough space for the string.
                lb.offset = withIntOffset;

                // slow path
                /* final LinkedBuffer rb = */
                StringSerializer.writeUTF8(str, index, len,
                        lb.buffer, withIntOffset, lb.buffer.length, session, lb);

                int size = session.size - lastSize;

                if (size < lowerLimit)
                {
                    session.size += (--expectedSize);

                    // we've nothing existing to flush
                    // move one slot to the right (re-using the withIntOffset variable)
                    withIntOffset = ++offset;

                    for (; --expectedSize > 0; size >>>= 7)
                        lb.buffer[offset++] = (byte) ((size & 0x7F) | 0x80);

                    lb.buffer[offset] = (byte) (size);

                    // flush
                    out.write(lb.buffer, withIntOffset, lb.offset - withIntOffset);
                    // reset
                    lb.offset = lb.start;

                    // assert rb != lb;
                    // flush and reset nodes
                    flushAndReset(lb.next, out);

                    return lb;
                }

                // update size
                session.size += expectedSize;

                for (; --expectedSize > 0; size >>>= 7)
                    lb.buffer[offset++] = (byte) ((size & 0x7F) | 0x80);

                lb.buffer[offset] = (byte) (size);

                // assert rb != lb;
                // flush and reset nodes
                flushAndReset(lb, out);

                return lb;
            }
        }

        // everything fits
        lb.offset = withIntOffset;

        final LinkedBuffer rb = StringSerializer.writeUTF8(str, index, len, session, lb);

        int size = session.size - lastSize;

        if (size < lowerLimit)
        {
            // if the buffer was fully used
            // or if the string was atleast 683 bytes
            // for this method, expected size only either be 2/3/4/5
            if (rb != lb || expectedSize != 2)
            {
                // flush it
                session.size += (--expectedSize);

                // flush existing
                if (offset != lb.start)
                    out.write(lb.buffer, lb.start, offset - lb.start);

                // move one slot to the right (re-using the withIntOffset variable)
                withIntOffset = ++offset;

                for (; --expectedSize > 0; size >>>= 7)
                    lb.buffer[offset++] = (byte) ((size & 0x7F) | 0x80);

                lb.buffer[offset] = (byte) (size);

                // flush
                out.write(lb.buffer, withIntOffset, lb.offset - withIntOffset);
                // reset
                lb.offset = lb.start;

                if (rb != lb)
                {
                    // flush and reset nodes
                    flushAndReset(lb.next, out);
                }

                return lb;
            }

            // move one slot to the left
            System.arraycopy(lb.buffer, withIntOffset, lb.buffer, withIntOffset - 1,
                    lb.offset - withIntOffset);

            expectedSize--;
            lb.offset--;
        }

        // update size
        session.size += expectedSize;

        for (; --expectedSize > 0; size >>>= 7)
            lb.buffer[offset++] = (byte) ((size & 0x7F) | 0x80);

        lb.buffer[offset] = (byte) (size);

        if (rb != lb)
        {
            // flush and reset nodes
            flushAndReset(lb, out);
        }

        return lb;
    }

    /**
     * The length of the utf8 bytes is written first before the string - which is a variable int (1 to 5 bytes).
     */
    public static LinkedBuffer writeUTF8VarDelimited(final String str, final WriteSession session,
            final OutputStream out, final LinkedBuffer lb) throws IOException
    {
        final int len = str.length();
        if (len == 0)
        {
            if (lb.offset == lb.buffer.length)
            {
                // buffer full
                // flush
                out.write(lb.buffer, lb.start, lb.offset - lb.start);
                lb.offset = lb.start;
            }

            // write zero
            lb.buffer[lb.offset++] = 0x00;
            // update size
            session.size++;
            return lb;
        }

        if (len < StringSerializer.ONE_BYTE_EXCLUSIVE)
        {
            // the varint will be max 1-byte. (even if all chars are non-ascii)
            return writeUTF8OneByteDelimited(str, 0, len, session, out, lb);
        }

        if (len < StringSerializer.TWO_BYTE_EXCLUSIVE)
        {
            // the varint will be max 2-bytes and could be 1-byte. (even if all non-ascii)
            return writeUTF8VarDelimited(str, 0, len, StringSerializer.TWO_BYTE_LOWER_LIMIT, 2,
                    session, out, lb);
        }

        if (len < StringSerializer.THREE_BYTE_EXCLUSIVE)
        {
            // the varint will be max 3-bytes and could be 2-bytes. (even if all non-ascii)
            return writeUTF8VarDelimited(str, 0, len, StringSerializer.THREE_BYTE_LOWER_LIMIT, 3,
                    session, out, lb);
        }

        if (len < StringSerializer.FOUR_BYTE_EXCLUSIVE)
        {
            // the varint will be max 4-bytes and could be 3-bytes. (even if all non-ascii)
            return writeUTF8VarDelimited(str, 0, len, StringSerializer.FOUR_BYTE_LOWER_LIMIT, 4,
                    session, out, lb);
        }

        // the varint will be max 5-bytes and could be 4-bytes. (even if all non-ascii)
        return writeUTF8VarDelimited(str, 0, len, StringSerializer.FIVE_BYTE_LOWER_LIMIT, 5, session, out, lb);
    }

}
