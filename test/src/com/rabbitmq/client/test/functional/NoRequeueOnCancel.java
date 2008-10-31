//   The contents of this file are subject to the Mozilla Public License
//   Version 1.1 (the "License"); you may not use this file except in
//   compliance with the License. You may obtain a copy of the License at
//   http://www.mozilla.org/MPL/
//
//   Software distributed under the License is distributed on an "AS IS"
//   basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//   License for the specific language governing rights and limitations
//   under the License.
//
//   The Original Code is RabbitMQ.
//
//   The Initial Developers of the Original Code are LShift Ltd.,
//   Cohesive Financial Technologies LLC., and Rabbit Technologies Ltd.
//
//   Portions created by LShift Ltd., Cohesive Financial Technologies
//   LLC., and Rabbit Technologies Ltd. are Copyright (C) 2007-2008
//   LShift Ltd., Cohesive Financial Technologies LLC., and Rabbit
//   Technologies Ltd.;
//
//   All Rights Reserved.
//
//   Contributor(s): ______________________________________.
//

package com.rabbitmq.client.test.functional;

import java.io.IOException;

import com.rabbitmq.client.QueueingConsumer;

public class NoRequeueOnCancel extends BrokerTestCase
{
    protected final String Q = "NoRequeueOnCancel";

    protected void createResources() throws IOException {
        channel.queueDeclare(ticket, Q);
    }

    protected void releaseResources() throws IOException {
        channel.queueDelete(ticket, Q);
    }

    public void testNoRequeueOnCancel()
        throws IOException, InterruptedException
    {
        channel.basicPublish(ticket, "", Q, null, "1".getBytes());

        QueueingConsumer c;
        QueueingConsumer.Delivery d;

        c = new QueueingConsumer(channel);
        String consumerTag = channel.basicConsume(ticket, Q, false, c);
        d = c.nextDelivery();
        channel.basicCancel(consumerTag);

        assertNull(channel.basicGet(ticket, Q, true));

        closeChannel();
        openChannel();

        assertNotNull(channel.basicGet(ticket, Q, true));
    }
}
