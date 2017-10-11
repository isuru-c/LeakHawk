/*
 *    Copyright 2017 SWIS
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package bolt;

import bolt.core.LeakHawkUtility;
import model.Statics;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Tuple;

import java.sql.Connection;

/**
 * This bolt is used to collect the statics of posts processed in different nodes
 * in the LeakHawk topology. All the statics are emitted from different nodes and
 * are collected from this bolt and do the necessary processing for them.
 *
 * Main objective is to write statics to the database so the LeakHawk monitor can
 * later collect those statics.
 *
 * @author Isuru Chandima
 */
public class StaticsCounter extends LeakHawkUtility {

    private Connection connection;

    @Override
    public void prepareUtility() {
        // Create a database connection in here as required
    }

    @Override
    public void executeUtility(Tuple tuple, OutputCollector collector) {

        Statics statics = (Statics) tuple.getValue(0);

        System.out.println(statics.getBoltType() + ": in - " + statics.getInCount() + " out - " + statics.getOutCount());
    }
}