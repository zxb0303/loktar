package com.loktar.util.wx.aes;

import java.util.ArrayList;

class ByteGroup {
	final ArrayList<Byte> byteContainer = new ArrayList<>();

	public byte[] toBytes() {
		byte[] bytes = new byte[byteContainer.size()];
		for (int i = 0; i < byteContainer.size(); i++) {
			bytes[i] = byteContainer.get(i);
		}
		return bytes;
	}

	public ByteGroup addBytes(byte[] bytes) {
		for (byte b : bytes) {
			byteContainer.add(b);
		}
		return this;
	}

	public int size() {
		return byteContainer.size();
	}
}
