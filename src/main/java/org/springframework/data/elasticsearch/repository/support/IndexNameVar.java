/*
 * Copyright 2017 the original author or authors.
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
package org.springframework.data.elasticsearch.repository.support;

import java.util.function.Supplier;

/**
 * @author zzt
 */
public class IndexNameVar {

	private ThreadLocal<String> vars;

	public IndexNameVar(String var) {
		vars = ThreadLocal.withInitial(() -> var);
	}

	public IndexNameVar(Supplier<? extends String> supplier) {
		vars = ThreadLocal.withInitial(supplier);
	}

	public void setVars(String var) {
		vars.set(var);
	}

	public String toString() {
		return vars.get();
	}

}
