# bookingtech
```properties
spring_profiles_active=prod
PROD_DB_HOST=containers-us-west-82.railway.app
PROD_DB_PORT=7316
PROD_DB_NAME=railway
PROD_DB_PASSWORD=***************
PROD_DB_USERNAME=postgres
```

wget https://github.com/mozilla/geckodriver/releases/download/v0.32.0/geckodriver-v0.32.0-linux64.tar.gz

mkdir drivers

sudo tar -xzvf geckodriver-v0.32.0-linux64.tar.gz -C /home/ec2-user/drivers

chmod +x /home/ec2-user/drivers/geckodriver

export PATH=$PATH:/home/ec2-user/drivers/

geckodriver
