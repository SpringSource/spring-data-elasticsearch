/*
 * Copyright 2021 the original author or authors.
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
package org.springframework.data.elasticsearch.core.index;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexInformation;
import org.springframework.data.elasticsearch.junit.jupiter.ElasticsearchRestTemplateConfiguration;
import org.springframework.data.elasticsearch.junit.jupiter.SpringIntegrationTest;
import org.springframework.test.context.ContextConfiguration;

import lombok.Data;

/**
 * @author George Popides
 */
@SpringIntegrationTest
@ContextConfiguration(classes = { ElasticsearchRestTemplateConfiguration.class })
public class IndexOperationTests {
	@Autowired
	protected ElasticsearchOperations operations;


	@Test // #1646
	@DisplayName("should return info of all indices using rest template")
	void shouldReturnInformationList() {
		IndexOperations indexOps = operations.indexOps(EntityWithSettingsAndMappingsRest.class);

		indexOps.create();
		indexOps.putMapping();

		indexOps.alias(new AliasActions(
				new AliasAction.Add(AliasActionParameters.builder().withIndices("test-index-rest-information-list").withAliases("alias").build()))
		);

		List<IndexInformation> indexInformationList = indexOps.getInformation();

		IndexInformation indexInformation = indexInformationList.get(0);

		assertThat(indexInformationList.size()).isEqualTo(1);
		assertThat(indexInformation.getSettings().get("index.number_of_shards")).isEqualTo("1");
		assertThat(indexInformation.getSettings().get("index.number_of_replicas")).isEqualTo("0");
		assertThat(indexInformation.getSettings().get("index.analysis.analyzer.emailAnalyzer.type")).isEqualTo("custom");

		assertThat(indexInformation.getMappings()).containsKey("properties");

		assertThat(indexInformation.getName()).isEqualTo("test-index-rest-information-list");
		assertThat(indexInformation.getMappings()).isInstanceOf(org.springframework.data.elasticsearch.core.document.Document.class);
		assertThat(indexInformation.getSettings()).isInstanceOf(org.springframework.data.elasticsearch.core.document.Document.class);
		assertThat(indexInformation.getAliases()).isInstanceOf(List.class);
	}

	@Data
	@Document(indexName = "test-index-rest-information-list", createIndex = false)
	@Setting(settingPath = "settings/test-settings.json")
	@Mapping(mappingPath = "mappings/test-mappings.json")
	private static class EntityWithSettingsAndMappingsRest {
		@Id
		String id;
	}
}
