/*
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.eventsourcing.eventstore;

import org.axonframework.eventhandling.DomainEventMessage;
import org.junit.jupiter.api.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.axonframework.eventsourcing.utils.EventStoreTestUtils.AGGREGATE;
import static org.axonframework.eventsourcing.utils.EventStoreTestUtils.createEvents;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class validating the specifics around a {@link BatchingEventStorageEngine}.
 *
 * @author Rene de Waele
 */
@Transactional
public abstract class BatchingEventStorageEngineTest<E extends BatchingEventStorageEngine, EB extends BatchingEventStorageEngine.Builder>
        extends AbstractEventStorageEngineTest<E, EB> {

    private BatchingEventStorageEngine testSubject;

    @Test
    void testLoadLargeAmountOfEvents() {
        int eventCount = testSubject.batchSize() + 10;
        testSubject.appendEvents(createEvents(eventCount));
        assertEquals(eventCount, testSubject.readEvents(AGGREGATE).asStream().count());
        Optional<? extends DomainEventMessage<?>> resultEventMessage =
                testSubject.readEvents(AGGREGATE).asStream().reduce((a, b) -> b);
        assertTrue(resultEventMessage.isPresent());
        assertEquals(eventCount - 1, resultEventMessage.get().getSequenceNumber());
    }

    protected void setTestSubject(BatchingEventStorageEngine testSubject) {
        super.setTestSubject(this.testSubject = testSubject);
    }
}
