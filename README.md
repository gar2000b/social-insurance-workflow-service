# social-insurance-workflow-service
Social Insurance Workflow Service

docker network create -d bridge social-insurance  
docker network ls  

docker build -t onlineinteract/social-insurance-workflow-service .  
docker run -it -d -p 9081:9081 --network="social-insurance" --name social-insurance-workflow-service onlineinteract/social-insurance-workflow-service  

All optional:

docker create -it onlineinteract/social-insurance-workflow-service bash  
docker ps -a  
docker start ####  
docker ps  
docker attach ####  
docker remove ####  
docker image rm onlineinteract/social-insurance-workflow-service  
docker exec -it social-insurance-workflow-service sh  