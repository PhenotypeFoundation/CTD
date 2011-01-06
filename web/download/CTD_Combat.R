############################################################################
#                                                                          #
# CTD_Combat.R                                                             #
#                                                                          #
# Author:  Philip de Groot <Philip.deGroot@wur.nl>, modified from the      #
#          original ComBat.R script (see reference below).                 #
# Version: 1.0                                                             #
# Date:    15 october 2010                                                 #
#                                                                          #
# Modified 19 October 2010:                                                #
# -Added the options to set the working directory and to define the        #
#  output.file name.                                                       #
#                                                                          #
# Description:                                                             #
# I took the original Combat.R script and removed the stuff I do not need. #
# In addition, I load and combine the different .gct-files myself, so the  #
# script only takes the resulting objects. The Combat results are          #
# automatically saved; the possibility to return the final object has been #
# removed (I do not need it).                                              #
#                                                                          #
# Reference:                                                               #
# Wynn L Walker, Isaac H Liao, Donald L Gilbert, Brenda Wong, Katherine S  #
# Pollard, Charles E McCulloch, Lisa Lit, and Frank R Sharp, Empirical     #
# Bayes accomodation of batch-effects in microarray data using identical   #
# replicate reference samples: application to RNA expression profiling of  #
# blood from Duchenne muscular dystrophy patients, BMC Genomics. 2008;     #
# 9: 494.                                                                  #
#                                                                          #
############################################################################

parseCmdLine <- function(args) {
	output.file <- ''
	workdir <- ''
	for(i in 1:length(args)) {
		flag <- substring(args[[i]], 0, 2)
		value <- substring(args[[i]], 3, nchar(args[[i]]))
		if(flag=='-o') {
			output.file <- value
		} else if(flag=='-w') {
			workdir <- value
		} else {
			cat(args)
			cat("\n")
			stop(paste("unknown option", flag, sep=": "), .call=FALSE)
		} 
	}
	setwd(workdir)
      return(output.file)
}



ComBat <- function(dat, saminfo, Probe_Information, output.file) {
  tmp <- match(colnames(dat),saminfo[,1])
  if(any(is.na(tmp)))
  {
    return('ERROR: Sample Information File and Data Array Names are not the same!')
  }
  tmp1 <- match(saminfo[,1],colnames(dat))
  saminfo <- saminfo[tmp1[!is.na(tmp1)],]		

  design <- design.mat(saminfo)	
  batches <- list.batch(saminfo)
  n.batch <- length(batches)
  n.batches <- sapply(batches, length)
  n.array <- sum(n.batches)

  ## Check for missing values
  NAs = any(is.na(dat))
  if(NAs)
  {
    cat(c('Found',sum(is.na(dat)),'Missing Data Values\n'),sep=' ')
  }

  ##Standardize Data across genes
  cat('Standardizing Data across genes\n')
  if (!NAs)
  {
    B.hat <- solve(t(design)%*%design)%*%t(design)%*%t(as.matrix(dat))
  } else {
    B.hat=apply(dat,1,Beta.NA,design)
  } #Standarization Model
  grand.mean <- t(n.batches/n.array)%*%B.hat[1:n.batch,]
  if (!NAs)
  {
    var.pooled <- ((dat-t(design%*%B.hat))^2)%*%rep(1/n.array,n.array)
  } else {
    var.pooled <- apply(dat-t(design%*%B.hat),1,var,na.rm=T)
  }

  stand.mean <- t(grand.mean)%*%t(rep(1,n.array))
  if(!is.null(design))
  {
    tmp <- design;tmp[,c(1:n.batch)] <- 0
    stand.mean <- stand.mean+t(tmp%*%B.hat)
  }
  s.data <- (dat-stand.mean)/(sqrt(var.pooled)%*%t(rep(1,n.array)))

  ##Get regression batch effect parameters
  cat("Fitting L/S model and finding priors\n")
  batch.design <- design[,1:n.batch]
  if (!NAs)
  {
    gamma.hat <- solve(t(batch.design)%*%batch.design)%*%t(batch.design)%*%t(as.matrix(s.data))
  } else {
    gamma.hat=apply(s.data,1,Beta.NA,batch.design)
  }
  delta.hat <- NULL
  for (i in batches)
  {
    delta.hat <- rbind(delta.hat,apply(s.data[,i], 1, var,na.rm=T))
  }

  ##Find Priors
  gamma.bar <- apply(gamma.hat, 1, mean)
  t2 <- apply(gamma.hat, 1, var)
  a.prior <- apply(delta.hat, 1, aprior)
  b.prior <- apply(delta.hat, 1, bprior)

  ##Find EB batch adjustments
  gamma.star <- delta.star <- NULL
  cat("Finding parametric adjustments\n")
  for (i in 1:n.batch)
  {
    temp <- it.sol(s.data[,batches[[i]]],gamma.hat[i,],delta.hat[i,],gamma.bar[i],t2[i],a.prior[i],b.prior[i])
    gamma.star <- rbind(gamma.star,temp[1,])
    delta.star <- rbind(delta.star,temp[2,])
  }

  ### Normalize the Data ###
  cat("Adjusting the Data\n")
  bayesdata <- s.data
  j <- 1
  for (i in batches)
  {
    bayesdata[,i] <- (bayesdata[,i]-t(batch.design[i,]%*%gamma.star))/(sqrt(delta.star[j,])%*%t(rep(1,n.batches[j])))
    j <- j+1
  }

  bayesdata <- log2((bayesdata*(sqrt(var.pooled)%*%t(rep(1,n.array))))+stand.mean)
  cat("#1.2\n", file=sprintf("%s.gct", output.file))
  cat(dim(bayesdata)[1],"\t", dim(bayesdata)[2],"\n", file=sprintf("%s.gct", output.file),append=TRUE)
  bayesdata <- data.frame(Probe_Information, bayesdata, check.rows=TRUE)
  cat(paste(colnames(bayesdata), sep="", collapse="\t"), "\n", file=sprintf("%s.gct", output.file), append=TRUE)
  write.table(bayesdata, file=sprintf("%s.gct", output.file), sep="\t",row.names=FALSE, col.names=FALSE, append=TRUE)
}

