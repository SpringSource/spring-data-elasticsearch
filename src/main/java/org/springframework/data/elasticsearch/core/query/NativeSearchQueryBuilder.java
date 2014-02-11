/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.core.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.facet.FacetRequest;

/**
 * NativeSearchQuery
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Artur Konczak
 */

public class NativeSearchQueryBuilder {

	private QueryBuilder queryBuilder;
	private FilterBuilder filterBuilder;
	private List<SortBuilder> sortBuilders = new ArrayList<SortBuilder>();
	private List<FacetRequest> facetRequests = new ArrayList<FacetRequest>();
	private HighlightBuilder.Field[] highlightFields;
	private Pageable pageable;
	private String[] indices;
	private String[] types;
	private String[] fields;
	private float minScore;

	public NativeSearchQueryBuilder withQuery(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
		return this;
	}

	public NativeSearchQueryBuilder withFilter(FilterBuilder filterBuilder) {
		this.filterBuilder = filterBuilder;
		return this;
	}

	public NativeSearchQueryBuilder withSort(SortBuilder sortBuilder) {
		this.sortBuilders.add(sortBuilder);
		return this;
	}

	public NativeSearchQueryBuilder withFacet(FacetRequest facetRequest) {
		facetRequests.add(facetRequest);
		return this;
	}

	public NativeSearchQueryBuilder withHighlightFields(HighlightBuilder.Field... highlightFields) {
		this.highlightFields = highlightFields;
		return this;
	}

	public NativeSearchQueryBuilder withPageable(Pageable pageable) {
		this.pageable = pageable;
		return this;
	}

	public NativeSearchQueryBuilder withIndices(String... indices) {
		this.indices = indices;
		return this;
	}

	public NativeSearchQueryBuilder withTypes(String... types) {
		this.types = types;
		return this;
	}

	public NativeSearchQueryBuilder withFields(String... fields) {
		this.fields = fields;
		return this;
	}

	public NativeSearchQueryBuilder withMinScore(float minScore) {
		this.minScore = minScore;
		return this;
	}

	public NativeSearchQuery build() {
		NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryBuilder, filterBuilder, sortBuilders, highlightFields);
		if (pageable != null) {
			nativeSearchQuery.setPageable(pageable);
		}
		if (indices != null) {
			nativeSearchQuery.addIndices(indices);
		}
		if (types != null) {
			nativeSearchQuery.addTypes(types);
		}
		if (fields != null) {
			nativeSearchQuery.addFields(fields);
		}
		if (CollectionUtils.isNotEmpty(facetRequests)) {
			nativeSearchQuery.setFacets(facetRequests);
		}

		if (minScore > 0) {
			nativeSearchQuery.setMinScore(minScore);
		}
		return nativeSearchQuery;
	}
}
