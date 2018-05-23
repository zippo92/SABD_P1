library("dplyr")
library("LiblineaR")


unisci_id <- function(data)
{
  
  id= paste(c(data[3], data[2], data[1]), collapse = "_")
  
  return(id)
}

timestamp <- function(data)
{
  
  output <- sapply(data,function(y){
    
    
    aa <- as.POSIXct(y, origin="1970-01-01")
    boh <- unclass(as.POSIXlt(aa))
    
    return(boh$hour)
    
  })
  
  
}


hour <- function(data)
{
  
  output <- sapply(data,function(y){
    
    
    aa <- as.POSIXct(y, origin="1970-01-01")
    boh <- unclass(as.POSIXlt(aa))
    
    return(boh$mday)
    
  })
  
}

dividiore <- function(data)
{
  output <- sapply(data,function(hours){
    timeZone <- 0; 
    if(hours >= 0 && hours <= 5){
      timeZone <- 0;
    }
    else if(hours >= 6 && hours <= 11){
      timeZone <- 1;
    }
    else if(hours >= 12 && hours <= 17){
      timeZone <- 2;
    }
    else if(hours >= 18 && hours <= 23){
      timeZone <- 3;
    }
    
    return(timeZone)
    
  })
  
}


dataset <- read.csv("~/Documenti/UNI/SABD/progetto1/SABD_P1/data/d14_filtered.csv", header=FALSE) 
#names(dataset) <- c("id","timestamp","value","property","plug_id","houseOld_id","house_id")
dataset[,8] <- apply(dataset[,5:7],1,unisci_id);
dataset <- dataset[dataset$V4 == 0,];
dataset <- dataset[,-c(4,5,6,7)];
names(dataset) <- c("id","timestamp","value","house_houseold_plug")

dataset$timestamp <- timestamp(dataset$timestamp)
dataset_day <- dataset


dataset_day$wday <- hour(dataset$timestamp)
dataset_day$house_houseold_plug <- dataset$house_houseold_plug

names(dataset_day) <- c("id","hour","value","house_houseold_plug","wday")




dataset_day <- read.csv("~/dataset_day.txt", sep="")
dataset_day$hour <- dividiore(dataset_day$hour)



write.table(dataset_day,"dataset_day.txt")




out <- split( dataset_day , f= dataset$house_houseold_plug)

out0 <- split( out$`6_0_1` , f = out$`6_0_1`$hour)
out00 <-split( out0$`1` , f = out0$`1`$wday)

View(out00$`9`)


meannn <- function(x) 
{
  
  
  max <- max(x$value)
  min <- min(x$value)

  return (max-min)
}


sums <- lapply(out00,meannn)

boh <- 0;
for(i in 1:length(sums))
{
  boh <- boh + sums[[i]]
  
}  

mean602 <- boh / length(sums)

mean600 + mean601 + mean602


View(out00$`0`)
aa<-1378760900
aa <- as.POSIXct(aa, origin="1970-01-01")


#boh <- unclass(as.POSIXlt(aa))
#boh$wday
