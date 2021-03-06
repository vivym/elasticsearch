/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.search.aggregations.metrics;

import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.AggregatorFactories;
import org.elasticsearch.search.aggregations.AggregatorFactory;
import org.elasticsearch.search.aggregations.CardinalityUpperBound;
import org.elasticsearch.search.aggregations.support.AggregationContext;
import org.elasticsearch.search.aggregations.support.CoreValuesSourceType;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregatorFactory;
import org.elasticsearch.search.aggregations.support.ValuesSourceConfig;
import org.elasticsearch.search.aggregations.support.ValuesSourceRegistry;
import org.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.Map;

class GeoBoundsAggregatorFactory extends ValuesSourceAggregatorFactory {

    private final boolean wrapLongitude;

    GeoBoundsAggregatorFactory(String name,
                                ValuesSourceConfig config,
                                boolean wrapLongitude,
                                AggregationContext context,
                                AggregatorFactory parent,
                                AggregatorFactories.Builder subFactoriesBuilder,
                                Map<String, Object> metadata) throws IOException {
        super(name, config, context, parent, subFactoriesBuilder, metadata);
        this.wrapLongitude = wrapLongitude;
    }

    @Override
    protected Aggregator createUnmapped(SearchContext searchContext,
                                            Aggregator parent,
                                            Map<String, Object> metadata) throws IOException {
        return new GeoBoundsAggregator(name, searchContext, parent, config, wrapLongitude, metadata);
    }

    @Override
    protected Aggregator doCreateInternal(
        SearchContext searchContext,
        Aggregator parent,
        CardinalityUpperBound cardinality,
        Map<String, Object> metadata
    ) throws IOException {
        return context.getValuesSourceRegistry()
            .getAggregator(GeoBoundsAggregationBuilder.REGISTRY_KEY, config)
            .build(name, searchContext, parent, config, wrapLongitude, metadata);
    }

    static void registerAggregators(ValuesSourceRegistry.Builder builder) {
        builder.register(GeoBoundsAggregationBuilder.REGISTRY_KEY, CoreValuesSourceType.GEOPOINT, GeoBoundsAggregator::new, true);
    }
}
