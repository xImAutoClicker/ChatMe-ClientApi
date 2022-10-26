package de.fhdw.nohn.cm.client.api.network.netty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

import de.fhdw.nohn.cm.client.api.network.packet.PacketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.util.ByteProcessor;

@SuppressWarnings("deprecation")
public class PacketBuffer extends ByteBuf {
	private final ByteBuf buffer;

	public PacketBuffer(ByteBuf buffer) {
		this.buffer = buffer;
	}

	/**
	 * Calculates the number of bytes required to fit the supplied int (0-5) if it were to be read/written using readVarInt or writeVarInt
	 */
	public static int getVarIntSize(int value) {
		if ((value & -128) == 0) {
			return 1;
		} else if ((value & -16384) == 0) {
			return 2;
		} else if ((value & -2097152) == 0) {
			return 3;
		} else if ((value & -268435456) == 0) {
			return 4;
		}
		return 5;
	}

	public int readVarInt() {
		int number = 0;
		int round = 0;
		byte currentByte;

		do {
			currentByte = this.readByte();
			number |= (currentByte & 127) << round++ * 7;

			if (round > 5) {
				throw new RuntimeException("VarInt is too big");
			}
		} while ((currentByte & 128) == 128);

		return number;
	}

	public void writeVarInt(int number) {
		while ((number & -128) != 0) {
			this.writeByte(number & 127 | 128);
			number >>>= 7;
		}

		this.writeByte(number);
	}

	/**
	 * Reads a string from this buffer. Will throw IOException if string length exceeds 32767 bytes!
	 */
	@Deprecated
	public String readString() throws IOException {
		return PacketUtil.readString(this.buffer);
	}

	/**
	 * Writes a (UTF-8 encoded) String to this buffer. Will throw IOException if String length exceeds 32767 bytes
	 */
	@Deprecated
	public void writeString(String string) throws IOException {
		PacketUtil.writeString(this.buffer, string);
	}
	
	@Override
	public int capacity() {
		return this.buffer.capacity();
	}

	@Override
	public ByteBuf capacity(int newCapacity) {
		this.buffer.capacity(newCapacity);
		return this;
	}

	@Override
	public int maxCapacity() {
		return this.buffer.maxCapacity();
	}

	@Override
	public ByteBufAllocator alloc() {
		return this.buffer.alloc();
	}

	@Override
	public ByteOrder order() {
		return this.buffer.order();
	}

	@Override
	public ByteBuf order(ByteOrder endianness) {
		this.buffer.order(endianness);
		return this;
	}

	@Override
	public ByteBuf unwrap() {
		this.buffer.unwrap();
		return this;
	}

	@Override
	public boolean isDirect() {
		return this.buffer.isDirect();
	}

	@Override
	public int readerIndex() {
		return this.buffer.readerIndex();
	}

	@Override
	public ByteBuf readerIndex(int readerIndex) {
		this.buffer.readerIndex(readerIndex);
		return this;
	}

	@Override
	public int writerIndex() {
		return this.buffer.writerIndex();
	}

	@Override
	public ByteBuf writerIndex(int writerIndex) {
		this.buffer.writerIndex(writerIndex);
		return this;
	}

	@Override
	public ByteBuf setIndex(int readerIndex, int writerIndex) {
		this.buffer.setIndex(readerIndex, writerIndex);
		return this;
	}

	@Override
	public int readableBytes() {
		return this.buffer.readableBytes();
	}

	@Override
	public int writableBytes() {
		return this.buffer.writableBytes();
	}

	@Override
	public int maxWritableBytes() {
		return this.buffer.maxWritableBytes();
	}

	@Override
	public boolean isReadable() {
		return this.buffer.isReadable();
	}

	@Override
	public boolean isReadable(int size) {
		return this.buffer.isReadable(size);
	}

	@Override
	public boolean isWritable() {
		return this.buffer.isWritable();
	}

	@Override
	public boolean isWritable(int size) {
		return this.buffer.isWritable(size);
	}

	@Override
	public ByteBuf clear() {
		this.buffer.clear();
		return this;
	}

	@Override
	public ByteBuf markReaderIndex() {
		this.buffer.markReaderIndex();
		return this;
	}

	@Override
	public ByteBuf resetReaderIndex() {
		this.buffer.resetReaderIndex();
		return this;
	}

	@Override
	public ByteBuf markWriterIndex() {
		this.buffer.markWriterIndex();
		return this;
	}

