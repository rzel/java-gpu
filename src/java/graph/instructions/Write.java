/*
 * Parallelising JVM Compiler
 *
 * Copyright 2010 Peter Calvert, University of Cambridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package graph.instructions;

import graph.CodeVisitor;

import graph.state.State;

/**
 * 
 */
public class Write implements Stateful {
  private State    state;
  private Producer value;
  
  public Write(State state, Producer value) {
    this.state = state;
    this.value = value;
  }
  
  public State getState() {
    return state;
  }

  public Producer getValue() {
    return value;
  }
  
  @Override
  public String toString() {
    return "WRITE " + state;
  }
  
  public Producer[] getOperands() {
    Producer[] ret;
    Producer[] s = state.getOperands();
    
    ret = new Producer[s.length + 1];
    
    for(int i = 0; i < s.length; i++) {
      ret[i] = s[i];
    }
    
    ret[s.length] = value;
    
    return ret;
  }
  
  public <T> T accept(CodeVisitor<T> visitor) {
    if(visitor.getResult(this) != null) {
      return visitor.getResult(this);
    } else {
      T result = visitor.visit(this);
      
      visitor.putResult(this, result);
      
      return result;
    }
  }
}
