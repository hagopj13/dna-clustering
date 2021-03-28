# DNA Clustering

Just found this cool project, I've build with [Shant Derounian](https://github.com/shantd9), back in 2017.

The purpose of this project is to cluster large collections of DNA sequences using different algorithms and visualize the clustering process.

The algorithms that we used are (explained below):

 - Hierarchical agglomerative clustering
 - K-medoids clustering
 - Buckshot algorithm
 - mBKM clustering

![Main](images/main.jpg?raw=true)

## Algorithms

### Hierarchical agglomerative clustering

Hierarchical Clustering consists of generating a set of nested clusters neatly organized in a hierarchy of clusters (a tree) called a dendrogram. The root node of the dendrogram represents the whole data set and as we go further down the tree, each leaf node represents a data object. 

![Main](images/clusters.jpg?raw=true)

There are two general types of hierarchical clustering: 

 - agglomerative, which is a "bottom up" approach that merges clusters as one moves up the hierarchy
 - divisive, which is a "top down" approach which splits clusters as one moves down the hierarchy.

In this project we implement hierarchical agglomerative clustering (HAC) which seeks to both reduce errors and complexity and generate from its results a to-scale dendrogram. Nevertheless, complexity in agglomerative clustering is high and the approach is not too practical for huge numbers of sequences.

### K-medoids (partitional) clustering

Partitional clustering consists of dividing data objects into non overlapping subsets whereby each object is in exactly one cluster.

By far the most popular algorithm in partitional clustering is k means which allows the user to set the number of clusters k. It works by setting an initial k centroids and then assigning objects to their nearest centroid and recomputing centroids.

However, in this implementation, we modified the centroid approach of k means to take medoids (the most representative sequence in the data) as there is no clear way to find the center sequence of a subset of the data. Instead it is clearer to select the sequence in the subset which has the highest combined similarity to all the other sequences (which is in fact k-medoids).

K-medoids is much faster than HAC but is much less accurate in its bare form and requires the user to set k. Also, the initial medoid (or centroid) selection greatly affects the results produced.

### Buckshot algorithm

Hybrid clustering is a third main approach to clustering which tends to combine the two main strategies which are partitional and hierarchical clustering. 

The buckshot algorithm is one such approach which seeks to both: reduce the time complexity taken by HAC by maintaining the speed of k-means, and preserve the accuracy of HAC by fixing the issues produced by k-means.

Buckshot does this by computing HAC for a √𝑘𝑛 of the population and stopping at k to use the most representative sequence of each cluster from results generated as an initial medoid seed for k-means. Buckshot thus only adds a slight time addition √𝑛 while largely improving k-means’ initial random medoid selection. 

Buckshot can also properly identify outliers in the data and is thus a perfect candidate for a fast DNA clustering tool.

### mBKM clustering

The standard bisecting k-means (BKM) algorithm is a regular method used for divisive clustering where instead of the clusters being formed at the beginning, they start as one cluster and are bisected till reaching singleton sets. The two main issues with BKM are that: randomly chosen initial centroids will cause a local optimization to be reached and the largest cluster size is not an effective measure for naturally differently sized clusters.

This is where mBKM comes in and solves initial cluster centroids by picking maximum distance points to avoid obtaining adjacent initial elements and solves the splitting problem by using the variance of clusters instead of their size.

The way it works in detail is it: 

 - takes the entire dataset as a single cluster
 - picks a cluster to split
 - finds two sub-clusters and gets their initial centroids using maximum distance (minimum similarity) 
 - assigns points to these centroids (medoids in this case)
 - keeps recalculating the centroids and assigning points until no change in cluster centroid calculation is found
 - then calculates the variance of each cluster and takes the split that will produce a resulting cluster with the highest variance
 - these steps are repeated till k clusters are reached


mBKM is a much more powerful version of k-means which also seeks to solve the initial centroids issue (which we found to be very severe when it comes to DNA sequences).