	@Override
	public ByteBuf resetWriterIndex() {
		this.buffer.resetWriterIndex();
		return this;
	}

	@Override
	public ByteBuf discardReadBytes() {
		this.buffer.discardReadBytes();
		return this;
	}

	@Override
	public ByteBuf discardSomeReadBytes() {
		this.buffer.discardSomeReadBytes();
		return this;
	}

	@Override
	public ByteBuf ensureWritable(int minWritableBytes) {
		this.buffer.ensureWritable(minWritableBytes);
		return this;
	}

	@Override
	public int ensureWritable(int minWritableBytes, boolean force) {
		return this.buffer.ensureWritable(minWritableBytes, force);
	}

	@Override
	public boolean getBoolean(int index) {
		return this.buffer.getBoolean(index);
	}

	@Override
	public byte getByte(int index) {
		return this.buffer.getByte(index);
	}

	@Override
	public short getUnsignedByte(int index) {
		return this.buffer.getUnsignedByte(index);
	}

	@Override
	public short getShort(int index) {
		return this.buffer.getShort(index);
	}

	@Override
	public int getUnsignedShort(int index) {
		return this.buffer.getUnsignedShort(index);
	}

	@Override
	public int getMedium(int index) {
		return this.buffer.getMedium(index);
	}

	@Override
	public int getUnsignedMedium(int index) {
		return this.buffer.getUnsignedMedium(index);
	}

	@Override
	public int getInt(int index) {
		return this.buffer.getInt(index);
	}

	@Override
	public long getUnsignedInt(int index) {
		return this.buffer.getUnsignedInt(index);
	}

	@Override
	public long getLong(int index) {
		return this.buffer.getLong(index);
	}

	@Override
	public char getChar(int index) {
		return this.buffer.getChar(index);
	}

	@Override
	public float getFloat(int index) {
		return this.buffer.getFloat(index);
	}

	@Override
	public double getDouble(int index) {
		return this.buffer.getDouble(index);
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst) {
		this.buffer.getBytes(index, dst);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst, int length) {
		this.buffer.getBytes(index, dst, length);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
		this.buffer.getBytes(index, dst, dstIndex, length);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, byte[] dst) {
		this.buffer.getBytes(index, dst);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
		this.buffer.getBytes(index, dst, dstIndex, length);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuffer dst) {
		this.buffer.getBytes(index, dst);
		return this;
	}

