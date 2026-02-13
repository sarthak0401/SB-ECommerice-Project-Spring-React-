<h1 align="center">
SB-ECommerce-Web-Application
</h1>


Tech Stack
- 

- FrontEnd : React.js
- Backend : SpringBoot (Java)



Created the backend part of the application, which includes managing Categories, Products, Address, Cart, CartItem, OrderItem, Payment, User, Order, etc. Leveraging Spring data JPA to communicate with database efficiently, and Spring security by adding custom filter to validate/manage the JWT token attached to the request.
-

Implemented cookie based JWT token configured to authenticate the user requests. 
- 


- Containerized the whole backend using docker, created the docker image and pushed it to DockerHub 

<img width="1779" height="691" alt="image" src="https://github.com/user-attachments/assets/378c9251-fd62-4cc3-894b-d29135d5a59d" />


- The Docker image for backend is uploaded to the DockerHub

<img width="1891" height="705" alt="image" src="https://github.com/user-attachments/assets/04e9d0ac-3e8a-4291-a73a-de7735c5e3cc" />


- Used Docker compose to run this backend from DockerHub's image and the postgres database containers together and deployed the same on AWS EC2 instance.

- Dockerizing the application and running it using docker compose : 
`docker compose up --build`

<img width="1805" height="803" alt="image" src="https://github.com/user-attachments/assets/844233e7-8c99-486d-b4a2-f373cca9d386" />
<img width="1805" height="854" alt="image" src="https://github.com/user-attachments/assets/5e1ce39a-4ac5-4dcb-89e7-e722df39ffcd" />


- We can see the containers are created

<img width="1805" height="211" alt="image" src="https://github.com/user-attachments/assets/d9040782-a6f0-4b76-a996-77b977af6287" />

- The port 5433 of the system is mapped to port 5432 of the docker container on which the PostgreSQL is hosted

- Setting the server on pgadmin to connect with the postgres database running from the container

<img width="705" height="554" alt="image" src="https://github.com/user-attachments/assets/9e552dc9-153a-4ead-8262-ff087d980337" />

- Added this database running on container, to the pgadmin (NOTE : We have used the port 5433 here, since the container's 5432 default postgres port is manually mapped by us to the 5433 port of the system)


- We can see the database is connected, and these are the few user's we added manually to the database

<img width="1919" height="1009" alt="image" src="https://github.com/user-attachments/assets/ef493692-c6ae-4fe3-8dc5-fdf0d07ca50a" />

- Now we are signing through the sign-in endpoint for this admin user. We can see the Jwt token generated for them.

<img width="1914" height="952" alt="image" src="https://github.com/user-attachments/assets/58be3dcc-18aa-4b6c-a542-678becb90492" />

- Now with this Jwt token user can carry out all the api requests
- We can see the cookie is set with jwt token, this is passed with every request

<img width="1343" height="578" alt="image" src="https://github.com/user-attachments/assets/6909fabc-9d9f-40e5-a831-ef502b0acfe6" />

- Created the category with the same jwt token set in the cookie

<img width="1343" height="578" alt="image" src="https://github.com/user-attachments/assets/cd2e968e-1142-45b3-814b-c7261d5fcb70" />

- We can verify that the category of the product is added in the database

<img width="1522" height="955" alt="image" src="https://github.com/user-attachments/assets/77fa238d-ce17-423f-93d1-c25d4da18dcc" />


- We can verify that the entry in categories table is being created by getting interactive shell of the running postgres container and querying sql inside it

<img width="1357" height="804" alt="image" src="https://github.com/user-attachments/assets/dfd9aa6e-3e46-481d-8412-218ffc52b3d0" />



Now Configuring AWS EC2 for deployment
- 

- Created EC2 instance on AWS

<img width="1609" height="531" alt="image" src="https://github.com/user-attachments/assets/4e5e249e-5f7c-4387-bc07-1f6b59f9fa4e" />

- We have set the inbound traffic in the security group of ec2 instance to allow inbound traffic on port 8080, and on port 5433

- On port 8080 our app is running and on port 5433 of ec2 instance database container is exposed, so we could connect to it using pgadmin and see the database

<img width="1587" height="510" alt="image" src="https://github.com/user-attachments/assets/210dd1fb-6fe8-4bc9-baaf-f161c26fddb4" />


- We ssh into the ec2 instance and install docker and docker compose and git in it
- Cloning the project repository there

<img width="1768" height="822" alt="image" src="https://github.com/user-attachments/assets/8d082608-f728-43e1-ac81-39537989a7f8" />

- Navigated to the repository folder
- Added the .env file
- And running command `sudo docker compose up`
  which will run both the containers on ec2

<img width="1789" height="515" alt="image" src="https://github.com/user-attachments/assets/5d14f4a5-9692-4dd7-b2aa-764974c5ffb7" />


- Now we try to access the backend using the public api of EC2 server, and try to place an order using defined backend api's through postman


- Signing in with admin user



<img width="1345" height="687" alt="image" src="https://github.com/user-attachments/assets/59e5e8ee-7871-4264-be92-ee7011ccad8d" />


- Created new category "Clothing"


<img width="1345" height="687" alt="image" src="https://github.com/user-attachments/assets/7d8ba8a7-c624-4385-90c4-48e7322697c2" />

- Adding product with this category


<img width="1345" height="788" alt="image" src="https://github.com/user-attachments/assets/4df9c167-050c-4bd2-ac6f-5ce66df7edeb" />

- Added quantity = 2 of that product to the cart

<img width="1345" height="852" alt="image" src="https://github.com/user-attachments/assets/c3424339-249f-4f4f-89cc-4852df34cd04" />

- Added the address of the user


<img width="1345" height="745" alt="image" src="https://github.com/user-attachments/assets/df1cbb74-41f5-4934-8836-d032c9ebc876" />


- Now placing the order

<img width="1345" height="867" alt="image" src="https://github.com/user-attachments/assets/46911fcc-e45b-4716-b6eb-7f5f686e239b" />

<img width="1345" height="718" alt="image" src="https://github.com/user-attachments/assets/149198ca-e4ff-4513-934a-a000539b345b" />

- The order was placed successfully on the application running on docker containers using docker compose on EC2 instance AWS.


- We could verify the same using pgadmin, connecting to this database using the public ip of the instance and port 5543 of ec2 instance which is mapped to port 5542 on which postgres service is running inside the container

<img width="701" height="607" alt="image" src="https://github.com/user-attachments/assets/9c7665fc-ee06-4ef2-953e-b281e26f168b" />

- We can see the order entry is created in the orders table of the ecommerce database

<img width="1507" height="952" alt="image" src="https://github.com/user-attachments/assets/72758290-2ea3-4979-a86b-882cb2ed8c5e" />




<h3>
Successfully containerized the spring boot application using docker, and pushed the docker image to the docker hub and using this image from docker hub in the docker compose file. And deployed the same on the AWS EC2 instance
</h3>

<h4>
Thank you for checking out my project :)
</h4>
