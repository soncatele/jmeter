/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter.protocol.jms.client;

import java.io.Closeable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.jmeter.protocol.jms.Utils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * OnMessageSubscriber is designed to create the connection, session and
 * subscriber. The sampler is responsible for implementing
 * javax.jms.MessageListener interface and onMessage(Message msg) method.
 *
 * The implementation provides a close() method to clean up the client at the
 * end of a test. This is important to make sure there aren't any zombie threads
 * or odd memory leaks.
 */
public class OnMessageSubscriber implements Closeable {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private final Connection CONN;

    private final Session SESSION;

    private final MessageConsumer SUBSCRIBER;

    /**
     * Constructor takes the necessary JNDI related parameters to create a
     * connection and begin receiving messages.
     *
     * @param useProps
     * @param jndi
     * @param url
     * @param connfactory
     * @param destinationName
     * @param useAuth
     * @param user
     * @param pwd
     * @throws JMSException if could not create context or other problem occurred.
     * @throws NamingException 
     */
    public OnMessageSubscriber(boolean useProps, String jndi, String url, String connfactory, String destinationName,
            boolean useAuth, String user, String pwd) throws JMSException, NamingException {
        Context ctx = InitialContextFactory.getContext(useProps, jndi, url, useAuth, user, pwd);
        CONN = Utils.getConnection(ctx, connfactory);
        SESSION = CONN.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = Utils.lookupDestination(ctx, destinationName);
        SUBSCRIBER = SESSION.createConsumer(dest);
    }

    /**
     * resume will call Connection.start() to begin receiving inbound messages.
     */
    public void resume() {
        try {
            this.CONN.start();
        } catch (JMSException e) {
            log.error("failed to start recieving");
        }
    }

    /**
     * close will close all the objects
     */
    public void close() {
        log.info("Subscriber closed");
        Utils.close(SUBSCRIBER, log);
        Utils.close(SESSION, log);
        Utils.close(CONN, log);
    }

    /**
     * The sample uses this method to set itself as the listener. That means the
     * sampler need to implement MessageListener interface.
     *
     * @param listener
     */
    public void setMessageListener(MessageListener listener) {
        try {
            this.SUBSCRIBER.setMessageListener(listener);
        } catch (JMSException e) {
            log.error(e.getMessage());
        }
    }
}