[![Scala CI](https://github.com/htrc/Metadata-combine-seq-files/actions/workflows/ci.yml/badge.svg)](https://github.com/htrc/Metadata-combine-seq-files/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/htrc/Metadata-combine-seq-files/graph/badge.svg?token=pjfOeus5cx)](https://codecov.io/gh/htrc/Metadata-combine-seq-files)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/htrc/Metadata-combine-seq-files?include_prereleases&sort=semver)](https://github.com/htrc/Metadata-combine-seq-files/releases/latest)

# Metadata-combine-seq-files
This tool can be used to combine/update sequence files

# Build
`sbt clean stage` - generates the unpacked, runnable application in the `target/universal/stage/` folder.  
`sbt clean universal:packageBin` - generates an application ZIP file

# Usage
*Note:* Must use one of the supported JVMs for Apache Spark (at this time Java 8 through Java 11 are supported)
```text
combine-seq-files <version>
HathiTrust Research Center
  -l, --log-level  <LEVEL>    (Optional) The application log level; one of INFO,
                              DEBUG, OFF (default = INFO)
  -n, --num-partitions  <N>   (Optional) The number of partitions to split the
                              input set of HT IDs into, for increased
                              parallelism
  -o, --output  <DIR>         Write the output to DIR
      --spark-log  <FILE>     (Optional) Where to write logging output from
                              Spark to
  -h, --help                  Show help message
  -v, --version               Show version of this program

 trailing arguments:
  input (required)    The path to the folder containing the input data
  update (required)   The path to the folder containing the updated data that
                      should be added to (or overwrite) the input
```
