/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.core.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.lang.Nullable;

/**
 * A query created from Elasticsearch QueryBuilder instances. Note: the filter constructor parameter is used to create a
 * post_filter
 * {@see https://www.elastic.co/guide/en/elasticsearch/reference/7.9/filter-search-results.html#post-filter}, if a
 * filter is needed that filters before aggregations are build, it must be included in the query constructor parameter.
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Artur Konczak
 * @author Jean-Baptiste Nizet
 * @author Martin Choraine
 * @author Peter-Josef Meisch
 */
public class NativeSearchQuery extends AbstractQuery {

	@Nullable private final QueryBuilder query;
	@Nullable private QueryBuilder filter;
	@Nullable private List<SortBuilder<?>> sorts;
	private final List<ScriptField> scriptFields = new ArrayList<>();
	@Nullable private CollapseBuilder collapseBuilder;
	@Nullable private List<AbstractAggregationBuilder<?>> aggregations;
	@Nullable private HighlightBuilder highlightBuilder;
	@Nullable private HighlightBuilder.Field[] highlightFields;
	@Nullable private List<IndexBoost> indicesBoost;

	public NativeSearchQuery(@Nullable QueryBuilder query) {

		this.query = query;
	}

	public NativeSearchQuery(@Nullable QueryBuilder query, @Nullable QueryBuilder filter) {

		this.query = query;
		this.filter = filter;
	}

	public NativeSearchQuery(@Nullable QueryBuilder query, @Nullable QueryBuilder filter,
			@Nullable List<SortBuilder<?>> sorts) {

		this.query = query;
		this.filter = filter;
		this.sorts = sorts;
	}

	public NativeSearchQuery(@Nullable QueryBuilder query, @Nullable QueryBuilder filter,
			@Nullable List<SortBuilder<?>> sorts, @Nullable HighlightBuilder.Field[] highlightFields) {

		this.query = query;
		this.filter = filter;
		this.sorts = sorts;
		this.highlightFields = highlightFields;
	}

	public NativeSearchQuery(@Nullable QueryBuilder query, @Nullable QueryBuilder filter,
			@Nullable List<SortBuilder<?>> sorts, @Nullable HighlightBuilder highlightBuilder,
			@Nullable HighlightBuilder.Field[] highlightFields) {

		this.query = query;
		this.filter = filter;
		this.sorts = sorts;
		this.highlightBuilder = highlightBuilder;
		this.highlightFields = highlightFields;
	}

	@Nullable
	public QueryBuilder getQuery() {
		return query;
	}

	@Nullable
	public QueryBuilder getFilter() {
		return filter;
	}

	@Nullable
	public List<SortBuilder<?>> getElasticsearchSorts() {
		return sorts;
	}

	@Nullable
	public HighlightBuilder getHighlightBuilder() {
		return highlightBuilder;
	}

	@Nullable
	public HighlightBuilder.Field[] getHighlightFields() {
		return highlightFields;
	}

	public List<ScriptField> getScriptFields() {
		return scriptFields;
	}

	public void setScriptFields(List<ScriptField> scriptFields) {
		this.scriptFields.addAll(scriptFields);
	}

	public void addScriptField(ScriptField... scriptField) {
		scriptFields.addAll(Arrays.asList(scriptField));
	}

	@Nullable
	public CollapseBuilder getCollapseBuilder() {
		return collapseBuilder;
	}

	public void setCollapseBuilder(CollapseBuilder collapseBuilder) {
		this.collapseBuilder = collapseBuilder;
	}

	@Nullable
	public List<AbstractAggregationBuilder<?>> getAggregations() {
		return aggregations;
	}

	public void addAggregation(AbstractAggregationBuilder<?> aggregationBuilder) {

		if (aggregations == null) {
			aggregations = new ArrayList<>();
		}

		aggregations.add(aggregationBuilder);
	}

	public void setAggregations(List<AbstractAggregationBuilder<?>> aggregations) {
		this.aggregations = aggregations;
	}

	@Nullable
	public List<IndexBoost> getIndicesBoost() {
		return indicesBoost;
	}

	public void setIndicesBoost(List<IndexBoost> indicesBoost) {
		this.indicesBoost = indicesBoost;
	}

}
