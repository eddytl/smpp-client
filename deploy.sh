sudo mvn clean install
sudo scp -r target/smpp-client-0.0.1-SNAPSHOT.jar ronaldo@ec2-3-131-121-12.us-east-2.compute.amazonaws.com:/var/www/html/smpp
sudo ssh ronaldo@ec2-3-131-121-12.us-east-2.compute.amazonaws.com