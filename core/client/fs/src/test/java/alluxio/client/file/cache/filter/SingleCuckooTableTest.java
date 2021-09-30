/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0 (the
 * "License"). You may not use this work except in compliance with the License, which is available
 * at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.client.file.cache.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SingleCuckooTableTest {
  static final int NUM_BUCKETS = 16;
  static final int TAGS_PER_BUCKET = 4;
  static final int BITS_PER_TAG = 8;

  CuckooTable createCuckooTable() {
    AbstractBitSet bits = new BuiltinBitSet(NUM_BUCKETS * BITS_PER_TAG * 4);
    return new SingleCuckooTable(bits, NUM_BUCKETS, TAGS_PER_BUCKET, BITS_PER_TAG);
  }

  @Test
  public void readWriteTagTest() {
    CuckooTable cuckooTable = createCuckooTable();
    Random random = new Random();
    for (int i = 0; i < NUM_BUCKETS; i++) {
      for (int j = 0; j < TAGS_PER_BUCKET; j++) {
        int tag = random.nextInt(0xff);
        cuckooTable.writeTag(i, j, tag);
        int t = cuckooTable.readTag(i, j);
        assertEquals(tag, t);
      }
    }
  }

  @Test
  public void findTagTest() {
    CuckooTable cuckooTable = createCuckooTable();
    Random random = new Random();
    for (int i = 0; i < NUM_BUCKETS; i++) {
      for (int j = 0; j < TAGS_PER_BUCKET; j++) {
        int tag = random.nextInt(0xff);
        cuckooTable.writeTag(i, j, tag);
        assertTrue(cuckooTable.findTagInBucket(i, tag));
      }
    }
  }

  @Test
  public void deleteTagTest() {
    CuckooTable cuckooTable = createCuckooTable();
    Random random = new Random();
    for (int i = 0; i < NUM_BUCKETS; i++) {
      for (int j = 0; j < TAGS_PER_BUCKET; j++) {
        int tag = random.nextInt(0xff);
        cuckooTable.writeTag(i, j, tag);
        assertTrue(cuckooTable.deleteTagFromBucket(i, tag));
      }
    }
  }

  @Test
  public void rewriteTest() {
    CuckooTable cuckooTable = createCuckooTable();
    Random random = new Random();
    for (int i = 0; i < NUM_BUCKETS; i++) {
      for (int j = 0; j < TAGS_PER_BUCKET; j++) {
        int tag = random.nextInt(0xff);
        cuckooTable.writeTag(i, j, tag);
        assertTrue(cuckooTable.deleteTagFromBucket(i, tag));
        int tag2 = random.nextInt(0xff);
        cuckooTable.writeTag(i, j, tag2);
        assertTrue(cuckooTable.deleteTagFromBucket(i, tag2));
      }
    }
  }

  @Test
  public void insertOrKickOneTest() {
    CuckooTable cuckooTable = createCuckooTable();
    Random random = new Random();
    for (int i = 0; i < NUM_BUCKETS; i++) {
      Set<Integer> seen = new HashSet<>();
      for (int j = 0; j < TAGS_PER_BUCKET; j++) {
        int tag = random.nextInt(0xff);
        cuckooTable.writeTag(i, j, tag);
        seen.add(tag);
      }
      int tag = random.nextInt(0xff);
      int oldTag = cuckooTable.insertOrKickoutOne(i, tag);
      assertTrue(seen.contains(oldTag));
    }
  }
}
