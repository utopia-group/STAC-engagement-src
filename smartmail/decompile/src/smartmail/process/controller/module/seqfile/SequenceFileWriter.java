/*
 * Decompiled with CFR 0_121.
 */
package smartmail.process.controller.module.seqfile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import smartmail.datamodel.BodyWord;
import smartmail.datamodel.EmailAddress;
import smartmail.datamodel.EmailEvent;
import smartmail.logging.module.ObjSerializer;
import smartmail.process.controller.module.seqfile.ReaderWriter;

public class SequenceFileWriter {
    public static void writeWord(ReaderWriter writer, List<BytesWritable> eList, String outputuri) throws IOException {
        for (int i = 0; i < eList.size(); ++i) {
            IntWritable key = new IntWritable();
            key.set(i);
            BytesWritable obj = eList.get(i);
            writer.append(key, obj);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void writeEmail(ReaderWriter writer, List<EmailEvent> eList, String outputuri) throws IOException {
        try {
            for (int i = 0; i < eList.size(); ++i) {
                List<EmailAddress> destinations = eList.get(i).getOtherdestination();
                Iterator<EmailAddress> it = destinations.iterator();
                int poskey = 0;
                while (it.hasNext()) {
                    EmailAddress next = it.next();
                    Text t = new Text();
                    int lastIndexOf = next.getValue().lastIndexOf(58);
                    String k = lastIndexOf > 0 ? next.getValue().substring(0, lastIndexOf - 1) : next.getValue();
                    t.set(k);
                    byte[] xmlobj = ObjSerializer.serializeObj(next);
                    BytesWritable bindata = new BytesWritable(xmlobj);
                    IntWritable key = new IntWritable();
                    key.set(poskey);
                    ++poskey;
                    writer.append(key, bindata);
                }
                String bodywords = eList.get(i).getContent();
                String[] split = bodywords.split(" ");
                List<String> wordList = Arrays.asList(split);
                for (String nextWord : wordList) {
                    BodyWord bw = new BodyWord();
                    bw.setValue(nextWord);
                    Text t = new Text();
                    t.set(nextWord);
                    byte[] xmlobj = ObjSerializer.serializeObj(bw);
                    BytesWritable bindata = new BytesWritable(xmlobj);
                    IntWritable key = new IntWritable();
                    key.set(poskey);
                    ++poskey;
                    writer.append(key, bindata);
                }
            }
        }
        finally {
            IOUtils.closeStream(writer);
        }
    }
}

