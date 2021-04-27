package com.raqsoft.guide.web.querys;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class IOUtils {

	public static void writeArrayList(ObjectOutput out, ArrayList o)
			throws IOException {
		if (o == null) {
			out.writeShort((short) 0);
		} else {
			int size = o.size();
			out.writeShort((short) size);
			for (int i = 0; i < size; i++) {
				out.writeObject(o.get(i));
			}
		}
	}

	public static ArrayList readArrayList(ObjectInput in) throws IOException,
			ClassNotFoundException {
		int count = in.readShort();
		ArrayList list = null;
		if (count > 0) {
			list = new ArrayList();
			for (int i = 0; i < count; i++) {
				list.add(in.readObject());
			}
		}
		return list;
	}

	public static void writeColor(ObjectOutput out, Color o) throws IOException {
		boolean isNull = (o == null);
		out.writeBoolean(isNull);

		if (!isNull) {
			int rgb = o.getRGB();
			out.writeInt(rgb);
		}
	}

	public static Color readColor(ObjectInput in) throws IOException,
			ClassNotFoundException {
		boolean isNull = in.readBoolean();
		if (!isNull) {
			int rgb = in.readInt();
			return new Color(rgb);
		}
		return null;
	}

	public static void writeHashMap(/*Dm*/ObjectOutputStream out, HashMap o)
			throws IOException {
		if (o == null) {
			out.writeShort((short) 0);
		} else {
			int size = o.size();
			out.writeShort((short) size);
			Iterator keys = o.keySet().iterator();
			for (int i = 0; i < size; i++) {
				Object key = keys.next();
				out.writeObject(key);
				Object val = o.get(key);
				out.writeObject("Object");
				out.writeObject(val);
			}
		}
	}

	public static HashMap readHashMap(/*Dm*/ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int count = in.readShort();
		HashMap map = null;
		if (count > 0) {
			map = new HashMap();
			for (int i = 0; i < count; i++) {
				Object key = in.readObject();
				Object type = in.readObject();
				Object obj = null;
				if ("Object".equals(type)) {
					obj = in.readObject();
				}
				map.put(key, obj);
			}
		}
		return map;
	}


}

