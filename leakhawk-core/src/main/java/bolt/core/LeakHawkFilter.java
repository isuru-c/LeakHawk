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

package bolt.core;

import model.Post;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;

/**
 * This bolt can be extended to use for any type of filtering of post text data
 * <p>
 * isFilterPassed method needs to be override and use it for the filtering process
 *
 * @author Isuru Chandima
 */
public abstract class LeakHawkFilter extends LeakHawkBolt {

    @Override
    public void prepareBolt() {
        prepareFilter();
    }

    /**
     * This method is used to prepare the bolt as the LeakHawk application wants.
     * For creating necessary data structures and IO operations, override this method.
     * <p>
     * This method is called only once when the bolt is created in apache storm topology
     */
    public abstract void prepareFilter();

    @Override
    protected abstract String getBoltName();

    @Override
    public void execute(Tuple tuple) {
        Post post = (Post) tuple.getValue(0);

        if (isFilterPassed(post)) {
            collector.emit(post.getNextOutputStream(), tuple, new Values(post));
        }

        collector.ack(tuple);
        increaseInCount();
        runCounter();
    }

    /**
     * This method is called once for each post. Do necessary processing and return
     * true or false to forward the post more or not.
     * <p>
     * It is necessary to indicate next output stream in the post object if it is
     * going to forward further in LeakHawk core system.
     *
     * @param post Post object containing every detail of a single post
     * @return true if passed from the filter. false otherwise.
     */
    public abstract boolean isFilterPassed(Post post);

    @Override
    public abstract ArrayList<String> declareOutputStreams();
}