# Next two functions make the design matrix (X) from the sample info file 
build.design <- function(vec, des=NULL, start=2)
{
  tmp <- matrix(0,length(vec),nlevels(vec)-start+1)
  for (i in 1:ncol(tmp))
  {
    tmp[,i] <- vec==levels(vec)[i+start-1]
  }
  cbind(des,tmp)
}

design.mat <- function(saminfo)
{
  tmp <- which(colnames(saminfo) == 'Batch')
  tmp1 <- as.factor(saminfo[,tmp])
  cat("Found",nlevels(tmp1),'batches\n')
  design <- build.design(tmp1,start=1)
  ncov <- ncol(as.matrix(saminfo[,-c(1:2,tmp)]))
  cat("Found",ncov,'covariate(s)\n')
  if(ncov>0)
  {
    for (j in 1:ncov)
    {
      tmp1 <- as.factor(as.matrix(saminfo[,-c(1:2,tmp)])[,j])
      design <- build.design(tmp1,des=design)
    }
  }
  design
}

# Makes a list with elements pointing to which array belongs to which batch
list.batch <- function(saminfo)
{
  tmp1 <- as.factor(saminfo[,which(colnames(saminfo) == 'Batch')])
  batches <- NULL
  for (i in 1:nlevels(tmp1))
  {
    batches <- append(batches, list((1:length(tmp1))[tmp1==levels(tmp1)[i]]))
  }
  batches
}

# Trims the data of extra columns, note your array names cannot be named 'X' or start with 'X.'
trim.dat <- function(dat)
{
  tmp <- strsplit(colnames(dat),'\\.')
  tr <- NULL
  for (i in 1:length(tmp))
  {
    tr <- c(tr,tmp[[i]][1]!='X')
  }
  tr
}

# Following four find empirical hyper-prior values
aprior <- function(gamma.hat)
{
  m=mean(gamma.hat); s2=var(gamma.hat); (2*s2+m^2)/s2
}

bprior <- function(gamma.hat)
{
  m=mean(gamma.hat); s2=var(gamma.hat); (m*s2+m^3)/s2
}

postmean <- function(g.hat,g.bar,n,d.star,t2)
{
  (t2*n*g.hat+d.star*g.bar)/(t2*n+d.star)
}

postvar <- function(sum2,n,a,b)
{
  (.5*sum2+b)/(n/2+a-1)
}

# Pass in entire data set, the design matrix for the entire data,#
# the batch means, the batch variances, priors (m, t2, a, b),#
# columns of the data  matrix for the batch. Uses the EM to find#
# the parametric batch adjustments.
it.sol  <- function(sdat,g.hat,d.hat,g.bar,t2,a,b,conv=.0001)
{
  n <- apply(!is.na(sdat),1,sum)
  g.old <- g.hat
  d.old <- d.hat
  change <- 1
  count <- 0
  while(change>conv)
  {
    g.new <- postmean(g.hat,g.bar,n,d.old,t2)
    sum2 <- apply((sdat-g.new%*%t(rep(1,ncol(sdat))))^2, 1, sum,na.rm=T)
    d.new <- postvar(sum2,n,a,b)
    change <- max(abs(g.new-g.old)/g.old,abs(d.new-d.old)/d.old)
    g.old <- g.new
    d.old <- d.new
    count <- count+1
  }

  cat("This batch took", count, "iterations until convergence\n")
  adjust <- rbind(g.new, d.new)
  rownames(adjust) <- c("g.star","d.star")
  adjust
}

