sudo mvn clean install
sudo scp -r target/smpp-mtn-0.0.1-SNAPSHOT.jar eddy@ec2-3-134-44-92.us-east-2.compute.amazonaws.com:/var/www/html/smpp
sudo ssh eddy@ec2-3-134-44-92.us-east-2.compute.amazonaws.com