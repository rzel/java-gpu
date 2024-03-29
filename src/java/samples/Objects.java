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

package samples;

import tools.Parallel;

/**
 * Tests deep object support, and allows performance of object based copying and
 * code to be compared to other cases.
 */
public class Objects {
  double value;

  Objects() {
    value = Math.random() * 4;
  }

  private void compute() {
    value = (Math.sin(value) * Math.sin(value))
          + (Math.cos(value) * Math.cos(value));
  }

  @Parallel(loops = "i")
  public static long run(int size) {
    Objects[] nums = new Objects[size];

    // Parallelisable, but CUDA can't compile due to 'random' and 'new'
    for(int i = 0; i < nums.length; i++) {
      nums[i] = new Objects();
    }

    long time = System.currentTimeMillis();

    // KERNEL-------------------------------
    for(int i = 0; i < nums.length; i++) {
      nums[i].compute();
    }
    // -------------------------------------

    time = System.currentTimeMillis() - time;

    int errors = 0;

    for(int j = 0; j < nums.length; j++) {
      if(Math.abs(nums[j].value - 1.0) > 0.005) {
        errors++;
      }
    }

    if(errors > 0) {
      throw new RuntimeException("Failed (size = " + size + "): " + errors + " errors");
    }

    return time;
  }
}
