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
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.nio.serialization.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@edu.umd.cs.findbugs.annotations.SuppressWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public class MapIntDataResultParameters {

    /**
     * ClientMessageType of this message
     */
    public static final ClientMessageType TYPE = ClientMessageType.MAP_INT_DATA_RESULT;
    public Map<Integer,Data> map;

    private MapIntDataResultParameters(ClientMessage clientMessage) {
        map = new HashMap<Integer, Data>();
        for(Map.Entry<Integer,Data> entry:map.entrySet()){
            clientMessage.set(entry.getKey());
            clientMessage.set(entry.getValue());
        }
    }

    public static MapIntDataResultParameters decode(ClientMessage clientMessage) {
        return new MapIntDataResultParameters(clientMessage);
    }

    public static ClientMessage encode(Map<Integer,Data> map) {
        final int requiredDataSize = calculateDataSize(map);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.ensureCapacity(requiredDataSize);
        clientMessage.setMessageType(TYPE.id());
        for(Map.Entry<Integer,Data> entry:map.entrySet()){
            clientMessage.set(entry.getKey());
            clientMessage.set(entry.getValue());
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static int calculateDataSize(List<Data> keys, List<Data> values) {
        int dataSize = ClientMessage.HEADER_SIZE ;
        dataSize += ParameterUtil.calculateCollectionDataSize(keys);
        dataSize += ParameterUtil.calculateCollectionDataSize(values);
        return dataSize;
    }

    public static int calculateDataSize(Map<Integer,Data> map) {
        int dataSize = ClientMessage.HEADER_SIZE ;
        for(Map.Entry<Integer,Data> entry:map.entrySet()){
            dataSize += BitUtil.SIZE_OF_INT;
            dataSize += ParameterUtil.calculateDataSize(entry.getValue());
        }
        return dataSize;
    }
}


