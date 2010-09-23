/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.hdfs.DeprecatedUTF8;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableFactories;
import org.apache.hadoop.io.WritableFactory;

/**
 * Identifies a Block uniquely across the block pools
 */
public class ExtendedBlock implements Writable {
  private String poolId;
  private Block block;

  static { // register a ctor
    WritableFactories.setFactory(ExtendedBlock.class, new WritableFactory() {
      public Writable newInstance() {
        return new ExtendedBlock();
      }
    });
  }

  public ExtendedBlock() {
    this(null, 0, 0, 0);
  }

  // TODO:FEDERATION To remove when block pool ID related coding is complete
  public ExtendedBlock(final Block b) {
    this("TODO", b);
  }
  
  public ExtendedBlock(final ExtendedBlock b) {
    this(b.poolId, b.block);
  }
  
  public ExtendedBlock(final String poolId, final long blockId) {
    this(poolId, blockId, 0, 0);
  }
  
  public ExtendedBlock(String poolId, Block b) {
    this.poolId = poolId;
    this.block = b;
  }

  public ExtendedBlock(final String poolId, final long blkid, final long len,
      final long genstamp) {
    this.poolId = poolId;
    block = new Block(blkid, len, genstamp);
  }

  public void write(DataOutput out) throws IOException {
    DeprecatedUTF8.writeString(out, poolId);
    block.writeHelper(out);
  }

  public void readFields(DataInput in) throws IOException {
    this.poolId = DeprecatedUTF8.readString(in);
    block.readHelper(in);
  }

  // Write only the identifier part of the block
  public void writeId(DataOutput out) throws IOException {
    DeprecatedUTF8.writeString(out, poolId);
    block.writeId(out);
  }

  // Read only the identifier part of the block
  public void readId(DataInput in) throws IOException {
    this.poolId = DeprecatedUTF8.readString(in);
    block.readId(in);
  }
  
  public String getPoolId() {
    return poolId;
  }

  public String getBlockName() {
    return poolId + ":" + block;
  }

  public long getNumBytes() {
    return block.getNumBytes();
  }

  public long getBlockId() {
    return block.getBlockId();
  }

  public long getGenerationStamp() {
    return block.getGenerationStamp();
  }

  public void setBlockId(final long bid) {
    block.setBlockId(bid);
  }
  
  public void setGenerationStamp(final long genStamp) {
    block.setGenerationStamp(genStamp);
  }

  public void setNumBytes(final long len) {
    block.setNumBytes(len);
  }
  
  public void set(String poolId, long blkid, long gs, long len) {
    this.poolId = poolId;
    block.set(blkid, gs, len);
  }

  public static Block getLocalBlock(final ExtendedBlock b) {
    return b == null ? null : b.getLocalBlock();
  }
  
  public Block getLocalBlock() {
    return block;
  }
  
  @Override // Object
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ExtendedBlock)) {
      return false;
    }
    ExtendedBlock b = (ExtendedBlock)o;
    return b.block.equals(block) || b.poolId.equals(poolId);
  }
  
  @Override // Object
  public int hashCode() {
    return block.hashCode();
  }
  
  @Override // Object
  public String toString() {
    return getBlockName();
  }
}
