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

package com.hazelcast.client.impl.protocol.task.transactionalmultimap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.parameters.DataCollectionResultParameters;
import com.hazelcast.client.impl.protocol.parameters.TransactionalMultiMapRemoveParameters;
import com.hazelcast.client.impl.protocol.task.AbstractTransactionalMessageTask;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.instance.Node;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ActionConstants;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.transaction.TransactionContext;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransactionalMultiMapRemoveMessageTask
        extends AbstractTransactionalMessageTask<TransactionalMultiMapRemoveParameters> {

    public TransactionalMultiMapRemoveMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected ClientMessage innerCall() throws Exception {
        final TransactionContext context = endpoint.getTransactionContext(parameters.txnId);
        TransactionalMultiMap<Object, Object> multiMap = context.getMultiMap(parameters.name);
        Collection<Object> collection = multiMap.remove(parameters.key);
        List<Data> list = new ArrayList<Data>(collection.size());
        for (Object o : collection) {
            list.add(serializationService.toData(o));
        }
        return DataCollectionResultParameters.encode(list);
    }

    @Override
    protected long getClientThreadId() {
        return parameters.threadId;
    }

    @Override
    protected TransactionalMultiMapRemoveParameters decodeClientMessage(ClientMessage clientMessage) {
        return TransactionalMultiMapRemoveParameters.decode(clientMessage);
    }

    @Override
    public String getServiceName() {
        return MultiMapService.SERVICE_NAME;
    }

    @Override
    public Permission getRequiredPermission() {
        return new MultiMapPermission(parameters.name, ActionConstants.ACTION_REMOVE);
    }

    @Override
    public String getDistributedObjectName() {
        return parameters.name;
    }

    @Override
    public String getMethodName() {
        return "remove";
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{parameters.key};
    }
}
