library("dplyr")
library("LiblineaR")


unisci_id <- function(data)
{
  
  id= paste(c(data[3], data[2], data[1]), collapse = "_")
  
  return(id)
}


dataset <- read.csv("~/Documenti/UNI/SABD/progetto1/SABD_P1/data/d14_filtered.csv", header=FALSE, dec = ",") 
#names(dataset) <- c("id","timestamp","value","property","plug_id","houseOld_id","house_id")
dataset[,8] <- apply(dataset[,5:7],1,unisci_id);
dataset <- dataset[dataset$V4 == 0,];
dataset <- dataset[,-c(4,5,6,7)];
names(dataset) <- c("id","timestamp","value","house_houseold_plug")



out <- split( dataset , f = dataset$house_houseold_plug)
View(out$`0_0_1`)
