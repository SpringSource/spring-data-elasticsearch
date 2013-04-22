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
package org.springframework.data.elasticsearch.core;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.BoostableQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.springframework.data.elasticsearch.core.query.Criteria.OperationKey;

/**
 * CriteriaQueryProcessor
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Franck Marchand
 */
class CriteriaQueryProcessor {


    QueryBuilder createQueryFromCriteria(Criteria criteria) {
        if(criteria == null || criteria.getQueryCriteriaEntries().size() == 0)
            return null;

        List<QueryBuilder> shouldQueryBuilderList = new LinkedList<QueryBuilder>();
        List<QueryBuilder> mustNotQueryBuilderList = new LinkedList<QueryBuilder>();
        List<QueryBuilder> mustQueryBuilderList = new LinkedList<QueryBuilder>();


        ListIterator<Criteria> chainIterator = criteria.getCriteriaChain().listIterator();
        while (chainIterator.hasNext()) {
            Criteria chainedCriteria = chainIterator.next();
            QueryBuilder queryFragmentForCriteria = createQueryFragmentForCriteria(chainedCriteria);

            if(queryFragmentForCriteria!=null) {
                if(chainedCriteria.isOr()){
                    shouldQueryBuilderList.add(queryFragmentForCriteria);
                }else if(chainedCriteria.isNegating()){
                    mustNotQueryBuilderList.add(queryFragmentForCriteria);
                }else{
                    mustQueryBuilderList.add(queryFragmentForCriteria);
                }
            }
        }

        BoolQueryBuilder query = null;

        if(!shouldQueryBuilderList.isEmpty() || !mustNotQueryBuilderList.isEmpty() || !mustQueryBuilderList.isEmpty()) {

            query = boolQuery();

            for(QueryBuilder qb : shouldQueryBuilderList) {
                query.should(qb);
            }
            for(QueryBuilder qb : mustNotQueryBuilderList) {
                query.mustNot(qb);
            }
            for(QueryBuilder qb : mustQueryBuilderList) {
                query.must(qb);
            }
        }

        return query;
    }


    private QueryBuilder createQueryFragmentForCriteria(Criteria chainedCriteria) {
        if(chainedCriteria.getQueryCriteriaEntries().isEmpty())
            return null;

        Iterator<Criteria.CriteriaEntry> it = chainedCriteria.getQueryCriteriaEntries().iterator();
        boolean singeEntryCriteria = (chainedCriteria.getQueryCriteriaEntries().size() == 1);

        String fieldName = chainedCriteria.getField().getName();
        Assert.notNull(fieldName,"Unknown field");
        QueryBuilder query = null;

        if(singeEntryCriteria){
            Criteria.CriteriaEntry entry = it.next();
            query = processCriteriaEntry(entry.getKey(), entry.getValue(), fieldName);
        }else{
            query = boolQuery();
            while (it.hasNext()){
                Criteria.CriteriaEntry entry = it.next();
                ((BoolQueryBuilder)query).must(processCriteriaEntry(entry.getKey(), entry.getValue(), fieldName));
            }
        }

        addBoost(query, chainedCriteria.getBoost());
        return query;
    }


    private QueryBuilder processCriteriaEntry(OperationKey key, Object value, String fieldName) {
        if (value == null) {
            return null;
        }
        QueryBuilder query = null;

        switch (key){
            case  EQUALS:
                query = fieldQuery(fieldName, value); break;
            case CONTAINS:
                query = fieldQuery(fieldName,"*" + value + "*").analyzeWildcard(true); break;
            case STARTS_WITH:
                query = fieldQuery(fieldName,value +"*").analyzeWildcard(true); break;
            case ENDS_WITH:
                query = fieldQuery(fieldName, "*"+value).analyzeWildcard(true); break;
            case EXPRESSION:
                query = queryString((String)value).field(fieldName); break;
            case BETWEEN:
                Object[] ranges = (Object[]) value;
                query = rangeQuery(fieldName).from(ranges[0]).to(ranges[1]); break;
            case FUZZY:
                query = fuzzyQuery(fieldName, (String) value); break;
            case IN:
                query = boolQuery();
                Iterable<Object> collection = (Iterable<Object>) value;
                for(Object item : collection){
                   ((BoolQueryBuilder) query).should(fieldQuery(fieldName, item));
                }
                break;
            }

        return query;
    }

    private QueryBuilder buildNegationQuery(String fieldName, Iterator<Criteria.CriteriaEntry> it){
        BoolQueryBuilder notQuery =  boolQuery();
        while (it.hasNext()){
            notQuery.mustNot(fieldQuery(fieldName, it.next().getValue()));
        }
        return notQuery;
    }

    private void addBoost(QueryBuilder query, float boost){
        if(Float.isNaN(boost)){
            return;
        }
        if(query instanceof BoostableQueryBuilder){
            ((BoostableQueryBuilder)query).boost(boost);
        }

    }


}
