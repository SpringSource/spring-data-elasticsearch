/*
 * Copyright 2018 the original author or authors.
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
package org.springframework.data.elasticsearch.core;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.elasticsearch.index.query.QueryBuilders;
import org.reactivestreams.Publisher;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Interface that specifies a basic set of Elasticsearch operations executed in a reactive way.
 * <p>
 * Implemented by {@link ReactiveElasticsearchTemplate}. Not often used but a useful option for extensibility and
 * testability (as it can be easily mocked, stubbed, or be the target of a JDK proxy). Command execution using
 * {@link ReactiveElasticsearchOperations} is deferred until a {@link org.reactivestreams.Subscriber} subscribes to the
 * {@link Publisher}.
 *
 * @author Christoph Strobl
 * @since 4.0
 */
public interface ReactiveElasticsearchOperations {

	/**
	 * Execute within a {@link ClientCallback} managing resources and translating errors.
	 *
	 * @param callback must not be {@literal null}.
	 * @param <T>
	 * @return the {@link Publisher} emitting results.
	 */
	<T> Publisher<T> execute(ClientCallback<Publisher<T>> callback);

	/**
	 * Index the given entity, once available, extracting index and type from entity metadata.
	 *
	 * @param entityPublisher must not be {@literal null}.
	 * @param <T>
	 * @return a {@link Mono} emitting the saved entity.
	 */
	default <T> Mono<T> insert(Mono<? extends T> entityPublisher) {

		Assert.notNull(entityPublisher, "EntityPublisher must not be null!");
		return entityPublisher.flatMap(this::insert);
	}

	/**
	 * Index the given entity extracting index and type from entity metadata.
	 *
	 * @param entity must not be {@literal null}.
	 * @param <T>
	 * @return a {@link Mono} emitting the saved entity.
	 */
	default <T> Mono<T> insert(T entity) {
		return insert(entity, null);
	}

	/**
	 * Index the entity, once available, in the given {@literal index}. If the index is {@literal null} or empty the index
	 * name provided via entity metadata is used.
	 *
	 * @param entityPublisher must not be {@literal null}.
	 * @param index the name of the target index. Can be {@literal null}.
	 * @param <T>
	 * @return a {@link Mono} emitting the saved entity.
	 */
	default <T> Mono<T> insert(Mono<? extends T> entityPublisher, String index) {

		Assert.notNull(entityPublisher, "EntityPublisher must not be null!");
		return entityPublisher.flatMap(it -> insert(it, index));
	}

	/**
	 * Index the entity in the given {@literal index}. If the index is {@literal null} or empty the index name provided
	 * via entity metadata is used.
	 *
	 * @param entity must not be {@literal null}.
	 * @param index the name of the target index. Can be {@literal null}.
	 * @param <T>
	 * @return a {@link Mono} emitting the saved entity.
	 */
	default <T> Mono<T> insert(T entity, @Nullable String index) {
		return insert(entity, index, null);
	}

	/**
	 * Index the entity, once available, under the given {@literal type} in the given {@literal index}. If the
	 * {@literal index} is {@literal null} or empty the index name provided via entity metadata is used. Same for the
	 * {@literal type}.
	 *
	 * @param entityPublisher must not be {@literal null}.
	 * @param index the name of the target index. Can be {@literal null}.
	 * @param type the name of the type within the index. Can be {@literal null}.
	 * @param <T>
	 * @return a {@link Mono} emitting the saved entity.
	 */
	default <T> Mono<T> insert(Mono<? extends T> entityPublisher, @Nullable String index, @Nullable String type) {

		Assert.notNull(entityPublisher, "EntityPublisher must not be null!");
		return entityPublisher.flatMap(it -> insert(it, index, type));
	}

	/**
	 * Index the entity under the given {@literal type} in the given {@literal index}. If the {@literal index} is
	 * {@literal null} or empty the index name provided via entity metadata is used. Same for the {@literal type}.
	 *
	 * @param entity must not be {@literal null}.
	 * @param index the name of the target index. Can be {@literal null}.
	 * @param type the name of the type within the index. Can be {@literal null}.
	 * @param <T>
	 * @return a {@link Mono} emitting the saved entity.
	 */
	<T> Mono<T> insert(T entity, @Nullable String index, @Nullable String type);

	/**
	 * Find the document with the given {@literal id} mapped onto the given {@literal entityType}.
	 *
	 * @param id the {@literal _id} of the document to fetch.
	 * @param entityType the domain type used for mapping the document.
	 * @param <T>
	 * @return {@link Mono#empty()} if not found.
	 */
	default <T> Mono<T> findById(String id, Class<T> entityType) {
		return findById(id, entityType, null);
	}

	/**
	 * Fetch the entity with given {@literal id}.
	 *
	 * @param id the {@literal _id} of the document to fetch.
	 * @param entityType the domain type used for mapping the document.
	 * @param index the name of the target index. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @param <T>
	 * @return {@link Mono#empty()} if not found.
	 */
	default <T> Mono<T> findById(String id, Class<T> entityType, @Nullable String index) {
		return findById(id, entityType, index, null);
	}

