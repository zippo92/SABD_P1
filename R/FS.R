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
    
  
  
    
    
    return(boh$wday)
    
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




#write.table(dataset_day,"~/dataset_query3.txt")

#QUERY2
dataset_q2 <- read.csv("dataset_query2.txt", sep="")
#SPLIT BY ID
out <- split( dataset_q2 , f= dataset_q2$house_houseold_plug)
#SPLIT BY TZ (selezionare un id)
out0 <- split( out$`0_0_2` , f = out$`0_0_2`$tz)
#SPLIT BY DD (selezionare una tz)
out00 <-split( out0$`1` , f = out0$`1`$mday)

#QUERY2
dataset_q3 <- read.csv("dataset_query3.txt", sep="")
#SPLIT BY ID
out <- split( dataset_q3 , f= dataset_q3$house_houseold_plug)
#SPLIT BY TZ (selezionare un id)
out0 <- split( out$`0_0_2` , f = out$`0_0_2`$timestamp)
#SPLIT BY DD (selezionare una tz)
out00 <-split( out0$`1` , f = out0$`1`$wday)

sums <- lapply(out00,meannn)
sommatotale <- 0;
for(i in 1:length(sums))
{
  sommatotale <- sommatotale + sums[[i]]
  
}  

media <- sommatotale / length(sums)



#Conf Varie
#dataset <- read.csv("~/Documenti/UNI/SABD/progetto1/SABD_P1/data/d14_filtered.csv", header=FALSE) 
#names(dataset) <- c("id","timestamp","value","property","plug_id","houseOld_id","house_id")
#dataset[,8] <- apply(dataset[,5:7],1,unisci_id);
#dataset <- dataset[dataset$V4 == 0,];
#dataset <- dataset[,-c(4,5,6,7)];
#names(dataset) <- c("id","timestamp","value","house_houseold_plug")
#dataset_day <- dataset
#dataset_day$tz <- dividiore(dataset$timestamp)
#dataset_day$mday <- hour(dataset$timestamp)
#dataset$timestamp <- timestamp(dataset$timestamp)
#dataset_day$wday <- hour(dataset$timestamp)
#dataset_day$house_houseold_plug <- dataset$house_houseold_plug
#names(dataset_day) <- c("id","hour","value","house_houseold_plug","wday")




