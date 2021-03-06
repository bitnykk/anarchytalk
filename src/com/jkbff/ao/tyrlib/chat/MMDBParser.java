package com.jkbff.ao.tyrlib.chat;

import java.io.IOException;
import java.io.RandomAccessFile;
import ao.misc.ByteConvert;

//import no.geosoft.cc.util.ByteSwapper;
public class MMDBParser {

    public static String getMessage(long categoryId, long instanceId, String file) {
        RandomAccessFile in = null;

        try {
            in = new RandomAccessFile(file, "r");

            Entry category = findEntry(in, categoryId, 8);
            if (category == null) {
                throw new RuntimeException("Unknown Reference Type -- Could not find category. categoryId: '" + categoryId + "' instanceId: '" + instanceId + "'");
            }

            Entry instance = findEntry(in, instanceId, category.offset);
            if (instance == null) {
                throw new RuntimeException("Unknown Reference Type -- Could not find instance. categoryId: '" + categoryId + "' instanceId: '" + instanceId + "'");
            }

            in.seek(instance.offset);
            String message = readString(in);
            return message;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private static Entry readEntry(RandomAccessFile in) throws IOException {
        Entry entry = new Entry();
        entry.entryId = ByteConvert.swap(in.readInt());
        entry.offset = ByteConvert.swap(in.readInt());

        return entry;
    }

    private static Entry findEntry(RandomAccessFile in, long entryId, long offset) throws IOException {
        in.seek(offset);

        Entry previousEntry = null;
        Entry currentEntry = null;
        do {
            previousEntry = currentEntry;
            currentEntry = readEntry(in);

            if (previousEntry != null && currentEntry.entryId < previousEntry.entryId) {
                return null;
            }
        } while (entryId != currentEntry.entryId);

        return currentEntry;
    }

    private static String readString(RandomAccessFile in) throws IOException {
        String message = "";
        char character;

        character = (char) in.readByte();
        while (character != 0) {
            message += character;
            character = (char) in.readByte();
        }

        return message;
    }

    private static class Entry {

        public long entryId;
        public long offset;
    }
}
