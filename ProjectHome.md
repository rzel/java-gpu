By introducing a `@Parallel` annotation, loops can be annotated as parallel. An extra compilation can then offload these to NVIDIA CUDA compatible graphics cards. Data transfers are handled automatically, and no CUDA C code has to be written explicitly.

A full dissertation about this is available:
  * Peter Calvert: [Parallelisation of Java for Graphics Processors](http://www.cl.cam.ac.uk/~prc33/). Part II Dissertation, Computer Science Tripos, University of Cambridge June 2010.

AMD's [APARAPI](http://code.google.com/p/aparapi/) project attempts to achieve very similar goals to this project, and is still being maintained (so is more likely to work across a range of systems). The key difference is that they introduce _kernel_ classes rather than operating on `for` loops, and have slightly different restrictions on the code that can be placed within GPU sections. It is possible that some of the techniques used here could be incorporated into APARAPI.

### Compiling ###
Once you've downloaded a source package (or checked out code from subversion), you'll need to download 3 JAR files on which this compiler depends, and place them in the `lib` directory. These are:

  1. ASM: http://asm.ow2.org/ (version 3.3.1)
  1. jopt-simple: http://jopt-simple.sourceforge.net/ (version 3.2)
  1. log4j: http://logging.apache.org/log4j/1.2/

You can then perform a build using Ant by performing `ant dist` from the top level directory. This places everything you should need in the `dist` folder. In order for the Java-GPU compiler to find your CUDA and JDK installations you should also have the `CUDA_HOME` and `JDK_HOME` environment variables set. For example
```
export CUDA_HOME=/usr/local/cuda/
export JDK_HOME=/usr/lib/jvm/java-6-sun-1.6.0.20/
```

### Sample Usage ###
To compile the Mandelbrot set sample, you can simply perform:

```
java -jar Parallel.jar samples.Mandelbrot
```

from within the `dist` directory. This will produce two files (on Linux):

```
samples/Mandelbrot.class
libparallel.so
```

You should then by able to run the sample using:

```
java -cp .:Parallel.jar samples.Mandelbrot 2000 2000 out.png
```

If you have problems that are related to the compilation for your GPU, then the options passed to NVCC are specified in `src/java/cuda/CUDAExporter.java`.

### Example Code ###
Just as an example, the Java source code for the above Mandelbrot example is as follows. Note the `@Parallel` annotation that indicates the region for CUDA processing:

```
  @Parallel(loops = {"y", "x"})
  public void compute() {
    for(int y = 0; y < height; y++) {
      for(int x = 0; x < width; x++) {
        float Zr = 0.0f;
        float Zi = 0.0f;
        float Cr = (x * spacing - 1.5f);
        float Ci = (y * spacing - 1.0f);

        float ZrN = 0;
        float ZiN = 0;
        int i;

        for(i = 0; (i < iterations) && (ZiN + ZrN <= LIMIT); i++) {
          Zi = 2.0f * Zr * Zi + Ci;
          Zr = ZrN - ZiN + Cr;
          ZiN = Zi * Zi;
          ZrN = Zr * Zr;
        }
        
        data[y][x] = (short)((i * 255) / iterations);
      }
    }
  }
```