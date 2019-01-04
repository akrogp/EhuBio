# Analysis steps

## Grouping of samples
* One group is composed by the control (three replicates) and the other with the sample (three replicates)

## Filtering
* Reverse proteins (a '+' in the 'Reverse' column)
* Only identified by site ('+' in the corresponding column)
* Minimum two unique peptides
* At least three values in one group or two values in the two groups

## Transformation
* Transform original LFQ values to log2. The resulting distribution should be more or less normal.

## Imputation
Impute missing values from normal distribution:

1. Calculate width and center of the distribution of the LFQ values
2. Shrink the distributions to a factor of '0.3' (width) standard deviations
3. Shift it down by '1.8' (down shift) standard deviations
4. Simulate some random values that make up values to fill up the missing values

This proccess can be performed using two different modes:

* Column: applied to each expression column (replicate) separately
* **Matrix: the whole matrix at once**

## Fold-change
1. Calculate mean value of LFQ replicates of the sample (+)
2. Calculate mean value of LFQ replicates of the control (-)
3. Substract 1 minus 2

## Two-sample t-test (p-value)
Options:

* Perseus uses a two-sample t-test with a permutation-based FDR
* t* or moderated t-statistic (package limma of bioconductor)
* TREAT (package limma of bioconductor)
* [This paper](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4016238/) presents tTREAT2.
* [Prostar](http://www.prostar-proteomics.org/) uses bioconductor and it is for quantitative proteomics.
