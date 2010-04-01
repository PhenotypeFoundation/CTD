# Now, define the function GRSN
################################################################################
# 
# This is the "R" script that implements the 
# Global Rank-invariant Set Normalization (GRSN) method.
#
# Author: Carl R. Pelz
#
# SOFTWARE COPYRIGHT NOTICE AGREEMENT
# This software and its documentation are copyright 2004-2008 by
# the author and Oregon Health & Science University.
# All rights are reserved.
#
# 12-08-08 - REV 1.0 - Updated for R 2.8.0
# 12-12-08 - REV 1.1 - Work around for overlaping graphic output files on some
#                      systems. 
# 02-04-09 - REV 1.2 - Fixed error in MvA plots due to setting A=y when it 
# should be A=(x+y)/2.  Also fixed bug that occured when no "AFFY" control probe
# sets were found in the data passed to GRSN. 
#
# This method and implementation are provided free of charge for non-commercial 
# academic use.  Any commercial use requires prior written permission from the 
# author and Oregon Health and Science University and may require licensing 
# fees.
#
# This software is supplied without any warranty or guaranteed support
# whatsoever. Oregon Health & Science University nor Carl R. Pelz can 
# be responsible for its use, misuse, or functionality.
#
# Note that at the bottom of this script, the affy package is used to 
# demonstrate the use of the GRSN method.  For information on the affy 
# package please see:
#
#  Rafael A. Irizarry, Laurent Gautier, Benjamin Milo Bolstad, and
#  Crispin Miller with contributions from Magnus Astrand
#  <Magnus.Astrand@astrazeneca.com>, Leslie M. Cope, Robert Gentleman,
#  Jeff Gentry, Conrad Halling, Wolfgang Huber, James MacDonald,
#  Benjamin I. P. Rubinstein, Christopher Workman and John Zhang (2006).
#  affy: Methods for Affymetrix Oligonucleotide Arrays. R package
#  version 1.12.2. 
#
#
################################################################################

#
# Main GRSN implementation script.
#

GRSN <- function(data,           # exprSet from affy package or matrix with column for each sample.
                 width=15,       # Width (inches) of diagnostic plot.
                 height=5,       # Height (inches) of diagnostic plot.
                 pointsize=16,   # Point size for text of diagnostic plot.
                 filetype="png", # Diagnostic plot format "png", "wmf", or "postscript".
                 ReportName=NA,  # Name for Diagnostic plots.
                 count=5000,     # Size of Global Rank-invariant Set to use.
                 f=0.25)         # Smoother parameter for lowess.
{
  rawData <- 2^exprs(data)

  # Find Affymetrix(R) control probe sets by looking for 
  # probe set IDs starting in "AFFY".
  affyIdx <- grep ("^AFFX", attr(rawData, "dimnames")[[1]])

  # Data to normalize.
  adjust <- max(0, (0.25 - min(rawData)))
  M1 <- log2(rawData + adjust)
    
  # Get the average of the reference set.
  # Do a trimmed mean to be robust, but eliminate the "artifact" that 
  # shows up when doing median on an odd number of samples.
  Mavg <- apply(M1[, ], 1, mean, trim=0.25)

  # New method for a global invariant set.
  total <- dim(M1)[[1]]
  idx <- 1:total
  subSet <- 1:total

  # Exclude Affy control probe sets from 
  # approximate global rank invaraint set (GRiS).
  if (length(affyIdx) > 0)
  {
    total <- total - length(affyIdx)
    idx <- idx[-affyIdx]
    subSet <- subSet[-affyIdx]
  }

  # Calculate number of probe sets to exclude at each iteration.
  discardNumber <- (total - count) / 4

  ### Main iteration loop to get approximate GRiS. ###
  while (TRUE)
  {
    total <- floor(max(total - discardNumber, count))
    M2 <- cbind(apply(M1[idx, ], 2, rank))
    V2 <- apply(M2, 1, var)
    subSet <- order(V2, decreasing=FALSE)[1:total]     
    idx <- idx[subSet]
    if (total == count) break
  }
  invariantIdx <- idx
  
  # Use invariant set to normalize all samples to the average.
  Mnew <- NULL
  x <- Mavg
  for (b in 1:dim(M1)[[2]])
  {
    y <- M1[,b]

    ### M vs. A transformed data.  ###
    M <- y-x
    A <- (y+x)/2

    ### Lowess curve based on M vs. A transformed data. ###
    curve <- lowess(x=A[invariantIdx], y=M[invariantIdx], f=f)

    ### Create evenly space lookup from calibration curve. ###
    aCurve <- curve[[1]]
    mCurve <- curve[[2]]
    steps <- 1000
    sampleMin <- min(A)
    sampleMax <- max(A)
    step <- (sampleMax - sampleMin) / steps
    position <- seq(sampleMin, sampleMax, length=steps + 1)
    adjust <- array(0,c(steps+1))
    count <- length(aCurve)

    idxL <- 1
    idxR <- 2
    for (i in 1:(steps + 1))
    {
      while (idxR < count && position[i] > aCurve[idxR])
      {
        idxR <- idxR + 1
      }
      while ((idxL + 1) < idxR && position[i] > aCurve[idxL + 1])
      {
        idxL <- idxL + 1
      }
      while (idxR < count && aCurve[idxL] >= aCurve[idxR])
      {
        idxR <- idxR + 1
      }
      if (aCurve[idxL] < aCurve[idxR])
      {
        adjust[i] <- (((mCurve[idxR] - mCurve[idxL])/(aCurve[idxR] - aCurve[idxL]))
                       *(position[i] - aCurve[idxL]) + mCurve[idxL])
      }
    }

    ### Apply lookup to data.  Can be applied to transformed or untransformed data. ###
    yPrime <- y - adjust[(A - sampleMin) / step + 1.5]
    mPrime <- yPrime - x

    Mnew <- cbind(Mnew, yPrime)
  }

  rownames(Mnew) <- rownames(exprs(data))
  colnames(Mnew) <- colnames(exprs(data))
  exprs(data) <- Mnew
  return(data)
}

