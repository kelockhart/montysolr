package org.apache.lucene.queryParser.aqp.nodes;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.queryParser.core.nodes.FieldQueryNode;
import org.apache.lucene.queryParser.standard.nodes.WildcardQueryNode;

/**
 * A {@link NonAnalyzedQueryNode} represents a query that will not
 * be processed by an analyzer. It will be served to the search
 * engine as it is
 * 
 * Example: e(+)
 */
public class NonAnalyzedQueryNode extends WildcardQueryNode {

	private static final long serialVersionUID = 6921391439471630844L;

  /**
   * @param field
   *          - field name
   * @param text
   *          - the query
   * @param begin
   *          - position in the query string
   * @param end
   *          - position in the query string
   */
  public NonAnalyzedQueryNode(CharSequence field, CharSequence text, int begin,
      int end) {
    super(field, text, begin, end);
  }

  public NonAnalyzedQueryNode(FieldQueryNode fqn) {
    this(fqn.getField(), fqn.getText(), fqn.getBegin(), fqn.getEnd());
  }


  @Override
  public String toString() {
    return "<nonAnalyzed field='" + this.field + "' term='" + this.text + "'/>";
  }

  @Override
  public NonAnalyzedQueryNode cloneTree() throws CloneNotSupportedException {
    NonAnalyzedQueryNode clone = (NonAnalyzedQueryNode) super.cloneTree();

    // nothing to do here

    return clone;
  }

}
