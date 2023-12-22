sudo mvn clean install
sudo scp -r target/smpp-client-0.0.2-SNAPSHOT.jar eddy@ec2-3-143-150-184.us-east-2.compute.amazonaws.com:/var/www/html/smpp
sudo ssh eddy@ec2-3-143-150-184.us-east-2.compute.amazonaws.com