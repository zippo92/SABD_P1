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
    
    hours <- boh$hour
    
    if(boh$wday == 0 || boh$wday==6)
      return(3)
    else
    {
    if(hours>=0 && hours<6)
      return(1)
    if(hours>=6 && hours<18)
      return(0)
    if (hours>=18 && hours <= 23)
      return(2)
    }
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
  output <- sapply(data,function(y){
    
    
    aa <- as.POSIXct(y, origin="1970-01-01")
    boh <- unclass(as.POSIXlt(aa))
    
    hours <- boh$hour
    
    timeZone <- 0; 
    if(hours >= 0 && hours <= 5){
      return(0);
    }
    else if(hours >= 6 && hours <= 11){
      return(1);
    }
    else if(hours >= 12 && hours <= 17){
      return(2);
    }
    else if(hours >= 18 && hours <= 23){
      return(3);
    }
  })
  
}

meannn <- function(x) 
{
  
  
  max <- max(x$value)
  min <- min(x$value)
  
  return (max-min)
}

dataset <- read.csv("~/Documenti/UNI/SABD/progetto1/SABD_P1/data/d14_filtered.csv", header=FALSE) 
#names(dataset) <- c("id","timestamp","value","property","plug_id","houseOld_id","house_id")
dataset[,8] <- apply(dataset[,5:7],1,unisci_id);
dataset <- dataset[dataset$V4 == 0,];
dataset <- dataset[,-c(4,5,6,7)];
names(dataset) <- c("id","timestamp","value","house_houseold_plug")
dataset_day <- dataset

dataset_day$tz <- dividiore(dataset$timestamp)
dataset_day$mday <- hour(dataset$timestamp)
write.table(dataset_day,"dataset_day.txt")



#dataset$timestamp <- timestamp(dataset$timestamp)




#dataset_day$wday <- hour(dataset$timestamp)
#dataset_day$house_houseold_plug <- dataset$house_houseold_plug

#names(dataset_day) <- c("id","hour","value","house_houseold_plug","wday")






dataset_day <- read.csv("~/dataset_day.txt", sep="")
out <- split( dataset_day , f= dataset$house_houseold_plug)
out0 <- split( out$`8_0_0` , f = out$`8_0_0`$tz)
out00 <-split( out0$`0` , f = out0$`0`$mday)

View(out00$`15`)
sums <- lapply(out00,meannn)

sommatotale <- 0;
for(i in 1:length(sums))
{
  sommatotale <- sommatotale + sums[[i]]
  
}  

lamedia <- sommatotale / length(sums)



#View(out00$`0`)
#aa<-1377986760
#aa <- as.POSIXct(aa, origin="1970-01-01")

#boh <- unclass(as.POSIXlt(aa))