	/**
	 * Fetch the entity with given {@literal id}.
	 *
	 * @param id must not be {@literal null}.
	 * @param index the name of the target index. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @param type the name of the target type. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @param <T>
	 * @return the {@link Mono} emitting the entity or signalling completion if none found.
	 */
	<T> Mono<T> findById(String id, Class<T> entityType, @Nullable String index, @Nullable String type);

	/**
	 * Check if an entity with given {@literal id} exists.
	 * 
	 * @param id the {@literal _id} of the document to look for.
	 * @param entityType the domain type used.
	 * @return a {@link Mono} emitting {@literal true} if a matching document exists, {@literal false} otherwise.
	 */
	default Mono<Boolean> exists(String id, Class<?> entityType) {
		return exists(id, entityType, null);
	}

	/**
	 * Check if an entity with given {@literal id} exists.
	 *
	 * @param id the {@literal _id} of the document to look for.
	 * @param entityType the domain type used.
	 * @param index the name of the target index. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @return a {@link Mono} emitting {@literal true} if a matching document exists, {@literal false} otherwise.
	 */
	default Mono<Boolean> exists(String id, Class<?> entityType, @Nullable String index) {
		return exists(id, entityType, index, null);
	}

	/**
	 * Check if an entity with given {@literal id} exists.
	 *
	 * @param id the {@literal _id} of the document to look for.
	 * @param index the name of the target index. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @param type the name of the target type. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @return a {@link Mono} emitting {@literal true} if a matching document exists, {@literal false} otherwise.
	 */
	Mono<Boolean> exists(String id, Class<?> entityType, @Nullable String index, @Nullable String type);

	/**
	 * Search the index for entities matching the given {@link Query query}.
	 *
	 * @param query must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @param <T>
	 * @return a {@link Flux} emitting matching entities one by one.
	 */
	default <T> Flux<T> find(Query query, Class<T> entityType) {
		return find(query, entityType, entityType);
	}

	/**
	 * Search the index for entities matching the given {@link Query query}.
	 *
	 * @param query must not be {@literal null}.
	 * @param entityType The entity type for mapping the query. Must not be {@literal null}.
	 * @param returnType The mapping target type. Must not be {@literal null}. Th
	 * @param <T>
	 * @return a {@link Flux} emitting matching entities one by one.
	 */
	default <T> Flux<T> find(Query query, Class<?> entityType, Class<T> returnType) {
		return find(query, entityType, null, null, returnType);
	}

	/**
	 * Search the index for entities matching the given {@link Query query}.
	 *
	 * @param query must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @param <T>
	 * @return a {@link Flux} emitting matching entities one by one.
	 */
	default <T> Flux<T> find(Query query, Class<T> entityType, @Nullable String index) {
		return find(query, entityType, index, null);
	}

	/**
	 * Search the index for entities matching the given {@link Query query}.
	 *
	 * @param query must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @param index the name of the target index. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @param type the name of the target type. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @param <T>
	 * @returnm a {@link Flux} emitting matching entities one by one.
	 */
	default <T> Flux<T> find(Query query, Class<T> entityType, @Nullable String index, @Nullable String type) {
		return find(query, entityType, index, type, entityType);
	}

	/**
	 * Search the index for entities matching the given {@link Query query}.
	 *
	 * @param query must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @param index the name of the target index. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @param type the name of the target type. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @param resultType the projection result type.
	 * @param <T>
	 * @return a {@link Flux} emitting matching entities one by one.
	 */
	<T> Flux<T> find(Query query, Class<?> entityType, @Nullable String index, @Nullable String type,
			Class<T> resultType);

	/**
	 * Count the number of documents matching the given {@link Query}.
	 *
	 * @param entityType must not be {@literal null}.
	 * @return a {@link Mono} emitting the nr of matching documents.
	 */
	default Mono<Long> count(Class<?> entityType) {
		return count(new StringQuery(QueryBuilders.matchAllQuery().toString()), entityType, null);
	}

	/**
	 * Count the number of documents matching the given {@link Query}.
	 *
	 * @param query must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @return a {@link Mono} emitting the nr of matching documents.
	 */
	default Mono<Long> count(Query query, Class<?> entityType) {
		return count(query, entityType, null);
	}

	/**
	 * Count the number of documents matching the given {@link Query}.
	 *
	 * @param query must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @param index the name of the target index. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @return a {@link Mono} emitting the nr of matching documents.
	 */
	default Mono<Long> count(Query query, Class<?> entityType, @Nullable String index) {
		return count(query, entityType, index, null);
	}

	/**
	 * Count the number of documents matching the given {@link Query}.
	 *
	 * @param query must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @param index the name of the target index. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @param type the name of the target type. Overwrites document metadata from {@literal entityType} if not
	 *          {@literal null}.
	 * @return a {@link Mono} emitting the nr of matching documents.
	 */
	Mono<Long> count(Query query, Class<?> entityType, @Nullable String index, @Nullable String type);

	/**
	 * Callback interface to be used with {@link #execute(ClientCallback)} for operating directly on
	 * {@link ReactiveElasticsearchClient}.
	 *
	 * @param <T>
	 * @author Christoph Strobl
	 * @since 4.0
	 */
	interface ClientCallback<T extends Publisher<?>> {

		T doWithClient(ReactiveElasticsearchClient client);
	}
}