	@Override
	public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
		this.buffer.getBytes(index, out, length);
		return this;
	}

	@Override
	public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
		return this.buffer.getBytes(index, out, length);
	}

	@Override
	public ByteBuf setBoolean(int index, boolean value) {
		this.buffer.setBoolean(index, value);
		return this;
	}

	@Override
	public ByteBuf setByte(int index, int value) {
		this.buffer.setByte(index, value);
		return this;
	}

	@Override
	public ByteBuf setShort(int index, int value) {
		this.buffer.setShort(index, value);
		return this;
	}

	@Override
	public ByteBuf setMedium(int index, int value) {
		this.buffer.setMedium(index, value);
		return this;
	}

	@Override
	public ByteBuf setInt(int index, int value) {
		this.buffer.setInt(index, value);
		return this;
	}

	@Override
	public ByteBuf setLong(int index, long value) {
		this.buffer.setLong(index, value);
		return this;
	}

	@Override
	public ByteBuf setChar(int index, int value) {
		this.buffer.setChar(index, value);
		return this;
	}

	@Override
	public ByteBuf setFloat(int index, float value) {
		this.buffer.setFloat(index, value);
		return this;
	}

	@Override
	public ByteBuf setDouble(int index, double value) {
		this.buffer.setDouble(index, value);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src) {
		this.buffer.setBytes(index, src);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src, int length) {
		this.buffer.setBytes(index, src, length);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
		this.buffer.setBytes(index, src, srcIndex, length);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, byte[] src) {
		this.buffer.setBytes(index, src);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
		this.buffer.setBytes(index, src, srcIndex, length);
		return this;
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuffer src) {
		this.buffer.setBytes(index, src);
		return this;
	}

	@Override
	public int setBytes(int index, InputStream in, int length) throws IOException {
		return this.buffer.setBytes(index, in, length);
	}

	@Override
	public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
		return this.buffer.setBytes(index, in, length);
	}

	@Override
	public ByteBuf setZero(int index, int length) {
		return this.buffer.setZero(index, length);
	}

	@Override
	public boolean readBoolean() {
		return this.buffer.readBoolean();
	}

	@Override
	public byte readByte() {
		return this.buffer.readByte();
	}

	@Override
	public short readUnsignedByte() {
		return this.buffer.readUnsignedByte();
	}

	@Override
	public short readShort() {
		return this.buffer.readShort();
	}

	@Override
	public int readUnsignedShort() {
		return this.buffer.readUnsignedShort();
	}

	@Override
	public int readMedium() {
		return this.buffer.readMedium();
	}

	@Override
	public int readUnsignedMedium() {
		return this.buffer.readUnsignedMedium();
	}

	@Override
	public int readInt() {
		return this.buffer.readInt();
	}

	@Override
	public long readUnsignedInt() {
		return this.buffer.readUnsignedInt();
	}

	@Override
	public long readLong() {
		return this.buffer.readLong();
	}

	@Override
	public char readChar() {
		return this.buffer.readChar();
	}

	@Override
	public float readFloat() {
		return this.buffer.readFloat();
	}

	@Override
	public double readDouble() {
		return this.buffer.readDouble();
	}

	@Override
	public ByteBuf readBytes(int length) {
		return this.buffer.readBytes(length);
	}

	@Override
	public ByteBuf readSlice(int length) {
		this.buffer.readSlice(length);
		return this;
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst) {
		return this.buffer.readBytes(dst);
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst, int length) {
		return this.buffer.readBytes(dst, length);
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
		return this.buffer.readBytes(dst, dstIndex, length);
	}

	@Override
	public ByteBuf readBytes(byte[] dst) {
		return this.buffer.readBytes(dst);
	}

	@Override
	public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
		return this.buffer.readBytes(dst, dstIndex, length);
	}

	@Override
	public ByteBuf readBytes(ByteBuffer dst) {
		return this.buffer.readBytes(dst);
	}

	@Override
	public ByteBuf readBytes(OutputStream out, int length) throws IOException {
		return this.buffer.readBytes(out, length);
	}

	@Override
	public int readBytes(GatheringByteChannel out, int length) throws IOException {
		return this.buffer.readBytes(out, length);
	}

	@Override
	public ByteBuf skipBytes(int length) {
		this.buffer.skipBytes(length);
		return this;
	}

	@Override
	public ByteBuf writeBoolean(boolean value) {
		this.buffer.writeBoolean(value);
		return this;
	}

	@Override
	public ByteBuf writeByte(int value) {
		this.buffer.writeByte(value);
		return this;
	}

	@Override
	public ByteBuf writeShort(int value) {
		this.buffer.writeShort(value);
		return this;
	}

	@Override
	public ByteBuf writeMedium(int value) {
		this.buffer.writeMedium(value);
		return this;
	}

	@Override
	public ByteBuf writeInt(int value) {
		this.buffer.writeInt(value);
		return this;
	}

	@Override
	public ByteBuf writeLong(long value) {
		this.buffer.writeLong(value);
		return this;
	}

	@Override
	public ByteBuf writeChar(int value) {
		this.buffer.writeChar(value);
		return this;
	}

	@Override
	public ByteBuf writeFloat(float value) {
		this.buffer.writeFloat(value);
		return this;
	}

	@Override
	public ByteBuf writeDouble(double value) {
		this.buffer.writeDouble(value);
		return this;
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src) {
		this.buffer.writeBytes(src);
		return this;
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src, int length) {
		this.buffer.writeBytes(src, length);
		return this;
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
		this.buffer.writeBytes(src, srcIndex, length);
		return this;
	}

	@Override
	public ByteBuf writeBytes(byte[] src) {
		this.buffer.writeBytes(src);
		return this;
	}

	@Override
	public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
		this.buffer.writeBytes(src, srcIndex, length);
		return this;
	}

	@Override
	public ByteBuf writeBytes(ByteBuffer src) {
		this.buffer.writeBytes(src);
		return this;
	}

	@Override
	public int writeBytes(InputStream in, int length) throws IOException {
		return this.buffer.writeBytes(in, length);
	}

	@Override
	public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
		return this.buffer.writeBytes(in, length);
	}

	@Override
	public ByteBuf writeZero(int length) {
		this.buffer.writeZero(length);
		return this;
	}

	@Override
	public int indexOf(int fromIndex, int toIndex, byte value) {
		return this.buffer.indexOf(fromIndex, toIndex, value);
	}

	@Override
	public int bytesBefore(byte value) {
		return this.buffer.bytesBefore(value);
	}

	@Override
	public int bytesBefore(int length, byte value) {
		return this.buffer.bytesBefore(length, value);
	}

	@Override
	public int bytesBefore(int index, int length, byte value) {
		return this.buffer.bytesBefore(index, length, value);
	}

	@Override
	public ByteBuf copy() {
		return this.buffer.copy();
	}

	@Override
	public ByteBuf copy(int index, int length) {
		return this.buffer.copy(index, length);
	}

	@Override
	public ByteBuf slice() {
		return this.buffer.slice();
	}

	@Override
	public ByteBuf slice(int index, int length) {
		return this.buffer.slice(index, length);
	}

	@Override
	public ByteBuf duplicate() {
		return this.buffer.duplicate();
	}

	@Override
	public int nioBufferCount() {
		return this.buffer.nioBufferCount();
	}

	@Override
	public ByteBuffer nioBuffer() {
		return this.buffer.nioBuffer();
	}

	@Override
	public ByteBuffer nioBuffer(int index, int length) {
		return this.buffer.nioBuffer(index, length);
	}

	@Override
	public ByteBuffer internalNioBuffer(int index, int length) {
		return this.buffer.internalNioBuffer(index, length);
	}

	@Override
	public ByteBuffer[] nioBuffers() {
		return this.buffer.nioBuffers();
	}

	@Override
	public ByteBuffer[] nioBuffers(int index, int length) {
		return this.buffer.nioBuffers(index, length);
	}

	@Override
	public boolean hasArray() {
		return this.buffer.hasArray();
	}

	@Override
	public byte[] array() {
		return this.buffer.array();
	}

	@Override
	public int arrayOffset() {
		return this.buffer.arrayOffset();
	}

	@Override
	public boolean hasMemoryAddress() {
		return this.buffer.hasMemoryAddress();
	}

	@Override
	public long memoryAddress() {
		return this.buffer.memoryAddress();
	}

	@Override
	public String toString(Charset charset) {
		return this.buffer.toString(charset);
	}

	@Override
	public String toString(int index, int length, Charset charset) {
		return this.buffer.toString(index, length, charset);
	}

	@Override
	public int hashCode() {
		return this.buffer.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.buffer.equals(obj);
	}

	@Override
	public int compareTo(ByteBuf buffer) {
		return this.buffer.compareTo(buffer);
	}

	@Override
	public String toString() {
		return this.buffer.toString();
	}

	@Override
	public ByteBuf retain(int increment) {
		return this.buffer.retain(increment);
	}

	@Override
	public ByteBuf retain() {
		return this.buffer.retain();
	}

	@Override
	public int refCnt() {
		return this.buffer.refCnt();
	}

	@Override
	public boolean release() {
		return this.buffer.release();
	}

	@Override
	public boolean release(int decrement) {
		return this.buffer.release(decrement);
	}

	public int forEachByte(ByteBufProcessor processor) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int forEachByte(int index, int length, ByteBufProcessor processor) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int forEachByteDesc(ByteBufProcessor processor) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ByteBuf asReadOnly() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getShortLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUnsignedShortLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMediumLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUnsignedMediumLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIntLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getUnsignedIntLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLongLE(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CharSequence getCharSequence(int index, int length, Charset charset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf setShortLE(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf setMediumLE(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf setIntLE(int index, int value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf setLongLE(int index, long value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setCharSequence(int index, CharSequence sequence, Charset charset) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short readShortLE() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readUnsignedShortLE() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readMediumLE() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readUnsignedMediumLE() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readIntLE() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long readUnsignedIntLE() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long readLongLE() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ByteBuf readRetainedSlice(int length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence readCharSequence(int length, Charset charset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int readBytes(FileChannel out, long position, int length) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ByteBuf writeShortLE(int value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf writeMediumLE(int value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf writeIntLE(int value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf writeLongLE(long value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int writeBytes(FileChannel in, long position, int length) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int writeCharSequence(CharSequence sequence, Charset charset) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int forEachByte(ByteProcessor processor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int forEachByte(int index, int length, ByteProcessor processor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int forEachByteDesc(ByteProcessor processor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int forEachByteDesc(int index, int length, ByteProcessor processor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ByteBuf retainedSlice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf retainedSlice(int index, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf retainedDuplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf touch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuf touch(Object hint) {
		// TODO Auto-generated method stub
		return null;
	}
}