parseCmdLine <- function(args) {
	input.file.name <- ''
	output.file.name <- ''
	workdir <- ''
        libdir <- ''
	for(i in 1:length(args)) {
		flag <- substring(args[[i]], 0, 2)
		value <- substring(args[[i]], 3, nchar(args[[i]]))
		if(flag=='-i') {
			input.file.name <- value
		} else if(flag=='-o') {
			output.file.name <- value
		} else if(flag=='-w') {
			workdir <- value
		} else if(flag=='-l') {
                        libdir <- value
		} else {
			cat(args)
			cat("\n")
			stop(paste("unknown option", flag, sep=": "), .call=FALSE)
		} 
		
	}
	setwd(workdir)
	NuGOMakeCleanData(input.file.name=input.file.name, output.file.name=output.file.name)
}

NuGOMakeCleanData <- function(input.file.name, output.file.name)  {
	options("warn"=-1)
	zip.file.name <<- input.file.name
	suppressMessages(suppressPackageStartupMessages(library(affy)))

	if(input.file.name!='') {
		cel.file.names <- get.cel.file.names(input.file.name)
	} else {
		exit("Either a .zip file or .CEL files is/are required.")
	}
	
	is.compressed <- is.compressed(cel.file.names)

        # define the Affy CDF to MBNI CDF conversion table!
	CustomCDF_Table <- as.data.frame(rbind(c("hgu133a",        "hgu133ahsentrezg"),
        	                               c("hgu133a2",       "hgu133a2hsentrezg"),
                	                       c("hgu133b",        "hgu133bhsentrezg"),
                        	               c("hgu133plus2",    "hgu133plus2hsentrezg"),
                                	       c("hgu95av2",       "hgu95av2hsentrezg"),
	                                       c("hgu95b",         "hgu95bhsentrezg"),
	                                       c("hgu95c",         "hgu95chsentrezg"),
	                                       c("hgu95d",         "hgu95dhsentrezg"),
	                                       c("hgu95e",         "hgu95ehsentrezg"),
	                                       c("mouse430a2",     "mouse430a2mmentrezg"),
	                                       c("moe430a",        "moe430ammentrezg"),
	                                       c("moe430b",        "moe430bmmentrezg"),
	                                       c("mouse4302",      "mouse4302mmentrezg"),
	                                       c("rae230a",        "rae230arnentrezg"),
	                                       c("rae230b",        "rae230brnentrezg"),
	                                       c("rat2302",        "rat2302rnentrezg"),
	                                       c("rgu34a",         "rgu34arnentrezg"),
	                                       c("rgu34b",         "rgu34brnentrezg"),
	                                       c("rgu34c",         "rgu34crnentrezg"),
	                                       c("nugohs1a520180", "nugohs1a520180hsentrezg"),
	                                       c("nugomm1a520177", "nugomm1a520177mmentrezg"),
	                                       c("mgu74av2",       "mgu74av2mmentrezg"),
	                                       c("mgu74bv2",       "mgu74bv2mmentrezg"),
	                                       c("mgu74cv2",       "mgu74cv2mmentrezg"),
	                                       c("u133x3p",        "u133x3phsentrezg"),
	                                       c("hgfocus",        "hgfocushsentrezg"),
	                                       c("hugene10stv1",   "hugene10stv1hsentrezg"),
	                                       c("mogene10stv1",   "mogene10stv1mmentrezg"),
	                                       c("ragene10stv1",   "ragene10stv1rnentrezg"),
	                                       c("huex10stv2",     "huex10stv2hsentrezg"),
	                                       c("moex10stv1",     "moex10stv1mmentrezg"),
	                                       c("raex10stv1",     "raex10stv1rnentrezg"),
	                                       c("hthgu133a",      "hthgu133ahsentrezg"),
	                                       c("hthgu133b",      "hthgu133bhsentrezg"),
	                                       c("hthgu133pluspm", "hthgu133pluspmhsentrezg"),
	                                       c("htmg430a",       "htmg430ammentrezg"),
	                                       c("htmg430b",       "htmg430bmmentrezg"),
	                                       c("htmg430pm",      "htmg430pmmmentrezg"),
	                                       c("htrat230pm",     "htrat230pmrnentrezg"),
	                                       c("htratfocus",     "htratfocusrnentrezg")));
	colnames(CustomCDF_Table) <- c("AffyCDF", "CustomCDF")

	# Try to load a single .CEL-file
	CustomCDF_Name <- ""
	x_tmp <- ReadAffy(filenames=list.celfiles()[1], compress = is.compressed)
	Indices <- grep(annotation(x_tmp), CustomCDF_Table[,1], fixed=T)
	if (length(Indices) == 0)
        {
          stop("\nChip type is not supported!\nAborting...\n")
        }
	if (length(Indices) > 1)
	{
	  for (k in 1:length(Indices))
	  {
	    if (nchar(annotation(x_tmp)) == nchar(as.character(CustomCDF_Table[Indices[k], 1])))
	    {
	      CustomCDF_Name <- as.character(CustomCDF_Table[Indices[k], 2])  
	      break;
	    }
	  }
	} else {
	  CustomCDF_Name <- as.character(CustomCDF_Table[Indices, 2])
	}
	cdfname <- CustomCDF_Name

        # Load the required annotation library
        if (require(sprintf("%s.db", cdfname), character.only=TRUE, quietly=TRUE) == FALSE)
        {
          stop(sprintf("No annotation is available for your loaded microarrays: %s.db!\nAborting...", cdfname))
        }

	# Load the .CEL-files
	x <- ReadAffy(filenames=cel.file.names, compress=is.compressed, cdfname=cdfname)

	# Apply RMA normalization
	x.norm <- rma(x)

	# Apply GRSN correction
	x.norm <- GRSN(x.norm)

	# Create the .gct file
        Descriptions <- as.data.frame(as.character(mget(featureNames(x.norm), get(sprintf("%sGENENAME", cdfname)))))
        Gene_Names <- as.data.frame(as.character(mget(featureNames(x.norm), get(sprintf("%sSYMBOL", cdfname)))))
        feature_names <- as.data.frame(featureNames(x.norm))
        Tab_Data <- as.data.frame(2^exprs(x.norm))
        Tab_Data <- cbind(feature_names, Descriptions, Tab_Data)
        cat("#1.2\n", file=sprintf("%s.gct", output.file.name))
        cat(dim(exprs(x.norm))[1], "\t", dim(exprs(x.norm))[2], "\n", file=sprintf("%s.gct", output.file.name), append = T)
        cat(paste(c("Name", "Description", colnames(exprs(x.norm))), sep="", collapse="\t"), "\n", file=sprintf("%s.gct", output.file.name), append = T)
        write.table(Tab_Data, file = sprintf("%s.gct", output.file.name), append = TRUE, quote = FALSE, sep = "\t", eol = "\n", na = "", dec = ".", row.names = FALSE, col.names = FALSE)

	# Create the .chip file
	Tab_Data <- cbind(feature_names, Gene_Names, Descriptions)
	cat("Probe Set ID\tGene Symbol\tGene Title\n", file = sprintf("%s_CDF_%s_ANN_%s_%s.chip", output.file.name, package.version(sprintf("%scdf", cdfname)), cdfname, package.version(sprintf("%s.db", cdfname))))
	write.table(Tab_Data, file = sprintf("%s_CDF_%s_ANN_%s_%s.chip", output.file.name, package.version(sprintf("%scdf", cdfname)), cdfname, package.version(sprintf("%s.db", cdfname))), append = TRUE, quote = FALSE, sep = "\t", eol = "\n", na = "", dec = ".", row.names = FALSE, col.names = FALSE)
}

########################################################
# MISC FUNCTIONS

get.cel.file.names <- function(input.file.name) {
	isWindows <- Sys.info()[["sysname"]]=="Windows"
	if(isWindows) {
		zip.unpack(input.file.name, dest=getwd())
	} else {
		 zip <- getOption("unzip")
		 system(paste(zip, "-q", input.file.name))
	}

	files <- list.files(recursive=TRUE)
   cel.files <- files[grep(".[cC][eE][lL].gz$|.[cC][eE][lL]$", files)]
	return(cel.files)
}

is.compressed <- function(cel.files) {
	for(f in cel.files) {
		r <- grep(".[cC][eE][lL].gz$", f)
		if(length(r) > 0) {
			return(TRUE)
		}
	}
	return(FALSE)
}

# Execute this function (is normally called from within GP)
Arguments_To_Pass <- as.character(noquote(commandArgs()[7:length(commandArgs())]))
parseCmdLine(Arguments_To_Pass)