# likelihood function used below
L <- function(x,g.hat,d.hat)
{
  prod(dnorm(x,g.hat,sqrt(d.hat)))
}

# Monte Carlo integration function to find the nonparametric adjustments
int.eprior <- function(sdat,g.hat,d.hat)
{
  g.star <- d.star <- NULL
  r <- nrow(sdat)
  for(i in 1:r)
  {
    g <- g.hat[-i]
    d <- d.hat[-i]		
    x <- sdat[i,!is.na(sdat[i,])]
    n <- length(x)
    j <- numeric(n)+1
    dat <- matrix(as.numeric(x),length(g),n,byrow=T)
    resid2 <- (dat-g)^2
    sum2 <- resid2%*%j
    LH <- 1/(2*pi*d)^(n/2)*exp(-sum2/(2*d))
    LH[LH=="NaN"]=0
    g.star <- c(g.star,sum(g*LH)/sum(LH))
    d.star <- c(d.star,sum(d*LH)/sum(LH))
    if(i%%1000==0)
    {
      cat(i,'(from ',r,')\n')
    }
  }
  adjust <- rbind(g.star,d.star)
  rownames(adjust) <- c("g.star","d.star")
  adjust	
} 

#fits the L/S model in the presence of missing data values
Beta.NA = function(y,X)
{
  des=X[!is.na(y),]
  y1=y[!is.na(y)]
  B <- solve(t(des)%*%des)%*%t(des)%*%y1
  B
}


# Execute this function (is normally called from within GP)
Arguments_To_Pass <- as.character(noquote(commandArgs()[7:length(commandArgs())]))
output.file <- parseCmdLine(Arguments_To_Pass)


# Load the .gct-files and create the input objects
list.gctfiles <- function ()
{
  files <- list.files()
  return(as.character(files[grep("\\.[gG][cC][tT]$", files)]))
}
Loaded_Files <- lapply(list.gctfiles(), "read.delim", header = TRUE, sep = "\t", dec=".", skip=2, row.names=1)

# Determine which identifiers are available in all samples
Combined_Identifiers <- rownames(Loaded_Files[[1]])
for (i in 2:length(Loaded_Files))
{
  Combined_Identifiers <- Combined_Identifiers[!is.na(match(Combined_Identifiers, rownames(Loaded_Files[[i]])))]
}

# Now create the input matrix
dat <- as.matrix(Loaded_Files[[1]][Combined_Identifiers,2:ncol(Loaded_Files[[1]])])
for (i in 2:length(Loaded_Files))
{
  dat <- cbind(dat, as.matrix(Loaded_Files[[i]][Combined_Identifiers,2:ncol(Loaded_Files[[i]])]))
}

# Now create the saminfo object
saminfo <- cbind(colnames(Loaded_Files[[1]])[2:length(colnames(Loaded_Files[[1]]))], colnames(Loaded_Files[[1]])[2:length(colnames(Loaded_Files[[1]]))], as.character(rep.int(1, length(colnames(Loaded_Files[[1]]))-1)))
for (i in 2:length(Loaded_Files))
{
  saminfo <- rbind(saminfo, cbind(colnames(Loaded_Files[[i]])[2:length(colnames(Loaded_Files[[i]]))], colnames(Loaded_Files[[i]])[2:length(colnames(Loaded_Files[[i]]))], as.character(rep.int(i, length(colnames(Loaded_Files[[i]]))-1))))
}
colnames(saminfo) <- c("","","Batch")

# Now extract the included probe identifiers and their corresponding descriptions
# Put this is the object 'Probe_Information' to that it can be included when
# writing the ComBat output file.
Probe_Information <- cbind(Combined_Identifiers, as.character(Loaded_Files[[1]][match(Combined_Identifiers, rownames(Loaded_Files[[1]])), 1]))
colnames(Probe_Information) <- c("Name", "Description")

# Clean up the memory prior to executing ComBat
# Delete the old - no longer needed - gct files as well!
rm(Loaded_Files)
unlink(list.gctfiles())

# Finally execute the combat function
# The resulting file will be written in the function itself
ComBat(dat, saminfo, Probe_Information, output.file)

