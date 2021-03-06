/*
 * Copyright (c) 2008-2015, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.client.impl.protocol.parameters;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.ClientMessageType;
import com.hazelcast.client.impl.protocol.util.BitUtil;
import com.hazelcast.transaction.impl.SerializableXID;

import java.util.ArrayList;
import java.util.Collection;

@edu.umd.cs.findbugs.annotations.SuppressWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class TransactionRecoverAllResultParameters {

    public static final ClientMessageType TYPE = ClientMessageType.TRANSACTION_RECOVER_ALL;
    public Collection<SerializableXID> collection;

    private TransactionRecoverAllResultParameters(ClientMessage clientMessage) {
        int size = clientMessage.getInt();
        collection = new ArrayList<SerializableXID>(size);
        for (int i = 0; i < size; i++) {
            collection.add(XIDCodec.decode(clientMessage));
        }
    }

    public static TransactionRecoverAllResultParameters decode(ClientMessage clientMessage) {
        return new TransactionRecoverAllResultParameters(clientMessage);
    }

    public static ClientMessage encode(Collection<SerializableXID> collection) {
        final int requiredDataSize = calculateDataSize(collection);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.ensureCapacity(requiredDataSize);
        clientMessage.setMessageType(TYPE.id());
        clientMessage.set(collection.size());
        for (SerializableXID serializableXID : collection) {
            XIDCodec.encode(serializableXID, clientMessage);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static int calculateDataSize(Collection<SerializableXID> collection) {
        int dataSize = ClientMessage.HEADER_SIZE + BitUtil.SIZE_OF_INT;
        for (SerializableXID serializableXID : collection) {
            dataSize += XIDCodec.calculateDataSize(serializableXID);
        }
        return dataSize;
    }


}